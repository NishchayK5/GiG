package com.g08.gig.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.business.services.ProUserService;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.User;

public class EditProfileActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "USER_ID";
    public static final String EXTRA_IS_CLIENT = "IS_CLIENT";

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText phoneInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        int userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        boolean isClient = getIntent().getBooleanExtra(EXTRA_IS_CLIENT, true);

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);

        User user = null;
        if (isClient) {
            ClientUserService clientUserService = ((GiGApp) getApplication()).getClientUserService();
            Client client = clientUserService.getClientById(userId);
            user = client;
        } else {
            ProUserService proUserService = ((GiGApp) getApplication()).getProUserService();
            Pro pro = proUserService.getProById(userId);
            user = pro;
        }

        if (user != null) {
            firstNameInput.setText(user.getFirstName());
            lastNameInput.setText(user.getLastName());
            emailInput.setText(user.getEmail());
            if (user.getPhoneNumber() != null) {
                phoneInput.setText(user.getPhoneNumber());
            }
        }

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }
}
