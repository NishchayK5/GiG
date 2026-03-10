package com.g08.gig.presentation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.R;

/**
 * Splash screen shown when the app is opened. Displays the logo then navigates
 * to the appropriate home page if already logged in, or to the startup page otherwise.
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 2000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);

            Intent intent;
            if (isLoggedIn) {
                int userId = prefs.getInt("USER_ID", -1);
                String userType = prefs.getString("USER_TYPE", "");

                if ("CLIENT".equals(userType)) {
                    intent = new Intent(SplashActivity.this, ClientHome.class);
                } else {
                    intent = new Intent(SplashActivity.this, ProHome.class);
                }
                intent.putExtra("USER_ID", userId);
            } else {
                intent = new Intent(SplashActivity.this, StartUp.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY_MS);
    }
}
