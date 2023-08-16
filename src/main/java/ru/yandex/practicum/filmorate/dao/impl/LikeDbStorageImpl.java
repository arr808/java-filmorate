package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.LikeDbStorage;

@Component
@Qualifier("LikeDbStorageImpl")
@Slf4j
public class LikeDbStorageImpl implements LikeDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikeDbStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(int userId, int filmId) {
        String sql = "MERGE INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("Фильму {} оставлен лайк от пользователя {}", filmId, userId);
    }

    @Override
    public void removeLike(int userId, int filmId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
        log.info("У фильма {} удален лайк от пользователя {}", filmId, userId);
    }
}
