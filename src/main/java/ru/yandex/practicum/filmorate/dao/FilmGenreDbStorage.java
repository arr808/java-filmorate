package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.film.FilmGenreStorage;

import java.util.List;

public interface FilmGenreDbStorage extends FilmGenreStorage {

    void addFilmGenre(int filmId, int genreId);

    void deleteFilmGenre(int filmId, int genreId);

    void deleteAllFilmGenre(int filmId);

    List<FilmGenre> getFilmGenresById(int id);
}
