package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendshipRepository {

    private final JdbcTemplate jdbc;

    private static final String ADD_FRIEND =
            "MERGE INTO friendship (user_id, friend_id) VALUES (?, ?)";

    private static final String REMOVE_FRIEND =
            "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";

    private static final String FIND_FRIENDS_BY_USER =
            "SELECT friend_id FROM friendship WHERE user_id = ?";

    private static final String FIND_COMMON_FRIENDS =
            "SELECT f1.friend_id " +
                    "FROM friendship f1 " +
                    "JOIN friendship f2 ON f1.friend_id = f2.friend_id " +
                    "WHERE f1.user_id = ? AND f2.user_id = ?";

    public void addFriend(Long userId, Long friendId) {
        jdbc.update(ADD_FRIEND, userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        jdbc.update(REMOVE_FRIEND, userId, friendId);
    }

    public List<Long> findFriendIds(Long userId) {
        return jdbc.queryForList(FIND_FRIENDS_BY_USER, Long.class, userId);
    }

    public List<Long> findCommonFriendIds(Long userId, Long otherId) {
        return jdbc.queryForList(FIND_COMMON_FRIENDS, Long.class, userId, otherId);
    }

    public boolean exists(Long userId, Long friendId) {
        String sql = "SELECT COUNT(*) FROM friendship WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbc.queryForObject(sql, Integer.class, userId, friendId);
        return count != null && count > 0;
    }
}