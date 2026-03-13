package com.g08.gig.business.services;

import com.g08.gig.business.validation.LoginValidator;
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
    private final LoginValidator loginValidator;

    public AuthServiceImpl(UserRepository userRepository,
                           ClientRepository clientRepository,
                           ProRepository proRepository) {
        this(userRepository, clientRepository, proRepository, new LoginValidator());
    }
    public AuthServiceImpl(UserRepository userRepository,
                           ClientRepository clientRepository,
                           ProRepository proRepository,
                           LoginValidator loginValidator) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.proRepository = proRepository;
        this.loginValidator = loginValidator;
    }

    @Override
    public LoginResult login(String email, String password) {
        loginValidator.validate(email, password);

        User user = userRepository.getByEmail(email.trim().toLowerCase());
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
