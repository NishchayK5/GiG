package com.g08.gig.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.business.services.AuthService;
import com.g08.gig.business.services.LoginResult;

public class Login extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universal_login_page);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginbutton);

        AuthService authService = ((GiGApp) getApplication()).getAuthService();

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
            String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

            emailInput.setError(null);
            passwordInput.setError(null);

            try {
                LoginResult result = authService.login(email, password);
                if (result.isSuccess()) {
                    Intent intent = result.isClient()
                            ? new Intent(Login.this, ClientHome.class)
                            : new Intent(Login.this, ProHome.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("USER_ID", result.getUserId());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.login_error_invalid_credentials), Toast.LENGTH_SHORT).show();
                }
            }
            catch (com.g08.gig.business.validation.ValidationException ex) {
                EditText errorField = getErrorFieldForMessage(ex.getField());
                if (errorField != null) {
                    errorField.setError(ex.getMessage());
                    errorField.requestFocus();
                }
            }
        });
    }
    private EditText getErrorFieldForMessage(String field) {
        if (field == null) return emailInput;
        if (field.startsWith("email")) return emailInput;
        if (field.startsWith("password")) return passwordInput;
        return emailInput;
    }
}
