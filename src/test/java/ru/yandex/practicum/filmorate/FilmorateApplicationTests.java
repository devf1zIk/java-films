package ru.yandex.practicum.filmorate;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, GenreDbStorage.class, FilmDbStorage.class, MpaDbStorage.class})
class FilmorateApplicationTests {

	private final UserDbStorage userStorage;
	private final GenreDbStorage genreStorage;
	private final FilmDbStorage filmStorage;
	private final MpaDbStorage mpaStorage;

	@BeforeEach
	void setUp() {
		userStorage.create(new User(1, "altynai@example.com", "altynai_a", "Altynai", LocalDate.of(1995, 3, 10)));
		userStorage.create(new User(2, "nurbol@example.com", "nurbol_n", "Nurbol", LocalDate.of(1992, 8, 5)));
		userStorage.create(new User(3, "zhanel@example.com", "zhanel_z", "Zhanel", LocalDate.of(1990, 12, 1)));

		Film kelin = new Film(1, "Келін", "Казахская комедия о девушке, оказавшейся в ауыле.",
				LocalDate.of(2009, 10, 22), 82, new HashSet<>(), mpaStorage.getById(1), new HashSet<>());

		Film batyr = new Film(2, "Жаужүрек мың бала", "Исторический фильм о национальном герое.",
				LocalDate.of(2011, 5, 1), 100, new HashSet<>(), mpaStorage.getById(3), new HashSet<>());

		Film anagaZhol = new Film(3, "Анаға апарар жол", "Драма о сыне, разлучённом с матерью в годы репрессий.",
				LocalDate.of(2016, 9, 29), 112, new HashSet<>(), mpaStorage.getById(2), new HashSet<>());


		filmStorage.create(kelin);
		filmStorage.create(batyr);
		filmStorage.create(anagaZhol);

		int id = userStorage.findAll().getFirst().getId();
		userStorage.addFriend(id, id + 1);
	}

	@Test
	void testGetAllUsers() {
		List<User> users = userStorage.findAll();
		assertThat(users).hasSize(3);
		assertThat(users).extracting(User::getName)
				.containsExactlyInAnyOrder("Altynai", "Nurbol", "Zhanel");
	}

	@Test
	void testGetByUserId() {
		int id = userStorage.findAll().getFirst().getId();
		Optional<User> user = userStorage.findById(id);
		assertThat(user).hasValueSatisfying(u -> assertThat(u.getId()).isEqualTo(id));
	}

	@Test
	void testFriends() {
		int id = userStorage.findAll().getFirst().getId();
		List<User> friends = userStorage.getFriends(id);
		assertThat(friends).hasSize(1);
	}

	@Test
	void testAddFriend() {
		User user = new User(9,"f1zIk@gmail.com","f1zIk","alkaw",LocalDate.of(2004,04,04));
		userStorage.create(user);
		int id = userStorage.findAll().getLast().getId();
		Optional<User> add = userStorage.findById(id);

		assertThat(add).isNotNull();
		assertThat(add.get().getEmail()).isEqualTo("f1zIk@gmail.com");
		assertThat(add.get().getBirthday()).isEqualTo(LocalDate.of(2004,04,04));
	}

	@Test
	void testAllFilms() {
		List<Film> films = filmStorage.findAll();
		assertThat(films).hasSize(3);
		assertThat(films).extracting(Film::getName).containsExactlyInAnyOrder("Келін","Жаужүрек мың бала","Анаға апарар жол");
	}

	@Test
	void testFilmId() {
		Optional<Film> film = filmStorage.findById(1);
		assertThat(film).isNotNull();
	}

	@Test
	void testGetAllGenres() {
		List<Genre> genres = genreStorage.getAll();

		assertThat(genres).hasSize(6);
		assertThat(genres).extracting(Genre::getName)
				.containsExactlyInAnyOrder("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
	}

	@Test
	void testGetGenreById() {
		Genre genre = genreStorage.getById(1);

		assertThat(genre).isNotNull();
		assertThat(genre.getName()).isEqualTo("Комедия");
	}

	@Test
	void testGetAllMpa() {
		List<Mpa> ratings = mpaStorage.getAll();

		assertThat(ratings).hasSize(5);
		assertThat(ratings).extracting(Mpa::getName)
				.containsExactlyInAnyOrder("G", "PG", "PG-13", "R", "NC-17");
	}

	@Test
	void testGetMpaById() {
		Mpa rating = mpaStorage.getById(4);

		assertThat(rating).isNotNull();
		assertThat(rating.getName()).isEqualTo("R");
	}
}
