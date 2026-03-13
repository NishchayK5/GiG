package com.g08.gig.business.validation;

import android.util.Patterns;

import com.g08.gig.business.constants.ValidationMessages;

public class UserValidator {

    private static final int MAX_FIRST_NAME_LENGTH = 60;
    private static final int MAX_LAST_NAME_LENGTH = 60;
    private static final int MAX_EMAIL_LENGTH = 255;
    private static final int PHONE_MIN_DIGITS = 10;
    private static final int PHONE_MAX_DIGITS = 15;

    private static final int PASSWORD_LENGTH = 8;

    public UserValidator() {
    }

    public void validate(String firstName, String lastName, String email, String phone) {
        validateFirstName(firstName);
        validateLastName(lastName);
        validateEmailFormat(email);
        validatePhone(phone);
    }

    public void validate(String firstName, String lastName, String email, String phone, String password, String confirmPassword) {
        validate(firstName, lastName, email, phone);
        validatePasswordAndConfirmation(password, confirmPassword);
    }

    private void validateFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ValidationException(ValidationMessages.FIRST_NAME_REQUIRED, "fname");
        }
        if (firstName.trim().length() > MAX_FIRST_NAME_LENGTH) {
            throw new ValidationException(ValidationMessages.FIRST_NAME_MAX_LENGTH, "fname");
        }
        if (!firstName.trim().matches("[a-zA-Z \\-']+")) {
            throw new ValidationException(ValidationMessages.FIRST_NAME_LETTERS_ONLY, "fname");
        }
    }

    private void validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException(ValidationMessages.LAST_NAME_REQUIRED, "lname");
        }
        if (lastName.trim().length() > MAX_LAST_NAME_LENGTH) {
            throw new ValidationException(ValidationMessages.LAST_NAME_MAX_LENGTH, "lname");
        }
        if (!lastName.trim().matches("[a-zA-Z \\-']+")) {
            throw new ValidationException(ValidationMessages.LAST_NAME_LETTERS_ONLY, "lname");
        }
    }

    private void validateEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException(ValidationMessages.EMAIL_REQUIRED, "email");
        }
        if (email.trim().length() > MAX_EMAIL_LENGTH) {
            throw new ValidationException(ValidationMessages.EMAIL_MAX_LENGTH, "email");
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            throw new ValidationException(ValidationMessages.EMAIL_VALID_ADDRESS, "email");
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException(ValidationMessages.PHONE_REQUIRED, "phone");
        }
        String trimmed = phone.trim();
        if (!trimmed.matches("\\+?\\d+")) {
            throw new ValidationException(ValidationMessages.PHONE_DIGITS_ONLY, "phone");
        }
        String digitsOnly = trimmed.replaceAll("\\D", "");
        int len = digitsOnly.length();
        if (len < PHONE_MIN_DIGITS || len > PHONE_MAX_DIGITS) {
            throw new ValidationException(ValidationMessages.PHONE_DIGIT_RANGE, "phone");
        }
    }

    private void validatePasswordAndConfirmation(String password, String confirmPassword) {
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException(ValidationMessages.PASSWORD_REQUIRED, "password");
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            throw new ValidationException(ValidationMessages.CONFIRM_PASSWORD_REQUIRED, "confirmPassword");
        }
        if (!password.trim().equals(confirmPassword.trim())) {
            throw new ValidationException(ValidationMessages.PASSWORDS_MUST_MATCH, "confirmPassword");
        }
        if (password.trim().length() < PASSWORD_LENGTH) {
            throw new ValidationException(ValidationMessages.PASSWORD_MIN_LENGTH, "password");
        }
    }
}
