package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.custom_exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
public class UserService {

    private int id;
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) {
        validate(user);
        user.setId(getNewId());
        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void deleteAll() {
        userStorage.deleteAll();
        id = 0;
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(int id) {
        User user = userStorage.getById(id);
        if (user == null) throw new ValidationException(String.format("Пользователь с id: %d не найден", id));
        return user;
    }

    public void addFriend(int userId, int friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(int id) {
        User user = getById(id);
        List<User> friends = null;
        for (int friendId : user.getFriends()) {
            friends.add(getById(friendId));
        }
        return friends;
    }

    public List<User> getCommonFriends(int id, int otherId) {
        Set<Integer> userFriends = getById(id).getFriends();
        Set<Integer> otherUserFriends = getById(id).getFriends();
        List<Integer> friendIds;
        List<User> commonFriends = null;
        if (userFriends.size() > otherUserFriends.size()) {
            friendIds = getCommon(userFriends, otherUserFriends);
        } else friendIds = getCommon(otherUserFriends, userFriends);
        for (int friendId : friendIds) {
            commonFriends.add(getById(friendId));
        }
        return commonFriends;
    }

    private int getNewId() {
        return ++id;
    }

    private void validate(User user) {
        if (userStorage.getById(user.getId()) != null) {
            throw new ValidationException("Данный пользователь уже добавлен");
        }

        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
    }

    private List<Integer> getCommon(Set<Integer> userFriends, Set<Integer> otherUserFriends) {
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toList());
    }
}