package com.cuahang.view;

import com.cuahang.service.ReportService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class ReportSubForm extends SubForm {
    private final ReportService service = new ReportService();

    private final JButton lowStockButton = new JButton("Tồn kho thấp");
    private final JButton topSellButton = new JButton("Top bán chạy");
    private final JButton revenueButton = new JButton("Doanh thu theo ngày");

    private final JTextField thresholdField = new JTextField("10", 6);
    private final JTextField topNField = new JTextField("5", 6);
    private final JTextField fromField = new JTextField(LocalDate.now().minusDays(7).toString(), 12);
    private final JTextField toField = new JTextField(LocalDate.now().toString(), 12);

    private final DefaultTableModel model = new DefaultTableModel();
    private final JTable table = new JTable(model);

    public ReportSubForm() {
        super("Thống kê (JPQL)");
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Ngưỡng"));
        top.add(thresholdField);
        top.add(lowStockButton);
        top.add(new JLabel("Top"));
        top.add(topNField);
        top.add(topSellButton);
        top.add(new JLabel("Từ"));
        top.add(fromField);
        top.add(new JLabel("Đến"));
        top.add(toField);
        top.add(revenueButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        lowStockButton.addActionListener(e -> loadLowStock());
        topSellButton.addActionListener(e -> loadTopSell());
        revenueButton.addActionListener(e -> loadRevenue());

        loadLowStock();
    }

    private void loadLowStock() {
        int t;
        try {
            t = Integer.parseInt(thresholdField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngưỡng không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setBusy(true);
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() {
                return service.tonKhoThap(t);
            }

            @Override
            protected void done() {
                setBusy(false);
                model.setRowCount(0);
                model.setColumnCount(0);
                model.addColumn("MaSP");
                model.addColumn("TenSP");
                model.addColumn("SoLuongTon");
                try {
                    for (Object[] row : get()) {
                        model.addRow(row);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ReportSubForm.this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void loadTopSell() {
        int n;
        try {
            n = Integer.parseInt(topNField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Top N không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setBusy(true);
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() {
                return service.topSanPhamBanChay(n);
            }

            @Override
            protected void done() {
                setBusy(false);
                model.setRowCount(0);
                model.setColumnCount(0);
                model.addColumn("MaSP");
                model.addColumn("TenSP");
                model.addColumn("SoLuongBan");
                try {
                    for (Object[] row : get()) {
                        model.addRow(row);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ReportSubForm.this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void loadRevenue() {
        LocalDate from;
        LocalDate to;
        try {
            from = LocalDate.parse(fromField.getText().trim());
            to = LocalDate.parse(toField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày không hợp lệ. Dùng format yyyy-MM-dd", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setBusy(true);
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() {
                return service.doanhThuTheoNgay(from, to);
            }

            @Override
            protected void done() {
                setBusy(false);
                model.setRowCount(0);
                model.setColumnCount(0);
                model.addColumn("Ngay");
                model.addColumn("DoanhThu");
                try {
                    for (Object[] row : get()) {
                        model.addRow(row);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ReportSubForm.this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void setBusy(boolean busy) {
        lowStockButton.setEnabled(!busy);
        topSellButton.setEnabled(!busy);
        revenueButton.setEnabled(!busy);
    }
}

