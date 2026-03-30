package com.cuahang.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseBootstrap {
    public static void ensureDatabaseExists(String jdbcUrl, String username, String password) {
        if (jdbcUrl == null || jdbcUrl.isBlank()) return;

        String dbName = extractDatabaseName(jdbcUrl);
        if (dbName == null || dbName.isBlank()) return;

        String baseUrl = toServerJdbcUrl(jdbcUrl);
        try (Connection conn = DriverManager.getConnection(baseUrl, username, password)) {
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
                );
            }
        } catch (Exception ignored) {
        }
    }

    private static String extractDatabaseName(String jdbcUrl) {
        String noPrefix = jdbcUrl.startsWith("jdbc:") ? jdbcUrl.substring(5) : jdbcUrl;
        int slash = noPrefix.indexOf('/');
        if (slash < 0) return null;

        int secondSlash = noPrefix.indexOf('/', slash + 2);
        if (secondSlash < 0) return null;

        int q = jdbcUrl.indexOf('?');
        String tail = q >= 0 ? jdbcUrl.substring(0, q) : jdbcUrl;
        int lastSlash = tail.lastIndexOf('/');
        if (lastSlash < 0) return null;
        return tail.substring(lastSlash + 1).trim();
    }

    private static String toServerJdbcUrl(String jdbcUrl) {
        int q = jdbcUrl.indexOf('?');
        String params = q >= 0 ? jdbcUrl.substring(q) : "";
        String noParams = q >= 0 ? jdbcUrl.substring(0, q) : jdbcUrl;

        int lastSlash = noParams.lastIndexOf('/');
        if (lastSlash < 0) return jdbcUrl;

        String head = noParams.substring(0, lastSlash + 1);
        return head + params;
    }
}

