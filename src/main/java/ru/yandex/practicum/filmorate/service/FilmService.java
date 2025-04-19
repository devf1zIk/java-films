package ru.yandex.practicum.filmorate.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film create(Film film) {
        validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film update(Film film) {
        validateFilm(film);
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

    public void addLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        userStorage.getUser(userId);
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
        log.info("Добавлен лайк от пользователя {}", userId);
    }

    public void removeLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        userStorage.getUser(userId);
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
        log.info("Удалён лайк от пользователя {} у фильма с id={}", userId, id);
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

    private void validateFilm(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidateException("Дата выпуска фильма слишком старая");
        }
        if (film.getDuration() <= 0) {
            throw new ValidateException("Продолжительность фильма должна быть больше 0");
        }
    }
}
