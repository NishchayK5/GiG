package com.g08.gig.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.business.services.ProUserService;

import java.util.HashMap;
import java.util.Map;

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
                        firstNameInput.getText().toString(),
                        lastNameInput.getText().toString(),
                        emailInput.getText().toString(),
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
                Map<String, EditText> fieldMap = new HashMap<>();
                fieldMap.put("fname", firstNameInput);
                fieldMap.put("lname", lastNameInput);
                fieldMap.put("email", emailInput);
                fieldMap.put("phone", phoneInput);
                fieldMap.put("password", passwordInput);
                fieldMap.put("confirmPassword", confirmPassword);
                ValidationErrorBinder.setValidationError(fieldMap, ex.getField(), ex.getMessage(), emailInput);
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
