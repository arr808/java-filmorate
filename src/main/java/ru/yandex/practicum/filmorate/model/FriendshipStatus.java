package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendshipStatus {

    SEND (0),
    ACCEPTED (1);

    private final int title;
}
