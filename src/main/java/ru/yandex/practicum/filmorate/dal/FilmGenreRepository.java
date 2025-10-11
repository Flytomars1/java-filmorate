package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilmGenreRepository {

    private final JdbcTemplate jdbc;

    private static final String ADD_GENRE =
            "MERGE INTO film_genre (film_id, genre_id) VALUES (?, ?)";

    private static final String REMOVE_ALL_GENRES =
            "DELETE FROM film_genre WHERE film_id = ?";

    private static final String FIND_GENRES_BY_FILM =
            "SELECT genre_id FROM film_genre WHERE film_id = ?";

    public void saveGenres(Long filmId, List<Long> genreIds) {
        jdbc.update(REMOVE_ALL_GENRES, filmId);
        for (Long genreId : genreIds) {
            jdbc.update(ADD_GENRE, filmId, genreId);
        }
    }

    public List<Long> findGenreIdsByFilmId(Long filmId) {
        return jdbc.queryForList(FIND_GENRES_BY_FILM, Long.class, filmId);
    }
}