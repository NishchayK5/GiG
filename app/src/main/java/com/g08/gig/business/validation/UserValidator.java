package com.g08.gig.business.validation;

public class UserValidator {

    public void validate(String firstName, String lastName, String email) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ValidationException("First name is required.");
        }
        if (firstName.trim().length() > 60) {
            throw new ValidationException("First name must be 60 characters or less.");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("Last name is required.");
        }
        if (lastName.trim().length() > 60) {
            throw new ValidationException("Last name must be 60 characters or less.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required.");
        }
        if (email.trim().length() > 255) {
            throw new ValidationException("Email must be 255 characters or less.");
        }
        // Basic email format check
        if (!email.trim().contains("@")) {
            throw new ValidationException("Email must be a valid format.");
        }
    }
}
