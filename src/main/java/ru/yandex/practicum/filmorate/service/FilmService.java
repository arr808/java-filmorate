package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.custom_exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.custom_exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.custom_exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@Service
@Slf4j
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
        validateReleaseDate(film);
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
        if (film == null) {
            log.warn("Не найден фильм с id - {}", id);
            throw new NotFoundException("film");
        }
        return film;
    }

    public void addLike(int filmId, int userId) {
        Film film = getById(filmId);
        userService.getById(userId); //проверяем что пользователь существует
        film.getLikes().add(userId);
        log.debug("Поставлен лайк фильму - {}", film);
    }

    public void removeLike(int filmId, int userId) {
        Film film = getById(filmId);
        userService.getById(userId); //проверяем что пользователь существует
        film.getLikes().remove(userId);
        log.debug("Удален лайк у фильма - {}", film);
    }

    public List<Film> getPopular(int size) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparing(Film::getLikeCount).reversed())
                .limit(size)
                .collect(Collectors.toList());
    }

    private int getNewId() {
        return ++id;
    }

    private void validate(Film film) {
        if (filmStorage.getById(film.getId()) != null) {
            log.warn("Фильм {} уже добавлен", film);
            throw new AlreadyExistException("film");
        }
        validateReleaseDate(film);
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Не корректная дата релиза фильма - {}", film);
                throw new ValidationException("film", "Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
