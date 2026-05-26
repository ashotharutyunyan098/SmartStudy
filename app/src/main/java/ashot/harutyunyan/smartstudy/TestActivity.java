package ashot.harutyunyan.smartstudy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    TextView tvProgress, tvProblemDescription, tvQuestion, tvResult, tvCorrectAnswer;
    EditText etAnswer;
    Button btnCheck, btnNext;
    ProgressBar progressBar;

    String subject;
    int grade;
    int currentIndex = 0;
    int correctAnswers = 0;

    // Each item: [description, question, answer, docQuestionIndex]
    List<String[]> sessionQuestions = new ArrayList<>();

    FirebaseFirestore db;
    String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        subject = getIntent().getStringExtra("subject");
        grade   = getIntent().getIntExtra("grade", 1);
        docId   = subject + "_" + grade;
        db      = FirebaseFirestore.getInstance();

        tvProgress          = findViewById(R.id.tvProgress);
        tvProblemDescription = findViewById(R.id.tvProblemDescription);
        tvQuestion          = findViewById(R.id.tvQuestion);
        tvResult            = findViewById(R.id.tvResult);
        tvCorrectAnswer     = findViewById(R.id.tvCorrectAnswer);
        etAnswer            = findViewById(R.id.etAnswer);
        btnCheck            = findViewById(R.id.btnCheck);
        btnNext             = findViewById(R.id.btnNext);
        progressBar         = findViewById(R.id.progressBar);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadQuestions();

        btnCheck.setOnClickListener(v -> checkAnswer());
        btnNext.setOnClickListener(v -> nextQuestion());
    }

    private void loadQuestions() {
        tvProblemDescription.setText("Loading...");
        tvQuestion.setText("");

        db.collection("tests").document(docId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        tvProblemDescription.setText("No test available for this grade yet.");
                        btnCheck.setVisibility(View.GONE);
                        return;
                    }

                    List<String> descriptions = (List<String>) doc.get("descriptions");
                    List<String> questions    = (List<String>) doc.get("questions");
                    List<String> answers      = (List<String>) doc.get("answers");

                    if (descriptions == null || questions == null || answers == null || questions.isEmpty()) {
                        tvProblemDescription.setText("No questions left in the pool. Upload more!");
                        btnCheck.setVisibility(View.GONE);
                        return;
                    }

                    // Build full list of available questions with their original index
                    List<String[]> allAvailable = new ArrayList<>();
                    for (int i = 0; i < questions.size(); i++) {
                        allAvailable.add(new String[]{
                                descriptions.get(i),   // [0] description
                                questions.get(i),       // [1] question
                                answers.get(i),         // [2] answer
                                String.valueOf(i)       // [3] original index (for deletion)
                        });
                    }

                    // Shuffle and pick up to 10
                    Collections.shuffle(allAvailable);
                    int count = Math.min(10, allAvailable.size());
                    sessionQuestions = allAvailable.subList(0, count);

                    progressBar.setMax(sessionQuestions.size());
                    showQuestion();
                })
                .addOnFailureListener(e ->
                        tvProblemDescription.setText("Failed to load: " + e.getMessage()));
    }

    private void showQuestion() {
        String[] q = sessionQuestions.get(currentIndex);

        tvProgress.setText((currentIndex + 1) + "/" + sessionQuestions.size());
        progressBar.setProgress(currentIndex + 1);
        tvProblemDescription.setText(q[0]);
        tvQuestion.setText(q[1]);

        etAnswer.setText("");
        etAnswer.setBackgroundResource(R.drawable.bg_answer_field);
        tvResult.setVisibility(View.GONE);
        tvCorrectAnswer.setVisibility(View.GONE);
        btnCheck.setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.GONE);

        if (currentIndex == sessionQuestions.size() - 1) {
            btnNext.setText("See Results");
        } else {
            btnNext.setText("Next →");
        }
    }

    private void checkAnswer() {
        String[] q = sessionQuestions.get(currentIndex);
        String userAnswer    = normalize(etAnswer.getText().toString());
        String correctAnswer = normalize(q[2]);

        boolean isCorrect = userAnswer.equals(correctAnswer);

        if (isCorrect) {
            etAnswer.setBackgroundColor(Color.parseColor("#2ecc71"));
            tvResult.setText("Correct! ✓");
            tvResult.setTextColor(Color.parseColor("#2ecc71"));
            correctAnswers++;
            // Delete this question from Firestore pool so it won't appear again
            deleteQuestionFromPool(Integer.parseInt(q[3]));
        } else {
            etAnswer.setBackgroundColor(Color.parseColor("#e74c3c"));
            tvResult.setText("Wrong ✗");
            tvResult.setTextColor(Color.parseColor("#e74c3c"));
            tvCorrectAnswer.setText("Correct answer: " + q[2]);
            tvCorrectAnswer.setVisibility(View.VISIBLE);
        }

        tvResult.setVisibility(View.VISIBLE);
        btnCheck.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
    }

    private void deleteQuestionFromPool(int originalIndex) {
        db.collection("tests").document(docId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    List<String> descriptions = new ArrayList<>((List<String>) doc.get("descriptions"));
                    List<String> questions    = new ArrayList<>((List<String>) doc.get("questions"));
                    List<String> answers      = new ArrayList<>((List<String>) doc.get("answers"));

                    String targetQuestion = sessionQuestions.get(currentIndex)[1];
                    int idx = questions.indexOf(targetQuestion);

                    if (idx != -1) {
                        descriptions.remove(idx);
                        questions.remove(idx);
                        answers.remove(idx);

                        db.collection("tests").document(docId).update(
                                "descriptions", descriptions,
                                "questions",    questions,
                                "answers",      answers
                        );
                    }
                });
    }

    private void nextQuestion() {
        currentIndex++;
        if (currentIndex < sessionQuestions.size()) {
            showQuestion();
        } else {
            showResults();
        }
    }

    private void showResults() {
        int total      = sessionQuestions.size();
        int percentage = (correctAnswers * 100) / total;
        boolean passed = percentage >= 70;

        if (passed) {
            SharedPreferences prefs = getSharedPreferences("progress", MODE_PRIVATE);
            int testsPassed = prefs.getInt(subject + "_" + grade + "_tests_passed", 0);
            if (testsPassed < 3) {
                prefs.edit()
                        .putInt(subject + "_" + grade + "_tests_passed", testsPassed + 1)
                        .apply();
            }
        }

        Intent intent = new Intent(this, TestResultActivity.class);
        intent.putExtra("correct", correctAnswers);
        intent.putExtra("total",   total);
        intent.putExtra("passed",  passed);
        intent.putExtra("subject", subject);
        intent.putExtra("grade",   grade);
        startActivity(intent);
        finish();
    }

    private String normalize(String s) {
        return s.toLowerCase().trim()
                .replace("-", "")
                .replace(" ", "")
                .replace(",", "")
                .replace(".", "");
    }
}