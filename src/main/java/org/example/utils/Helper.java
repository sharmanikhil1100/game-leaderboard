package org.example.utils;

import org.example.model.LeaderboardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class Helper {

    private static final Logger logger = LoggerFactory.getLogger(Helper.class);

    /**
     * Checks if newDate is strictly after currDate.
     * @param currDate Date passed as String (in yyyy-MM-dd format)
     * @param newDate Date passed as String (in yyyy-MM-dd format)
     * @return Boolean
     */
    public static Boolean isNewDayAfter(String currDate, String newDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date currScoreDate = simpleDateFormat.parse(currDate);
            Date newScoreDate = simpleDateFormat.parse(newDate);

            return currScoreDate.before(newScoreDate);
        } catch (ParseException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Checks if new user high score already exits in the Leaderboard (Redis)
     * @param newScore new user high score
     * @param leaderboardModelSet current redis sorted set
     * @return Pair<LeaderboardModel, Boolean> LeaderboardModel param represents the same existing score model and Boolean param represents the result.
     */
    public static Pair<LeaderboardModel, Boolean> isSameUserScoreInLeaderboard(LeaderboardModel newScore, Set<LeaderboardModel> leaderboardModelSet) {
        if (leaderboardModelSet != null) {
            for (LeaderboardModel row: leaderboardModelSet) {
                if (row.getUserId().equals(newScore.getUserId()) && row.getScore() == newScore.getScore()) {
                    return Pair.of(row, true);
                }
            }
        }
        return Pair.of(newScore, false);
    }
}
