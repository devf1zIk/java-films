package ru.yandex.practicum.filmorate.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    private int getNextId() {
        return nextId++;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с email {} добавлен с ID {}", user.getEmail(), user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
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

