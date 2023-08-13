package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.custom_exceptions.NotFoundException;

import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.List;

@Component
@Qualifier("FilmDbStorageImpl")
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorageImpl implements FilmDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreStorage filmGenreStorage;
    private final LikeStorage likeStorage;

    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO films (film_id, name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        loadFilmGenres(film);
        log.info("Добавлен фильм - {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        int id = film.getId();
        getById(id);
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?" +
                     "WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                id);
        loadFilmGenres(film);
        log.info("Обновлены данные фильма - {}", film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper());
        log.info("Отправлен список пользователей - {}", films);
        return films;
    }

    @Override
    public void deleteAll(){
        String sql = "DELETE FROM films";
        jdbcTemplate.update(sql);
        log.info("Все фильмы удалены");
    }

    @Override
    public Film getById(int id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql,
                    new Object[] { id },
                    new int[]{Types.INTEGER},
                    new FilmRowMapper());

            log.info("Найден фильм {}", film);

            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("film");
        }
    }

    private void loadFilmGenres(Film film) {
        filmGenreStorage.deleteAllFilmGenre(film.getId());
        List<Genre> genres = film.getGenres();
        if (genres.size() != 0) {
            for (Genre genre : genres) {
                filmGenreStorage.addFilmGenre(film.getId(), genre.getId());
            }
        }
        setFilmGenres(film);
    }

    private void setFilmGenres(Film film) {
        film.getGenres().clear();
        List<FilmGenre> filmGenres = filmGenreStorage.getFilmGenresById(film.getId());
        for (FilmGenre filmGenre : filmGenres) {
            film.getGenres().add(filmGenre.getGenre());
        }
    }

    class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();

            film.setId(rs.getInt("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getLong("duration"));

            try {
                int mpaId = rs.getInt("mpa_id");
                film.setMpa(Mpa.forValues(mpaId));
            } catch (Exception e) {
                throw new NotFoundException("mpa id");
            }
            setFilmGenres(film);
            film.setLikes(new HashSet<>(likeStorage.getUserLiked(film.getId())));

            return film;
        }
    }
}
