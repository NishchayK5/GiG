package com.g08.gig.persistence.fake;

import com.g08.gig.persistence.UserRepository;
import com.g08.gig.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FakeUserRepository implements UserRepository {

    private final List<User> users;
    private int nextId;
    public FakeUserRepository(){
        users = new ArrayList<>();
        nextId = 1;
    }

    @Override
    public List<User> getAll() {
        return Collections.unmodifiableList(users);
    }

    @Override
    public User getById(int id) {
        for (User user : users) {
            if (user.getId() == id) return user;
        }
        return null;
    }

    @Override
    public User add(User user) {
        if (user == null) return null;

        int id = user.getId();
        if (id == 0) {
            id = nextId++;
        } else if (getById(id) != null) {
            return null;
        }

        User stored = new User(id, user.getEmail(), user.getFirstName(), user.getLastName(), user.getPassword(), null);
        users.add(stored);
        return stored;
    }

    @Override
    public boolean update(User user) {
        if (user == null) return false;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                User updated = new User(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getPassword(), user.getRole());
                users.set(i, updated);
                return true;
            }
        }
        return false;
    }


    @Override
    public User getByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) return user;
        }
        return null;
    }

    @Override
    public boolean delete(int id) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == id) {
                users.remove(i);
                return true;
            }
        }
        return false;
    }
}