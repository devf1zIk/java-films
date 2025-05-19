package ru.yandex.practicum.filmorate.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.*;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public Film create(Film film) {
        Mpa mpa = mpaStorage.getById(film.getMpa().getId());
        if (mpa == null) {
            throw new NotFoundException("MPA с id=" + film.getMpa().getId() + " не найден");
        }
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                Genre checked = genreStorage.getById(genre.getId());
                if (checked == null) {
                    throw new NotFoundException("Жанр с id=" + genre.getId() + " не найден");
                }
            }
        }
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getFilm(int id) {
        Film film = filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
        if (film == null) {
            throw new NotFoundException("Фильм с id=" + id + " не найден");
        }
        return film;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public Film deleteById(int id) {
        return filmStorage.delete(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    public void addLike(int id, int userId) {
        checkFilmAndUserExist(id, userId);
        filmStorage.addLike(id,userId);
        log.info("Добавлен лайк от пользователя {}",id, userId);
    }

    public void removeLike(int id, int userId) {
        checkFilmAndUserExist(id, userId);
        filmStorage.removeLike(id,userId);
        log.info("Удален лайк от пользователя {}", id, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findPopularFilms(count);
    }

    private void checkFilmAndUserExist(int filmId, int userId) {
        if (filmStorage.findById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

}
