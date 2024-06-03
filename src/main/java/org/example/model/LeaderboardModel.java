package org.example.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class LeaderboardModel implements Serializable {
    private Long gameId;
    private Long userId;
    // can be fetched from database
    private String username;
    private double score;

    public LeaderboardModel() {}

    public LeaderboardModel(Long gameId, Long userId, String username, double score) {
        this.gameId = gameId;
        this.userId = userId;
        this.username = username;
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeaderboardModel that = (LeaderboardModel) o;
        return Double.compare(that.score, score) == 0 &&
                Objects.equals(gameId, that.gameId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(username, that.username);
    }
}
