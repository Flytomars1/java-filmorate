package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

//@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        log.info("Создание фильма в хранилище: {}", film.getName());
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно создан с id={}", film.getId());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        log.info("Обновление фильма в хранилище, id={}", newFilm.getId());
        films.put(newFilm.getId(), newFilm);
        log.info("Фильм с id={} успешно обновлён", newFilm.getId());
        return newFilm;
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

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}