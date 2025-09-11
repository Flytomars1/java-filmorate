package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.InMemoryUserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;
    private User validUser;

    @BeforeEach
    void setUp() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        InMemoryUserService userService = new InMemoryUserService(userStorage);

        userController = new UserController(userService);

        validUser = new User();
        validUser.setEmail("test@test.com");
        validUser.setLogin("testlogin");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void createUser() {
        User created = userController.create(validUser);

        assertNotNull(created.getId());
        assertEquals(1L, created.getId());
        assertEquals("test@test.com", created.getEmail());
        assertEquals("testlogin", created.getLogin());
        assertEquals("testlogin", created.getName());
    }

    @Test
    void setNameToLoginWhenNameIsNull() {
        validUser.setName(null);

        User created = userController.create(validUser);

        assertEquals("testlogin", created.getName());
    }

    @Test
    void setNameToLoginWhenNameIsBlank() {
        validUser.setName("   ");

        User created = userController.create(validUser);

        assertEquals("testlogin", created.getName());
    }

    @Test
    void notCreateUserWithDuplicateEmail() {
        userController.create(validUser);

        User duplicate = new User();
        duplicate.setEmail("test@test.com");
        duplicate.setLogin("testlogin1");
        duplicate.setBirthday(LocalDate.of(1990, 1, 1));

        DuplicatedDataException exception = assertThrows(
                DuplicatedDataException.class,
                () -> userController.create(duplicate)
        );

        assertEquals("Этот имейл уже используется", exception.getMessage());
    }

    @Test
    void updateUser() {
        User created = userController.create(validUser);

        created.setName("Updated test");
        created.setEmail("updated@test.com");

        User updated = userController.update(created);

        assertEquals("Updated test", updated.getName());
        assertEquals("updated@test.com", updated.getEmail());
    }

    @Test
    void notUpdateNonExistingUser() {
        User user = new User();
        user.setId(999L);
        user.setEmail("test@test.com");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> userController.update(user)
        );

        assertEquals("Пользователь с таким id не найден", exception.getMessage());
    }

    @Test
    void notUpdateUserWithoutId() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testlogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> userController.update(user)
        );

        assertEquals("Id должен быть указан", exception.getMessage());
    }

    @Test
    void returnAllUsers() {
        userController.create(validUser);

        Collection<User> all = userController.findAll();

        assertEquals(1, all.size());
        assertTrue(all.contains(validUser));
    }
}