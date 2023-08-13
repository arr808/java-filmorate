package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.LikeDbStorage;
import ru.yandex.practicum.filmorate.model.Like;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

    @Override
    public List<Integer> getUserLiked(int filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Integer> userLiked = jdbcTemplate.queryForList(sql, Integer.class, filmId);
        log.info("Отправлены пользователи поставившие лайк фильму id {}", filmId);
        return userLiked;
    }

    static class LikeRowMapper implements RowMapper<Like> {
        @Override
        public Like mapRow(ResultSet rs, int rowNum) throws SQLException {
            Like like = new Like();

            like.setFilmId(rs.getInt("film_id"));
            like.setLikeCount(rs.getInt("like_count"));

            return like;
        }
    }
}
