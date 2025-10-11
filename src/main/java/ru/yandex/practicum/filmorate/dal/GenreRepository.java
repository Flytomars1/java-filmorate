package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreRepository {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    private static final String FIND_ALL = "SELECT * FROM genre";
    private static final String FIND_BY_ID = "SELECT * FROM genre WHERE genre_id = ?";

    public List<Genre> findAll() {
        return jdbc.query(FIND_ALL, mapper);
    }

    public Optional<Genre> findById(Long id) {
        return jdbc.query(FIND_BY_ID, mapper, id)
                .stream()
                .findFirst();
    }
}