# java-filmorate
Template repository for Filmorate project.

# Схема базы данных
![Схема БД](/src/main/resources/Filmorate.png)

Пример запросов:
    1. Получение всех пользователей:
        SELECT user_id,
               email,
               login,
               name,
               birtday
        FROM users;            
    2. Список общих друзей пользователя (id = n1) с другим пользователем (id = n2) cо статусом "дружба":
        SELECT u.user_id,
               u.email,
               u.login,
               u.name,
               u.birtday
        FROM users AS u
        INNER JOIN friends AS f ON u.user_id = f.friend_id
        WHERE f.user_id IN (n1, n2)
        AND friendship_status_id = 1 -- проверка статуса дружбы
        GROUP BY u.user_id
        HAVING COUNT(f.user_id) = 2 -- наличие пользователей в обоих списках друзей
        ORDER BY u.user_id;
    3. Получение всех фильмов:
        SELECT f.film_id,
               f.name,
               f.description,
               f.release_date,
               f.duration,
               g.genre_id genre,
               fr.rating_name rating
               COUNT(l.film_id) likes
        FROM films AS f
        LEFT JOIN genre AS g ON f.genre_id = g.genre_id
        LEFT JOIN film_rating AS fr ON f.rating_id = fr.raiting_id
        LEFT JOIN likes AS l ON f.film_id = l.film_id
        GROUP BY f.film_id;
    4. Топ N наиболее популярных фильмов:
        SELECT f.film_id,
               f.name,
               f.description,
               f.release_date,
               f.duration,
               g.genre_id genre,
               fr.rating_name rating
               COUNT(l.film_id) likes
        FROM films AS f
        LEFT JOIN genre AS g ON f.genre_id = g.genre_id
        LEFT JOIN film_rating AS fr ON f.rating_id = fr.raiting_id
        LEFT JOIN likes AS l ON f.film_id = l.film_id
        GROUP BY f.film_id
        ORDER BY likes DESC
        LIMIT N;
