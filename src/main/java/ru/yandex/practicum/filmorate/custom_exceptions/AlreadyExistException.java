package ru.yandex.practicum.filmorate.custom_exceptions;

import lombok.Getter;

@Getter
public class AlreadyExistException extends RuntimeException {

    private final String parameter;

    public AlreadyExistException(String parameter) {
        super("уже существует");
        this.parameter = parameter;
    }
}
