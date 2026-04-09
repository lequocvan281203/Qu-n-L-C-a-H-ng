package com.cuahang.view;

import com.cuahang.entity.LoaiSanPham;
import com.cuahang.service.LoaiSanPhamService;
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

public class LoaiSanPhamSubForm extends SubForm {
    private final LoaiSanPhamService service = new LoaiSanPhamService();
    private final boolean editable;

    private final JTextField searchField = new JTextField(30);
    private final JButton searchButton = new JButton("Tìm");
    private final JButton addButton = new JButton("Thêm");
    private final JButton editButton = new JButton("Sửa");
    private final JButton deleteButton = new JButton("Xóa");
    private final JButton refreshButton = new JButton("Tải lại");

    private final DefaultTableModel model = new DefaultTableModel(new Object[] {"MaLoai", "TenLoai"}, 0);
    private final JTable table = new JTable(model);

    public LoaiSanPhamSubForm(boolean editable) {
        super("Loại sản phẩm (CRUD)");
        this.editable = editable;


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

        UiDefaults.styleActionButton(addButton);
        UiDefaults.styleActionButton(editButton);
        UiDefaults.styleActionButton(deleteButton);
        UiDefaults.styleActionButton(searchButton);
        UiDefaults.styleActionButton(refreshButton);

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
        new SwingWorker<List<LoaiSanPham>, Void>() {
            @Override
            protected List<LoaiSanPham> doInBackground() {
                return service.search(searchField.getText());
            }

            @Override
            protected void done() {
                searchButton.setEnabled(true);
                refreshButton.setEnabled(true);
                model.setRowCount(0);
                try {
                    for (LoaiSanPham l : get()) {
                        model.addRow(new Object[] {l.getMaLoai(), l.getTenLoai()});
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoaiSanPhamSubForm.this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private LoaiSanPham getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        String maLoai = String.valueOf(model.getValueAt(row, 0));
        return service.findById(maLoai).orElse(null);
    }

    private void deleteSelected() {
        if (!editable) return;
        LoaiSanPham l = getSelected();
        if (l == null) {
            JOptionPane.showMessageDialog(this, "Chọn 1 loại để xóa.", "Thiếu chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Xóa loại " + l.getMaLoai() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            service.deleteById(l.getMaLoai());
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEditor(LoaiSanPham editing) {
        if (!editable) return;
        boolean isNew = editing == null;
        LoaiSanPham l = isNew ? new LoaiSanPham() : editing;

        JTextField maLoai = new JTextField(15);
        JTextField tenLoai = new JTextField(25);

        if (!isNew) {
            maLoai.setText(l.getMaLoai());
            maLoai.setEnabled(false);
            tenLoai.setText(l.getTenLoai());
        }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("MaLoai"), gbc);
        gbc.gridx = 1;
        form.add(maLoai, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("TenLoai"), gbc);
        gbc.gridx = 1;
        form.add(tenLoai, gbc);

        JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.setTitle(isNew ? "Thêm loại" : "Sửa loại");
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
                    String id = maLoai.getText().trim();
                    if (id.isBlank()) throw new IllegalArgumentException("MaLoai không được rỗng");
                    l.setMaLoai(id);
                }
                String name = tenLoai.getText().trim();
                if (name.isBlank()) throw new IllegalArgumentException("TenLoai không được rỗng");
                l.setTenLoai(name);
                service.saveOrUpdate(l);
                dialog.dispose();
                load();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }
}
