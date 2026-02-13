package com.g08.gig.business.validation;

/**
 * Validates user input (format, length, required fields).
 * Does not check persistence (e.g. email uniqueness); that belongs in the service layer.
 */
public class UserValidator {

    private static final int MAX_FIRST_NAME_LENGTH = 60;
    private static final int MAX_LAST_NAME_LENGTH = 60;
    private static final int MAX_EMAIL_LENGTH = 255;
    private static final int PHONE_LENGTH = 10;

    public UserValidator() {
    }

    public void validate(String firstName, String lastName, String email, String phone) {
        validateFirstName(firstName);
        validateLastName(lastName);
        validateEmailFormat(email);
        validatePhone(phone);
    }

    /**
     * Used for new user registration; validates profile fields plus password confirmation.
     */
    public void validate(String firstName, String lastName, String email, String phone, String password, String confirmPassword) {
        validate(firstName, lastName, email, phone);
        validatePasswordAndConfirmation(password, confirmPassword);
    }

    private void validateFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ValidationException("First name is required.");
        }
        if (firstName.trim().length() > MAX_FIRST_NAME_LENGTH) {
            throw new ValidationException("First name must be 60 characters or less.");
        }
    }

    private void validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("Last name is required.");
        }
        if (lastName.trim().length() > MAX_LAST_NAME_LENGTH) {
            throw new ValidationException("Last name must be 60 characters or less.");
        }
    }

    private void validateEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required.");
        }
        if (email.trim().length() > MAX_EMAIL_LENGTH) {
            throw new ValidationException("Email must be 255 characters or less.");
        }
        if (!email.trim().contains("@")) {
            throw new ValidationException("Email must be a valid format.");
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Phone number is required.");
        }
        if (phone.trim().length() != PHONE_LENGTH) {
            throw new ValidationException("Phone number must be exactly 10 digits long.");
        }
    }

    private void validatePasswordAndConfirmation(String password, String confirmPassword) {
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password is required.");
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            throw new ValidationException("Confirm password is required.");
        }
        if (!password.trim().equals(confirmPassword.trim())) {
            throw new ValidationException("Passwords must match.");
        }
    }
}
