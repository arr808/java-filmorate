package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.yandex.practicum.filmorate.custom_exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


@Component
@AllArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films;

    @Override
    public Film add(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        int id = film.getId();
        if (films.containsKey(id)) {
            films.put(id, film);
        } else throw new ValidationException(String.format("Фильм %s не найде", film));
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void deleteAll() {
        films.clear();
    }

    @Override
    public Film getById(int id) {
        return films.get(id);
    }
}
