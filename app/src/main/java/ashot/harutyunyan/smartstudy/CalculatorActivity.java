package ashot.harutyunyan.smartstudy;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity {

    TextView tvExpression, tvResult;
    StringBuilder expression = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        int[] numberIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9};
        String[] numbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        for (int i = 0; i < numberIds.length; i++) {
            final String num = numbers[i];
            findViewById(numberIds[i]).setOnClickListener(v -> append(num));
        }

        findViewById(R.id.btnAdd).setOnClickListener(v -> append("+"));
        findViewById(R.id.btnSubtract).setOnClickListener(v -> append("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> append("*"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> append("/"));
        findViewById(R.id.btnDot).setOnClickListener(v -> appendDot());
        findViewById(R.id.btnBracketOpen).setOnClickListener(v -> append("("));
        findViewById(R.id.btnBracketClose).setOnClickListener(v -> append(")"));
        findViewById(R.id.btnPower).setOnClickListener(v -> append("^"));

        findViewById(R.id.btnSin).setOnClickListener(v -> append("sin("));
        findViewById(R.id.btnCos).setOnClickListener(v -> append("cos("));
        findViewById(R.id.btnTan).setOnClickListener(v -> append("tan("));
        findViewById(R.id.btnLog).setOnClickListener(v -> append("log("));
        findViewById(R.id.btnLn).setOnClickListener(v -> append("ln("));
        findViewById(R.id.btnSqrt).setOnClickListener(v -> append("sqrt("));

        findViewById(R.id.btnPercent).setOnClickListener(v -> {
            try {
                double val = Double.parseDouble(expression.toString());
                expression = new StringBuilder(formatResult(val / 100));
                updateDisplay();
            } catch (Exception e) {
                tvResult.setText("Error");
            }
        });

        findViewById(R.id.btnSign).setOnClickListener(v -> {
                String expr = expression.toString();
                int i = expr.length() - 1;
                while (i >= 0 && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) i--;
                if (i >= 0 && expr.charAt(i) == '-') {
                    expression.deleteCharAt(i);
                } else {
                    expression.insert(i + 1, '-');
                }
                updateDisplay();
            });

        findViewById(R.id.btnBackspace).setOnClickListener(v -> {
            if (expression.length() > 0) {
                String expr = expression.toString();
                // Smart backspace: remove whole function name at end
                String[] funcs = {"sqrt(", "sin(", "cos(", "tan(", "log(", "ln("};
                boolean removed = false;
                for (String func : funcs) {
                    if (expr.endsWith(func)) {
                        expression = new StringBuilder(expr.substring(0, expr.length() - func.length()));
                        removed = true;
                        break;
                    }
                }
                if (!removed) {
                    expression.deleteCharAt(expression.length() - 1);
                }
                updateDisplay();
            }
        });

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            expression.setLength(0);
            tvExpression.setText("");
            tvResult.setText("");
        });

        findViewById(R.id.btnEquals).setOnClickListener(v -> {
            try {
                double result = evaluate(closeOpenBrackets(expression.toString()));
                String resultStr = formatResult(result);

                tvResult.setText(resultStr);
                tvExpression.setText("");

                float distance = tvExpression.getY() - tvResult.getY();

                tvResult.setPivotX(tvResult.getWidth());
                tvResult.setPivotY(0f);
                tvResult.setPivotY(0f);

                tvResult.animate()
                        .translationY(distance)
                        .scaleX(2.4f)
                        .scaleY(2.4f)
                        .setDuration(500)
                        .setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator())
                        .withEndAction(() -> {
                            tvResult.setTranslationY(0f);
                            tvResult.setScaleX(1f);
                            tvResult.setScaleY(1f);
                            tvResult.setText("");
                            tvExpression.setText(resultStr);
                            expression = new StringBuilder(resultStr);
                        })
                        .start();

            } catch (ArithmeticException e) {
                tvResult.setText("Division by zero");
            } catch (Exception e) {
                tvResult.setText("Error");
            }
        });
    }

    private void append(String val) {
        expression.append(val);
        updateDisplay();
    }

    private void appendDot() {
        String expr = expression.toString();
        int i = expr.length() - 1;
        while (i >= 0 && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) i--;
        String currentToken = expr.substring(i + 1);
        if (!currentToken.contains(".")) {
            append(".");
        }
    }

    private void updateDisplay() {
        tvExpression.setText(expression.toString());
        try {
            double result = evaluate(closeOpenBrackets(expression.toString()));
            String expr = expression.toString();
            boolean hasOperator = expr.matches(".*[0-9)][ +\\-*/^][0-9(].*");
            tvResult.setText(hasOperator ? formatResult(result) : "");
        } catch (Exception e) {
            tvResult.setText("");
        }
    }

    private double evaluate(String expr) {
        return new ExpressionEvaluator(expr).parse();
    }

    private String closeOpenBrackets(String expr) {
        int open = 0;
        for (char c : expr.toCharArray()) {
            if (c == '(') open++;
            else if (c == ')') open--;
        }
        StringBuilder sb = new StringBuilder(expr);
        for (int i = 0; i < open; i++) sb.append(')');
        return sb.toString();
    }
    private String formatResult(double result) {
        if (Double.isNaN(result)) return "Error";
        if (Double.isInfinite(result)) return result > 0 ? "∞" : "-∞";
        if (result == Math.floor(result) && Math.abs(result) < 1e15) {
            return String.valueOf((long) result);
        }
        String s = String.format("%.10f", result);
        s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }

    private static class ExpressionEvaluator {
        String expr;
        int pos = -1, ch;

        ExpressionEvaluator(String expr) {
            this.expr = expr
                    .replace("sin(", "S(")
                    .replace("cos(", "C(")
                    .replace("tan(", "T(")
                    .replace("log(", "G(")
                    .replace("ln(", "N(")
                    .replace("sqrt(", "R(")
                    .replace("^", "P");
        }

        void nextChar() {
            ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
        }

        boolean eat(int charToEat) {
            while (ch == ' ') nextChar();
            if (ch == charToEat) { nextChar(); return true; }
            return false;
        }

        double parse() {
            nextChar();
            double x = parseExpression();
            if (pos < expr.length()) throw new RuntimeException("Unexpected: " + (char) ch);
            return x;
        }

        double parseExpression() {
            double x = parseTerm();
            for (;;) {
                if (eat('+')) x += parseTerm();
                else if (eat('-')) x -= parseTerm();
                else return x;
            }
        }

        double parseTerm() {
            double x = parseFactor();
            for (;;) {
                if (eat('*')) x *= parseFactor();
                else if (eat('/')) {
                    double divisor = parseFactor();
                    if (divisor == 0) throw new ArithmeticException("Division by zero");
                    x /= divisor;
                }
                else return x;
            }
        }

        double parseFactor() {
            if (eat('+')) return parseFactor();
            if (eat('-')) return -parseFactor();

            double x;
            int startPos = this.pos;

            if (eat('(')) {
                x = parseExpression();
                eat(')');
            } else if (eat('S')) { eat('('); x = Math.sin(Math.toRadians(parseExpression())); eat(')'); }
            else if (eat('C')) { eat('('); x = Math.cos(Math.toRadians(parseExpression())); eat(')'); }
            else if (eat('T')) { eat('('); x = Math.tan(Math.toRadians(parseExpression())); eat(')'); }
            else if (eat('G')) { eat('('); x = Math.log10(parseExpression()); eat(')'); }
            else if (eat('N')) { eat('('); x = Math.log(parseExpression()); eat(')'); }
            else if (eat('R')) { eat('('); x = Math.sqrt(parseExpression()); eat(')'); }
            else if ((ch >= '0' && ch <= '9') || ch == '.') {
                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                x = Double.parseDouble(expr.substring(startPos, this.pos));
            } else {
                throw new RuntimeException("Unexpected: " + (char) ch);
            }

            if (eat('P')) x = Math.pow(x, parseFactor());

            return x;
        }
    }
}