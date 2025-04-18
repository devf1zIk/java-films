package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;
    private Film film;

    @BeforeEach
    void setUp() {
        InMemoryFilmStorage storage = new InMemoryFilmStorage();
        InMemoryUserStorage storageUser = new InMemoryUserStorage();
        FilmService filmService = new FilmService(storage,storageUser);
        filmController = new FilmController(filmService);

        film = new Film();
        film.setName("Harry Potter");
        film.setDescription("Harry Potter & Potter 4");
        film.setReleaseDate(LocalDate.of(2010, 2, 16));
        film.setDuration(150);
    }

    @Test
    void addFilmValidShouldAddToStorage() {
        Film result = filmController.createFilm(film);
        assertNotNull(result.getId(), "ID фильма должен быть установлен");
        assertEquals(1, filmController.getAllFilms().size(), "Фильм должен добавиться в хранилище");
    }

    @Test
    void addFilmInvalidShouldThrowException() {
        Film invalid = new Film();
        invalid.setName("Властелин Колец");
        invalid.setDescription("Сюжет трилогии следует за хоббитом Фродо Бэггинсом");
        invalid.setReleaseDate(LocalDate.of(1890, 1, 1));
        invalid.setDuration(0);

        assertThrows(ValidateException.class, () -> filmController.createFilm(invalid),
                "Должно выбрасываться исключение валидации");
    }

    @Test
    void updateFilmValidShouldUpdateStorage() {
        Film result = filmController.createFilm(film);
        result.setName("Updated Potter");

        Film updated = filmController.updateFilm(result);
        assertEquals("Updated Potter", updated.getName(), "Имя фильма должно обновиться");
    }

    @Test
    void deleteFilmShouldRemoveFromStorage() {
        Film created = filmController.createFilm(film);
        filmController.deleteFilm(created.getId());

        assertTrue(filmController.getAllFilms().isEmpty(), "Фильм должен быть удалён");
    }

    @Test
    void addAndRemoveLikeShouldAffectLikesSize() {
        Film created = filmController.createFilm(film);

        filmController.addLike(created.getId(), 1);
        Film liked = filmController.getFilmById(created.getId());
        assertEquals(1, liked.getLikes().size(), "Количество лайков должно быть 1");

        filmController.removeLike(created.getId(), 1);
        Film unliked = filmController.getFilmById(created.getId());
        assertEquals(0, unliked.getLikes().size(), "Лайк должен быть удалён");
    }

    @Test
    void getPopularFilmsShouldReturnSortedList() {
        Film film1 = filmController.createFilm(film);

        Film film2 = new Film();
        film2.setName("Spider-Man");
        film2.setDescription("Spider-Man returns");
        film2.setReleaseDate(LocalDate.of(2012, 7, 3));
        film2.setDuration(120);
        film2 = filmController.createFilm(film2);

        filmController.addLike(film2.getId(), 1);
        filmController.addLike(film2.getId(), 2);

        filmController.addLike(film1.getId(), 3);

        List<Film> popular = filmController.getPopularFilms(2);
        assertEquals(film2.getId(), popular.get(0).getId(), "Фильм с наибольшим количеством лайков должен быть первым");
    }
}
