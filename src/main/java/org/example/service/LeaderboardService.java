package org.example.service;

import org.example.config.Constants;
import org.example.model.LeaderboardModel;
import org.example.repository.LeaderboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class LeaderboardService {
    @Autowired
    private LeaderboardRepository leaderboardRepository;

    private final long leaderboardMaxCount = 5;

    public LeaderboardModel addElement(LeaderboardModel inputData) {
        if (isLeaderboardCountMax() == false) {
            leaderboardRepository.addEntity(inputData);
        }
        else {
            if (inputData.getScore() >= leaderboardRepository.peekSet(Constants.REVERSE_RANGE).get().getScore()) {
                Boolean addSuccess = this.leaderboardRepository.addEntity(inputData);
                if (addSuccess) {
                    this.leaderboardRepository.popSet();
                }
            }
        }

        return inputData;
    }

    public Set<LeaderboardModel> getLeaderboardSet() {
        return this.leaderboardRepository.getAll(Constants.REVERSE_RANGE);
    }

    public Boolean isLeaderboardCountMax() {
        return this.leaderboardRepository.getCount() >= leaderboardMaxCount;
    }
}
