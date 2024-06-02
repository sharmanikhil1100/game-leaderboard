package org.example.controller;

import org.example.model.LeaderboardModel;
import org.example.service.FileIOService;
import org.example.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class LeaderboardController {

    @Autowired
    private FileIOService fileIOService;

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping("leaderboard")
    public Set<LeaderboardModel> getLeaderboardEntities() {
        Set<LeaderboardModel> response = this.leaderboardService.getLeaderboardSet();

        return response;
    }

    @PostMapping("publish-score")
    public LeaderboardModel createLeaderboardEntity(@RequestBody LeaderboardModel leaderboardData) {

        // this will be updated
        //leaderboardData.setRank(1);

        String filePath = "src/main/resources/LeaderboardData.json";
        // read to existing file
        try {
            fileIOService.appendFile(filePath, leaderboardData);
        } catch (Exception e) {
            throw e;
        }

        return leaderboardData;
    }

}
