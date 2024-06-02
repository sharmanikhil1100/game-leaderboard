package org.example.service;

import org.example.model.LeaderboardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class DummyKafkaConsumer {
    private final Logger logger = LoggerFactory.getLogger(DummyKafkaConsumer.class);

    @Autowired
    private FileIOService fileIOService;

    @Autowired
    private LeaderboardService leaderboardService;

    @Value("${app_config.leaderboard_file_path}")
    private String leaderboardFilePath;

    public DummyKafkaConsumer() {
        startConsumer();
    }

    // In the dummy solution we can solve for concurrency problem where multiple thread try to access same file
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private void startConsumer() {
        logger.info("Starting cronjob to add scores to leaderboard from LeaderboardData.json file...");
        Runnable task = () -> {
            try {
                List<LeaderboardModel> leaderboardModelList = fileIOService.readFile(leaderboardFilePath);

                if (leaderboardModelList != null) {
                    for (LeaderboardModel row: leaderboardModelList) {
                        // add leaderboard entity to redis SortedSet
                        leaderboardService.updateRedisLeaderboard(row);
                    }
                    fileIOService.deleteFile(leaderboardFilePath);
                }
            }
            catch (Exception e) {
                logger.error(e.getMessage());
            }
        };

        // cron runs every 1 second to read LeaderboardData file changes
        executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }
}
