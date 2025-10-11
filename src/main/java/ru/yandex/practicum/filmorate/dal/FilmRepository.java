package ru.yandex.practicum.filmorate.dal;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FilmRepository extends BaseRepository<Film> {

    private static final String FIND_ALL = "SELECT * FROM film";
    private static final String FIND_BY_ID = "SELECT * FROM film WHERE film_id = ?";
    private static final String INSERT_FILM =
            "INSERT INTO film (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM =
            "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";

    private final FilmRowMapper filmRowMapper;
    private final FilmGenreRepository filmGenreRepository;

    public FilmRepository(org.springframework.jdbc.core.JdbcTemplate jdbc, FilmRowMapper filmRowMapper, FilmGenreRepository filmGenreRepository) {
        super(jdbc, filmRowMapper);
        this.filmRowMapper = filmRowMapper;
        this.filmGenreRepository = filmGenreRepository;
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL);
    }

    public Optional<Film> findById(Long id) {
        return findOne(FIND_BY_ID, id);
    }

    public Film create(Film film) {
        long id = insert(INSERT_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);

        List<Long> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        filmGenreRepository.saveGenres(id, genreIds);
        return film;
    }

    public Film update(Film film) {
        update(UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        List<Long> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        filmGenreRepository.saveGenres(film.getId(), genreIds);

        return film;
    }
}