package com.g08.gig.presentation;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.R;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universal_login_page);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginbutton);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
            String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

            emailInput.setError(null);
            passwordInput.setError(null);

            boolean hasError = false;
            if (email.isEmpty()) {
                emailInput.setError(getString(R.string.login_error_email_required));
                hasError = true;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError(getString(R.string.login_error_email_invalid));
                hasError = true;
            }
            if (password.isEmpty()) {
                passwordInput.setError(getString(R.string.login_error_password_required));
                hasError = true;
            }
            if (hasError) {
                if (emailInput.getError() != null) {
                    emailInput.requestFocus();
                } else {
                    passwordInput.requestFocus();
                }
                return;
            }
            // TODO: proceed with login
        });
    }
}
