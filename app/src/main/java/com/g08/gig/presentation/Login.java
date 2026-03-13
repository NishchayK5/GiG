package com.g08.gig.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.business.services.AuthService;
import com.g08.gig.business.services.LoginResult;
import com.g08.gig.business.validation.ValidationException;
import com.g08.gig.session.SessionManager;

import java.util.HashMap;
import java.util.Map;

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

        loginButton.setOnClickListener(v -> {
            try {
                LoginResult result = attemptLogin();
                if (result.isSuccess()) {
                    onLoginSuccess(result);
                } else {
                    Toast.makeText(this, getString(R.string.login_error_invalid_credentials), Toast.LENGTH_SHORT).show();
                }
            } catch (ValidationException ex) {
                Map<String, EditText> fieldMap = new HashMap<>();
                fieldMap.put("email", emailInput);
                fieldMap.put("password", passwordInput);
                ValidationErrorBinder.setValidationError(fieldMap, ex.getField(), ex.getMessage(), emailInput);
            }
        });
    }

    private LoginResult attemptLogin() throws ValidationException {
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";
        emailInput.setError(null);
        passwordInput.setError(null);
        AuthService authService = GiGApp.get(this).getAuthService();
        return authService.login(email, password);
    }

    private void onLoginSuccess(LoginResult result) {
        SessionManager.setLoginResult(this, result.getUserId(), result.isClient());
        Intent intent = result.isClient()
                ? new Intent(Login.this, ClientHome.class)
                : new Intent(Login.this, ProHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(SessionManager.KEY_USER_ID, result.getUserId());
        startActivity(intent);
        finish();
    }
}
