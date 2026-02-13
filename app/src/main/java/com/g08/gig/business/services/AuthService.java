package com.g08.gig.business.services;

public interface AuthService {
    LoginResult login(String email, String password);
}
