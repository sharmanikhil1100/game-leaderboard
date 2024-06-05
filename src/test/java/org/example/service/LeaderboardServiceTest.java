package org.example.service;

import org.example.config.Constants;
import org.example.model.LeaderboardModel;
import org.example.repository.LeaderboardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeaderboardServiceTest {

    @Mock
    private LeaderboardRepository leaderboardRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;

    @Value("${app_config.leaderboard_size}")
    private long leaderboardMaxCount = 5;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateRedisLeaderboard_whenLeaderboardIsNotFull() {
        LeaderboardModel model = new LeaderboardModel(1L, 3L, "Nikhil", 642.0);

        when(leaderboardRepository.getLeaderboardSize()).thenReturn(-1L);
        when(leaderboardRepository.addEntity(model)).thenReturn(true);

        leaderboardService.updateRedisLeaderboard(model);

        verify(leaderboardRepository, times(1)).addEntity(model);
    }

    @Test
    public void testUpdateRedisLeaderboard_WhenLeaderboardIsNotFullAndSameUserScoreWithNewDate() {
        LeaderboardModel newEntry = new LeaderboardModel(1L, 1L, "Nikhil", 100.0, "2024-01-02");
        LeaderboardModel existingEntry = new LeaderboardModel(1L, 1L, "Nikhil", 100.0, "2024-01-01");

        when(leaderboardRepository.getAll(Constants.REVERSE_RANGE)).thenReturn(Set.of(existingEntry));
        when(leaderboardRepository.getLeaderboardSize()).thenReturn(-1L);

        leaderboardService.updateRedisLeaderboard(newEntry);

        verify(leaderboardRepository, times(1)).remove(existingEntry);
        verify(leaderboardRepository, times(1)).addEntity(newEntry);
    }

    @Test
    public void testUpdateRedisLeaderboard_WhenLeaderboardIsNotFullAndSameUserScoreWithOldDate() {
        LeaderboardModel newEntry = new LeaderboardModel(1L, 1L, "Nikhil", 100.0, "2024-01-01");
        LeaderboardModel existingEntry = new LeaderboardModel(1L, 1L, "Nikhil", 100.0, "2024-01-02");

        when(leaderboardRepository.getAll(Constants.REVERSE_RANGE)).thenReturn(Set.of(existingEntry));
        when(leaderboardRepository.getLeaderboardSize()).thenReturn(-1L);

        leaderboardService.updateRedisLeaderboard(newEntry);

        verify(leaderboardRepository, times(0)).addEntity(newEntry);
    }

    @Test
    void testUpdateRedisLeaderboard_whenLeaderboardIsFullAndModelIsEligible() {
        LeaderboardModel model = new LeaderboardModel(1L, 3L, "Nikhil", 642.0);
        LeaderboardModel lowestModel = new LeaderboardModel(2L, 4L, "John", 600.0);

        when(leaderboardRepository.getLeaderboardSize()).thenReturn(5L);
        when(leaderboardRepository.peekSet(Constants.REVERSE_RANGE)).thenReturn(Optional.of(lowestModel));
        when(leaderboardRepository.addEntity(model)).thenReturn(true);

        leaderboardService.updateRedisLeaderboard(model);

        verify(leaderboardRepository, times(1)).addEntity(model);
        verify(leaderboardRepository, times(1)).popSet();
    }

    @Test
    public void testUpdateRedisLeaderboard_WhenSameUserScoreWithNewDate() {
        LeaderboardModel newEntry = new LeaderboardModel(1L, 1L, "Nikhil", 100.0, "2024-01-02");
        LeaderboardModel existingEntry = new LeaderboardModel(1L, 1L, "Nikhil", 100.0, "2024-01-01");
        when(leaderboardRepository.getAll(Constants.REVERSE_RANGE)).thenReturn(Set.of(existingEntry));
        when(leaderboardRepository.peekSet(Constants.REVERSE_RANGE)).thenReturn(Optional.of(existingEntry));

        leaderboardService.updateRedisLeaderboard(newEntry);

        verify(leaderboardRepository, times(1)).remove(existingEntry);
        verify(leaderboardRepository, times(1)).addEntity(newEntry);
    }

    @Test
    public void testUpdateRedisLeaderboard_WhenSameUserScoreWithOldDate() {
        LeaderboardModel newEntry = new LeaderboardModel(1L, 1L, "Nikhil", 100.0, "2024-01-01");
        LeaderboardModel existingEntry = new LeaderboardModel(1L, 1L, "Nikhil", 100.0, "2024-01-02");
        when(leaderboardRepository.getAll(Constants.REVERSE_RANGE)).thenReturn(Set.of(existingEntry));
        when(leaderboardRepository.peekSet(Constants.REVERSE_RANGE)).thenReturn(Optional.of(existingEntry));

        leaderboardService.updateRedisLeaderboard(newEntry);

        verify(leaderboardRepository, times(0)).addEntity(newEntry);
    }


    @Test
    void testUpdateRedisLeaderboard_whenLeaderboardIsFullAndModelIsNotEligible() {
        LeaderboardModel model = new LeaderboardModel(1L, 3L, "Nikhil", 642.0);
        LeaderboardModel lowestModel = new LeaderboardModel(2L, 4L, "John", 650.0);

        when(leaderboardRepository.getLeaderboardSize()).thenReturn(5L);
        when(leaderboardRepository.peekSet(Constants.REVERSE_RANGE)).thenReturn(Optional.of(lowestModel));

        leaderboardService.updateRedisLeaderboard(model);

        verify(leaderboardRepository, times(0)).addEntity(model);
        verify(leaderboardRepository, times(0)).popSet();
    }

    @Test
    void testGetLeaderboard() {
        Set<LeaderboardModel> expectedSet = Collections.singleton(new LeaderboardModel(1L, 3L, "Nikhil", 642.0));

        when(leaderboardRepository.getAll(Constants.REVERSE_RANGE)).thenReturn(expectedSet);

        Set<LeaderboardModel> actualSet = leaderboardService.getLeaderboard();

        assertEquals(expectedSet, actualSet);
    }
}
