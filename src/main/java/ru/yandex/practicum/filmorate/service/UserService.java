package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import ru.yandex.practicum.filmorate.custom_exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.custom_exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipService friendshipService;

    @Autowired
    public UserService(@Qualifier("UserDbStorageImpl") UserStorage userStorage,
                       FriendshipService friendshipService) {
        this.userStorage = userStorage;
        this.friendshipService = friendshipService;
    }

    public User add(User user) {
        validate(user);
        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void deleteAll() {
        userStorage.deleteAll();
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
        friendshipService.addFriend(userId, friendId);
        log.debug("Пользователь - {} отправил заявку на дружбу с пользователем {}", user, friend);
    }

    public void acceptFriend(int userId, int friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        friendshipService.acceptFriend(userId, friendId);
        log.debug("Пользовател - {} подтвердил дружбу с пользователем {}", user, friend);
    }

    public void deleteFriend(int userId, int friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        friendshipService.deleteFriend(userId, friendId);
        log.debug("У пользователя- {} удален друг {}", user, friend);
    }

    public List<User> getFriends(int id) {
        return userStorage.getFriendsById(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        return userStorage.getCommonFriends(id, otherId);
    }

    private void validate(User user) {
        int id = user.getId();
        if (id != 0) {
            if (userStorage.getById(id) != null) {
                log.warn("Пользователь {} уже добавлен", user);
                throw new AlreadyExistException("user");
            }
        }

        if (user.getName().isBlank() || user.getName() == null) {
            log.debug("Пользователю {} в качестве имени установлен логин", user);
            user.setName(user.getLogin());
        }
    }

}