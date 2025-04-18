package ru.yandex.practicum.filmorate.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserStorage userStorage;

    public User update(User user) {
        return userStorage.updateUser(user);
    }

    public User create(User user) {
        return userStorage.createUser(user);
    }

    public List<User> getAll() {
        return userStorage.getAllUsers();
    }

    public User getId(int id) {
        return userStorage.getUser(id);
    }

    public User deleteById(int id) {
        return userStorage.deleteById(id);
    }

    public List<User> addFriendship(int id, int friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        log.info("Пользователи '{}' и '{}' теперь друзья", user.getName(), friend.getName());
        return List.of(user, friend);
    }

    public List<User> removeFriendship(int userId, int friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Пользователи '{}' и '{}' больше не друзья", user.getName(), friend.getName());

        return List.of(user, friend);
    }

    public List<User> getFriendsListById(int id) {
        User user = userStorage.getUser(id);
        log.info("Получен список друзей пользователя '{}'", user.getName());
        return user.getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommon(int me, int myfriendId) {
        User first = userStorage.getUser(me);
        User second = userStorage.getUser(myfriendId);

        log.info("Список общих друзей: '{}' и '{}' успешно отправлен", first.getName(), second.getName());
        return first.getFriends().stream()
                .filter(second.getFriends()::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }
}
