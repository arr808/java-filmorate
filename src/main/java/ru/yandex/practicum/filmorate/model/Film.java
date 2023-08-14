package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @NotNull
    @Past
    private LocalDate releaseDate;
    @Positive
    private long duration;
    private Mpa mpa;
    private List<Genre> genres = new ArrayList<>();
    @JsonIgnore
    private final Set<Integer> likes = new HashSet<>();

    @JsonIgnore
    public int getLikeCount() {
        return likes.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return duration == film.duration &&
                Objects.equals(name, film.name) &&
                Objects.equals(description, film.description) &&
                Objects.equals(releaseDate, film.releaseDate) &&
                mpa == film.mpa && Objects.equals(genres, film.genres)
                && Objects.equals(likes, film.likes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, releaseDate, duration, mpa, genres, likes);
    }
}
