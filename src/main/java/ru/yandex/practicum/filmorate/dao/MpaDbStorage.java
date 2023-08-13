package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

public interface MpaDbStorage extends MpaStorage {

    List<Mpa> getAll();

    Mpa getById(int id);
}
