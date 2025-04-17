package ru.yandex.practicum.filmorate.model;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.validate.ReleaseDateConstraint;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Film {

    private int id;

    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;

    @Size(max = 200, message = "Описание не может превышать 200 символов.")
    private String description;

    @NotNull(message = "Дата релиза не может быть пустой.")
    @PastOrPresent(message = "Дата релиза не может быть в будущем.")
    @ReleaseDateConstraint
    private LocalDate releaseDate;

    @Min(value = 1, message = "Продолжительность фильма должна быть больше 0.")
    private int duration;

    private Set<Long> likes = new HashSet<>();
}
