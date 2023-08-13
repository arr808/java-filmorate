package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.custom_exceptions.FriendshipException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;

import java.util.Collection;

@Service
@Slf4j
public class FriendshipService {

    private final FriendshipStorage friendshipStorage;

    public FriendshipService(@Qualifier("FriendshipDbStorageImpl") FriendshipStorage friendshipStorage) {
        this.friendshipStorage = friendshipStorage;
    }

    public void addFriend(int userId, int friendId) {
        validateUserId(userId, friendId);
        friendshipStorage.addFriend(userId, friendId, FriendshipStatus.SEND);
    }

    public void acceptFriend(int userId, int friendId) {
        validateUserId(userId, friendId);

        if (validateFriendshipExist(userId, friendId)) {
            friendshipStorage.updateFriend(userId, friendId, FriendshipStatus.ACCEPTED);
            log.info("Дружба между id {} и id {} подтверждена", userId, friendId);
        } else {
            friendshipStorage.addFriend(userId, friendId, FriendshipStatus.ACCEPTED);
        }

        if (validateFriendshipExist(friendId, userId)) {
            friendshipStorage.updateFriend(friendId, userId, FriendshipStatus.ACCEPTED);
            log.info("Дружба между id {} и id {} подтверждена", friendId, userId);
        } else {
            friendshipStorage.addFriend(userId, friendId, FriendshipStatus.ACCEPTED);
        }
    }

    public void deleteFriend(int userId, int friendId) {
        validateUserId(userId, friendId);

        if (validateFriendshipExist(userId, friendId)) {
            friendshipStorage.deleteFriend(userId, friendId);
            log.info("У пользователя id {} удален друг id {}", userId, friendId);
        }

        if (validateFriendshipExist(friendId, userId)) {
            friendshipStorage.updateFriend(friendId, userId, FriendshipStatus.SEND);
            log.info("У пользователя id {} изменен статус дружбы", friendId);
        }
    }

    public Collection<Integer> getFriendsIdById(int userId) {
        Collection<Integer> friendsId = friendshipStorage.getFriendsIdById(userId);
        log.info("Отправлен список id {} всех друзей пользователя с id {}", friendsId, userId);
        return friendsId;
    }

    public Friendship getFriendshipById(int userId, int friendId) {
        Friendship friendship = friendshipStorage.getFriendshipById(userId, friendId);
        log.info("Отправлена дружба {}", friendship);
        return friendship;
    }

    private void validateUserId(int userId, int friendId) {
        if (userId == friendId) {
            throw new FriendshipException("Пользователь не может дружить сам с собой");
        }
    }

    private boolean validateFriendshipExist(int userId, int friendId) {
        Friendship localFriendship = getFriendshipById(userId, friendId);
        return localFriendship != null;
    }
}
