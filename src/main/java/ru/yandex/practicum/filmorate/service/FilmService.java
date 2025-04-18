package ru.yandex.practicum.filmorate.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;

    public Film create(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film update(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(int id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        return film;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film deleteById(int id) {
        return filmStorage.deleteFilm(id);
    }

    public Film addLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
        log.info("Добавлен лайк от пользователя {}", userId);
        return film;
    }

    public Film removeLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
        log.info("Удалён лайк от пользователя {} у фильма с id={}", userId, id);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .filter(f -> !f.getLikes().isEmpty())
                .sorted(Comparator
                        .comparingInt((Film f) -> f.getLikes().size()).reversed()
                        .thenComparingInt(Film::getId))
                .limit(count)
                .collect(Collectors.toList());
    }
}
