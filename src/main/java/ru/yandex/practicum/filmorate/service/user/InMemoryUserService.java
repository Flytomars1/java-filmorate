package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FriendshipRepository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {

    private final UserStorage userStorage;
    private final FriendshipRepository friendshipRepository;

    @Override
    public User create(User user) {
        log.info("Создание пользователя через сервис: email={}, login={}", user.getEmail(), user.getLogin());

        checkEmailDuplicateOnCreate(user.getEmail());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя не указано, устанавливаем login вместо имени: {}", user.getName());
        }

        return userStorage.create(user);
    }

    @Override
    public User update(User newUser) {
        log.info("Обновление пользователя через сервис, id={}", newUser.getId());

        if (newUser.getId() == null) {
            log.warn("Попытка обновления пользователя без указания id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User oldUser = userStorage.findById(newUser.getId());
        if (oldUser == null) {
            log.warn("Попытка обновления несуществующего пользователя с id={}", newUser.getId());
            throw new ConditionsNotMetException("Пользователь с таким id не найден");
        }

        checkEmailDuplicateOnUpdate(newUser.getEmail(), newUser.getId());

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
            log.info("Имя не указано или пустое, устанавливаем login как имя: {}", newUser.getName());
        }

        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName());
        oldUser.setBirthday(newUser.getBirthday());

        return userStorage.update(newUser);
    }

    @Override
    public Collection<User> findAll() {
        log.info("Получение всех пользователей через сервис");
        return userStorage.findAll();
    }

    @Override
    public User findById(Long id) {
        return userStorage.findById(id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.info("Пользователь id={} добавляет в друзья пользователя id={}", userId, friendId);

        if (userId.equals(friendId)) {
            log.warn("Попытка добавить себя в друзья, userId={}", userId);
            throw new ValidationException("Нельзя добавить себя в друзья");
        }

        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        friendshipRepository.addFriend(userId, friendId);

        log.info("Пользователь id={} добавил в друзья пользователя id={}", userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        log.info("Пользователь id={} удаляет из друзей пользователя id={}", userId, friendId);

        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        friendshipRepository.removeFriend(userId, friendId);

        log.info("Пользователь id={} удалил из друзей пользователя id={}", userId, friendId);
    }

    @Override
    public Set<User> findFriends(Long userId) {
        getUserOrThrow(userId);

        var friendIds = friendshipRepository.findFriendIds(userId);
        return friendIds.stream()
                .map(userStorage::findById)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<User> findCommonFriends(Long userId, Long otherId) {
        getUserOrThrow(userId);
        getUserOrThrow(otherId);

        var commonFriendIds = friendshipRepository.findCommonFriendIds(userId, otherId);
        return commonFriendIds.stream()
                .map(userStorage::findById)
                .collect(Collectors.toSet());
    }

    private void checkEmailDuplicateOnCreate(String email) {
        boolean emailExists = userStorage.findAll().stream()
                .anyMatch(u -> email.equalsIgnoreCase(u.getEmail()));
        if (emailExists) {
            log.warn("Попытка создания пользователя с уже существующим email: {}", email);
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
    }

    private void checkEmailDuplicateOnUpdate(String email, Long currentUserId) {
        boolean emailExists = userStorage.findAll().stream()
                .anyMatch(u -> !u.getId().equals(currentUserId) &&
                        email.equalsIgnoreCase(u.getEmail()));
        if (emailExists) {
            log.warn("Попытка обновления email на уже используемый: {}", email);
            throw new DuplicatedDataException("Этот email уже используется другим пользователем");
        }
    }

    private User getUserOrThrow(Long id) {
        User user = userStorage.findById(id);
        if (user == null) {
            throw new ConditionsNotMetException("Пользователь с id=" + id + " не найден");
        }
        return user;
    }
}