package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

public interface LikeDbStorage extends LikeStorage {

    void addLike(int userId, int filmId);

    void removeLike(int userId, int filmId);
}
