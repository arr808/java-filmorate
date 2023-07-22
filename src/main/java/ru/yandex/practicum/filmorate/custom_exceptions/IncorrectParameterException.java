package ru.yandex.practicum.filmorate.custom_exceptions;

import lombok.Getter;

@Getter
public class IncorrectParameterException extends RuntimeException {

    private final String parameter;

    public IncorrectParameterException(String parameter) {
        super("не может быть отрицательным");
        this.parameter = parameter;
    }
}
