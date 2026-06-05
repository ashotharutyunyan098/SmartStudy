package ashot.harutyunyan.smartstudy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import androidx.appcompat.app.AlertDialog;

public class SignUpActivity extends AppCompatActivity {

    Button btnSignUp;
    TextView tvGoToSignIn;
    TextInputEditText etName, etEmail, etPassword;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth       = FirebaseAuth.getInstance();
        etName     = findViewById(R.id.etName);
        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp  = findViewById(R.id.btnSignUp);
        tvGoToSignIn = findViewById(R.id.tvGoToSignIn);

        btnSignUp.setOnClickListener(v -> {
            String name     = etName.getText() != null ? etName.getText().toString().trim() : "";
            String email    = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Password must be at least 6 characters (Firebase minimum)
            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSignUp.setEnabled(false);
            btnSignUp.setText("Creating account…");

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(result -> {
                        if (result.getUser() == null) {
                            Toast.makeText(this, "Unexpected error. Please try again.", Toast.LENGTH_SHORT).show();
                            resetSignUpButton();
                            return;
                        }
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        result.getUser().updateProfile(profileUpdate)
                                .addOnCompleteListener(task ->
                                        result.getUser().sendEmailVerification()
                                                .addOnCompleteListener(verifyTask -> {
                                                    auth.signOut();
                                                    new AlertDialog.Builder(this)
                                                            .setTitle(" Account Created!")
                                                            .setMessage("We sent a verification email to " + email + "\n\nPlease check your inbox before signing in.")
                                                            .setCancelable(false)
                                                            .setPositiveButton("Got it!", (d, w) -> {
                                                                startActivity(new Intent(this, SignInActivity.class));
                                                                finish();
                                                            })
                                                            .show();
                                                })
                                );
                    })
                    .addOnFailureListener(e -> {
                        String msg = e.getMessage() != null ? e.getMessage() : "Sign up failed.";
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        resetSignUpButton();
                    });
        });

        tvGoToSignIn.setOnClickListener(v -> finish());
    }

    private void resetSignUpButton() {
        btnSignUp.setEnabled(true);
        btnSignUp.setText("Sign Up");
    }
}
