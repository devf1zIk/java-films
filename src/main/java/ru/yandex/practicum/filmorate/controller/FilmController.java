package ru.yandex.practicum.filmorate.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> storage = new LinkedHashMap<>();
    private int nextId = 1;

    private int getNextId() {
        return nextId++;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidateException("Имя фильма не может быть пустым");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidateException("Дата выпуска фильма слишком старая");
        }
        if (film.getDuration() <= 0) {
            throw new ValidateException("Продолжительность фильма должна быть больше 0");
        }
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        film.setId(getNextId());
        storage.put(film.getId(), film);
        log.info("Фильм добавлен: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film == null) {
            throw new ValidateException("Фильм не может быть пустым или без id");
        }
        if (!storage.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        validateFilm(film);
        storage.put(film.getId(), film);
        log.info("Фильм обновлён: {}", film);
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен список всех фильмов: {}", storage.size());
        return new ArrayList<>(storage.values());
    }
}
