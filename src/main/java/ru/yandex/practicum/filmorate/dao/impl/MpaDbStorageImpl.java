package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.custom_exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;

@Component
@Qualifier("MpaDbStorageImpl")
@RequiredArgsConstructor
@Slf4j
public class MpaDbStorageImpl implements MpaDbStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAll() {
        String sql = "SELECT * FROM mpa";
        List<Mpa> mpas = jdbcTemplate.query(sql, new MpaRowMapper());
        log.info("Отправлен список рейтингов {}", mpas);
        return mpas;
    }

    @Override
    public Mpa getById(int id) {
        String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(sql,
                    new Object[] { id },
                    new int[]{Types.INTEGER},
                    new MpaRowMapper());
            log.info("Отправлен рейтинг {}", mpa);
            return mpa;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("mpa");
        }
    }

    static class MpaRowMapper implements RowMapper<Mpa> {
        @Override
        public Mpa mapRow(ResultSet rs, int rowNum) {
            try {
                Mpa mpa;
                int mpaId = rs.getInt("mpa_id");
                mpa = Mpa.forValues(mpaId);
                return mpa;
            } catch (Exception e) {
                throw new NotFoundException("mpa id");
            }
        }
    }
}
