package com.g08.gig.business.services;

import com.g08.gig.models.Client;
import com.g08.gig.models.Pro;
import com.g08.gig.models.User;
import com.g08.gig.persistence.ClientRepository;
import com.g08.gig.persistence.ProRepository;
import com.g08.gig.persistence.UserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ProRepository proRepository;

    public AuthServiceImpl(UserRepository userRepository,
                           ClientRepository clientRepository,
                           ProRepository proRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.proRepository = proRepository;
    }

    @Override
    public LoginResult login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null) {
            return LoginResult.failure();
        }

        //checks if user email exists in system
        User user = userRepository.getByEmail(email.trim());
        if (user == null) {
            return LoginResult.failure();
        }

        //checks if password is stored in system
        String storedHash = user.getPassword();
        if (storedHash == null || storedHash.isEmpty()) {
            return LoginResult.failure();
        }

        //verifies that the entered password is correct, uses BCrypt to check the encrypted password in our database
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash.toCharArray());
        if (!result.verified) {
            return LoginResult.failure();
        }

        int userId = user.getId();
        Client client = clientRepository.getById(userId);
        if (client != null) {
            return LoginResult.success(userId, true, false);
        }
        Pro pro = proRepository.getById(userId);
        if (pro != null) {
            return LoginResult.success(userId, false, true);
        }

        return LoginResult.failure();
    }
}
