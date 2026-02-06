package com.g08.gig.presentation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.R;
import com.g08.gig.application.GiGApp;
import com.g08.gig.business.services.ProUserService;

public class StartUp extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universal_home_page);


        Button loginPageButton = findViewById(R.id.loginPageButton);
        Button clientCreationButton = findViewById(R.id.clientCreationButton);
        Button proCreationButton = findViewById(R.id.proCreationButton);

        loginPageButton.setOnClickListener(v ->
                startActivity(new Intent(StartUp.this, Login.class))
        );

        clientCreationButton.setOnClickListener(v -> {
            startActivity(new Intent(StartUp.this, CreateClientUser.class));
        });

        proCreationButton.setOnClickListener(v -> {
            startActivity(new Intent(StartUp.this, CreateProUser.class));
        });

    }

}
