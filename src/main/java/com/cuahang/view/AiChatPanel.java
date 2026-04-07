package com.cuahang.view;

import com.cuahang.ai.AiQueryResult;
import com.cuahang.ai.AiQueryService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class AiChatPanel extends JPanel {
    private final JTextArea chatArea = new JTextArea();
    private final JTextArea sqlArea = new JTextArea();
    private final JTextField inputField = new JTextField(50);
    private final JButton sendButton = new JButton("Gửi");
    private final JComboBox<String> sampleBox = new JComboBox<>(
        new String[] {
            "Top 5 sản phẩm bán chạy",
            "Doanh thu theo ngày trong 7 ngày gần nhất",
            "Danh sách hóa đơn Complete mới nhất",
            "Sản phẩm tồn kho thấp (<= 10)",
            "Khách hàng có điểm tích lũy cao nhất"
        }
    );
    private final JButton insertSampleButton = new JButton("Chèn mẫu");

    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable resultTable = new JTable(tableModel);

    private final AiQueryService aiQueryService = new AiQueryService();

    /**
     * Khởi tạo panel AI chat: nhập câu hỏi tự nhiên → sinh SQL → hiển thị bảng kết quả.
     */
    public AiChatPanel() {
        setLayout(new BorderLayout());

        chatArea.setEditable(false);
        sqlArea.setEditable(false);
        sqlArea.setRows(5);

        JScrollPane chatScroll = new JScrollPane(chatArea);

        JPanel right = new JPanel(new BorderLayout());
        right.add(new JScrollPane(sqlArea), BorderLayout.NORTH);
        right.add(new JScrollPane(resultTable), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatScroll, right);
        split.setResizeWeight(0.55);
        add(split, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(sampleBox);
        bottom.add(insertSampleButton);
        bottom.add(inputField);
        bottom.add(sendButton);
        add(bottom, BorderLayout.SOUTH);

        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        sendButton.addActionListener(e -> send());
        inputField.addActionListener(e -> send());
        insertSampleButton.addActionListener(e -> {
            Object selected = sampleBox.getSelectedItem();
            if (selected != null) {
                inputField.setText(String.valueOf(selected));
                inputField.requestFocus();
            }
        });
    }

    /**
     * Gửi câu hỏi, gọi AI sinh SQL và thực thi truy vấn đọc dữ liệu.
     */
    private void send() {
        String text = inputField.getText().trim();
        if (text.isBlank()) return;

        inputField.setText("");
        append("Bạn: " + text);
        append("AI: Đang xử lý...");
        sqlArea.setText("");
        sendButton.setEnabled(false);
        inputField.setEnabled(false);

        new SwingWorker<AiQueryResult, Void>() {
            private String sql;
            private long startedAt;

            @Override
            protected AiQueryResult doInBackground() {
                startedAt = System.currentTimeMillis();
                sql = aiQueryService.generateSqlFromText(text);
                return aiQueryService.executeSelectQuery(sql);
            }

            @Override
            protected void done() {
                sendButton.setEnabled(true);
                inputField.setEnabled(true);
                try {
                    AiQueryResult result = get();
                    long tookMs = Math.max(0, System.currentTimeMillis() - startedAt);
                    sqlArea.setText(sql);
                    append("AI: Trả về " + result.rows().size() + " dòng (" + tookMs + " ms).");
                    setTable(result.columns(), result.rows());
                } catch (Exception ex) {
                    append("AI: Lỗi: " + ex.getMessage());
                }
            }
        }.execute();
    }

    /**
     * Đổ dữ liệu vào JTable theo danh sách cột và danh sách dòng.
     */
    private void setTable(List<String> columns, List<List<Object>> rows) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        for (String col : columns) {
            tableModel.addColumn(col);
        }

        for (List<Object> row : rows) {
            tableModel.addRow(row.toArray());
        }
    }

    /**
     * Append 1 dòng log vào khung chat.
     */
    private void append(String line) {
        chatArea.append(line + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}
