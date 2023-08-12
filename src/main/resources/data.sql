--Добавление статуса дружбы:
MERGE INTO friendship_status (friendship_status_id, status_name) VALUES ('0', 'SEND');
MERGE INTO friendship_status (friendship_status_id, status_name) VALUES ('1', 'ACCEPTED');
--Добавление рейтинга:
MERGE INTO mpa (mpa_id, mpa_name, mpa_description) VALUES (1,'G', 'У фильма нет возрастных ограничений');
MERGE INTO mpa (mpa_id, mpa_name, mpa_description) VALUES (2, 'PG', 'Детям рекомендуется смотреть фильм с родителями');
MERGE INTO mpa (mpa_id, mpa_name, mpa_description) VALUES (3, 'PG-13', 'Детям до 13 лет просмотр не желателен');
MERGE INTO mpa (mpa_id, mpa_name, mpa_description) VALUES (4, 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого');
MERGE INTO mpa (mpa_id, mpa_name, mpa_description) VALUES (5, 'NC-17', 'Лицам до 18 лет просмотр запрещён');