package com.iwor.junit.service;

import com.iwor.junit.dto.User;
import com.iwor.junit.paramresolver.UserServiceParamResolver;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("fast")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(UserServiceParamResolver.class)
public class UserServiceTest {
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");

    private final UserService userService;

    public UserServiceTest(UserService userService) {
        this.userService = userService;
    }

    @BeforeAll
    static void init() {
        System.out.println("\nBefore all");
    }

    @BeforeEach
    void prepare() {
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"value1", "value2"})
    void values(String value) {
        System.out.println("Value: " + value);
    }

    @Test
    @Order(1)
    @DisplayName("users are empty if no-one is added")
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
    @Order(2)
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);

        userService.add(IVAN, PETR);

        List<User> users = userService.getAll();

        assertThat(users).hasSize(2);
    }

    @Test
    @Order(-1)
    void usersConvertedToMapById() {
        System.out.println("Test 2: " + this);

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

    @AfterEach
    void deleteDataFromDatabase() {
        userService.getAll().clear();
        // System.out.println("After each: " + this);
    }

    @AfterAll
    static void closeConnectionPool() {
        System.out.println("\nAfter all");
    }

    @Nested
    @Tag("login")
    class LoginTest {

        @ParameterizedTest(name = "{arguments}")
        @MethodSource("com.iwor.junit.service.UserServiceTest#getLoginTestArguments")
        // @CsvFileSource(resources = {"/login-test-data.csv"}, delimiter = ',', numLinesToSkip = 1)
        // @CsvSource(value = {"Ivan,123", "Petr,111"})
        @DisplayName("login param test")
        void login(String username, String password, Optional<User> user) {
            userService.add(IVAN, PETR);

            Optional<User> maybeUser = userService.login(username, password);

            assertThat(maybeUser).isEqualTo(user);
        }

        @Test
        void loginSuccessIfUserExists() {
            System.out.println("Test 2: " + this);

            userService.add(IVAN);

            Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

            assertThat(maybeUser)
                    .isPresent()
                    .contains(IVAN);
        }

        @Test
        void loginFailIfPasswordIsNotCorrect() {
            System.out.println("Test 2: " + this);

            userService.add(IVAN);

            Optional<User> maybeUser = userService.login(IVAN.getUsername(), "dummy");

            assertThat(maybeUser).isNotPresent();
        }

        @Test
        void loginFailIfUserDoesNotExist() {
            System.out.println("Test 2: " + this);

            userService.add(IVAN);

            Optional<User> maybeUser = userService.login("dummy", IVAN.getPassword());

            assertThat(maybeUser).isEmpty();
        }

        @Test
        void throwExceptionIfUsernameOrPasswordIsNull() {
            System.out.println("Test 2: " + this);

            assertAll(
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy")),
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null))
            );
        }
    }

    private static Stream<Arguments> getLoginTestArguments() {
        return Stream.of(
                Arguments.of(IVAN.getUsername(), IVAN.getPassword(), Optional.of(IVAN)),
                Arguments.of(PETR.getUsername(), PETR.getPassword(), Optional.of(PETR)),
                Arguments.of("dummy", IVAN.getPassword(), Optional.empty()),
                Arguments.of(IVAN.getUsername(), "dummy", Optional.empty())
        );
    }
}
