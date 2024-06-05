package org.example.controller;

import org.example.model.LeaderboardModel;
import org.example.service.FileIOService;
import org.example.service.LeaderboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class LeaderboardController {

    private final Logger logger = LoggerFactory.getLogger(LeaderboardController.class);

    @Autowired
    private FileIOService fileIOService;

    @Autowired
    private LeaderboardService leaderboardService;

    @Value("${app_config.leaderboard_file_path}")
    private String leaderboardFilePath;

    @GetMapping("leaderboard")
    public Set<LeaderboardModel> getLeaderboardEntities() {
        Set<LeaderboardModel> response = null;
        try {
            response = this.leaderboardService.getLeaderboard();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        return response;
    }

    @PostMapping("publish-score")
    public LeaderboardModel createLeaderboardEntity(@RequestBody LeaderboardModel leaderboardData)  {
        String filePath = leaderboardFilePath;
        // read to existing file
        try {
            fileIOService.appendFile(filePath, leaderboardData);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }

        return leaderboardData;
    }

}
