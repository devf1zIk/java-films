package ru.yandex.practicum.filmorate.mapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

@Component
public class FilmMapper implements RowMapper<Film> {

    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Mpa rating = null;
        String rateName = rs.getString("rating_name");
        if (rateName != null) {
            try {
                rating = new Mpa(rs.getLong("rate_id"),rateName);
            } catch (IllegalArgumentException e) {
                throw new NotFoundException("Некорректный рейтинг!" + rateName);
            }
        }
        return new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"),
                new HashSet<>(),
                rating,
                new HashSet<>()
        );
    }

}
