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

    // Can be configured to maintain any number of top entries in the leaderboard
    @Value("${app_config.leaderboard_size}")
    private long leaderboardMaxCount;

    /**
     * @param leaderboardModel new user score entry
     * Updates new eligible high score in the redis Sorted set data structure. It maintains the size of Set to be 5
     */
    public void updateRedisLeaderboard(LeaderboardModel leaderboardModel) {
        // if leaderboard size is >=5, then only add eligible entity to leaderboard and maintain size = 5
        if (isLeaderboardCountMax()) {
            if (leaderboardModel.getScore() >= leaderboardRepository.peekSet(Constants.REVERSE_RANGE).get().getScore()) {
                // check if the user has done same high score in the past
                Pair<LeaderboardModel, Boolean> isSameUserScorePresent = Helper.isSameUserScoreInLeaderboard(leaderboardModel, leaderboardRepository.getAll(Constants.REVERSE_RANGE));

                if (isSameUserScorePresent.getSecond()) {
                    LeaderboardModel pastUserScore = isSameUserScorePresent.getFirst();
                    // handle the edge case which should take care of only updating the latest timestamp high score
                    Boolean isNewDate = Helper.isNewDayAfter(pastUserScore.getCreatedAt(), leaderboardModel.getCreatedAt());
                    if (isNewDate) {
                        // replace old score in leaderboard with updated timestamp
                        leaderboardRepository.remove(pastUserScore);
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
                LeaderboardModel pastUserScore = isSameUserScorePresent.getFirst();
                if (Helper.isNewDayAfter(pastUserScore.getCreatedAt(), leaderboardModel.getCreatedAt())) {
                    // replace old score in leaderboard with updated timestamp
                    leaderboardRepository.remove(pastUserScore);
                    leaderboardRepository.addEntity(leaderboardModel);
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

    /**
     * Return Top 5 entries from Redis Sorted set
     * @return Set<LeaderboardModel>
     */
    public Set<LeaderboardModel> getLeaderboard() {
        return leaderboardRepository.getAll(Constants.REVERSE_RANGE);
    }

    /**
     * Based on configured value from yml file, it returns whether the size of Redis sorted set is within the max limit
     * @return Boolean
     */
    private Boolean isLeaderboardCountMax() {
        return leaderboardRepository.getLeaderboardSize() >= leaderboardMaxCount;
    }
}
