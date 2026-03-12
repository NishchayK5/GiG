package com.g08.gig.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.business.services.ClientUserService;

import java.util.HashMap;
import java.util.Map;

public class CreateClientUser extends AppCompatActivity {
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPassword;
    private EditText addressInput;
    private EditText phoneInput;
    private ClientUserService clientUserService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile_client);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPassword = findViewById(R.id.confirmPassword);
        addressInput = findViewById(R.id.addressInput);
        phoneInput = findViewById(R.id.phoneInput);

        clientUserService = ((GiGApp) getApplication()).getClientUserService();

        Button addUserButton = findViewById(R.id.addCustomerUserButton);

        addUserButton.setOnClickListener(v -> {
            clearAllErrors();
            try {
                clientUserService.addClient(
                        firstNameInput.getText().toString(),
                        lastNameInput.getText().toString(),
                        emailInput.getText().toString(),
                        passwordInput.getText().toString(),
                        confirmPassword.getText().toString(),
                        phoneInput.getText().toString(),
                        addressInput.getText().toString()
                );
                clearInputs();
                Intent intent = new Intent(CreateClientUser.this, Login.class);
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
        addressInput.setError(null);
    }

    private void clearInputs() {
        firstNameInput.setText("");
        lastNameInput.setText("");
        emailInput.setText("");
        phoneInput.setText("");
        addressInput.setText("");
        passwordInput.setText("");
        confirmPassword.setText("");
    }
}
