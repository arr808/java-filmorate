package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

public interface FilmGenreStorage {

    void addFilmGenre(int filmId, int genreId);

    void deleteFilmGenre(int filmId, int genreId);

    void deleteAllFilmGenre(int filmId);

    List<FilmGenre> getFilmGenresById(int id);
}
