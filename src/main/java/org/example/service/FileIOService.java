package org.example.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.model.LeaderboardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileIOService {

    private final Logger logger = LoggerFactory.getLogger(FileIOService.class);

    public List<LeaderboardModel> readFile(String filePath) {
        Gson gson = new Gson();

        String contents = null;
        try {
            contents = new String(Files.readAllBytes(Paths.get(filePath)));

            List<LeaderboardModel> data = gson.fromJson(contents, new TypeToken<List<LeaderboardModel>>() {
            }.getType());

            return data;
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void appendFile(String filePath, LeaderboardModel inputData) {
        Gson gson = new Gson();
        String contents = null;
        FileWriter fileWriter = null;

        try {
            List<LeaderboardModel> data = readFile(filePath);
            if (data == null) {
                data = new ArrayList<>();
            }
            data.add(inputData);

            fileWriter = new FileWriter(filePath);
            gson.toJson(data, fileWriter);
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException e) {
                logger.info(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public void deleteFile(String filePath) {
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(filePath);
            fileWriter.write("");
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException e) {
                logger.info(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
