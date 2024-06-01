package org.example.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaderboardModel {
    public String id;
    public String gameId;
    public String userId;
    public int score;
    public int rank;

}
