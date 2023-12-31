package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.custom_exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.dao.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Component
@Qualifier("FriendshipDbStorageImpl")
@RequiredArgsConstructor
@Slf4j
public class FriendshipDbStorageImpl implements FriendshipDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(int userId, int friendId, FriendshipStatus friendshipStatus) {
        String sql = "MERGE INTO friends (user_id, friend_id, friendship_status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, friendshipStatus.getTitle());
        log.info("Пользователю id {} добавлен друг id {}", userId, friendId);
    }

    @Override
    public void updateFriend(int userId, int friendId, FriendshipStatus friendshipStatus) {
        String firstSql = "UPDATE friends SET friendship_status_id = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(firstSql, friendshipStatus.getTitle(), userId, friendId);
        log.info("Пользователю id {} добавлен друг id {}", userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
        log.info("У пользователя id {} удален друг id {}", userId, friendId);
    }

    @Override
    public Friendship getFriendshipById(int userId, int friendId) {
        try {
            String sql = "SELECT * FROM friends WHERE user_id = ? AND friend_id = ?";
            Friendship friendship = jdbcTemplate.queryForObject(sql,
                    new Object[] { userId, friendId },
                    new int[]{Types.INTEGER, Types.INTEGER},
                    new FriendshipRowMapper());
            log.info("Отправлена дружба {}", friendship);
            return friendship;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    class FriendshipRowMapper implements RowMapper<Friendship> {
        @Override
        public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
            Friendship friendship = new Friendship();

            friendship.setUserId(rs.getInt("user_id"));
            friendship.setFriendId(rs.getInt("friend_id"));

            try {
                int status = rs.getInt("friendship_status_id");
                FriendshipStatus[] friendshipStatuses = FriendshipStatus.values();
                friendship.setFriendshipStatus(friendshipStatuses[status]);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NotFoundException("friendship status id");
            }
            return friendship;
        }
    }
}
