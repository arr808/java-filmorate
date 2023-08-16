package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.custom_exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;


@Component
@AllArgsConstructor
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films;

    @Override
    public Film add(Film film) {
        films.put(film.getId(), film);
        log.debug("Добавлен фильм - {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        int id = film.getId();
        if (films.containsKey(id)) {
            films.put(id, film);
            log.debug("Обновлен фильм - {}", film);
        } else {
            log.warn("Не найден фильм - {}", film);
            throw new NotFoundException("film");
        }
        return film;
    }

    @Override
    public List<Film> getAll() {
        log.trace("Отправлен список всех фильмов - {}", films);
        return new ArrayList<>(films.values());
    }

    @Override
    public void deleteAll() {
        log.debug("Все фильмы удалены");
        films.clear();
    }

    @Override
    public Film getById(int id) {
        log.trace("Отправлен фильм - {}", films.get(id));
        return films.get(id);
    }

    @Override
    public List<Film> getPopular(int size) {
        return getAll().stream()
                .sorted(Comparator.comparing(Film::getLikeCount).reversed())
                .limit(size)
                .collect(Collectors.toList());
    }
}
