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

        User user = userRepository.getByEmail(email.trim());
        if (user == null) {
            return LoginResult.failure();
        }

        String storedHash = user.getPassword();
        if (storedHash == null || storedHash.isEmpty()) {
            return LoginResult.failure();
        }

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
