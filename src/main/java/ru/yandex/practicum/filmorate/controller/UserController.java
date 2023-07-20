package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@Getter
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        return userService.add(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id,
                          @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping
    public void deleteAll() {
        userService.deleteAll();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id,
                             @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping
    public List<User> findAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User findGyId(@PathVariable int id) {
        return userService.getById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> findFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id,
                                      @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
