package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


import ru.yandex.practicum.filmorate.custom_exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;


@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id,
                        @PathVariable int userId) {
        if (id <= 0) throw new IncorrectParameterException("id: " + id);
        if (userId <= 0) throw new IncorrectParameterException("userId: " + userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping
    public void  clean() {
        filmService.deleteAll();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id,
                           @PathVariable int userId) {
        if (id <= 0) throw new IncorrectParameterException("id: " + id);
        if (userId <= 0) throw new IncorrectParameterException("userId: " + userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable int id) {
        if (id <= 0) throw new IncorrectParameterException("id: " + id);
        return filmService.getById(id);
    }

    @GetMapping("/popular")
    public List<Film> findPopular(@RequestParam(defaultValue = "10") int count) {
        if (count <= 0) throw new IncorrectParameterException("count: " + count);
        return filmService.getPopular(count);
    }
}
