package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    private Film film;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        film = new Film();
        film.setName("Harry Potter");
        film.setDescription("Harry Potter & Potter 4");
        film.setReleaseDate(LocalDate.of(2010, 2, 16));
        film.setDuration(150);
    }

    @Test
    void addFilmValidShouldAddToStorage() {
        Film result = filmController.createFilm(film);
        assertNotNull(result.getId());
        assertEquals(1, filmController.getFilms().size());
    }

    @Test
    void addFilmInvalidShouldThrowException() {
        Film invalid = new Film();
        invalid.setName("Властелин Колец");
        invalid.setDescription("Сюжет трилогии следует за хоббитом Фродо Бэггинсом");
        invalid.setReleaseDate(LocalDate.of(1890, 1, 1));
        invalid.setDuration(0);
        assertThrows(ValidateException.class, () -> filmController.createFilm(invalid));
    }

    @Test
    void updateFilmValidShouldUpdateStorage() {
        Film result = filmController.createFilm(film);
        result.setName("Update Potter");
        filmController.updateFilm(result);
        assertEquals("Update Potter", filmController.getFilms().get(0).getName());
    }
}
