package com.g08.gig.business.services;

public final class LoginResult {
    private final boolean success;
    private final int userId;
    private final boolean isClient;
    private final boolean isPro;

    private LoginResult(boolean success, int userId, boolean isClient, boolean isPro) {
        this.success = success;
        this.userId = userId;
        this.isClient = isClient;
        this.isPro = isPro;
    }

    public static LoginResult failure() {
        return new LoginResult(false, -1, false, false);
    }

    public static LoginResult success(int userId, boolean isClient, boolean isPro) {
        return new LoginResult(true, userId, isClient, isPro);
    }

    public boolean isSuccess() {
        return success;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isClient() {
        return isClient;
    }

    public boolean isPro() {
        return isPro;
    }
}
