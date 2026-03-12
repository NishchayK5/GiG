package com.g08.gig.presentation;

import android.app.Activity;
import android.content.Intent;

import com.g08.gig.session.SessionManager;

public final class NavigationHelper {

    private NavigationHelper() {
    }

    public static void logout(Activity activity) {
        SessionManager.clearSession(activity);
        Intent intent = new Intent(activity, StartUp.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void startEditProfile(Activity activity, int userId, boolean isClient) {
        Intent intent = new Intent(activity, EditProfileActivity.class);
        intent.putExtra(SessionManager.KEY_USER_ID, userId);
        intent.putExtra(EditProfileActivity.EXTRA_IS_CLIENT, isClient);
        activity.startActivity(intent);
    }
}
