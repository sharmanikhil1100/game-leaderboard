package org.example.repository;

import org.example.config.Constants;
import org.example.model.LeaderboardModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public class LeaderboardRepository {
    @Autowired
    private RedisTemplate<String, LeaderboardModel> redisTemplate;

    @Value("${app_config.redis_leaderboard_key}")
    private String leaderboardKey;

    public Boolean addEntity(LeaderboardModel inputData) {
        return redisTemplate.opsForZSet().add(leaderboardKey, inputData, inputData.getScore());
    }

    public Set<LeaderboardModel> getAll(Constants order) {
        if (order == Constants.RANGE) { // ascending order
            return redisTemplate.opsForZSet().range(leaderboardKey, 0, -1);
        }
        else { // descending order
            return redisTemplate.opsForZSet().reverseRange(leaderboardKey, 0, -1);
        }
    }

    public Optional<LeaderboardModel> peekSet(Constants order) {
        if (order == Constants.RANGE) { // get max element
            return redisTemplate.opsForZSet().reverseRange(leaderboardKey, 0, 0).stream().findFirst();
        }
        else { // get min element
            return redisTemplate.opsForZSet().range(leaderboardKey, 0, 0).stream().findFirst();
        }
    }

    public Long getLeaderboardSize() {
        return redisTemplate.opsForZSet().count(leaderboardKey, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public void popSet() {
        redisTemplate.opsForZSet().removeRange(leaderboardKey, 0, 0);
    }
}