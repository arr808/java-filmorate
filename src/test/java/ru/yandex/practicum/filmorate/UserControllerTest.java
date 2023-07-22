package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private final String query = "/users";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private final User user = User.builder()
            .id(1)
            .login("login")
            .email("my@mail.ru")
            .name("name")
            .birthday(LocalDate.of(2021,1,1))
            .build();

    private final User friend = User.builder()
            .id(2)
            .login("friend")
            .email("his@mail.ru")
            .name("name")
            .birthday(LocalDate.of(2021,1,2))
            .build();

    @AfterEach
    public void clean() throws Exception {
        mockMvc.perform(delete(query));
    }

    @Test
    public void shouldAddAndReturnUsers() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        List<User> users = new ArrayList<>();
        users.add(user);

        mockMvc.perform(get(query))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    public void shouldAddUserWithEmptyName() throws Exception {
        User nonameUser = User.builder()
                .id(1)
                .login("login")
                .email("my@mail.ru")
                .name("")
                .birthday(LocalDate.of(2021,1,1))
                .build();

        user.setName(user.getLogin());

        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(nonameUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    public void shouldNotAddDuplicateUsers() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(409));
    }

    @Test
    public void shouldNotAddUserWithEmptyLogin() throws Exception {
        user.setLogin("");
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotAddUserFromFuture() throws Exception {
        user.setBirthday(LocalDate.now().plusDays(1));
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotAddUserWithNullBirthday() throws Exception {
        user.setBirthday(null);
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        user.setName("NAME");

        mockMvc.perform(put(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        List<User> users = new ArrayList<>();
        users.add(user);

        mockMvc.perform(get(query))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));

    }

    @Test
    public void shouldNotUpdateUnknownUser() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        user.setId(50);

        mockMvc.perform(put(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldNotUpdateFilmWithEmptyLogin() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        user.setLogin("");

        mockMvc.perform(put(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotUpdateUserFromFuture() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        user.setBirthday(LocalDate.now().plusDays(1));

        mockMvc.perform(put(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldNotUpdateFilmWithNullReleaseDate() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        user.setBirthday(null);

        mockMvc.perform(put(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500));
    }

    @Test
    public void shouldReturnUser() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get(query + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    public void shouldNotReturnUnknownUser() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get(query + "/11"))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldAddAndReturnFriend() throws Exception {
        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post(query)
                        .content(objectMapper.writeValueAsString(friend))
                        .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(put(query + "/1/friends/2"))
                .andExpect(status().isOk());

        List<User> friends = new ArrayList<>();
        friends.add(friend);

        mockMvc.perform(get(query + "/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(friends)));
    }

    @Test
    public void shouldNotAddUnknownUserAsFriend() throws Exception {
        mockMvc.perform(post(query)
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(put(query + "/1/friends/2"))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldRemoveFriend() throws Exception {
        mockMvc.perform(post(query)
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post(query)
                .content(objectMapper.writeValueAsString(friend))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(put(query + "/1/friends/2"));

        mockMvc.perform(delete(query + "/1/friends/2"));

        mockMvc.perform(get(query + "/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ArrayList<User>())));
    }

    @Test
    public void shouldReturnCommonFriends() throws Exception {
        User common = User.builder()
                .id(3)
                .login("common")
                .email("com@mail.ru")
                .name("name")
                .birthday(LocalDate.of(2021,1,2))
                .build();

        mockMvc.perform(post(query)
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post(query)
                .content(objectMapper.writeValueAsString(friend))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post(query)
                .content(objectMapper.writeValueAsString(common))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(put(query + "/1/friends/3"))
                .andExpect(status().isOk());

        mockMvc.perform(put(query + "/2/friends/3"))
                .andExpect(status().isOk());

        List<User> friends = new ArrayList<>();
        friends.add(common);

        mockMvc.perform(get(query + "/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(friends)));
    }

    @Test
    public void shouldReturnEmptyCommonFriends() throws Exception {
        mockMvc.perform(post(query)
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get(query + "/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ArrayList<User>())));
    }
}
