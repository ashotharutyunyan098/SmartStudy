package ashot.harutyunyan.smartstudy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import android.content.SharedPreferences;
import android.graphics.Color;

public class SelectClassActivity extends AppCompatActivity {

    TextView tvSubjectTitle;
    CardView btn1, btn2, btn3, btn4, btn5, btn6,
            btn7, btn8, btn9, btn10, btn11, btn12;
    TextView[] progressViews;
    ImageView[] lockViews;
    String subject;
    int[] grades;
    SharedPreferences prefs;

    @Override
    protected void onResume() {
        super.onResume();
        updateGradeCards();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_class);

        prefs = getSharedPreferences("progress", MODE_PRIVATE);

        tvSubjectTitle = findViewById(R.id.tvSubjectTitle);
        btn1 = findViewById(R.id.btnGrade1);
        btn2 = findViewById(R.id.btnGrade2);
        btn3 = findViewById(R.id.btnGrade3);
        btn4 = findViewById(R.id.btnGrade4);
        btn5 = findViewById(R.id.btnGrade5);
        btn6 = findViewById(R.id.btnGrade6);
        btn7 = findViewById(R.id.btnGrade7);
        btn8 = findViewById(R.id.btnGrade8);
        btn9 = findViewById(R.id.btnGrade9);
        btn10 = findViewById(R.id.btnGrade10);
        btn11 = findViewById(R.id.btnGrade11);
        btn12 = findViewById(R.id.btnGrade12);

        subject = getIntent().getStringExtra("subject");
        tvSubjectTitle.setText(subject);

        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        if ("Mathematics".equals(subject)) {
            grades = new int[]{1, 2, 3, 4, 5, 6};
            btn7.setVisibility(View.GONE);
            btn8.setVisibility(View.GONE);
            btn9.setVisibility(View.GONE);
            btn10.setVisibility(View.GONE);
            btn11.setVisibility(View.GONE);
            btn12.setVisibility(View.GONE);
            progressViews = new TextView[]{
                    findViewById(R.id.tvProgress1), findViewById(R.id.tvProgress2),
                    findViewById(R.id.tvProgress3), findViewById(R.id.tvProgress4),
                    findViewById(R.id.tvProgress5), findViewById(R.id.tvProgress6)
            };
            lockViews = new ImageView[]{
                    findViewById(R.id.ivLock1), findViewById(R.id.ivLock2),
                    findViewById(R.id.ivLock3), findViewById(R.id.ivLock4),
                    findViewById(R.id.ivLock5), findViewById(R.id.ivLock6)
            };
        } else {
            grades = new int[]{7, 8, 9, 10, 11, 12};
            btn1.setVisibility(View.GONE);
            btn2.setVisibility(View.GONE);
            btn3.setVisibility(View.GONE);
            btn4.setVisibility(View.GONE);
            btn5.setVisibility(View.GONE);
            btn6.setVisibility(View.GONE);
            progressViews = new TextView[]{
                    findViewById(R.id.tvProgress7), findViewById(R.id.tvProgress8),
                    findViewById(R.id.tvProgress9), findViewById(R.id.tvProgress10),
                    findViewById(R.id.tvProgress11), findViewById(R.id.tvProgress12)
            };
            lockViews = new ImageView[]{
                    findViewById(R.id.ivLock7), findViewById(R.id.ivLock8),
                    findViewById(R.id.ivLock9), findViewById(R.id.ivLock10),
                    findViewById(R.id.ivLock11), findViewById(R.id.ivLock12)
            };
        }

        CardView[] buttons = getGradeButtons();
        for (int i = 0; i < buttons.length; i++) {
            final int grade = grades[i];
            final int index = i;
            buttons[i].setOnClickListener(v -> handleGradeClick(grade, index));
        }
    }

    private CardView[] getGradeButtons() {
        if ("Mathematics".equals(subject)) {
            return new CardView[]{btn1, btn2, btn3, btn4, btn5, btn6};
        } else {
            return new CardView[]{btn7, btn8, btn9, btn10, btn11, btn12};
        }
    }

    private void handleGradeClick(int grade, int index) {
        if (index == 0) {
            openTopics(grade);
            return;
        }
        int previousGrade = grades[index - 1];
        int testsPassed = prefs.getInt(subject + "_" + previousGrade + "_tests_passed", 0);
        if (testsPassed >= 3) {
            openTopics(grade);
        } else {
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setTitle("Grade Locked")
                    .setMessage("Grade " + grade + " is locked.\n\nTo unlock it you need to complete Grade "
                            + previousGrade + ".\n\nYou can:\n• Study all topics in Grade " + previousGrade +
                            "\n• Pass the Grade " + previousGrade + " test 3 times with 7/10 or higher")                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private void openTopics(int grade) {
        Intent intent = new Intent(this, TopicsActivity.class);
        intent.putExtra("subject", subject);
        intent.putExtra("grade", grade);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void updateGradeCards() {
        CardView[] buttons = getGradeButtons();

        for (int i = 0; i < grades.length; i++) {
            int testsPassed = prefs.getInt(subject + "_" + grades[i] + "_tests_passed", 0);
            boolean isUnlocked = i == 0 || prefs.getInt(subject + "_" + grades[i - 1] + "_tests_passed", 0) >= 3;

            if (testsPassed >= 3) {
                buttons[i].setCardBackgroundColor(Color.parseColor("#2ecc71"));
                progressViews[i].setText("Done");
                progressViews[i].setVisibility(View.VISIBLE);
                lockViews[i].setVisibility(View.GONE);
            } else if (isUnlocked) {
                buttons[i].setCardBackgroundColor(Color.parseColor("#0466c8"));
                progressViews[i].setText(testsPassed + "/3");
                progressViews[i].setVisibility(View.VISIBLE);
                lockViews[i].setVisibility(View.GONE);
            } else {
                buttons[i].setCardBackgroundColor(Color.parseColor("#979dac"));
                progressViews[i].setVisibility(View.GONE);
                lockViews[i].setVisibility(View.VISIBLE);
            }
        }
    }
}