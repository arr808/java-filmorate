package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class FilmControllerTests {

    private final String MAPP = "/films";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private Film film = Film.builder()
            .id(1)
            .name("Name")
            .description("Description")
            .releaseDate(LocalDate.of(2000, 1, 1))
            .duration(90)
            .build();

    @AfterEach
    public void clean() throws Exception {
        mockMvc.perform(delete(MAPP));
    }

    @Test
    public void shouldAddAndReturnFilms() throws Exception {
        mockMvc.perform(post(MAPP)
                .content(objectMapper.writeValueAsString(film))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));

        List<Film> films = new ArrayList<>();
        films.add(film);

        mockMvc.perform(get(MAPP))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(films)));
    }

    @Test
    public void shouldNotAddDuplicateFilms() throws Exception {
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));

        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotAddFilmWithEmptyName() throws Exception {
        film.setName("");
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldAddFilmWithDescriptionSize200() throws Exception {
        film.setDescription("W".repeat(200));
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));
    }

    @Test
    public void shouldNotAddFilmWithDescriptionSizeMore200() throws Exception {
        film.setDescription("W".repeat(201));
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotAddFilmWithEmptyDescription() throws Exception {
        film.setDescription("");
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldAddFilmWithReleaseDate1895_12_28() throws Exception {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));
    }

    @Test
    public void shouldNotAddFilmWithWrongReleaseDate() throws Exception {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotAddFilmFromFuture() throws Exception {
        film.setReleaseDate(LocalDate.now().plusDays(1));
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotAddFilmWithNullReleaseDate() throws Exception {
        film.setReleaseDate(null);
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotAddFilmWithZeroDuration() throws Exception {
        film.setDuration(0);
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotAddFilmWithNegativeDuration() throws Exception {
        film.setDuration(-1);
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldUpdateFilm() throws Exception {
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));

        film.setDuration(50);

        mockMvc.perform(put(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));

        List<Film> films = new ArrayList<>();
        films.add(film);

        mockMvc.perform(get(MAPP))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(films)));

    }

    @Test
    public void shouldNotUpdateUnknownFilm() throws Exception {
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));

        film.setId(50);

        mockMvc.perform(put(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotUpdateFilmWithEmptyName() throws Exception {
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));

        film.setName("");

        mockMvc.perform(put(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotUpdateFilmWithDescriptionSizeMore200() throws Exception {
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));

        film.setDescription("W".repeat(201));

        mockMvc.perform(put(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotUpdateFilmWithEmptyDescription() throws Exception {
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));

        film.setDescription("");

        mockMvc.perform(put(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotUpdateFilmWithWrongReleaseDate() throws Exception {
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));

        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        mockMvc.perform(put(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotUpdateFilmFromFuture() throws Exception {
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));

        film.setReleaseDate(LocalDate.now().plusDays(1));

        mockMvc.perform(put(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotUpdateFilmWithNullReleaseDate() throws Exception {
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));

        film.setReleaseDate(null);

        mockMvc.perform(put(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotUpdateFilmWithZeroDuration() throws Exception {
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));

        film.setDuration(0);

        mockMvc.perform(put(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotUpdateFilmWithNegativeDuration() throws Exception {
        mockMvc.perform(post(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));

        film.setDuration(-1);

        mockMvc.perform(put(MAPP)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }
}