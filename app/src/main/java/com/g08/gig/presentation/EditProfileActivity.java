package com.g08.gig.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.business.profile.ProfileLoader;
import com.g08.gig.models.User;
import com.g08.gig.session.SessionManager;

public class EditProfileActivity extends AppCompatActivity {

    public static final String EXTRA_IS_CLIENT = "IS_CLIENT";

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText phoneInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        int userId = getIntent().getIntExtra(SessionManager.KEY_USER_ID, -1);
        boolean isClient = getIntent().getBooleanExtra(EXTRA_IS_CLIENT, true);

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);

        ProfileLoader loader = ((GiGApp) getApplication()).getProfileLoader(isClient);
        User user = loader.getUserForEdit(userId);

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
