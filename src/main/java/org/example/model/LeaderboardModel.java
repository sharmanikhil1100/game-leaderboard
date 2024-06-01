package org.example.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaderboardModel {
    private String id;
    private String gameId;
    private String userId;
    private int score;
    private int rank;
}
