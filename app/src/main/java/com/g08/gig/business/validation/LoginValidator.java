package com.g08.gig.business.validation;
import com.g08.gig.business.constants.ValidationMessages;
import java.util.regex.Pattern;

public class LoginValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public void validate(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException(ValidationMessages.EMAIL_REQUIRED, "email");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException(ValidationMessages.PASSWORD_REQUIRED, "password");
        }

        email = email.trim();
        password = password.trim();

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(ValidationMessages.EMAIL_VALID_ADDRESS, "email");
        }
    }
}

