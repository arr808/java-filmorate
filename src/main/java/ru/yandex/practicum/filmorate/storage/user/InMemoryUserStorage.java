package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.custom_exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users;

    @Override
    public User add(User user) {
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь - {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        int id = user.getId();
        if (users.containsKey(id)) {
            log.debug("Обновлен пользователь - {}", user);
            users.put(id, user);
        } else {
            log.warn("Не найден пользователь - {}", user);
            throw new NotFoundException("user");
        }
        return user;
    }

    @Override
    public void deleteAll() {
        log.debug("Все пользователи удалены");
        users.clear();
    }

    @Override
    public List<User> getAll() {
        log.trace("Отправлен спикок всех пользователей - {}", users);
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(int id) {
        log.trace("Отправлен пользователь - {}", users.get(id));
        return users.get(id);
    }

    @Override
    public List<User> getFriendsById(int id) {
        User user = getById(id);
        List<User> friends = new ArrayList<>();
        for (int friendId : user.getFriends()) {
            friends.add(getById(friendId));
        }
        log.trace("Отправлен список друзей {} пользователя {}", friends, user);
        return friends;
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        List<User> firstUserFriends = getFriendsById(id);
        List<User> secondUserFriends = getFriendsById(otherId);
        List<User> commonFriends = new ArrayList<>();
        if (firstUserFriends.size() > secondUserFriends.size()) {
            for (User user : firstUserFriends) {
                if (secondUserFriends.contains(user)) commonFriends.add(user);
            }
        } else {
            for (User user : secondUserFriends) {
                if (firstUserFriends.contains(user)) commonFriends.add(user);
            }
        }
        return commonFriends;
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
}
