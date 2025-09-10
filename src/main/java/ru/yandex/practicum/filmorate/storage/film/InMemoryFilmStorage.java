package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public Film create(Film film) {
        log.info("Пытаемся создать фильм: {}", film.getName());

        validateReleaseDate(film);

        film.setId(getNextId());
        films.put(film.getId(), film);

        log.info("Фильм успешно создан с id={}", film.getId());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Попытка обновления фильма без указания id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        log.info("Пытаемся обновить фильм с id={}", newFilm.getId());

        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            log.warn("Попытка обновления несуществующего фильма с id={}", newFilm.getId());
            throw new ConditionsNotMetException("Фильм с таким id не найден");
        }

        validateReleaseDate(newFilm);

        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());

        log.info("Фильм с id={} успешно обновлён", oldFilm.getId());
        return oldFilm;
    }

    @Override
    public Collection<Film> findAll() {
        log.info("Выполнен запрос на получение всех фильмов. Кол-во фильмов: {}", films.size());
        return films.values();
    }

    @Override
    public Film findById(Long id) {
        return films.get(id);
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}