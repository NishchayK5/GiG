package com.g08.gig.presentation;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.business.services.ClientUserService;

public class CreateClientUser extends AppCompatActivity {
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText addressInput;
    private EditText phoneInput;
    private ClientUserService clientUserService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile_client);

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        addressInput = findViewById(R.id.addressInput);
        phoneInput = findViewById(R.id.phoneInput);

        clientUserService = ((GiGApp) getApplication()).getClientUserService();

        Button addUserButton = findViewById(R.id.addCustomerUserButton);

        addUserButton.setOnClickListener(v -> {
            try {
                clientUserService.addClient(
                        firstNameInput.getText().toString(),
                        lastNameInput.getText().toString(),
                        emailInput.getText().toString(),
                        passwordInput.getText().toString(),
                        phoneInput.getText().toString(),
                        addressInput.getText().toString()
                );

            } catch (com.g08.gig.business.validation.ValidationException ex) {
                emailInput.setError(ex.getMessage());
                emailInput.requestFocus();
            }
        });
    }
}
