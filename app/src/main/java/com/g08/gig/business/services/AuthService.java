/**
 * File: AuthService.java

 * Purpose:
 * Manages the authorization of login by validating email and verify password from the database to give a go-ahead.
 *
 */
package com.g08.gig.business.services;

public interface AuthService {
    LoginResult login(String email, String password);
}
