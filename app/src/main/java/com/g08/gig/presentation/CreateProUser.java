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
            try {
                String[] qualifications = qualificationsInput.getText().toString().split(",");

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
                startActivity(new Intent(CreateProUser.this, StartUp.class));


            } catch (com.g08.gig.business.validation.ValidationException ex) {
                emailInput.setError(ex.getMessage());
                emailInput.requestFocus();
            }
        });
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
