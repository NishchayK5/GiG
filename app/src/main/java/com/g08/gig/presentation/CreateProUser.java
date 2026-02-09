package com.g08.gig.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.business.services.ProUserService;

public class CreateProUser extends AppCompatActivity {
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPassword;
    private EditText phoneInput;
    private EditText cityInput;
    private EditText qualificationsInput;

    private ProUserService proUserService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile_pro);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        proUserService = ((GiGApp) getApplication()).getProUserService();

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);

        //TODO: Add confirm password
        passwordInput = findViewById(R.id.passwordInput);
        confirmPassword = findViewById(R.id.confirmPassword);
        phoneInput = findViewById(R.id.phoneInput);
        cityInput = findViewById(R.id.cityInput);
        qualificationsInput = findViewById(R.id.qualificationsInput);

        Button addUserButton = findViewById(R.id.addProUserButton);

        addUserButton.setOnClickListener(v -> {
            clearAllErrors();
            try {
                String qualificationsText = qualificationsInput.getText().toString();
                String[] qualifications = qualificationsText.isEmpty() ? new String[0] : qualificationsText.split(",");

                proUserService.addPro(
                        emailInput.getText().toString(),
                        firstNameInput.getText().toString(),
                        lastNameInput.getText().toString(),
                        phoneInput.getText().toString(),
                        passwordInput.getText().toString(),
                        confirmPassword.getText().toString(),
                        qualifications,
                        cityInput.getText().toString()
                );
                clearInputs();
                Intent intent = new Intent(CreateProUser.this, Login.class);
                startActivity(intent);
                finish();
            } catch (com.g08.gig.business.validation.ValidationException ex) {
                EditText errorField = getErrorFieldForMessage(ex.getMessage());
                if (errorField != null) {
                    errorField.setError(ex.getMessage());
                    errorField.requestFocus();
                }
            }
        });
    }

    private void clearAllErrors() {
        firstNameInput.setError(null);
        lastNameInput.setError(null);
        emailInput.setError(null);
        passwordInput.setError(null);
        confirmPassword.setError(null);
        phoneInput.setError(null);
        cityInput.setError(null);
        qualificationsInput.setError(null);
    }

    private EditText getErrorFieldForMessage(String message) {
        if (message == null) return emailInput;
        if (message.startsWith("First name")) return firstNameInput;
        if (message.startsWith("Last name")) return lastNameInput;
        if (message.startsWith("Email")) return emailInput;
        if (message.startsWith("Phone number")) return phoneInput;
        if (message.equals("Password is required.")) return passwordInput;
        if (message.equals("Confirm password is required.") || message.equals("Passwords must match.")) return confirmPassword;
        return emailInput;
    }

    private void clearInputs() {
        firstNameInput.setText("");
        lastNameInput.setText("");
        emailInput.setText("");
        phoneInput.setText("");
        cityInput.setText("");
        qualificationsInput.setText("");
        passwordInput.setText("");
        confirmPassword.setText("");
    }
}
