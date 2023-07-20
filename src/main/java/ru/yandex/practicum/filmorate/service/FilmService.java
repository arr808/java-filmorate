package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.custom_exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@Service
public class FilmService {

    private int id;
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film add(Film film) {
        validate(film);
        film.setId(getNewId());
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void deleteAll() {
        filmStorage.deleteAll();
        id = 0;
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(int id) {
        Film film = filmStorage.getById(id);
        if (film == null) throw new ValidationException("Фильм не найден");
        return film;
    }

    public void addLike(int filmId, int userId) {
        Film film = getById(filmId);
        userService.getById(userId); //проверяем что пользователь существует
        film.getLikes().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = getById(filmId);
        userService.getById(userId); //проверяем что пользователь существует
        film.getLikes().remove(userId);
    }

    public List<Film> getPopular(int size) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt(film -> film.getLikes().size()))
                .limit(size)
                .collect(Collectors.toList());
    }

    private int getNewId() {
        return ++id;
    }

    private void validate(Film film) {
        if (filmStorage.getById(film.getId()) != null) {
            throw new ValidationException(String.format("Фильм %s уже добавлен", film));
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
