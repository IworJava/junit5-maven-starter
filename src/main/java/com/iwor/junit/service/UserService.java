package com.iwor.junit.service;

import com.iwor.junit.dto.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserService {
    private final List<User> users = new ArrayList<>();

    public List<User> getAll() {
        return users;
    }

    public boolean add(User user) {
        return users.add(user);
    }

    public Optional<User> login(String username, String password) {
        return users.stream()
                .filter(user -> Objects.equals(username, user.getUsername()))
                .filter(user -> Objects.equals(password, user.getPassword()))
                .findFirst();
    }
}
