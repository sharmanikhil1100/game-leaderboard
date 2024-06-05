package org.example.service;

import org.example.config.Constants;
import org.example.model.LeaderboardModel;
import org.example.repository.LeaderboardRepository;
import org.example.utils.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
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
                Pair<LeaderboardModel, Boolean> isSameUserScorePresent = Helper.isSameUserScoreInLeaderboard(leaderboardModel, leaderboardRepository.getAll(Constants.REVERSE_RANGE));

                if (isSameUserScorePresent.getSecond()) {
                    Boolean isNewDate = Helper.isNewDayAfter(isSameUserScorePresent.getFirst().getCreatedAt(), leaderboardModel.getCreatedAt());
                    if (isNewDate) {
                        // replace old score in leaderboard with updated timestamp
                        leaderboardRepository.remove(isSameUserScorePresent.getFirst());
                        leaderboardRepository.addEntity(leaderboardModel);
                        logger.info("Leaderboard successfully updated by userId: {} with latest score: {}",
                                leaderboardModel.getUserId(), leaderboardModel.getScore());
                    }
                }
                else {
                    leaderboardRepository.popSet();
                    leaderboardRepository.addEntity(leaderboardModel);
                    logger.info("Leaderboard successfully updated by userId: {}, score: {}",
                            leaderboardModel.getUserId(), leaderboardModel.getScore());
                }
            }
        }
        else {
            Pair<LeaderboardModel, Boolean> isSameUserScorePresent = Helper.isSameUserScoreInLeaderboard(leaderboardModel, leaderboardRepository.getAll(Constants.REVERSE_RANGE));

            // if same user score present in leaderboard, then keep only latest date score
            if (isSameUserScorePresent.getSecond()) {
                if (Helper.isNewDayAfter(isSameUserScorePresent.getFirst().getCreatedAt(), leaderboardModel.getCreatedAt())) {
                    leaderboardRepository.addEntity(leaderboardModel);

                    // replace old score in leaderboard with updated timestamp
                    leaderboardRepository.remove(isSameUserScorePresent.getFirst());
                    logger.info("Leaderboard successfully updated by userId: {} with latest score: {} (Leaderboard Size <= 5)",
                            leaderboardModel.getUserId(), leaderboardModel.getScore());
                }
            }
            else {
                leaderboardRepository.addEntity(leaderboardModel);
                logger.info("Leaderboard successfully updated by userId: {}, score: {} (Leaderboard Size <= 5)",
                        leaderboardModel.getUserId(), leaderboardModel.getScore());
            }
        }
    }

    public Set<LeaderboardModel> getLeaderboard() {
        return leaderboardRepository.getAll(Constants.REVERSE_RANGE);
    }

    private Boolean isLeaderboardCountMax() {
        return leaderboardRepository.getLeaderboardSize() >= leaderboardMaxCount;
    }
}
