package org.example.service;

import org.example.model.LeaderboardModel;
import org.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class DummyKafkaConsumerService {
    private final Logger logger = LoggerFactory.getLogger(DummyKafkaConsumerService.class);

    @Autowired
    private FileIOService fileIOService;

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private UserServiceImpl userService;

    @Value("${app_config.leaderboard_file_path}")
    private String leaderboardFilePath;

    public DummyKafkaConsumerService() {
        startConsumer();
    }

    // In the dummy solution we can solve for concurrency problem where multiple thread try to access same file
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    /**
     * This cronjob runs and processes POST /api/publish-score data every 1 second.
     * This is kind of a simulation of kafka consumer in production
     */
    private void startConsumer() {
        logger.info("Starting cronjob to add scores to leaderboard from LeaderboardData.json file...");
        Runnable task = this::uploadScoreToLeaderboard;

        // cron runs every 1 second to read LeaderboardData file changes
        executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Reads file data.
     * Checks if the userId in payload is registered.
     * Calls updateRedisLeaderboard() of LeaderboardService class and updates the Leaderboard(Redis)
     */
    private void uploadScoreToLeaderboard() {
        try {
            List<LeaderboardModel> leaderboardModelList = fileIOService.readFile(leaderboardFilePath);

            if (leaderboardModelList != null) {
                for (LeaderboardModel row: leaderboardModelList) {
                    // only add registered users
                    Optional<User> user = userService.fetchById(row.getUserId());
                    if (user.isPresent()) {
                        // add leaderboard entity to redis SortedSet
                        leaderboardService.updateRedisLeaderboard(row);

                        // update high score in User table
                        if (row.getScore() > user.get().getHighScore()) {
                            user.get().setHighScore(row.getScore());
                            userService.saveUser(user.get());
                        }
                    }
                }
                fileIOService.deleteFile(leaderboardFilePath);
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
