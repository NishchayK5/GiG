package com.g08.gig.business.services;

import com.g08.gig.business.validation.UserValidator;
import com.g08.gig.models.User;
import com.g08.gig.persistence.UserRepository;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final UserValidator validator;

    public UserServiceImpl(UserRepository repo) {
        this(repo, new UserValidator());
    }

    public UserServiceImpl(UserRepository repo, UserValidator validator) {
        this.repo = repo;
        this.validator = validator;
    }

    @Override
    public List<User> getAllUsers() {
        return repo.getAll();
    }

    @Override
    public User addUser(String firstName, String lastName, String email) {
        validator.validate(firstName, lastName, email);

        User toCreate = new User(0, safeTrim(email), firstName.trim(), lastName.trim(), null);

        return repo.add(toCreate);
    }

    @Override
    public boolean updateUser(int id, String firstName, String lastName, String email) {
        validator.validate(firstName, lastName, email);

        User existing = repo.getById(id);
        if (existing == null) return false;

        User updated = new User(id, safeTrim(email), firstName.trim(), lastName.trim(), existing.getPassword());

        return repo.update(updated);
    }

    @Override
    public boolean deleteUser(int id) {
        return repo.delete(id);
    }

    private String safeTrim(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
