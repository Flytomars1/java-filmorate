package old;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.InMemoryFilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LikesControllerTest {

    private InMemoryFilmService filmService;
    private InMemoryFilmStorage filmStorage;
    private InMemoryUserStorage userStorage;

    private Film film;
    private User user;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new InMemoryFilmService(filmStorage, userStorage);

        film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film = filmStorage.create(film);

        user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user = userStorage.create(user);
    }

    @Test
    void addLike() {
        filmService.addLike(film.getId(), user.getId());

        assertEquals(1, film.getLikes().size());
        assertTrue(film.getLikes().contains(user.getId()));
    }

    @Test
    void addLikeTwiceNotAdded() {
        filmService.addLike(film.getId(), user.getId());
        filmService.addLike(film.getId(), user.getId());

        assertEquals(1, film.getLikes().size());
    }

    @Test
    void removeLike() {
        filmService.addLike(film.getId(), user.getId());
        filmService.removeLike(film.getId(), user.getId());

        assertEquals(0, film.getLikes().size());
    }

    @Test
    void findPopularFilms() {
        Film film2 = new Film();
        film2.setName("Второй фильм");
        film2.setDescription("Описание 2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(100);
        film2 = filmStorage.create(film2); // id=2

        filmService.addLike(film.getId(), 1L);
        userStorage.create(createUser(2L));
        filmService.addLike(film.getId(), 2L);
        userStorage.create(createUser(3L));
        filmService.addLike(film2.getId(), 3L);

        List<Film> popular = filmService.findPopularFilms(10);

        assertEquals(film, popular.get(0));
        assertEquals(film2, popular.get(1));
    }

    @Test
    void addLikeIfFilmNotFound() {
        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> filmService.addLike(999L, user.getId())
        );

        assertEquals("Фильм с id=999 не найден", exception.getMessage());
    }

    @Test
    void addLikeIfUserNotFound() {
        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> filmService.addLike(film.getId(), 999L)
        );

        assertEquals("Пользователь с id=999 не найден", exception.getMessage());
    }

    private User createUser(Long id) {
        User u = new User();
        u.setId(id);
        u.setEmail("user" + id + "@example.com");
        u.setLogin("user" + id);
        u.setBirthday(LocalDate.of(1990, 1, 1));
        return u;
    }
}