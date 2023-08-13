package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

public interface GenreDbStorage extends GenreStorage {

    List<Genre> getAll();

    Genre getById(int id);
}
