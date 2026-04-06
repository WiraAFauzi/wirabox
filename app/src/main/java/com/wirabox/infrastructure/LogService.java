package com.wirabox.infrastructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LogService {

    // Read entire file
    public static String loadFile(Path path) throws IOException {
        return Files.readString(path);
    }

    // Read file as lines
    public static List<String> loadLines(Path path) throws IOException {
        return Files.readAllLines(path);
    }

    // Filter lines based on keyword
    public static List<String> filterLines(List<String> lines, String keyword) {

        if (keyword == null || keyword.isEmpty()) {
            return lines;
        }

        return lines.stream()
                .filter(line -> line.toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }
}