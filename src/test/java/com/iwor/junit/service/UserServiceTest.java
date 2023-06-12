package com.iwor.junit.service;

import com.iwor.junit.dto.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    @Test
    void usersEmptyIfNoUserAdded() {
        var userService = new UserService();
        var users = userService.getAll();
        assertTrue(users.isEmpty(), () -> "List of users should be empty");
    }

    @Test
    void usersSizeIfUserAdded() {
        var userService = new UserService();
        userService.add(new User());
        userService.add(new User());

        var users = userService.getAll();
        assertEquals(2, userService.getAll().size());
    }
}
