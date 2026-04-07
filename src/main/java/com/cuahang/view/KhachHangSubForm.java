package com.cuahang.view;

import com.cuahang.entity.KhachHang;
import com.cuahang.service.KhachHangService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class KhachHangSubForm extends SubForm {
    private final KhachHangService service = new KhachHangService();
    private final boolean editable;

    private final JTextField searchField = new JTextField(30);
    private final JButton searchButton = new JButton("Tìm");
    private final JButton addButton = new JButton("Thêm");
    private final JButton editButton = new JButton("Sửa");
    private final JButton deleteButton = new JButton("Xóa");
    private final JButton refreshButton = new JButton("Tải lại");

    private final DefaultTableModel model = new DefaultTableModel(
        new Object[] {"MaKH", "TenKH", "SoDT", "DiemTichLuy"},
        0
    );
    private final JTable table = new JTable(model);

    public KhachHangSubForm(boolean editable) {
        super("Khách hàng (CRUD)");
        this.editable = editable;
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Từ khóa:"));
        top.add(searchField);
        top.add(searchButton);
        top.add(addButton);
        top.add(editButton);
        top.add(deleteButton);
        top.add(refreshButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        searchButton.addActionListener(e -> load());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            load();
        });
        addButton.addActionListener(e -> openEditor(null));
        editButton.addActionListener(e -> openEditor(getSelected()));
        deleteButton.addActionListener(e -> deleteSelected());

        addButton.setEnabled(editable);
        editButton.setEnabled(editable);
        deleteButton.setEnabled(editable);

        load();
    }

    private void load() {
        searchButton.setEnabled(false);
        refreshButton.setEnabled(false);
        new SwingWorker<List<KhachHang>, Void>() {
            @Override
            protected List<KhachHang> doInBackground() {
                return service.search(searchField.getText());
            }

            @Override
            protected void done() {
                searchButton.setEnabled(true);
                refreshButton.setEnabled(true);
                model.setRowCount(0);
                try {
                    for (KhachHang kh : get()) {
                        model.addRow(new Object[] {kh.getMaKH(), kh.getTenKH(), kh.getSoDT(), kh.getDiemTichLuy()});
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(KhachHangSubForm.this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private KhachHang getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        String maKH = String.valueOf(model.getValueAt(row, 0));
        return service.findById(maKH).orElse(null);
    }

    private void deleteSelected() {
        KhachHang kh = getSelected();
        if (kh == null) {
            JOptionPane.showMessageDialog(this, "Chọn 1 khách hàng để xóa.", "Thiếu chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Xóa khách hàng " + kh.getMaKH() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            service.deleteById(kh.getMaKH());
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEditor(KhachHang editing) {
        if (!editable) return;
        boolean isNew = editing == null;
        KhachHang kh = isNew ? new KhachHang() : editing;

        JTextField maKH = new JTextField(15);
        JTextField tenKH = new JTextField(25);
        JTextField soDT = new JTextField(15);
        JTextField diem = new JTextField(10);

        if (!isNew) {
            maKH.setText(kh.getMaKH());
            maKH.setEnabled(false);
            tenKH.setText(kh.getTenKH());
            soDT.setText(kh.getSoDT());
            diem.setText(String.valueOf(kh.getDiemTichLuy()));
        }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("MaKH"), gbc);
        gbc.gridx = 1;
        form.add(maKH, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("TenKH"), gbc);
        gbc.gridx = 1;
        form.add(tenKH, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("SoDT"), gbc);
        gbc.gridx = 1;
        form.add(soDT, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("DiemTichLuy"), gbc);
        gbc.gridx = 1;
        form.add(diem, gbc);

        JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.setTitle(isNew ? "Thêm khách hàng" : "Sửa khách hàng");
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        JButton ok = new JButton("Lưu");
        JButton cancel = new JButton("Hủy");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(ok);
        actions.add(cancel);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        cancel.addActionListener(e -> dialog.dispose());
        ok.addActionListener(e -> {
            try {
                if (isNew) {
                    String id = maKH.getText().trim();
                    if (id.isBlank()) throw new IllegalArgumentException("MaKH không được rỗng");
                    kh.setMaKH(id);
                }
                String name = tenKH.getText().trim();
                if (name.isBlank()) throw new IllegalArgumentException("TenKH không được rỗng");
                kh.setTenKH(name);
                kh.setSoDT(soDT.getText().trim());
                String d = diem.getText().trim();
                kh.setDiemTichLuy(d.isBlank() ? 0 : Integer.parseInt(d));
                service.saveOrUpdate(kh);
                dialog.dispose();
                load();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }
}
