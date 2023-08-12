package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

public interface FilmDbStorage extends FilmStorage {

    Film add(Film film);

    Film update(Film film);

    List<Film> getAll();

    void deleteAll();

    Film getById(int id);
}
