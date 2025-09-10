package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        log.info("Пытаемся создать пользователя: email={}, login={}", user.getEmail(), user.getLogin());

        checkEmailDuplicateOnCreate(user.getEmail());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя не указано, устанавливаем login вместо имени: {}", user.getName());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Пользователь создан, id={}", user.getId());
        return user;
    }

    @Override
    public User update(User newUser) {
        log.info("Пытаемся обновить пользователя с id={}", newUser.getId());

        if (newUser.getId() == null) {
            log.warn("Попытка обновления пользователя без указания id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            log.warn("Попытка обновления несуществующего пользователя с id={}", newUser.getId());
            throw new ConditionsNotMetException("Пользователь с таким id не найден");
        }

        checkEmailDuplicateOnUpdate(newUser.getEmail(), newUser.getId());

        oldUser.setEmail(newUser.getEmail());
        log.info("Пользователь id={} - email обновлён на {}", oldUser.getId(), oldUser.getEmail());

        oldUser.setLogin(newUser.getLogin());
        log.info("Пользователь id={} - login обновлён на {}", oldUser.getId(), oldUser.getLogin());

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            oldUser.setName(newUser.getLogin());
            log.info("Имя не указано или пустое, устанавливаем login как имя: {}", oldUser.getName());
        } else {
            oldUser.setName(newUser.getName());
            log.info("Пользователь id={} - имя обновлено на {}", oldUser.getId(), oldUser.getName());
        }

        oldUser.setBirthday(newUser.getBirthday());
        log.info("Пользователь id={} - дата рождения обновлена", oldUser.getId());

        log.info("Пользователь id={} успешно обновлён", oldUser.getId());
        return oldUser;
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

    private void checkEmailDuplicateOnCreate(String email) {
        boolean emailExists = users.values().stream()
                .anyMatch(u -> email.equalsIgnoreCase(u.getEmail()));
        if (emailExists) {
            log.warn("Попытка создания пользователя с уже существующим email: {}", email);
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
    }

    private void checkEmailDuplicateOnUpdate(String email, Long currentUserId) {
        boolean emailExists = users.values().stream()
                .anyMatch(u -> !u.getId().equals(currentUserId) &&
                        email.equalsIgnoreCase(u.getEmail()));
        if (emailExists) {
            log.warn("Попытка обновления email на уже используемый: {}", email);
            throw new DuplicatedDataException("Этот email уже используется другим пользователем");
        }
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