package com.g08.gig.business.validation;

import com.g08.gig.persistence.UserRepository;

public class UserValidator {

    UserRepository repo;

    public UserValidator(UserRepository repo)
    {
        this.repo = repo;
    }
    public void validate(String firstName, String lastName, String email, String phone)
    {
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
        if (repo.getByEmail(email.trim()) != null) {
            throw new ValidationException("Email already exists. Please use a different one.");
        }

        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("Phone number is required.");
        }
        if (phone.trim().length() != 10) {
            throw new ValidationException("Phone number must be exactly 10 digits long.");
        }
    }


    public void validate(String firstName, String lastName, String email, String phone, String password, String confirmPassword) {
        validate(firstName, lastName, email, phone);

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
