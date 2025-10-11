package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeRepository {

    private final JdbcTemplate jdbc;

    private static final String ADD_LIKE =
            "MERGE INTO film_like (film_id, user_id) VALUES (?, ?)"; //мерж, чтобы избежать дублирования

    private static final String REMOVE_LIKE =
            "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";

    private static final String FIND_ALL_LIKES_BY_FILM =
            "SELECT user_id FROM film_like WHERE film_id = ?";

    private static final String FIND_ALL_LIKES_BY_USER =
            "SELECT film_id FROM film_like WHERE user_id = ?";

    public void addLike(Long filmId, Long userId) {
        jdbc.update(ADD_LIKE, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        jdbc.update(REMOVE_LIKE, filmId, userId);
    }

    public List<Long> findUserIdsByFilmId(Long filmId) {
        return jdbc.queryForList(FIND_ALL_LIKES_BY_FILM, Long.class, filmId);
    }

    public List<Long> findFilmIdsByUserId(Long userId) {
        return jdbc.queryForList(FIND_ALL_LIKES_BY_USER, Long.class, userId);
    }
}