package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.InMemoryUserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FriendsControllerTest {

    private InMemoryUserService userService;
    private InMemoryUserStorage userStorage;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {

        userStorage = new InMemoryUserStorage();
        userService = new InMemoryUserService(userStorage);

        user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        user1 = userStorage.create(user1); // id=1

        user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1992, 2, 2));
        user2 = userStorage.create(user2); // id=2

        user3 = new User();
        user3.setEmail("user3@example.com");
        user3.setLogin("user3");
        user3.setName("User Three");
        user3.setBirthday(LocalDate.of(1993, 3, 3));
        user3 = userStorage.create(user3); // id=3
    }

    @Test
    void addFriends() {
        userService.addFriend(user1.getId(), user2.getId());

        assertTrue(user1.getFriends().contains(user2.getId()));

        assertTrue(user2.getFriends().contains(user1.getId()));
    }

    @Test
    void removeFriends() {
        userService.addFriend(user1.getId(), user2.getId());

        userService.removeFriend(user1.getId(), user2.getId());

        assertFalse(user1.getFriends().contains(user2.getId()));
        assertFalse(user2.getFriends().contains(user1.getId()));
    }

    @Test
    void findFriends() {
        // Добавляем двух друзей user1
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());

        Set<User> friends = userService.findFriends(user1.getId());

        assertEquals(2, friends.size());
        assertTrue(friends.contains(user2));
        assertTrue(friends.contains(user3));
    }

    @Test
    void findCommonFriends() {
        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());

        userService.addFriend(user2.getId(), user3.getId());

        Set<User> commonFriends = userService.findCommonFriends(user1.getId(), user2.getId());

        assertEquals(1, commonFriends.size());
        assertTrue(commonFriends.contains(user3));
    }

    @Test
    void throwExceptionIfFriendsNotExist() {
        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> userService.addFriend(999L, user2.getId()) // несуществующий пользователь
        );

        assertEquals("Пользователь с id=999 не найден", exception.getMessage());
    }

    @Test
    void cannotAddSelfAsFriend() {
        userStorage.create(user1);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.addFriend(1L, 1L)
        );

        assertEquals("Нельзя добавить себя в друзья", exception.getMessage());
    }
}