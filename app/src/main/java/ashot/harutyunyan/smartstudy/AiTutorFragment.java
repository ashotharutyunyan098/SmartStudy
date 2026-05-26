package ashot.harutyunyan.smartstudy;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class AiTutorFragment extends Fragment {

    LinearLayout chatContainer;
    ScrollView scrollView;
    EditText etMessage;
    TextView btnSend;
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler mainHandler = new Handler(Looper.getMainLooper());

    String apiKey = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_tutor, container, false);

        chatContainer = view.findViewById(R.id.chatContainer);
        scrollView = view.findViewById(R.id.scrollView);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);

        addMessage("Hello! I am your AI Math Tutor. Ask me any math question!", false);

        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (message.isEmpty()) return;
            etMessage.setText("");
            addMessage(message, true);
            addMessage("Thinking...", false);
            sendMessage(message);        });

        return view;
    }

    private void sendMessage(String userMessage) {
        executor.execute(() -> {
            try {
                String prompt = "You are a math tutor for a student learning app. " +
                        "Only answer math related questions. " +
                        "If the question is not about math, politely say you can only help with math. " +
                        "Keep answers clear and educational. " +
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
                    scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    removeLastMessage();
                    addMessage("Error: " + e.getMessage(), false);                });
            }
        });
    }

    private void addMessage(String text, boolean isUser) {
        TextView tv = new TextView(getActivity());
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
            params.gravity = Gravity.END;
            params.leftMargin = 100;
        } else {
            tv.setBackgroundColor(Color.parseColor("#f0f0f0"));
            tv.setTextColor(Color.parseColor("#1a1a2e"));
            params.gravity = Gravity.START;
            params.rightMargin = 100;
        }

        tv.setLayoutParams(params);
        chatContainer.addView(tv);
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private void removeLastMessage() {
        int count = chatContainer.getChildCount();
        if (count > 0) chatContainer.removeViewAt(count - 1);
    }
}