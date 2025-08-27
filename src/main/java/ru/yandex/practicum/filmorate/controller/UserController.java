package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Выполнен запрос на получение пользовательей. Кол-во пользователей: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Пытаемся создать пользователя: email={}, login={}", user.getEmail(), user.getLogin());

        boolean emailExist = users.values().stream()
                .anyMatch(oldUser -> user.getEmail().equalsIgnoreCase(oldUser.getEmail()));
        if (emailExist) {
            log.warn("Попытка создания пользователя с уже существующим email: {}", user.getEmail());
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя не указано, устанавливаем login вместо имени: {}", user.getName());
        }

        user.setId((getNextId()));

        users.put(user.getId(), user);
        log.info("Пользователь создан, id={}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
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

        boolean emailExists = users.values().stream()
                .anyMatch(u -> !u.getId().equals(newUser.getId()) &&
                        u.getEmail().equalsIgnoreCase(newUser.getEmail()));
        if (emailExists) {
            log.warn("Попытка обновления email на уже используемый: {}", newUser.getEmail());
            throw new DuplicatedDataException("Этот email уже используется другим пользователем");
        }

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

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
