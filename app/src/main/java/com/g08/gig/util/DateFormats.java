package com.g08.gig.util;

import java.time.format.DateTimeFormatter;

/**
 * Shared date/time format constants for the app.
 */
public final class DateFormats {

    private DateFormats() {
    }

    /** Format used for request time (e.g. "Mar 11, 2025 · 2:30 PM"). */
    public static final DateTimeFormatter REQUEST_TIME =
            DateTimeFormatter.ofPattern("MMM d, yyyy · h:mm a");
}
