package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(value = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/dropDb.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class FilmControllerTests {

    private final String query = "/films";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private final Film film = Film.builder()
            .name("Name")
            .description("Description")
            .releaseDate(LocalDate.of(2000, 1, 1))
            .duration(90)
            .mpa(Mpa.G)
            .genres(new ArrayList<>())
            .build();

    private final Film expectedFilm = Film.builder()
            .id(1)
            .name("Name")
            .description("Description")
            .releaseDate(LocalDate.of(2000, 1, 1))
            .duration(90)
            .mpa(Mpa.G)
            .genres(new ArrayList<>())
            .build();

    @Test
    public void shouldAddAndReturnFilms() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedFilm)));

        List<Film> films = new ArrayList<>();
        films.add(expectedFilm);

        mockMvc.perform(get(query))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(films)));
    }

    @Test
    public void shouldNotAddDuplicateFilms() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(409));
    }

    @Test
    public void shouldNotAddFilmWithEmptyName() throws Exception {
        film.setName("");
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldAddFilmWithDescriptionSize200() throws Exception {
        expectedFilm.setDescription("W".repeat(200));
        film.setDescription("W".repeat(200));
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedFilm)));
    }

    @Test
    public void shouldNotAddFilmWithDescriptionSizeMore200() throws Exception {
        film.setDescription("W".repeat(201));
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotAddFilmWithEmptyDescription() throws Exception {
        film.setDescription("");
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldAddFilmWithReleaseDate1895_12_28() throws Exception {
        expectedFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedFilm)));
    }

    @Test
    public void shouldNotAddFilmWithWrongReleaseDate() throws Exception {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotAddFilmFromFuture() throws Exception {
        film.setReleaseDate(LocalDate.now().plusDays(1));
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotAddFilmWithNullReleaseDate() throws Exception {
        film.setReleaseDate(null);
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotAddFilmWithZeroDuration() throws Exception {
        film.setDuration(0);
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotAddFilmWithNegativeDuration() throws Exception {
        film.setDuration(-1);
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldUpdateFilm() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedFilm)));

        film.setDuration(50);
        film.setId(1);

        mockMvc.perform(put(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));

        List<Film> films = new ArrayList<>();
        films.add(film);

        mockMvc.perform(get(query))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(films)));

    }

    @Test
    public void shouldNotUpdateUnknownFilm() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        film.setId(50);

        mockMvc.perform(put(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldNotUpdateFilmWithEmptyName() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        film.setName("");

        mockMvc.perform(put(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotUpdateFilmWithDescriptionSizeMore200() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        film.setDescription("W".repeat(201));

        mockMvc.perform(put(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotUpdateFilmWithEmptyDescription() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        film.setDescription("");

        mockMvc.perform(put(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotUpdateFilmWithWrongReleaseDate() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        mockMvc.perform(put(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotUpdateFilmFromFuture() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        film.setReleaseDate(LocalDate.now().plusDays(1));

        mockMvc.perform(put(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotUpdateFilmWithNullReleaseDate() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        film.setReleaseDate(null);

        mockMvc.perform(put(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotUpdateFilmWithZeroDuration() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        film.setDuration(0);

        mockMvc.perform(put(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotUpdateFilmWithNegativeDuration() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        film.setDuration(-1);

        mockMvc.perform(put(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldReturnFilm() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(query + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedFilm)));
    }

    @Test
    public void shouldNotReturnUnknownFilm() throws Exception {
        mockMvc.perform(post(query)
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get(query + "/11"))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldAddLike() throws Exception {
        User user = User.builder()
                .login("login")
                .email("my@mail.ru")
                .name("name")
                .birthday(LocalDate.of(2021,1,1))
                .build();

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post(query)
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(put(query + "/1/like/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldNotAddLikeFromUnknownUser() throws Exception {
        mockMvc.perform(post(query)
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(put(query + "/1/like/1"))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldNotAddLikeToUnknownFilm() throws Exception {
        User user = User.builder()
                .login("login")
                .email("my@mail.ru")
                .name("name")
                .birthday(LocalDate.of(2021,1,1))
                .build();

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(put(query + "/1/like/1"))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldReturnMostPopular() throws Exception {
        User user = User.builder()
                .login("login")
                .email("my@mail.ru")
                .name("name")
                .birthday(LocalDate.of(2021,1,1))
                .build();

        User otherUser = User.builder()
                .login("login2")
                .email("my2@mail.ru")
                .name("name")
                .birthday(LocalDate.of(2021,1,1))
                .build();

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(otherUser))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        mockMvc.perform(post(query)
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        mockMvc.perform(put(query + "/1/like/1")).andExpect(status().isOk());

        mockMvc.perform(put(query + "/1/like/2")).andExpect(status().isOk());

        List<Film> popularFilms = new ArrayList<>();

        popularFilms.add(expectedFilm);

        mockMvc.perform(get(query + "/popular?count"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(popularFilms)));
    }
}