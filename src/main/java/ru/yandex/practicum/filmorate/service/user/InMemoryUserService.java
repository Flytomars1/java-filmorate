package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {

    private final UserStorage userStorage;

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.info("Пользователь id={} добавляет в друзья пользователя id={}", userId, friendId);

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().add(friendId);

        friend.getFriends().add(userId);

        log.info("Пользователи id={} и id={} теперь друзья", userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        log.info("Пользователь id={} удаляет из друзей пользователя id={}", userId, friendId);

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Дружба между id={} и id={} разорвана", userId, friendId);
    }

    @Override
    public Set<User> findFriends(Long userId) {
        User user = getUserOrThrow(userId);
        return user.getFriends().stream()
                .map(userStorage::findById)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<User> findCommonFriends(Long userId, Long otherId) {
        User user = getUserOrThrow(userId);
        User other = getUserOrThrow(otherId);

        Set<Long> commonFriendIds = user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .collect(Collectors.toSet());

        return commonFriendIds.stream()
                .map(userStorage::findById)
                .collect(Collectors.toSet());
    }

    private User getUserOrThrow(Long id) {
        User user = userStorage.findById(id);
        if (user == null) {
            throw new ConditionsNotMetException("Пользователь с id=" + id + " не найден");
        }
        return user;
    }
}