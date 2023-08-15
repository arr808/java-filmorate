package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

public interface UserDbStorage extends UserStorage {

    User add(User user);

    User update(User user);

    void deleteAll();

    List<User> getAll();

    User getById(int id);

    List<User> getFriendsById(int id);

    List<User> getCommonFriends(int id, int otherId);
}
