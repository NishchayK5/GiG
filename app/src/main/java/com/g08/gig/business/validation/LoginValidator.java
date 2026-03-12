package com.g08.gig.business.validation;

import android.util.Patterns;

import com.g08.gig.business.constants.ValidationMessages;

public class LoginValidator {
    public LoginValidator(){
    }

    public void validate(String email, String password){
        if (email == null || email.isEmpty()) {
            throw new ValidationException(ValidationMessages.EMAIL_REQUIRED, "email");
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw new ValidationException(ValidationMessages.EMAIL_VALID_ADDRESS, "email");
        }
        if (password == null || password.isEmpty()) {
            throw new ValidationException(ValidationMessages.PASSWORD_REQUIRED, "password");
        }
    }
}
