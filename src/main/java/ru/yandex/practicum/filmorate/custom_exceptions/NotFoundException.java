package ru.yandex.practicum.filmorate.custom_exceptions;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final String parameter;

    public NotFoundException(String parameter) {
        super("не найден");
        this.parameter = parameter;
    }
}
