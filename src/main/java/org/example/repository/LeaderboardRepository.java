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
        try {
            this.redisTemplate.opsForZSet().add("leaderboard", inputData, inputData.getScore());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<LeaderboardModel> getAll(Constants order) {
        Set<LeaderboardModel> leaderboardModelSet = null;
        try {
            if (order == Constants.RANGE) { // ascending order
                leaderboardModelSet = this.redisTemplate.opsForZSet().range("leaderboard", 0, -1);
            }
            else { // descending order
                leaderboardModelSet = this.redisTemplate.opsForZSet().reverseRange("leaderboard", 0, -1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return leaderboardModelSet;
    }

    public Optional<LeaderboardModel> peekSet(Constants order) {
        Optional<LeaderboardModel> element = null;
        try {
            if (order == Constants.RANGE) { // get max element
                element = this.redisTemplate.opsForZSet().reverseRange("leaderboard", 0, 1).stream().findFirst();
            }
            else { // get min element
                element = this.redisTemplate.opsForZSet().range("leaderboard", 0, 1).stream().findFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return element;
    }

    public Long getCount() {
        Long setCount = null;

        try {
            setCount = this.redisTemplate.opsForZSet().count("leaderboard", Integer.MIN_VALUE, Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return setCount;
    }

    public void popSet() {
        Optional<LeaderboardModel> element = null;
        try {
            element.ifPresent(leaderboardModel -> this.redisTemplate.opsForZSet().remove("leaderboard", element));
            //TODO: do logging
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}