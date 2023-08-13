package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Mpa {

    G(1, "G", "У фильма нет возрастных ограничений"),
    PG(2, "PG", "Детям рекомендуется смотреть фильм с родителями"),
    PG13(3, "PG-13", "Детям до 13 лет просмотр не желателен"),
    R(4, "R", "Лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
    NC17(5, "NC-17", "Лицам до 18 лет просмотр запрещён");

    private final int id;
    private final String name;
    private final String description;

    @JsonCreator
    public static Mpa forValues(@JsonProperty("id") int id) {
        for (Mpa mpa : Mpa.values()) {
            if (mpa.id == id) {
                return mpa;
            }
        }
        return null;
    }
}
