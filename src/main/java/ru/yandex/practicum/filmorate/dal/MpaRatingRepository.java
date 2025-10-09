package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaRatingRepository {
    private final JdbcTemplate jdbc;
    private final MpaRatingRowMapper mapper;

    private static final String FIND_ALL = "SELECT * FROM mpa_rating";
    private static final String FIND_BY_ID = "SELECT * FROM mpa_rating WHERE rating_id = ?";

    public List<MpaRating> findAll() {
        return jdbc.query(FIND_ALL, mapper);
    }

    public Optional<MpaRating> findById(Long id) {
        return jdbc.query(FIND_BY_ID, mapper, id)
                .stream()
                .findFirst();
    }
}