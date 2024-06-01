package org.example.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileIOService<T> {
    public List<T> readFile(String filePath) {
        Gson gson = new Gson();

        String contents = null;
        try {
            contents = new String(Files.readAllBytes(Paths.get(filePath)));

            List<T> data = gson.fromJson(contents, new TypeToken<List<T>>() {
            }.getType());

            return data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void appendFile(String filePath, T inputData) {
        Gson gson = new Gson();

        String contents = null;
        FileWriter fileWriter = null;
        try {
            contents = new String(Files.readAllBytes(Paths.get(filePath)));

            List<T> data = gson.fromJson(contents, new TypeToken<List<T>>() {
            }.getType());

            if (data == null) {
                data = new ArrayList<>();
            }
            data.add(inputData);

            fileWriter = new FileWriter(filePath);
            gson.toJson(data, fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
