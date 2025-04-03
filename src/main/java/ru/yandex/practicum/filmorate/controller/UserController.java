package ru.yandex.practicum.filmorate.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    private int getNextId() {
        return nextId++;
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new ValidateException("Пользователь не может быть null");
        }
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidateException("Некорректный email");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidateException("Логин не может быть пустым");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с email {} добавлен с ID {}", user.getEmail(), user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validateUser(user);
        if (!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("ID пользователя не может быть null или отсутствовать при обновлении");
        }
        users.put(user.getId(), user);
        log.info("Пользователь с email {} обновлен", user.getEmail());
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Запрос на получение всех пользователей. Всего пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }
}

