package com.g08.gig.business.services;

import com.g08.gig.business.validation.UserValidator;
import com.g08.gig.models.Client;
import com.g08.gig.models.User;
import com.g08.gig.persistence.ClientRepository;
import com.g08.gig.persistence.UserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.List;

public class ClientUserServiceImpl implements ClientUserService{

    private static final int BCRYPT_COST = 12;

    private final ClientRepository repo;

    public UserRepository userRepo;
    private final UserValidator validator;

    public ClientUserServiceImpl(ClientRepository repo, UserRepository userRepo) {

        this(repo, userRepo, new UserValidator(userRepo));
    }

    public ClientUserServiceImpl(ClientRepository repo, UserRepository userRepo, UserValidator validator) {
        this.repo = repo;
        this.validator = validator;
        this.userRepo = userRepo;
    }

    @Override
    public Client addClient(String firstName, String lastName, String email, String password, String confirmPassword, String phone, String address) {
        validator.validate(firstName, lastName, email, phone, password, confirmPassword);
        String hashedPassword = BCrypt.withDefaults().hashToString(BCRYPT_COST, password.trim().toCharArray());
        User toCreate = new User(0, safeTrim(email), firstName.trim(), lastName.trim(), phone.trim(), hashedPassword, "CLIENT");
        User userToAdd = userRepo.add(toCreate);
        Client clientToAdd = new Client(userToAdd.getId(), userToAdd.getEmail(), userToAdd.getFirstName(), userToAdd.getLastName(), userToAdd.getPhoneNumber(), address.trim(), userToAdd.getPassword());
        return repo.add(clientToAdd);
    }

    @Override
    public boolean updateClient(int id, String firstName, String lastName, String email, String password, String phone, String address) {
        validator.validate(firstName, lastName, email, phone);
        Client existing = repo.getById(id);
        if (existing == null) return false;

        Client updated = new Client(id, safeTrim(email), firstName.trim(), lastName.trim(), phone.trim(), address.trim(), existing.getPassword());

        return repo.update(updated)&& userRepo.update(updated);
    }

    @Override
    public boolean deleteClient(int id) {
        return repo.delete(id);
    }

    private String safeTrim(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
