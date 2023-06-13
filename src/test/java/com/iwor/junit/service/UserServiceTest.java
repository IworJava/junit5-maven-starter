package com.iwor.junit.service;

import com.iwor.junit.dto.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserServiceTest {
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");

    private UserService userService;

    @BeforeAll
    static void init() {
        System.out.println("\nBefore all");
    }

    @BeforeEach
    void prepare() {
        System.out.println("\nBefore each: " + this);
        userService = new UserService();
    }

    @Test
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test 1: " + this);

        List<User> users = userService.getAll();

        // Jupiter
        assertTrue(users.isEmpty(), () -> "List of users should be empty");
        // AssertJ
        assertThat(users).isEmpty();
        // Hamcrest
        MatcherAssert.assertThat(users, IsEmptyCollection.empty());
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);
        userService.add(IVAN, PETR);

        List<User> users = userService.getAll();

        assertThat(users).hasSize(2);
    }

    @Test
    void loginSuccessIfUserExists() {
        userService.add(IVAN);

        Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

        assertThat(maybeUser)
                .isPresent()
                .contains(IVAN);
    }

    @Test
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);
        Map<Integer, User> users = userService.getAllConvertedById();

        // AssertJ
        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(users).containsValues(IVAN, PETR)
        );

        // Hamcrest
        assertAll(
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasKey(IVAN.getId())),
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasKey(PETR.getId())),
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasValue(IVAN)),
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasValue(PETR))
        );
    }

    @Test
    void loginFailIfPasswordIsNotCorrect() {
        userService.add(IVAN);

        Optional<User> maybeUser = userService.login(IVAN.getUsername(), "dummy");

        assertThat(maybeUser).isNotPresent();
    }

    @Test
    void loginFailIfUserDoesNotExist() {
        userService.add(IVAN);

        Optional<User> maybeUser = userService.login("dummy", IVAN.getPassword());

        assertThat(maybeUser).isEmpty();
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each: " + this);
    }

    @AfterAll
    static void closeConnectionPool() {
        System.out.println("\nAfter all");
    }
}
