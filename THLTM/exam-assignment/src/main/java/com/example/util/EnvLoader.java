package com.example.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EnvLoader {

    private EnvLoader() {
    }

    public static Map<String, String> loadFromWorkingDir(String fileName) {
        Path path = Paths.get(System.getProperty("user.dir"), fileName);
        return loadFromPath(path);
    }

    public static Map<String, String> loadFromPath(Path path) {
        Map<String, String> map = new HashMap<>();
        if (path == null || !Files.exists(path)) {
            return map;
        }

        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int idx = trimmed.indexOf('=');
                if (idx <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, idx).trim();
                String value = trimmed.substring(idx + 1).trim();
                value = stripQuotes(value);
                map.put(key, value);
            }
        } catch (IOException e) {
            System.err.println("Khong doc duoc .env: " + e.getMessage());
        }
        return map;
    }

    public static String getOrDefault(Map<String, String> env, String key, String defaultValue) {
        if (env == null) {
            return defaultValue;
        }
        String value = env.get(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value.trim();
    }

    private static String stripQuotes(String value) {
        if (value == null || value.length() < 2) {
            return value;
        }
        if ((value.startsWith("\"") && value.endsWith("\""))
                || (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
