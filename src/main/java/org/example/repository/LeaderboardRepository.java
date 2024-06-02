package org.example.repository;

import org.example.config.Constants;
import org.example.model.LeaderboardModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public class LeaderboardRepository {
    @Autowired
    private RedisTemplate<String, LeaderboardModel> redisTemplate;

    @Value("leaderboard_key")
    private String leaderboardKey;

    public void addEntity(LeaderboardModel inputData) {
        this.redisTemplate.opsForZSet().add("leaderboard", inputData, inputData.getScore());
    }

    public Set<LeaderboardModel> getAll(Constants order) {
        Set<LeaderboardModel> leaderboardModelSet = null;
        if (order == Constants.RANGE) { // ascending order
            leaderboardModelSet = this.redisTemplate.opsForZSet().range("leaderboard", 0, -1);
        }
        else { // descending order
            leaderboardModelSet = this.redisTemplate.opsForZSet().reverseRange("leaderboard", 0, -1);
        }

        return leaderboardModelSet;
    }

    public Optional<LeaderboardModel> peekSet(Constants order) {
        Optional<LeaderboardModel> element = null;
        if (order == Constants.RANGE) { // get max element
            element = this.redisTemplate.opsForZSet().reverseRange("leaderboard", 0, 1).stream().findFirst();
        }
        else { // get min element
            element = this.redisTemplate.opsForZSet().range("leaderboard", 0, 1).stream().findFirst();
        }

        return element;
    }

    public Long getCount() {
        return this.redisTemplate.opsForZSet().count("leaderboard", Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public void popSet() {
        Optional<LeaderboardModel> element = this.peekSet(Constants.REVERSE_RANGE);

        if (element.isPresent()) {
            this.redisTemplate.opsForZSet().remove("leaderboard", element);
        }
    }
}