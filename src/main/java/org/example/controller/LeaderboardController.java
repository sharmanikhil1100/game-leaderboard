package org.example.controller;

import org.example.model.LeaderboardModel;
import org.example.service.FileIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LeaderboardController {

    @Autowired
    private FileIOService fileIOService;

    @GetMapping("leaderboard")
    public List<LeaderboardModel> getLeaderboardEntities() {
        LeaderboardModel data = new LeaderboardModel();
        List<LeaderboardModel> responseData = new ArrayList<>();
        responseData.add(data);

        return responseData;
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
