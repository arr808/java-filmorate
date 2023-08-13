package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.util.List;

public interface LikeDbStorage extends LikeStorage {

    void addLike(int userId, int filmId);

    void removeLike(int userId, int filmId);

    List<Integer> getUserLiked(int filmId);
}
