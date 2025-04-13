package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private User user;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        user = new User(1, "harry.potter@mail.com", "harrypotter", "Harry Potter", LocalDate.of(1990, 7, 31));
    }

    @Test
    void shouldCreateUserSuccessfully() {
        User createdUser = userController.createUser(user);
        assertNotNull(createdUser);
        assertEquals(1, createdUser.getId());
        assertEquals("Harry Potter", createdUser.getName());
        assertEquals("harry.potter@mail.com", createdUser.getEmail());
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        userController.createUser(user);
        user.setName("Updated Name");

        User updatedUser = userController.updateUser(user);

        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
    }

    @Test
    void shouldGetUsersSuccessfully() {
        userController.createUser(user);

        List<User> users = userController.getUsers();

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }
}
