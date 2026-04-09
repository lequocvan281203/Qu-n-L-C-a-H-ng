package com.cuahang.main;

import com.cuahang.ai.AiQueryResult;
import com.cuahang.ai.AiQueryService;
import java.util.List;

public class AiCliDemo {
    public static void main(String[] args) {
        String question = args != null && args.length > 0 ? String.join(" ", args).trim() : "Top 5 sản phẩm bán chạy";
        if (question.isBlank()) {
            System.err.println("Thiếu câu hỏi.");
            System.exit(2);
        }

        AiQueryService service = new AiQueryService();
        try {
            String sql = service.generateSqlFromText(question);
            System.out.println("QUESTION: " + question);
            System.out.println("SQL: " + sql);

            AiQueryResult result = service.executeSelectQuery(sql);
            System.out.println("COLUMNS: " + result.columns());

            List<List<Object>> rows = result.rows();
            int limit = Math.min(rows.size(), 10);
            for (int i = 0; i < limit; i++) {
                System.out.println("ROW " + (i + 1) + ": " + rows.get(i));
            }
            System.out.println("TOTAL_ROWS: " + rows.size());
        } catch (Exception e) {
            System.err.println("AI demo failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
