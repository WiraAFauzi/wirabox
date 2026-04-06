package com.wirabox.infrastructure;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectionService {

    private static final String FILE_NAME = "connections.txt";

    public void saveConnection(String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(path);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> loadConnections() {
        List<String> connections = new ArrayList<>();

        File file = new File(FILE_NAME);
        if (!file.exists()) return connections;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                connections.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return connections;
    }
}