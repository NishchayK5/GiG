package com.g08.gig.business.constants;

public final class ValidationMessages {

    private ValidationMessages() {
    }

    public static final String FIRST_NAME_REQUIRED = "First name is required.";
    public static final String FIRST_NAME_MAX_LENGTH = "First name must be 60 characters or less.";
    public static final String FIRST_NAME_LETTERS_ONLY = "First name must contain only letters.";

    public static final String LAST_NAME_REQUIRED = "Last name is required.";
    public static final String LAST_NAME_MAX_LENGTH = "Last name must be 60 characters or less.";
    public static final String LAST_NAME_LETTERS_ONLY = "Last name must contain only letters.";

    public static final String EMAIL_REQUIRED = "Email is required.";
    public static final String EMAIL_MAX_LENGTH = "Email must be 255 characters or less.";
    public static final String EMAIL_INVALID_FORMAT = "Email must be a valid format.";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists. Please use a different one.";
    public static final String EMAIL_VALID_ADDRESS = "Please enter a valid email address.";

    public static final String PHONE_REQUIRED = "Phone number is required.";
    public static final String PHONE_DIGITS_ONLY = "Phone number must contain only digits, with an optional + at the start.";
    public static final String PHONE_DIGIT_RANGE = "Phone number must be 10 to 15 digits.";

    public static final String PASSWORD_REQUIRED = "Password is required.";
    public static final String CONFIRM_PASSWORD_REQUIRED = "Confirm password is required.";
    public static final String PASSWORDS_MUST_MATCH = "Passwords must match.";
    public static final String PASSWORD_MIN_LENGTH = "Password must be at least 8 characters.";

    public static final String REQUEST_TITLE_REQUIRED = "Title is required.";
    public static final String TITLE_MAX_LENGTH = "Title must be 50 characters or less.";
    public static final String REQUEST_BODY_REQUIRED = "Description is required.";
}
