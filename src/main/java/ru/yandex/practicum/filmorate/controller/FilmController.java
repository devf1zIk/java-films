package ru.yandex.practicum.filmorate.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final Map<Integer, Film> storage = new LinkedHashMap<>();
    private int nextId = 1;

    private int getNextId() {
        return nextId++;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
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
