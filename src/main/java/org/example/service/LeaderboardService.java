package org.example.service;

import org.example.config.Constants;
import org.example.model.LeaderboardModel;
import org.example.repository.LeaderboardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class LeaderboardService {
    private final Logger logger = LoggerFactory.getLogger(LeaderboardService.class);

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Value("${app_config.leaderboard_size}")
    private long leaderboardMaxCount;

    /**
     *
     * @param leaderboardModel
     */
    public void updateRedisLeaderboard(LeaderboardModel leaderboardModel) {
        // if leaderboard size is >=5, then only add eligible entity to leaderboard and maintain size = 5
        if (isLeaderboardCountMax()) {
            if (leaderboardModel.getScore() >= leaderboardRepository.peekSet(Constants.REVERSE_RANGE).get().getScore()) {
                Boolean updateSuccessful = leaderboardRepository.addEntity(leaderboardModel);
                if (updateSuccessful) {
                    leaderboardRepository.popSet();
                    logger.info("Leaderboard successfully updated by userId: {}, score: {}",
                            leaderboardModel.getUserId(), leaderboardModel.getScore());
                }
            }
        }
        else {
            leaderboardRepository.addEntity(leaderboardModel);
            logger.info("Leaderboard successfully updated by userId: {}, score: {} (Leaderboard Size <= 5)",
                    leaderboardModel.getUserId(), leaderboardModel.getScore());
        }
    }

    public Set<LeaderboardModel> getLeaderboard() {
        return leaderboardRepository.getAll(Constants.REVERSE_RANGE);
    }

    private Boolean isLeaderboardCountMax() {
        return leaderboardRepository.getLeaderboardSize() >= leaderboardMaxCount;
    }
}
