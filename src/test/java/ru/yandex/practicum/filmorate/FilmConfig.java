package ru.yandex.practicum.filmorate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;

@Configuration
public class FilmConfig {
    @Bean
    public FilmMapper filmMapper() {
        return new FilmMapper();
    }
}
