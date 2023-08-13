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
}
