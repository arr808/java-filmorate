package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.custom_exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.dao.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmGenreDbStorageImpl implements FilmGenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFilmGenre(int filmId, int genreId) {
        String sql = "MERGE INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
        log.info("Фильму id {} добавлен жанр id {}", filmId, genreId);
    }

    @Override
    public void deleteFilmGenre(int filmId, int genreId) {
        String sql = "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?";
        jdbcTemplate.update(sql, filmId, genreId);
        log.info("У фильма id {} удален жанр id {}", filmId, genreId);
    }

    @Override
    public void deleteAllFilmGenre(int filmId) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
        log.info("У фильма id {} удалены все жанры", filmId);
    }

    @Override
    public List<FilmGenre> getFilmGenresById(int id) {
        String sql = "SELECT * FROM film_genre WHERE film_id = ?";
        try {
            List<FilmGenre> filmGenres = jdbcTemplate.query(sql, new FilmToGenreRowMapper(), id);
            log.info("Отправлен список жанров {}", filmGenres);
            return filmGenres;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("filmGenres");
        }
    }

    class FilmToGenreRowMapper implements RowMapper<FilmGenre> {
        @Override
        public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
            FilmGenre filmToGenre = new FilmGenre();

            filmToGenre.setFilmId(rs.getInt("film_id"));

            try {
                Genre genre;
                int genreId = rs.getInt("genre_id");
                genre = Genre.forValues(genreId);
                filmToGenre.setGenre(genre);
            } catch (Exception e) {
                throw new NotFoundException("genre id");
            }

            return filmToGenre;
        }
    }
}
