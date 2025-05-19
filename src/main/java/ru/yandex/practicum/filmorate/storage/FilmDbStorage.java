package ru.yandex.practicum.filmorate.storage;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());
        saveGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String checkSql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, film.getId());

        if (count == null || count == 0) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        saveGenres(film);
        return film;
    }

    @Override
    public Optional<Film> findById(int id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        return films.stream().findFirst();
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Optional<Film> delete(int id) {
        Optional<Film> filmOptional = findById(id);
        filmOptional.ifPresent(f -> {
            jdbcTemplate.update("DELETE FROM film_likes WHERE film_id = ?", id);
            jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", id);
            jdbcTemplate.update("DELETE FROM films WHERE id = ?", id);
        });
        return filmOptional;
    }

    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        String sql = """
        SELECT f.*, COUNT(fl.user_id) AS like_count
        FROM films f
        LEFT JOIN film_likes fl ON f.id = fl.film_id
        GROUP BY f.id
        ORDER BY like_count DESC, f.id ASC
        LIMIT ?
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs, rowNum), count);
    }


    private Set<Integer> loadLikes(int filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("user_id"), filmId));
    }

    private void saveGenres(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        int filmId = rs.getInt("id");

        Mpa mpa = jdbcTemplate.queryForObject("SELECT * FROM mpa WHERE id = ?",
                (mprs, rn) -> new Mpa(mprs.getLong("id"), mprs.getString("name")),
                rs.getInt("mpa_id"));

        Set<Genre> genres = new HashSet<>(jdbcTemplate.query(
                "SELECT g.id, g.name FROM film_genres fg JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id = ?",
                (grs, rn) -> new Genre(grs.getLong("id"), grs.getString("name")),
                filmId
        ));

        Set<Integer> likes = loadLikes(filmId);

        Film film = new Film();
        film.setId(filmId);
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(mpa);
        film.setGenres(genres);
        film.setLikes(likes);

        return film;
    }
}
