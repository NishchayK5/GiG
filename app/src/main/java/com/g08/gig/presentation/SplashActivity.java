package com.g08.gig.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.g08.gig.application.GiGApp;
import com.g08.gig.R;
import com.g08.gig.session.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 2000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (SessionManager.isLoggedIn(this)) {
                int userId = SessionManager.getUserId(this);
                String userType = SessionManager.getUserType(this);

                if (SessionManager.USER_TYPE_CLIENT.equals(userType)) {
                    intent = new Intent(SplashActivity.this, ClientHome.class);
                    intent.putExtra(SessionManager.KEY_USER_ID, userId);
                } else {
                    if (GiGApp.get(SplashActivity.this).getProUserService().getProById(userId) == null) {
                        SessionManager.clearSession(SplashActivity.this);
                        intent = new Intent(SplashActivity.this, StartUp.class);
                    } else {
                        intent = new Intent(SplashActivity.this, ProHome.class);
                        intent.putExtra(SessionManager.KEY_USER_ID, userId);
                    }
                }
            } else {
                intent = new Intent(SplashActivity.this, StartUp.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY_MS);
    }
}
