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
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmGenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("FilmDbStorageImpl")
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorageImpl implements FilmDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreStorage filmGenreStorage;

    @Override
    public Film add(Film film) {
        if (getAll().contains(film)) throw new AlreadyExistException("film");

        SimpleJdbcInsert simpleInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Map<String, String> message = new HashMap<>();
        message.put("name", film.getName());
        message.put("description", film.getDescription());
        message.put("release_date", String.valueOf(film.getReleaseDate()));
        message.put("duration", String.valueOf(film.getDuration()));
        message.put("mpa_id", String.valueOf(film.getMpa().getId()));

        int id = simpleInsert.executeAndReturnKey(message).intValue();
        film.setId(id);

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
        log.info("Отправлен список фильмов - {}", films);
        return films;
    }

    @Override
    public void deleteAll() {
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

    @Override
    public List<Film> getPopular(int size) {
        String sql = "SELECT f.film_id, " +
                            "f.name, " +
                            "f.description, " +
                            "f.release_date, " +
                            "f.duration, " +
                            "f.mpa_id, " +
                            "COUNT(l.film_id) likes " +
                     "FROM films AS f " +
                     "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                     "GROUP BY f.film_id " +
                     "ORDER BY likes DESC " +
                     "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), size);
        log.info("Отправлен список популярных фильмов - {}", films);
        return films;
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

            return film;
        }
    }
}
