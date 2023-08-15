package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.custom_exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.custom_exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("UserDbStorageImpl")
@RequiredArgsConstructor
@Slf4j
public class UserDbStorageImpl implements UserDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        if (getAll().contains(user)) throw new AlreadyExistException("user");

        SimpleJdbcInsert simpleInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        Map<String, String> message = new HashMap<>();
        message.put("email", user.getEmail());
        message.put("login", user.getLogin());
        message.put("name", user.getName());
        message.put("birthday", String.valueOf(user.getBirthday()));

        int id = simpleInsert.executeAndReturnKey(message).intValue();
        user.setId(id);

        log.info("Добавлен пользователь - {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        int id = user.getId();
        getById(id);
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), id);
        log.info("Обновлены данные для пользователя - {}", user);
        return user;
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM users";
        jdbcTemplate.update(sql);
        log.info("Все пользователи удалены");
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper());
        log.info("Отправлен список пользователей - {}", users);
        return users;
    }

    @Override
    public User getById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql,
                    new Object[] { id },
                    new int[]{Types.INTEGER}, new UserRowMapper());

            log.info("Найден пользователь {}", user);
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("user");
        }
    }

    @Override
    public List<User> getFriendsById(int id) {
        String sql = "SELECT u.id, " +
                            "u.email, " +
                            "u.login, " +
                            "u.name, " +
                            "u.birthday " +
                     "FROM users AS u " +
                     "INNER JOIN friends AS f ON u.id = f.friend_id " +
                     "WHERE f.user_id = ?";
        List<User> friends = jdbcTemplate.query(sql, new UserRowMapper(), id);
        log.info("Отправлен список друзей - {}", friends);
        return friends;
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        String sql = "SELECT u.id, " +
                            "u.email, " +
                            "u.login, " +
                            "u.name, " +
                            "u.birthday " +
                     "FROM users AS u " +
                     "INNER JOIN friends AS f ON u.id = f.friend_id " +
                     "WHERE f.user_id IN (?, ?) " +
                     "GROUP BY u.id " +
                     "HAVING COUNT (f.user_id) = 2 " +
                     "ORDER BY u.id";
        List<User> commonFriends = jdbcTemplate.query(sql, new UserRowMapper(), id, otherId);
        log.info("Отправлен список общих друзей - {}", commonFriends);
        return commonFriends;
    }

    class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();

            user.setId(rs.getInt("id"));
            user.setLogin(rs.getString("login"));
            user.setEmail(rs.getString("email"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());

            return user;
        }
    }
}
