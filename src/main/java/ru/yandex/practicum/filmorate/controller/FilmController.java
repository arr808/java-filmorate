package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import ru.yandex.practicum.filmorate.custom_exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private int id = 0;
    private Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> findAll() {
        log.info("Отправлен список всех фильмов - {}", films);
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validation(film);
        film.setId(getNewId());
        films.put(film.getId(), film);
        log.debug("Добавлен фильм - {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validation(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Обновлен фильм - {}", film);
        } else {
            log.warn("Фильм не найден - {} ", film);
            throw new ValidationException(String.format("Фильм %s не найден", film));
        }
        return film;
    }

    @DeleteMapping
    public void clean() {
        films.clear();
        id = 0;
        log.info("Все фильмы удалены");
    }

    private int getNewId() {
        return ++id;
    }

    private void validation(Film film) {
        if (films.containsValue(film)) {
            log.warn("Данный фильм уже добавлен - {} ", film);
            throw new ValidationException("Данный фильм уже добавлен");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза раньше 28 декабря 1895 года - {} ", film);
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
