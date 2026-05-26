package ashot.harutyunyan.smartstudy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TestResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        int correct = getIntent().getIntExtra("correct", 0);
        int total = getIntent().getIntExtra("total", 0);
        boolean passed = getIntent().getBooleanExtra("passed", false);
        String subject = getIntent().getStringExtra("subject");
        int grade = getIntent().getIntExtra("grade", 1);

        TextView tvScore = findViewById(R.id.tvScore);
        TextView tvStars = findViewById(R.id.tvStars);
        TextView tvResultMessage = findViewById(R.id.tvResultMessage);
        Button btnTryAgain = findViewById(R.id.btnTryAgain);
        Button btnBackToGrade = findViewById(R.id.btnBackToGrade);

        int percentage = (correct * 100) / total;
        tvScore.setText(correct + "/" + total);

        if (percentage >= 90) {
            tvStars.setText("⭐⭐⭐");
            tvResultMessage.setText("Outstanding! Perfect score!");
            tvScore.setTextColor(Color.parseColor("#2ecc71"));
        } else if (percentage >= 70) {
            tvStars.setText("⭐⭐");
            tvResultMessage.setText("Great job! You passed!");
            tvScore.setTextColor(Color.parseColor("#0466c8"));
        } else if (percentage >= 50) {
            tvStars.setText("⭐");
            tvResultMessage.setText("Good effort! Keep practicing.");
            tvScore.setTextColor(Color.parseColor("#FFA500"));
        } else {
            tvStars.setText("✗");
            tvResultMessage.setText("Keep studying and try again!");
            tvScore.setTextColor(Color.parseColor("#e74c3c"));
        }

        btnTryAgain.setOnClickListener(v -> {
            Intent intent = new Intent(this, TestActivity.class);
            intent.putExtra("subject", subject);
            intent.putExtra("grade", grade);
            startActivity(intent);
            finish();
        });

        btnBackToGrade.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }
}