package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.Collection;

public interface FriendshipStorage {

    void addFriend(int userId, int friendId, FriendshipStatus friendshipStatus);

    void updateFriend(int userId, int friendId, FriendshipStatus friendshipStatus);

    void deleteFriend(int userId, int friendId);

    Friendship getFriendshipById(int userId, int friendId);
}
