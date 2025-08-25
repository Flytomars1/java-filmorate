package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Выполнен запрос на получение всех фильмов. Кол-во фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        log.info("Пытаемся создать фильм: {}", film.getName());

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        if (film.getDurationInMinutes() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        film.setId((getNextId()));
        films.put(film.getId(), film);

        log.info("Фильм успешно создан с id={}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film newFilm) {
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

        if (newFilm.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }

        if (newFilm.getDurationInMinutes() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        oldFilm.setName(newFilm.getName());
        oldFilm.setDescription(newFilm.getDescription());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDurationInMinutes(newFilm.getDurationInMinutes());

        log.info("Фильм с id={} успешно обновлён", oldFilm.getId());
        return oldFilm;
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
