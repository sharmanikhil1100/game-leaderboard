package org.example.service;

import com.google.gson.Gson;
import org.example.model.LeaderboardModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class DummyKafkaConsumer<T> {

    @Autowired
    private FileIOService fileIOService;

    @Autowired
    private LeaderboardService leaderboardService;

    public DummyKafkaConsumer() {
        start();
    }

    //TODO: In the dummy solution we need to solve for concurrency problem where multiple thread try to access same file
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public void start() {
        System.out.println("Adding scores to the leaderboard...");
        Runnable task = () -> {
            try {
                Gson gson = new Gson();
                String filePath = "src/main/resources/LeaderboardData.json";
                List<LeaderboardModel> leaderboardModelList = this.fileIOService.readFile(filePath);

                if (leaderboardModelList != null) {
                    for (LeaderboardModel row: leaderboardModelList) {
                        this.leaderboardService.addElement(row);
                    }
                }

                fileIOService.deleteFile(filePath);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        };

        executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }
}
