package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        UserDbStorage.class,
        FilmDbStorage.class,
        UserRepository.class,
        FriendshipRepository.class,
        FilmRepository.class,
        LikeRepository.class,
        FilmGenreRepository.class,
        UserRowMapper.class,
        FilmRowMapper.class,
        GenreRowMapper.class,
        MpaRatingRepository.class,
        MpaRatingRowMapper.class,
        GenreRepository.class
})
class FilmoRateApplicationTests {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    void testFindUserById() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User savedUser = userStorage.create(user);

        User foundUser = userStorage.findById(savedUser.getId());
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void testFindAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.create(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1995, 5, 5));
        userStorage.create(user2);

        Collection<User> users = userStorage.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getEmail)
                .contains("user1@example.com", "user2@example.com");
    }

    @Test
    void testFindFilmById() {
        Film film = new Film();
        MpaRating mpa = new MpaRating();
        film.setName("Титаник");
        film.setDescription("Фильм про корабль");
        film.setReleaseDate(LocalDate.of(1997, 12, 19));
        film.setDuration(194);
        mpa.setId(3L);
        film.setMpa(mpa);
        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        film.setGenres(Arrays.asList(genre1, genre2));
        Film savedFilm = filmStorage.create(film);

        Film foundFilm = filmStorage.findById(savedFilm.getId());

        assertThat(foundFilm).isNotNull();
        assertThat(foundFilm.getId()).isEqualTo(savedFilm.getId());
        assertThat(foundFilm.getName()).isEqualTo("Титаник");
        List<Long> genreIds = foundFilm.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());
        assertThat(genreIds).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void testFindAllFilms() {
        Film film = new Film();
        MpaRating mpa = new MpaRating();
        mpa.setId(3L);
        film.setName("Матрица");
        film.setDescription("Симуляция");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpa(mpa);
        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        film.setGenres(Arrays.asList(genre1, genre2));
        filmStorage.create(film);

        Collection<Film> films = filmStorage.findAll();

        assertThat(films).hasSize(1);
        assertThat(films).first().extracting(Film::getName).isEqualTo("Матрица");
    }
}