package com.g08.gig.presentation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.business.services.ClientUserService;
import com.g08.gig.business.services.RequestService;
import com.g08.gig.business.validation.ValidationException;
import com.g08.gig.models.Client;
import com.g08.gig.session.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class CreateRequest extends AppCompatActivity {
    private EditText requestTitleInput;
    private EditText requestDescriptionInput;
    private RequestService requestService;
    private ClientUserService clientUserService;
    private Map<String, EditText> requestFieldToView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_request);

        requestService = GiGApp.get(this).getRequestService();
        clientUserService = GiGApp.get(this).getClientUserService();

        requestTitleInput = findViewById(R.id.requestTitleInput);
        requestDescriptionInput = findViewById(R.id.requestDescriptionInput);
        requestFieldToView = new HashMap<>();
        requestFieldToView.put("title", requestTitleInput);
        requestFieldToView.put("body", requestDescriptionInput);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        Button submitButton = findViewById(R.id.submitRequestButton);
        submitButton.setOnClickListener(v -> submitRequest());
    }

    private void submitRequest() {
        String title = requestTitleInput.getText() != null ? requestTitleInput.getText().toString() : "";
        String body = requestDescriptionInput.getText() != null ? requestDescriptionInput.getText().toString() : "";
        try {
            int clientId = SessionManager.getUserId(this);
            Client client = clientUserService.getClientById(clientId);
            requestService.addRequest(client, title, body);
            setResult(RESULT_OK);
            finish();
        } catch (ValidationException ex) {
            ValidationErrorBinder.setValidationError(requestFieldToView, ex.getField(), ex.getMessage(), null);
        }
    }
}
