package com.iwor.junit.service;

import com.iwor.junit.dto.User;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final List<User> users = new ArrayList<>();

    public List<User> getAll() {
        return users;
    }

    public boolean add(User user) {
        return users.add(user);
    }
}