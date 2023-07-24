package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.custom_exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.custom_exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
@Slf4j
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
        if (user == null) {
            log.warn("Не найден пользователь c id - {}", id);
            throw new NotFoundException("user");
        }
        return user;
    }

    public void addFriend(int userId, int friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        user.getFriends().add(friendId);
        log.debug("Пользователю - {} добавлен в друзья {}", user, friend);
        friend.getFriends().add(userId);
        log.debug("Пользователю - {} добавлен в друзья {}", friend, user);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        user.getFriends().remove(friendId);
        log.debug("У пользователюя- {} удален друг {}", user, friend);
        friend.getFriends().remove(userId);
        log.debug("У пользователюя- {} удален друг {}", friend, user);
    }

    public List<User> getFriends(int id) {
        User user = getById(id);
        List<User> friends = new ArrayList<>();
        for (int friendId : user.getFriends()) {
            friends.add(getById(friendId));
        }
        log.trace("Отправлен список друзей {} пользователя {}", friends, user);
        return friends;
    }

    public List<User> getCommonFriends(int id, int otherId) {
        Set<Integer> userFriends = getById(id).getFriends();
        Set<Integer> otherUserFriends = getById(otherId).getFriends();
        List<User> commonFriends = new ArrayList<>();
        if (userFriends.size() > otherUserFriends.size()) {
            commonFriends = getCommon(userFriends, otherUserFriends);
        } else commonFriends = getCommon(otherUserFriends, userFriends);
        log.trace("Отправлен список общих друзей {} пользователей {}, {}",
                commonFriends, getById(id), getById(otherId));
        return commonFriends;
    }

    private int getNewId() {
        return ++id;
    }

    private void validate(User user) {
        if (userStorage.getById(user.getId()) != null) {
            log.warn("Пользователь {} уже добавлен", user);
            throw new AlreadyExistException("user");
        }

        if (user.getName().isBlank() || user.getName() == null) {
            log.debug("Пользователю {} в качестве имени установлен логин", user);
            user.setName(user.getLogin());
        }
    }

    private List<User> getCommon(Set<Integer> userFriends, Set<Integer> otherUserFriends) {
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }
}