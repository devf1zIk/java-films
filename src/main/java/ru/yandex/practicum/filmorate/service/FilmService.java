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
        validateFilmExists(id);
        filmStorage.getFilm(id).getLikes().add(userId);
        log.info("Добавлено пользователю {}", userId);
        return filmStorage.getFilm(id);
    }

    public Film removeLike(int id, int userId) {
        validateFilmExists(id);
        filmStorage.getFilm(id).getLikes().remove(userId);
        log.info("Удален лайк у пользователя {}", userId);
        return filmStorage.getFilm(id);
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
        log.info("Список популярных фильмов отправлен");
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilmExists(int id) {
        if (filmStorage.getFilm(id) == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
    }
}
