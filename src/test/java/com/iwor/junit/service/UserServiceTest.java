package com.iwor.junit.service;

import com.iwor.junit.TestBase;
import com.iwor.junit.dao.UserDao;
import com.iwor.junit.dto.User;
import com.iwor.junit.extension.A;
import com.iwor.junit.extension.ConditionalExtension;
import com.iwor.junit.extension.PostProcessingExtension;
import com.iwor.junit.extension.UserServiceParamResolver;
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
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("fast")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({
        UserServiceParamResolver.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
        MockitoExtension.class
        // ThrowableExtension.class

})
@Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
public class UserServiceTest extends TestBase {
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");

    // @Mock
    @Mock(strictness = Mock.Strictness.LENIENT)
    private UserDao userDao;
    @InjectMocks
    private UserService userService;
    @Captor
    private ArgumentCaptor<Integer> captor;

    public UserServiceTest(TestInfo testInfo) {
    }

    @A
    @BeforeAll
    static void init() {
        System.out.println("\nBefore all");
    }

    @BeforeEach
    void prepare() {
        System.out.println();
        // Mockito.doReturn(true).when(userDao).deleteById(IVAN.getId());
        // BDDMockito.given(userDao.deleteById(IVAN.getId())).willReturn(true);
        BDDMockito.willReturn(true).given(userDao).deleteById(IVAN.getId());
    }

    @Test
    void shouldDeleteExistedUser() {
        boolean deleted = userService.delete(IVAN.getId());

        // Mockito.verify(userDao).deleteById(IVAN.getId());
        // Mockito.verify(userDao).deleteById(captor.capture());
        BDDMockito.then(userDao).should().deleteById(captor.capture());

        assertThat(deleted).isTrue();
        assertThat(captor.getValue()).isEqualTo(IVAN.getId());
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

        if (true) {
            // throw new RuntimeException();
        }

        userService.add(IVAN, PETR);

        List<User> users = userService.getAll();

        assertThat(users).hasSize(2);
    }

    @A
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

        @RepeatedTest(3)
        void checkLoginFunctionalityPerformance() {
            System.out.println(Thread.currentThread().getName());

            assertTimeout(Duration.ofMillis(200), () -> {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(190);
                return userService.login("dummy", "dummy");
            });

            assertTimeoutPreemptively(
                    Duration.ofMillis(200),
                    () -> System.out.println(Thread.currentThread().getName())
            );
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
