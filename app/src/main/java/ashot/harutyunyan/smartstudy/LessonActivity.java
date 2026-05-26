package ashot.harutyunyan.smartstudy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class LessonActivity extends AppCompatActivity {

    TextView tvTopicTitle, tvExplanation, tvExamples, btnBack;
    FloatingActionButton btnAskAi;

    String explanation;
    String examples;
    String subject;
    String topic;
    int grade;

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler mainHandler = new Handler(Looper.getMainLooper());
    String apiKey = "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";

    LinearLayout chatContainer;
    ScrollView chatScrollView;
    EditText etAiMessage;
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        tvTopicTitle = findViewById(R.id.tvTopicTitle);
        tvExplanation = findViewById(R.id.tvExplanation);
        tvExamples = findViewById(R.id.tvExamples);
        btnAskAi = findViewById(R.id.btnAskAi);

        topic = getIntent().getStringExtra("topic");
        subject = getIntent().getStringExtra("subject");
        grade = getIntent().getIntExtra("grade", 1);

        tvTopicTitle.setText(topic);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        loadTopicFromFirestore();

        btnAskAi.setOnClickListener(v -> openAiChat());
    }

    private void openAiChat() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_ai, null);
        dialog.setContentView(sheetView);
        BottomSheetBehavior behavior = BottomSheetBehavior.from((View) sheetView.getParent());
        behavior.setDraggable(false);

        chatContainer = sheetView.findViewById(R.id.chatContainer);
        chatScrollView = sheetView.findViewById(R.id.scrollView);
        etAiMessage = sheetView.findViewById(R.id.etMessage);
        TextView btnSend = sheetView.findViewById(R.id.btnSend);

        addMessage("Hello! Ask me anything about " + topic + "!", false);

        btnSend.setOnClickListener(v -> {
            String message = etAiMessage.getText().toString().trim();
            if (message.isEmpty()) return;
            etAiMessage.setText("");
            addMessage(message, true);
            addMessage("Thinking...", false);
            sendToAi(message);
        });

        dialog.show();
    }

    private void sendToAi(String userMessage) {
        executor.execute(() -> {
            try {
                String prompt = "You are a math tutor for students.\n" +
                        "Rules:\n" +
                        "- Keep answers short, maximum 4-5 sentences\n" +
                        "- Never use markdown formatting like ** or ## or *\n" +
                        "- Write in plain simple text only\n" +
                        "- Use simple clear language\n" +
                        "User question: " + userMessage;

                JSONObject message = new JSONObject();
                message.put("role", "user");
                message.put("content", prompt);

                JSONArray messages = new JSONArray();
                messages.put(message);

                JSONObject body = new JSONObject();
                body.put("model", "openrouter/free");
                body.put("messages", messages);

                RequestBody requestBody = RequestBody.create(
                        body.toString(),
                        MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                        .url("https://openrouter.ai/api/v1/chat/completions")
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .addHeader("Content-Type", "application/json")
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                JSONObject json = new JSONObject(responseBody);

                if (json.has("error")) {
                    String errMsg = json.getJSONObject("error").getString("message");
                    mainHandler.post(() -> {
                        removeLastMessage();
                        addMessage("Error: " + errMsg, false);
                    });
                    return;
                }

                String aiReply = json
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                mainHandler.post(() -> {
                    removeLastMessage();
                    addMessage(aiReply, false);
                    chatScrollView.post(() -> chatScrollView.fullScroll(ScrollView.FOCUS_DOWN));
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    removeLastMessage();
                    addMessage("Error: " + e.getMessage(), false);
                });
            }
        });
    }

    private void addMessage(String text, boolean isUser) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(15f);
        tv.setPadding(32, 20, 32, 20);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = 12;
        tv.setMaxWidth((int) (getResources().getDisplayMetrics().widthPixels * 0.75));

        if (isUser) {
            tv.setBackgroundColor(Color.parseColor("#0466c8"));
            tv.setTextColor(Color.WHITE);
            params.gravity = android.view.Gravity.END;
            params.leftMargin = 100;
        } else {
            tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
            tv.setTextColor(Color.parseColor("#1a1a2e"));
            params.gravity = android.view.Gravity.START;
            params.rightMargin = 100;
        }

        tv.setLayoutParams(params);
        chatContainer.addView(tv);
        chatScrollView.post(() -> chatScrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private void removeLastMessage() {
        int count = chatContainer.getChildCount();
        if (count > 0) chatContainer.removeViewAt(count - 1);
    }

    private void loadTopicFromFirestore() {
        String docId = subject + "_" + grade + "_" + topic;
        FirebaseFirestore.getInstance().collection("topics").document(docId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        explanation = doc.getString("explanation");
                        examples = doc.getString("examples");
                        tvExplanation.setText(explanation);
                        tvExamples.setText(examples);
                    } else {
                        tvExplanation.setText("Topic not found.");
                    }
                })
                .addOnFailureListener(e -> tvExplanation.setText("Failed to load: " + e.getMessage()));
    }
}