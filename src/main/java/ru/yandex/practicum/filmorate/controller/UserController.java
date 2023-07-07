package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import ru.yandex.practicum.filmorate.custom_exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private int id = 0;
    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAll() {
        log.info("Отправлен список всех пользователей - {}", users);
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        validation(user);
        user.setId(getNewId());
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь - {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validation(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.debug("Обновлен пользователь - {}", user);
        } else {
            log.warn("Пользователь не найден - {}", user);
            throw new ValidationException(String.format("Пользователь %s не найден", user));
        }
        return user;
    }

    @DeleteMapping
    public void clean() {
        users.clear();
        id = 0;
        log.info("Все пользователи удалены");
    }

    private int getNewId() {
        return ++id;
    }

    private void validation(User user) {
        if (users.containsValue(user)) {
            log.warn("Данный пользователь уже добавлен - {} ", user);
            throw new ValidationException("Данный пользователь уже добавлен");
        }

        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.debug("Имени пользователя {} присвоено значение логина", user);
        }
    }
}
