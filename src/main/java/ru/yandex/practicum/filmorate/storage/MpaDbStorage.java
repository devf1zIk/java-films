package ru.yandex.practicum.filmorate.storage;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAll() {
        String sql = "SELECT id, name FROM mpa";
        return jdbcTemplate.query(sql, this::mapToMpa);
    }

    @Override
    public Mpa getById(int id) {
        String sql = "SELECT id, name FROM mpa WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapToMpa, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Рейтинг с ID: " + id + " не найден");
        }
    }

    private Mpa mapToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("id"), rs.getString("name"));
    }
}
