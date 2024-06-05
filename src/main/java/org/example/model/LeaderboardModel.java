package org.example.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
public class LeaderboardModel implements Serializable {
    private Long gameId;
    private String createdAt;
    private Long userId;
    private String username;
    private double score;

    public LeaderboardModel() {}

    public LeaderboardModel(Long gameId, Long userId, String username, double score) {
        this.gameId = gameId;
        this.userId = userId;
        this.username = username;
        this.score = score;
    }

    public LeaderboardModel(Long gameId, Long userId, String username, double score, String createdAt) {
        this.gameId = gameId;
        this.createdAt = createdAt;
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

    public String currentDate() {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date());
    }
}
