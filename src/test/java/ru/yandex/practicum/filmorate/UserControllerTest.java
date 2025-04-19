package ru.yandex.practicum.filmorate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserController userController;
    private User user;
    private User friend;

    @BeforeEach
    void setUp() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);

        user = new User(0, "harry.potter@mail.com", "harrypotter", "Harry Potter",
                LocalDate.of(1990, 7, 31));
        friend = new User(0, "ron.weasley@mail.com", "ronweasley", "Ron Weasley",
                LocalDate.of(1990, 3, 1));
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

    @Test
    void shouldGetUserByIdSuccessfully() {
        User createdUser = userController.createUser(user);

        User fetchedUser = userController.getUser(createdUser.getId());

        assertNotNull(fetchedUser);
        assertEquals(createdUser.getId(), fetchedUser.getId());
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        User createdUser = userController.createUser(user);

        User deletedUser = userController.deleteUser(createdUser.getId());

        assertEquals(createdUser.getId(), deletedUser.getId());
        assertTrue(userController.getUsers().isEmpty());
    }

    @Test
    void shouldAddFriendSuccessfully() {
        User createdUser = userController.createUser(user);
        User createdFriend = userController.createUser(friend);

        userController.addFriend(createdUser.getId(), createdFriend.getId());

        List<User> userFriends = userController.getFriendsList(createdUser.getId());
        assertEquals(1, userFriends.size());
        assertEquals(createdFriend.getId(), userFriends.get(0).getId());
    }

    @Test
    void shouldRemoveFriendSuccessfully() {
        User createdUser = userController.createUser(user);
        User createdFriend = userController.createUser(friend);

        userController.addFriend(createdUser.getId(), createdFriend.getId());

        userController.removeFriend(createdUser.getId(), createdFriend.getId());

        List<User> userFriends = userController.getFriendsList(createdUser.getId());
        assertTrue(userFriends.isEmpty());
    }

    @Test
    void shouldGetCommonFriendsSuccessfully() {
        User user1 = userController.createUser(user);
        User user2 = userController.createUser(friend);
        User mutualFriend = new User(0, "hermione.granger@mail.com", "hermione", "Hermione Granger",
                LocalDate.of(1990, 9, 19));
        User createdMutual = userController.createUser(mutualFriend);

        userController.addFriend(user1.getId(), createdMutual.getId());
        userController.addFriend(user2.getId(), createdMutual.getId());

        List<User> common = userController.getFriendsCommon(user1.getId(), user2.getId());

        assertEquals(1, common.size());
        assertEquals(createdMutual.getId(), common.get(0).getId());
    }
}
