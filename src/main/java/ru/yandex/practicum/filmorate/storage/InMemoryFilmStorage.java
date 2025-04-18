package ru.yandex.practicum.filmorate.storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> storage = new LinkedHashMap<>();
    private int nextId = 1;

    private int getNextId() {
        return nextId++;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film == null) {
            throw new ValidateException("Фильм не может быть пустым");
        }
        if (!storage.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        storage.put(film.getId(), film);
        log.info("Фильм обновлён: {}", film);
        return film;
    }

    @Override
    public Film getFilm(int id) {
        return storage.get(id);
    }

    @Override
    public Film deleteFilm(int id) {
        return storage.remove(id);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return storage.values();
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(getNextId());
        storage.put(film.getId(), film);
        log.info("Фильм добавлен: {}", film);
        return film;
    }
}
