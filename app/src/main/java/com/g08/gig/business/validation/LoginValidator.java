package com.g08.gig.business.validation;

import android.util.Log;
import android.util.Patterns;

import com.g08.gig.R;

public class LoginValidator {
    public LoginValidator(){
    }

    public void validate(String email, String password){
        if (email.isEmpty()) {
            throw new ValidationException("Email is required.");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw new ValidationException("Please enter a valid email address.");
        }

        if (password.isEmpty()) {
            throw new ValidationException("Password is required.");
        }
    }


}
