package ru.yandex.practicum.filmorate.model;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Setter
@Getter
public class User {

    private int id;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Некорректный формат электронной почты")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения обязательна для заполнения")
    @PastOrPresent(message = "Дата рождения должна быть в прошлом или настоящем")
    private LocalDate birthday;

    public User(int i, String mail, String login, String name, LocalDate birthday) {
        this.id = i;
        this.email = mail;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
