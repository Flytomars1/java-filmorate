# java-filmorate
Template repository for Filmorate project.
## Схема базы данных
[Интерактивная схема БД Filmorate на dbdiagram.io](https://dbdiagram.io/d/Filmorate-68cbd2f15779bb72650fe4af)

## Примеры запросов

### Получить всех пользователей
```sql
SELECT user_id, 
    email, 
    login,name, 
    birthday
FROM users;
```
### Получить все фильмы с рейтингом
```sql
SELECT f.name, f.description, f.release_date, f.duration, r.name AS rating
FROM film f
INNER JOIN mpa_rating r ON f.rating_id = r.rating_id;
```

### Топ-10 самых популярных фильмов
```sql
SELECT 
    f.name, 
    COUNT(fl.user_id) AS like_count
FROM film f
LEFT JOIN film_like fl ON f.film_id = fl.film_id
GROUP BY f.film_id
ORDER BY like_count DESC
LIMIT 10;
```

### Получить список общих друзей между пользователями (user_id = 1 и user_id = 2)
```sql
SELECT 
    f1.friend_id AS common_friend_id, 
    u.name AS friend_name
FROM friendship f1
INNER JOIN friendship f2 ON f1.friend_id = f2.friend_id
INNER JOIN users u ON u.user_id = f1.friend_id
WHERE f1.user_id = 1
  AND f2.user_id = 2
  AND f1.status = true
  AND f2.status = true;