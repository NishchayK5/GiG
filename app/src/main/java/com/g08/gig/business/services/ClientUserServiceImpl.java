package com.g08.gig.business.services;

import com.g08.gig.business.validation.UserValidator;
import com.g08.gig.models.Client;
import com.g08.gig.persistence.ClientRepository;

import java.util.List;

public class ClientUserServiceImpl implements ClientUserService{

    private final ClientRepository repo;
    private final UserValidator validator;

    public ClientUserServiceImpl(ClientRepository repo) {
        this(repo, new UserValidator());
    }

    public ClientUserServiceImpl(ClientRepository repo, UserValidator validator) {
        this.repo = repo;
        this.validator = validator;
    }

    @Override
    public Client addClient(String firstName, String lastName, String email, String password, String phone, String address) {
        validator.validate(firstName, lastName, email);
        //TODO: Add phone number
        Client toCreate = new Client(0, safeTrim(email), firstName.trim(), lastName.trim(), phone.trim(), address.trim(), password.trim());

        return repo.add(toCreate);
    }

    @Override
    public boolean updateClient(int id, String firstName, String lastName, String email, String password, String phone, String address) {
        validator.validate(firstName, lastName, email);

        Client existing = repo.getById(id);
        if (existing == null) return false;

        Client updated = new Client(id, safeTrim(email), firstName.trim(), lastName.trim(), phone.trim(), address.trim(), existing.getPassword());

        return repo.update(updated);
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
