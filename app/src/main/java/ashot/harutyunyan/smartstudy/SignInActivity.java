package ashot.harutyunyan.smartstudy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import android.content.SharedPreferences;

public class SignInActivity extends AppCompatActivity {

    Button btnSignIn, btnGuest;
    TextView tvGoToSignUp;
    TextInputEditText etEmail, etPassword;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_sign_in);

        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn  = findViewById(R.id.btnSignIn);
        btnGuest   = findViewById(R.id.btnGuest);
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp);

        btnSignIn.setOnClickListener(v -> {
            String email    = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable button while signing in to prevent double-tap
            btnSignIn.setEnabled(false);
            btnSignIn.setText("Signing in…");

            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(result -> {
                        if (result.getUser() != null && result.getUser().isEmailVerified()) {
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            auth.signOut();
                            Toast.makeText(this, "Please verify your email first! Check your inbox.", Toast.LENGTH_LONG).show();
                            resetSignInButton();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Wrong email or password!", Toast.LENGTH_LONG).show();
                        resetSignInButton();
                    });
        });

        btnGuest.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        tvGoToSignUp.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void resetSignInButton() {
        btnSignIn.setEnabled(true);
        btnSignIn.setText("Sign In");
    }
}