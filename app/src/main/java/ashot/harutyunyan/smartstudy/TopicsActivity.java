package ashot.harutyunyan.smartstudy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class TopicsActivity extends AppCompatActivity {

    TextView tvTitle;
    CardView[] topicCards = new CardView[10];
    TextView[] topicTitles = new TextView[10];
    TextView[] topicProgress = new TextView[10];
    CardView cardTest;
    String[] topics;
    String subject;
    int grade;

    @Override
    protected void onResume() {
        super.onResume();
        updateTopicProgress();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);

        tvTitle = findViewById(R.id.tvTitle);
        cardTest = findViewById(R.id.cardTest);

        topicCards[0] = findViewById(R.id.cardTopic1);
        topicCards[1] = findViewById(R.id.cardTopic2);
        topicCards[2] = findViewById(R.id.cardTopic3);
        topicCards[3] = findViewById(R.id.cardTopic4);
        topicCards[4] = findViewById(R.id.cardTopic5);
        topicCards[5] = findViewById(R.id.cardTopic6);
        topicCards[6] = findViewById(R.id.cardTopic7);
        topicCards[7] = findViewById(R.id.cardTopic8);
        topicCards[8] = findViewById(R.id.cardTopic9);
        topicCards[9] = findViewById(R.id.cardTopic10);

        topicTitles[0] = findViewById(R.id.tvTopic1);
        topicTitles[1] = findViewById(R.id.tvTopic2);
        topicTitles[2] = findViewById(R.id.tvTopic3);
        topicTitles[3] = findViewById(R.id.tvTopic4);
        topicTitles[4] = findViewById(R.id.tvTopic5);
        topicTitles[5] = findViewById(R.id.tvTopic6);
        topicTitles[6] = findViewById(R.id.tvTopic7);
        topicTitles[7] = findViewById(R.id.tvTopic8);
        topicTitles[8] = findViewById(R.id.tvTopic9);
        topicTitles[9] = findViewById(R.id.tvTopic10);

        topicProgress[0] = findViewById(R.id.tvTopicProgress1);
        topicProgress[1] = findViewById(R.id.tvTopicProgress2);
        topicProgress[2] = findViewById(R.id.tvTopicProgress3);
        topicProgress[3] = findViewById(R.id.tvTopicProgress4);
        topicProgress[4] = findViewById(R.id.tvTopicProgress5);
        topicProgress[5] = findViewById(R.id.tvTopicProgress6);
        topicProgress[6] = findViewById(R.id.tvTopicProgress7);
        topicProgress[7] = findViewById(R.id.tvTopicProgress8);
        topicProgress[8] = findViewById(R.id.tvTopicProgress9);
        topicProgress[9] = findViewById(R.id.tvTopicProgress10);

        subject = getIntent().getStringExtra("subject");
        grade = getIntent().getIntExtra("grade", 1);

        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        tvTitle.setText("Grade " + grade);
        topics = getTopics(grade);

        for (int i = 0; i < topics.length; i++) {
            topicTitles[i].setText(topics[i]);
        }

        for (int i = 0; i < topicCards.length; i++) {
            final int index = i;
            topicCards[i].setOnClickListener(v -> openLesson(topics[index]));
        }

        cardTest.setOnClickListener(v -> {
            Intent intent = new Intent(this, TestActivity.class);
            intent.putExtra("subject", subject);
            intent.putExtra("grade", grade);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private String[] getTopics(int grade) {
        if (subject.equals("Algebra")) {
            switch (grade) {
                case 7: return new String[]{"Variables and Expressions", "Linear Equations", "Inequalities", "Order of Operations", "Coordinate Geometry", "Expressions and Equations", "Number Properties", "Geometry and Algebra", "Ratios and Proportions", "Statistics and Probability"};
                case 8: return new String[]{"Systems of Equations", "Polynomials", "Factoring", "Functions", "Quadratic Expressions", "Linear Functions", "Geometry with Algebra", "Data Analysis", "Number Systems", "Algebraic Reasoning"};
                case 9: return new String[]{"Quadratic Equations", "Functions and Graphs", "Exponents", "Sequences", "Word Problems", "Polynomial Operations", "Radical Expressions", "Rational Expressions", "Systems of Inequalities", "Modeling with Algebra"};
                case 10: return new String[]{"Logarithms", "Trigonometry Basics", "Matrices", "Probability", "Complex Numbers", "Exponential Functions", "Rational Functions", "Conic Sections", "Sequences and Series", "Advanced Statistics"};
                case 11: return new String[]{"Derivatives", "Integrals", "Limits", "Vectors", "Statistics", "Differential Calculus Applications", "Integral Calculus Applications", "Parametric Equations", "Polar Functions", "Mathematical Modeling"};
                case 12: return new String[]{"Advanced Functions", "Mathematical Induction", "Combinatorics", "Advanced Calculus", "Binomial Theorem", "Advanced Proof Techniques", "Number Theory", "Graph Theory", "Abstract Algebra Basics", "Applied Mathematics"};
            }
        } else if (subject.equals("Geometry")) {
            switch (grade) {
                case 7: return new String[]{"Basic Shapes", "Angles", "Triangles", "Perimeter and Area", "Symmetry", "Coordinate Planes", "Parallel and Perpendicular Lines", "Transformations Intro", "Circles Intro", "Data and Graphs"};
                case 8: return new String[]{"Pythagorean Theorem", "Circles", "Volume", "Surface Area", "Analytic Geometry", "Coordinate Geometry", "Transformations", "Angle Relationships", "Similar Figures", "Statistics and Geometry"};
                case 9: return new String[]{"Congruence", "Similarity", "Trigonometry", "Polygons", "Geometric Proofs", "Coordinate Geometry", "Circles and Angles", "Area and Volume", "Vectors Intro", "Geometric Constructions"};
                case 10: return new String[]{"Geometric Vectors", "Transformations", "Circle Theorems", "3D Shapes", "Locus", "Trigonometric Identities", "Coordinate Geometry Advanced", "Geometric Probability", "Inversion in Geometry", "Analytic Geometry Advanced"};
                case 11: return new String[]{"Conic Sections", "Advanced Trigonometry", "Geometric Series", "Polar Coordinates", "Parametric Equations", "Hyperbolic Functions", "Spherical Geometry", "Projective Geometry", "Differential Equations in Geometry", "Geometric Transformations Advanced"};
                case 12: return new String[]{"Differential Geometry", "Topology Basics", "Non-Euclidean Geometry", "Advanced Vectors", "Geometric Calculus", "Manifolds Intro", "Riemannian Geometry", "Fractal Geometry", "Computational Geometry", "Advanced Geometric Proofs"};
            }
        } else {
            switch (grade) {
                case 1: return new String[]{"Numbers 1-100", "Addition", "Subtraction", "Shapes", "Comparing Numbers", "Missing Numbers", "Number Patterns", "Simple Word Problems", "Time and Clock", "Money and Coins"};
                case 2: return new String[]{"Numbers up to 1000", "Multiplication", "Division", "Even and Odd Numbers", "Simple Fractions", "Place Value", "Skip Counting", "Measurement", "Word Problems", "Number Lines"};
                case 3: return new String[]{"Multiplication Table", "Large Number Addition", "Large Number Subtraction", "Simple Fractions", "Perimeter", "Long Division", "Mixed Numbers", "Time Problems", "Money Problems", "Geometry Basics"};
                case 4: return new String[]{"Large Numbers", "Fractions", "Rounding Numbers", "Area", "Roman Numerals", "Factors and Multiples", "Decimals", "Angles", "Word Problems", "Patterns and Sequences"};
                case 5: return new String[]{"Decimal Fractions", "Percentages", "Proportions", "Negative Numbers", "Prime Numbers", "Algebra Basics", "Geometry", "Statistics", "Word Problems", "Number Theory"};
                case 6: return new String[]{"Negative Numbers", "Average", "Ratios", "Powers and Roots", "Coordinate Plane", "Probability", "Equations", "Geometry", "Statistics", "Financial Math"};
            }
        }
        return new String[]{"Topic 1", "Topic 2", "Topic 3", "Topic 4", "Topic 5", "Topic 6", "Topic 7", "Topic 8", "Topic 9", "Topic 10"};
    }

    private void openLesson(String topic) {
        Intent intent = new Intent(this, LessonActivity.class);
        intent.putExtra("topic", topic);
        intent.putExtra("subject", subject);
        intent.putExtra("grade", grade);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void updateTopicProgress() {
        SharedPreferences prefs = getSharedPreferences("progress", MODE_PRIVATE);
        for (int i = 0; i < topics.length; i++) {
            boolean done = prefs.getBoolean(subject + "_" + grade + "_" + topics[i], false);
            if (done) {
                topicCards[i].setCardBackgroundColor(Color.parseColor("#2ecc71"));
                topicProgress[i].setText("✓");
            } else {
                topicCards[i].setCardBackgroundColor(Color.parseColor("#0466c8"));
                topicProgress[i].setText(String.valueOf(i + 1));
            }
        }
    }
}