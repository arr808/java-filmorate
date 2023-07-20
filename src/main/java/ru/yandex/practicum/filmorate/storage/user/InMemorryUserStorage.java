package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.custom_exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class InMemorryUserStorage implements UserStorage {

    private final Map<Integer, User> users;

    @Override
    public User add(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        int id = user.getId();
        if (users.containsKey(id)) {
            users.put(id, user);
        } else throw new ValidationException(String.format("Пользователь %s не найден", user));
        return user;
    }

    @Override
    public void deleteAll() {
        users.clear();
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(int id) {
        return users.get(id);
    }
}
