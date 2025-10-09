package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя не указано, установлено значение логина: {}", user.getName());
        }

        User savedUser = userRepository.create(user);
        log.info("Пользователь успешно создан в БД, id={}", savedUser.getId());
        return savedUser;
    }

    @Override
    public User update(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя пользователя не указано, установлено значение логина: {}", user.getName());
        }

        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            throw new RuntimeException("Пользователь с id=" + user.getId() + " не найден для обновления");
        }

        User updatedUser = userRepository.update(user);
        log.info("Пользователь успешно обновлён в БД, id={}", updatedUser.getId());
        return updatedUser;
    }

    @Override
    public Collection<User> findAll() {
        log.debug("Запрос на получение всех пользователей");
        List<User> users = userRepository.findAll();
        users.forEach(this::loadFriends);
        return users;
    }

    @Override
    public User findById(Long id) {
        log.debug("Запрос на получение пользователя по id={}", id);
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            loadFriends(user);
        }
        return user;
    }

    private void loadFriends(User user) {
        List<Long> friendIds = friendshipRepository.findFriendIds(user.getId());
        user.setFriends(new HashSet<>(friendIds));
    }
}