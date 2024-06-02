package org.example.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LeaderboardModel implements Serializable {
    private String gameId;
    private String userId;
    private double score;
    private long rank;
}
