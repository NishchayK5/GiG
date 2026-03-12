package com.g08.gig.business.services;

import com.g08.gig.business.constants.ValidationMessages;
import com.g08.gig.business.validation.UserValidator;
import com.g08.gig.business.validation.ValidationException;
import com.g08.gig.models.Pro;
import com.g08.gig.models.User;
import com.g08.gig.persistence.ProRepository;
import com.g08.gig.persistence.UserRepository;
import com.g08.gig.util.Strings;

import at.favre.lib.crypto.bcrypt.BCrypt;


public class ProUserServiceImpl implements ProUserService {

    private static final int BCRYPT_COST = 12;
    private final UserRepository userRepo;
    private final ProRepository repo;
    private final UserValidator validator;

    public ProUserServiceImpl(ProRepository repo, UserRepository userRepo) {
        this(repo, userRepo, new UserValidator());
    }

    public ProUserServiceImpl(ProRepository repo, UserRepository userRepo, UserValidator validator) {
        this.repo = repo;
        this.validator = validator;
        this.userRepo = userRepo;
    }

    @Override
    public Pro addPro(String firstName, String lastName, String email, String phone, String password, String confirmPassword, String[] skillSet, String cityOfOperation) {
        String trimmedEmail = Strings.safeTrim(email);
        if (trimmedEmail != null && userRepo.getByEmail(trimmedEmail.toLowerCase()) != null) {
            throw new ValidationException(ValidationMessages.EMAIL_ALREADY_EXISTS, "email");
        }
        validator.validate(firstName, lastName, email, phone, password, confirmPassword);

        String hashedPassword = BCrypt.withDefaults().hashToString(BCRYPT_COST, password.trim().toCharArray());
        User toCreate = new User(0, Strings.safeTrim(email).toLowerCase(), firstName.trim(), lastName.trim(), phone.trim(), hashedPassword, "PRO");
        User userToAdd = userRepo.add(toCreate);
        Pro proToAdd = new Pro(userToAdd.getId(), userToAdd.getEmail(), userToAdd.getFirstName(), userToAdd.getLastName(), userToAdd.getPhoneNumber(), userToAdd.getPassword(), skillSet, cityOfOperation.trim());
        return repo.add(proToAdd);
    }

    @Override
    public boolean updatePro(int id, String email, String firstName, String lastName, String phone, String[] skillSet, String cityOfOperation) {
        validator.validate(firstName, lastName, email, phone);

        Pro existing = repo.getById(id);
        if (existing == null) return false;

        Pro updated = new Pro(id, Strings.safeTrim(email).toLowerCase(), firstName.trim(), lastName.trim(), phone.trim(), existing.getPassword(), skillSet, cityOfOperation.trim());

        return repo.update(updated);
    }

    @Override
    public boolean deletePro(int id) {
        return repo.delete(id);
    }

    @Override
    public Pro getProById(int id) {
        return repo.getById(id);
    }
}
