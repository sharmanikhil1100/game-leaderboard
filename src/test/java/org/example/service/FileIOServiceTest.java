package org.example.service;

import com.google.gson.Gson;
import org.example.model.LeaderboardModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class FileIOServiceTest {

    @Mock
    private Logger logger;

    @InjectMocks
    private FileIOService fileIOService;

    @BeforeEach
    void setUp() {
        fileIOService = new FileIOService();
    }

    @Test
    void testReadFile_withValidData() throws IOException {
        String filePath = "src/test/resources/test.json";
        String jsonContent = "[{\"gameId\":\"1\",\"userId\":\"3\",\"username\":\"Nikhil\",\"score\":642.0,\"rank\":0}]";

        Files.write(Paths.get(filePath), jsonContent.getBytes());

        List<LeaderboardModel> expectedData = new ArrayList<>();
        LeaderboardModel leaderboardData = new LeaderboardModel();
        leaderboardData.setGameId("1");
        leaderboardData.setUserId("3");
        leaderboardData.setUsername("Nikhil");
        leaderboardData.setScore(642.00);
        expectedData.add(leaderboardData);

        List<LeaderboardModel> actualData = fileIOService.readFile(filePath);

        assertEquals(expectedData, actualData);

        Files.delete(Paths.get(filePath));
    }

    @Test
    void testReadFile_withIOException() throws IOException {
        String filePath = "path/to/nowhere.json";

        assertThrows(RuntimeException.class, () -> fileIOService.readFile(filePath));
    }

    @Test
    void testAppendFile_withValidData() throws IOException {
        String filePath = "src/test/resources/test.json";
        LeaderboardModel leaderboardData = new LeaderboardModel();
        leaderboardData.setGameId("1");
        leaderboardData.setUserId("3");
        leaderboardData.setUsername("Nikhil");
        leaderboardData.setScore(642.00);

        List<LeaderboardModel> initialData = new ArrayList<>();

        try (FileWriter writer = new FileWriter(filePath)) {
            new Gson().toJson(initialData, writer);
        }

        fileIOService.appendFile(filePath, leaderboardData);

        List<LeaderboardModel> actualData = fileIOService.readFile(filePath);
        assertEquals(1, actualData.size());
        assertEquals(leaderboardData, actualData.get(0));

        Files.delete(Paths.get(filePath));
    }

    @Test
    void testAppendFile_withIOException() throws IOException {
        String filePath = "path/to/nowhere.json";
        LeaderboardModel leaderboardData = new LeaderboardModel();

        assertThrows(RuntimeException.class, () -> fileIOService.appendFile(filePath, leaderboardData));
    }

    @Test
    void testDeleteFile() throws IOException {
        String filePath = "src/test/resources/test.json";
        Files.createFile(Paths.get(filePath));

        fileIOService.deleteFile(filePath);

        assertEquals(0, Files.size(Paths.get(filePath)));

        Files.delete(Paths.get(filePath));
    }

    @Test
    void testDeleteFile_withIOException() throws IOException {
        String filePath = "path/to/nowhere.json";

        assertThrows(RuntimeException.class, () -> fileIOService.deleteFile(filePath));
    }
}
