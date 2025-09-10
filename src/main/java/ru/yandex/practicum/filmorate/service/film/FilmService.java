package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    void addLike(Long filmId, Long userId);
    void removeLike(Long filmId, Long userId);
    List<Film> findPopularFilms(int count);
}