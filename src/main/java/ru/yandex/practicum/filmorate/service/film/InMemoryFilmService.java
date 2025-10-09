package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.LikeRepository;
import ru.yandex.practicum.filmorate.dal.MpaRatingRepository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryFilmService implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeRepository likeRepository;
    private final MpaRatingRepository mpaRatingRepository;
    private final GenreRepository genreRepository;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public Film create(Film film) {
        log.info("Создание фильма через сервис: {}", film.getName());
        validateReleaseDate(film);
        validateMpa(film);
        validateGenres(film);
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film film) {
        log.info("Обновление фильма через сервис, id={}", film.getId());

        if (film.getId() == null) {
            log.warn("Попытка обновления фильма без указания id");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        Film existingFilm = filmStorage.findById(film.getId());
        if (existingFilm == null) {
            log.warn("Попытка обновления несуществующего фильма с id={}", film.getId());
            throw new ConditionsNotMetException("Фильм с таким id не найден");
        }

        validateReleaseDate(film);
        validateMpa(film);
        validateGenres(film);

        existingFilm.setName(film.getName());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());
        existingFilm.setMpa(film.getMpa());

        return filmStorage.update(existingFilm);
    }

    @Override
    public Collection<Film> findAll() {
        log.info("Получение всех фильмов через сервис");
        return filmStorage.findAll();
    }

    @Override
    public Film findById(Long id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        log.info("Пользователь id={} ставит лайк фильму id={}", userId, filmId);

        Film film = getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        likeRepository.addLike(filmId, userId);

        log.info("Лайк добавлен. Теперь у фильма id={} {} лайков", filmId, film.getLikes().size());
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        log.info("Пользователь id={} удаляет лайк у фильма id={}", userId, filmId);

        Film film = getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        likeRepository.removeLike(filmId, userId);

        log.info("Лайк удалён. Теперь у фильма id={} {} лайков", filmId, film.getLikes().size());
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        log.info("Запрос на получение топ-{} популярных фильмов", count);

        return filmStorage.findAll().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }

    private void validateMpa(Film film) {
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ValidationException("Поле 'mpa' должно быть указано");
        }
        boolean exists = mpaRatingRepository.findById(film.getMpa().getId()).isPresent();
        if (!exists) {
            throw new NotFoundException("MPA с id=" + film.getMpa().getId() + " не найден");
        }
    }

    private void validateGenres(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genre.getId() == null) {
                    throw new ValidationException("ID жанра не может быть пустым");
                }
                boolean exists = genreRepository.findById(genre.getId()).isPresent();
                if (!exists) {
                    throw new NotFoundException("Жанр с id=" + genre.getId() + " не найден");
                }
            }
        }
    }

    private Film getFilmOrThrow(Long id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new ConditionsNotMetException("Фильм с id=" + id + " не найден");
        }
        return film;
    }

    private void getUserOrThrow(Long id) {
        if (userStorage.findById(id) == null) {
            throw new ConditionsNotMetException("Пользователь с id=" + id + " не найден");
        }
    }
}