package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserService {
    User create(User user);

    User update(User user);

    Collection<User> findAll();

    User findById(Long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    Set<User> findFriends(Long userId);

    Set<User> findCommonFriends(Long userId, Long otherId);
}