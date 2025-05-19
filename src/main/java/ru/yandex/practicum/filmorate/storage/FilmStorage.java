package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);
    Film update(Film film);
    Optional<Film> findById(int id);
    List<Film> findAll();
    Optional<Film> delete(int id);
    void addLike(int filmId, int likeId);
    void removeLike(int filmId, int likeId);
    List<Film> findPopularFilms(int count);
}

