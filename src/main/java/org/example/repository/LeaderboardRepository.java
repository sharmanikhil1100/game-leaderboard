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

    @Value("${app_config.leaderboard_key}")
    private String leaderboardKey;

    public Boolean addEntity(LeaderboardModel inputData) {
        return this.redisTemplate.opsForZSet().add(leaderboardKey, inputData, inputData.getScore());
    }

    public Set<LeaderboardModel> getAll(Constants order) {
        Set<LeaderboardModel> leaderboardModelSet = null;
        if (order == Constants.RANGE) { // ascending order
            leaderboardModelSet = this.redisTemplate.opsForZSet().range(leaderboardKey, 0, -1);
        }
        else { // descending order
            leaderboardModelSet = this.redisTemplate.opsForZSet().reverseRange(leaderboardKey, 0, -1);
        }

        return leaderboardModelSet;
    }

    public Optional<LeaderboardModel> peekSet(Constants order) {
        Optional<LeaderboardModel> element = null;
        if (order == Constants.RANGE) { // get max element
            element = this.redisTemplate.opsForZSet().reverseRange(leaderboardKey, 0, 1).stream().findFirst();
        }
        else { // get min element
            element = this.redisTemplate.opsForZSet().range(leaderboardKey, 0, 1).stream().findFirst();
        }

        return element;
    }

    public Long getCount() {
        return this.redisTemplate.opsForZSet().count(leaderboardKey, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public void popSet() {
        Optional<LeaderboardModel> element = this.peekSet(Constants.REVERSE_RANGE);

        if (element.isPresent()) {
            this.redisTemplate.opsForZSet().remove(leaderboardKey, element.get());
        }
    }
}