package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(int id);

    Film deleteFilm(int id);

    Collection<Film> getAllFilms();
}

