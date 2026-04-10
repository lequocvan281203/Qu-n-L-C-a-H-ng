package com.cuahang.main;

import com.cuahang.util.HibernateUtil;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;

public class DbSeedRunner {
    public static void main(String[] args) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.doWork(conn -> {
                try {
                    runSqlResource(conn, "/db/schema.sql");
                    runSqlResource(conn, "/db/seed.sql");
                    printCounts(conn);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            System.err.println("Seed DB failed: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void runSqlResource(Connection conn, String resourcePath) throws Exception {
        InputStream in = DbSeedRunner.class.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalStateException("Không tìm thấy resource: " + resourcePath);
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }

        List<String> statements = splitStatements(sb.toString());
        try (Statement st = conn.createStatement()) {
            for (String sql : statements) {
                st.execute(sql);
            }
        }
    }

    private static List<String> splitStatements(String sqlFileContent) {
        String normalized = sqlFileContent.replace("\uFEFF", "");
        List<String> out = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;

        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            if (c == '\'') {
                inSingleQuote = !inSingleQuote;
            }
            if (c == ';' && !inSingleQuote) {
                String stmt = current.toString().trim();
                current.setLength(0);
                if (!stmt.isEmpty()) out.add(stmt);
                continue;
            }
            current.append(c);
        }

        String tail = current.toString().trim();
        if (!tail.isEmpty()) out.add(tail);
        return out;
    }

    private static void printCounts(Connection conn) throws Exception {
        String[] tables = {"SanPham", "HoaDon", "ChiTietHoaDon", "KhachHang", "NhanVien", "TaiKhoan"};
        try (Statement st = conn.createStatement()) {
            for (String t : tables) {
                try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + t)) {
                    if (rs.next()) {
                        System.out.println(t + ": " + rs.getLong(1));
                    }
                }
            }
        }
    }
}
