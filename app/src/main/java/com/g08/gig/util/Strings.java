package com.g08.gig.util;

public final class Strings {

    private Strings() {
    }

    public static String safeTrim(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
