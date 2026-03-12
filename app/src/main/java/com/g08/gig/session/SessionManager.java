package com.g08.gig.session;

import android.content.Context;
import android.content.SharedPreferences;

public final class SessionManager {

    public static final String PREFS_NAME = "MyAppPrefs";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_IS_LOGGED_IN = "IS_LOGGED_IN";
    public static final String KEY_USER_TYPE = "USER_TYPE";

    public static final String USER_TYPE_CLIENT = "CLIENT";
    public static final String USER_TYPE_PRO = "PRO";

    private SessionManager() {
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static int getUserId(Context context) {
        return getPrefs(context).getInt(KEY_USER_ID, -1);
    }

    public static void setUserId(Context context, int userId) {
        getPrefs(context).edit().putInt(KEY_USER_ID, userId).apply();
    }

    public static void setLoginResult(Context context, int userId, boolean isClient) {
        getPrefs(context).edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_USER_TYPE, isClient ? USER_TYPE_CLIENT : USER_TYPE_PRO)
                .apply();
    }

    public static void clearSession(Context context) {
        getPrefs(context).edit().clear().apply();
    }

    public static boolean isLoggedIn(Context context) {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public static String getUserType(Context context) {
        return getPrefs(context).getString(KEY_USER_TYPE, "");
    }
}
