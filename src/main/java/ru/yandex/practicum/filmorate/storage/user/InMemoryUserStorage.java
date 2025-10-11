package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("inMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        log.info("Создание пользователя в хранилище: email={}, login={}", user.getEmail(), user.getLogin());
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь создан, id={}", user.getId());
        return user;
    }

    @Override
    public User update(User newUser) {
        log.info("Обновление пользователя в хранилище, id={}", newUser.getId());
        users.put(newUser.getId(), newUser);
        log.info("Пользователь id={} успешно обновлён", newUser.getId());
        return newUser;
    }

    @Override
    public Collection<User> findAll() {
        log.info("Выполнен запрос на получение пользователей. Кол-во пользователей: {}", users.size());
        return users.values();
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}