package com.g08.gig.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.R;

/**
 * Form to create a new service request (title + description).
 * Returns the new request title and body via result intent.
 */
public class CreateRequest extends AppCompatActivity {

    public static final String EXTRA_REQUEST_TITLE = "extra_request_title";
    public static final String EXTRA_REQUEST_BODY = "extra_request_body";

    private EditText requestTitleInput;
    private EditText requestDescriptionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_request);

        requestTitleInput = findViewById(R.id.requestTitleInput);
        requestDescriptionInput = findViewById(R.id.requestDescriptionInput);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        Button submitButton = findViewById(R.id.submitRequestButton);
        submitButton.setOnClickListener(v -> submitRequest());
    }

    private void submitRequest() {
        String title = requestTitleInput.getText() != null ? requestTitleInput.getText().toString().trim() : "";
        String body = requestDescriptionInput.getText() != null ? requestDescriptionInput.getText().toString().trim() : "";

        if (title.isEmpty()) {
            requestTitleInput.setError(getString(R.string.create_request_error_title_required));
            requestTitleInput.requestFocus();
            return;
        }
        if (body.isEmpty()) {
            requestDescriptionInput.setError(getString(R.string.create_request_error_description_required));
            requestDescriptionInput.requestFocus();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_REQUEST_TITLE, title);
        data.putExtra(EXTRA_REQUEST_BODY, body);
        setResult(RESULT_OK, data);
        finish();
    }
}
