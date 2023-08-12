package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;

import java.util.Collection;

public interface FriendshipDbStorage extends FriendshipStorage {

    void addFriend(int userId, int friendId, FriendshipStatus friendshipStatus);

    void updateFriend(int userId, int friendId, FriendshipStatus friendshipStatus);

    void deleteFriend(int userId, int friendId);

    Collection<Integer> getFriendsIdById(int userId);

    Friendship getFriendshipById(int userId, int friendId);
}
