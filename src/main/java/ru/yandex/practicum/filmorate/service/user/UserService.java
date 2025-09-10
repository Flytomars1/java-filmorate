package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

public interface UserService {
    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Set<User> findFriends(Long userId);

    Set<User> findCommonFriends(Long userId, Long otherId);
}