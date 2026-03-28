package com.cuahang.view;

import com.cuahang.ai.AiQueryResult;
import com.cuahang.ai.AiQueryService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class AiChatPanel extends JPanel {
    private final JTextArea chatArea = new JTextArea();
    private final JTextField inputField = new JTextField(50);
    private final JButton sendButton = new JButton("Gửi");

    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable resultTable = new JTable(tableModel);

    private final AiQueryService aiQueryService = new AiQueryService();

    public AiChatPanel() {
        setLayout(new BorderLayout());

        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(inputField);
        bottom.add(sendButton);
        add(bottom, BorderLayout.SOUTH);

        add(new JScrollPane(resultTable), BorderLayout.EAST);
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        sendButton.addActionListener(e -> send());
        inputField.addActionListener(e -> send());
    }

    private void send() {
        String text = inputField.getText().trim();
        if (text.isBlank()) return;

        inputField.setText("");
        append("Bạn: " + text);
        append("AI: Đang xử lý...");
        sendButton.setEnabled(false);

        new SwingWorker<AiQueryResult, Void>() {
            private String sql;

            @Override
            protected AiQueryResult doInBackground() {
                sql = aiQueryService.generateSqlFromText(text);
                return aiQueryService.executeSelectQuery(sql);
            }

            @Override
            protected void done() {
                sendButton.setEnabled(true);
                try {
                    AiQueryResult result = get();
                    append("AI (SQL): " + sql);
                    append("AI: Trả về " + result.rows().size() + " dòng.");
                    setTable(result.columns(), result.rows());
                } catch (Exception ex) {
                    append("AI: Lỗi: " + ex.getMessage());
                }
            }
        }.execute();
    }

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

    private void append(String line) {
        chatArea.append(line + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}

