package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryFilmService implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public void addLike(Long filmId, Long userId) {
        log.info("Пользователь id={} ставит лайк фильму id={}", userId, filmId);

        Film film = getFilmOrThrow(filmId);
        getUserOrThrow(userId);

        film.getLikes().add(userId);

        log.info("Лайк добавлен. Теперь у фильма id={} {} лайков", filmId, film.getLikes().size());
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        log.info("Пользователь id={} удаляет лайк у фильма id={}", userId, filmId);

        Film film = getFilmOrThrow(filmId);
        getUserOrThrow(userId);

        film.getLikes().remove(userId);

        log.info("Лайк удалён. Теперь у фильма id={} {} лайков", filmId, film.getLikes().size());
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        log.info("Запрос на получение топ-{} популярных фильмов", count);

        return filmStorage.findAll().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilmOrThrow(Long id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new ConditionsNotMetException("Фильм с id=" + id + " не найден");
        }
        return film;
    }

    private void getUserOrThrow(Long id) {
        if (userStorage.findById(id) == null) {
            throw new ConditionsNotMetException("Пользователь с id=" + id + " не найден");
        }
    }
}