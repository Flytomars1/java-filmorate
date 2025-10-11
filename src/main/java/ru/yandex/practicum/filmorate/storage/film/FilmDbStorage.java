package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final FilmRepository filmRepository;
    private final LikeRepository likeRepository;
    private final FilmGenreRepository filmGenreRepository;
    private final MpaRatingRepository mpaRatingRepository;
    private final GenreRepository genreRepository;

    @Override
    public Film create(Film film) {
        Film savedFilm = filmRepository.create(film);
        log.info("Фильм успешно создан в БД, id={}", savedFilm.getId());
        List<Long> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        filmGenreRepository.saveGenres(savedFilm.getId(), genreIds);

        loadLikesAndGenres(savedFilm);
        return savedFilm;
    }

    @Override
    public Film update(Film film) {
        filmRepository.update(film);
        List<Long> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        filmGenreRepository.saveGenres(film.getId(), genreIds);
        loadLikesAndGenres(film);
        log.info("Фильм успешно обновлён в БД, id={}", film.getId());
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        List<Film> films = filmRepository.findAll();
        films.forEach(this::loadLikesAndGenres);
        return films;
    }

    @Override
    public Film findById(Long id) {
        Film film = filmRepository.findById(id).orElse(null);
        if (film != null) {
            loadLikesAndGenres(film);
        }
        return film;
    }

    private void loadLikesAndGenres(Film film) {
        List<Long> likes = likeRepository.findUserIdsByFilmId(film.getId());
        film.setLikes(new HashSet<>(likes));

        if (film.getMpa() != null && film.getMpa().getId() != null) {
            MpaRating mpa = mpaRatingRepository.findById(film.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("MPA с id=" + film.getMpa().getId() + " не найден"));
            film.setMpa(mpa);
        }

        List<Long> genreIds = filmGenreRepository.findGenreIdsByFilmId(film.getId());
        List<Genre> genres = genreIds.stream()
                .map(id -> genreRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Жанр с id=" + id + " не найден")))
                .collect(Collectors.toList());
        film.setGenres(genres);
    }
}