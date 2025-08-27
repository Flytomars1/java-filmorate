package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;
    private Film validFilm;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();

        validFilm = new Film();
        validFilm.setName("Test name");
        validFilm.setDescription("Test description");
        validFilm.setReleaseDate(LocalDate.of(2005, 5, 5));
        validFilm.setDuration(5);
    }

    @Test
    void createFilm() {
        Film created = filmController.create(validFilm);

        assertNotNull(created.getId());
        assertEquals(1L, created.getId());
        assertEquals("Test name", created.getName());
        assertEquals("Test description", created.getDescription());
    }

    @Test
    void notCreateFilmWithReleaseDateBeforeMinDate() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(validFilm)
        );

        assertEquals("Дата релиза должна быть не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void updateFilm() {
        Film created = filmController.create(validFilm);

        created.setName("Новое имя");
        created.setDescription("Новое описание");
        created.setDuration(10);

        Film updated = filmController.update(created);

        assertEquals("Новое имя", updated.getName());
        assertEquals("Новое описание", updated.getDescription());
        assertEquals(10, updated.getDuration());
    }

    @Test
    void notUpdateFilmWithoutId() {
        validFilm.setId(null);

        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> filmController.update(validFilm)
        );

        assertEquals("Id должен быть указан", exception.getMessage());
    }

    @Test
    void notUpdateNonExistingFilm() {
        validFilm.setId(999L);

        ConditionsNotMetException exception = assertThrows(
                ConditionsNotMetException.class,
                () -> filmController.update(validFilm)
        );

        assertEquals("Фильм с таким id не найден", exception.getMessage());
    }

    @Test
    void notUpdateFilmWithReleaseDateBeforeMinDate() {
        Film created = filmController.create(validFilm);
        created.setReleaseDate(LocalDate.of(1800, 1, 1));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.update(created)
        );

        assertEquals("Дата релиза должна быть не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void returnAllUsers() {
        filmController.create(validFilm);

        Collection<Film> all = filmController.findAll();

        assertEquals(1, all.size());
        assertTrue(all.contains(validFilm));
    }
}
