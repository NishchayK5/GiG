package com.g08.gig.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.business.profile.ProfileLoader;
import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.business.services.ProUserService;
import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.User;
import com.g08.gig.session.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    public static final String EXTRA_IS_CLIENT = "IS_CLIENT";

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText phoneInput;
    private EditText addressInput;
    private EditText cityInput;
    private ClientUserService clientUserService;
    private ProUserService proUserService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        int userId = getIntent().getIntExtra(SessionManager.KEY_USER_ID, -1);
        boolean isClient = getIntent().getBooleanExtra(EXTRA_IS_CLIENT, true);
        clientUserService = ((GiGApp) getApplication()).getClientUserService();
        proUserService = ((GiGApp) getApplication()).getProUserService();

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        cityInput = findViewById(R.id.cityInput);
        addressInput = findViewById(R.id.addressInput);

        if(isClient) {
            cityInput.setVisibility(View.GONE);
        } else {
            addressInput.setVisibility(View.GONE);
        }

        ProfileLoader loader = ((GiGApp) getApplication()).getProfileLoader(isClient);
        User user = loader.getUserForEdit(userId);

        if (user != null) {
            firstNameInput.setText(user.getFirstName());
            lastNameInput.setText(user.getLastName());
            emailInput.setText(user.getEmail());
            if (user.getPhoneNumber() != null) {
                phoneInput.setText(user.getPhoneNumber());
            }

            if(user instanceof Pro pro) {
                cityInput.setText(pro.getCityOfOperation());
            } else if (user instanceof Client client) {
                addressInput.setText(client.getAddress());
            }


        }

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            try {
                if (user instanceof Pro pro) {
                    proUserService.updatePro(userId, emailInput.getText().toString(), firstNameInput.getText().toString(), lastNameInput.getText().toString(), phoneInput.getText().toString(), pro.getSkillSet(), cityInput.getText().toString());
                } else if (user instanceof Client client) {
                    clientUserService.updateClient(userId, firstNameInput.getText().toString(), lastNameInput.getText().toString(), emailInput.getText().toString(), phoneInput.getText().toString(), addressInput.getText().toString());
                }
                finish();
            } catch (com.g08.gig.business.validation.ValidationException ex) {
                Map<String, EditText> fieldMap = new HashMap<>();
                fieldMap.put("fname", firstNameInput);
                fieldMap.put("lname", lastNameInput);
                fieldMap.put("email", emailInput);
                fieldMap.put("phone", phoneInput);
                ValidationErrorBinder.setValidationError(fieldMap, ex.getField(), ex.getMessage(), emailInput);
            }
        });



        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }
}
