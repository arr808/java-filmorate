package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.custom_exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;

@Component
@Qualifier("GenreDbStorageImpl")
@RequiredArgsConstructor
@Slf4j
public class GenreDbStorageImpl implements GenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT * FROM genre";
        List<Genre> genres = jdbcTemplate.query(sql, new GenreRowMapper());
        log.info("Отправлен список жанров {}", genres);
        return genres;
    }

    @Override
    public Genre getById(int id) {
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        try {
            Genre genre = jdbcTemplate.queryForObject(sql,
                    new Object[] { id },
                    new int[]{Types.INTEGER},
                    new GenreRowMapper());
            log.info("Отправлен жанр {}", genre);
            return genre;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("genre");
        }
    }

    class GenreRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) {
            try {
                Genre genre;
                int genreId = rs.getInt("genre_id");
                genre = Genre.forValues(genreId);
                return genre;
            } catch (Exception e) {
                throw new NotFoundException("genre id");
            }
        }
    }
}
