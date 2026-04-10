package com.cuahang.view;

import com.cuahang.entity.LoaiSanPham;
import com.cuahang.entity.NhaCungCap;
import com.cuahang.entity.SanPham;
import com.cuahang.service.SanPhamService;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class SanPhamSubForm extends SubForm {
    private final SanPhamService service = new SanPhamService();
    private final boolean editable;

    private final JTextField searchField = new JTextField(30);
    private final JButton searchButton = new JButton("Tìm");
    private final JButton addButton = new JButton("Thêm");
    private final JButton editButton = new JButton("Sửa");
    private final JButton deleteButton = new JButton("Xóa");
    private final JButton refreshButton = new JButton("Tải lại");

    private final DefaultTableModel model = new DefaultTableModel(
        new Object[] {"MaSP", "TenSP", "GiaBan", "DonViTinh", "SoLuongTon", "MaLoai", "MaNCC"},
        0
    );
    private final JTable table = new JTable(model);
    private final JLabel statusLabel = new JLabel(" ");

    public SanPhamSubForm(boolean editable) {
        super("Sản phẩm (CRUD)");
        this.editable = editable;
        setLayout(new BorderLayout());

        searchField.putClientProperty("JTextField.placeholderText", "Tìm theo mã hoặc tên sản phẩm...");
        searchField.setPreferredSize(new Dimension(320, 32));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        searchPanel.add(new JLabel("Từ khóa"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        actionsPanel.add(addButton);
        actionsPanel.add(editButton);
        actionsPanel.add(deleteButton);

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setBorder(new EmptyBorder(8, 10, 8, 10));
        top.add(searchPanel, BorderLayout.WEST);
        top.add(actionsPanel, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(new EmptyBorder(6, 10, 8, 10));
        bottom.add(statusLabel, BorderLayout.WEST);
        add(bottom, BorderLayout.SOUTH);

        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(240);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(70);
        table.getColumnModel().getColumn(6).setPreferredWidth(70);

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

        new SwingWorker<List<SanPham>, Void>() {
            @Override
            protected List<SanPham> doInBackground() {
                return service.search(searchField.getText());
            }

            @Override
            protected void done() {
                searchButton.setEnabled(true);
                refreshButton.setEnabled(true);
                model.setRowCount(0);
                try {
                    List<SanPham> list = get();
                    for (SanPham sp : list) {
                        model.addRow(
                            new Object[] {
                                sp.getMaSP(),
                                sp.getTenSP(),
                                sp.getGiaBan(),
                                sp.getDonViTinh(),
                                sp.getSoLuongTon(),
                                sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getMaLoai() : null,
                                sp.getNhaCungCap() != null ? sp.getNhaCungCap().getMaNCC() : null
                            }
                        );
                    }
                    statusLabel.setText("Tổng: " + list.size() + " sản phẩm");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(SanPhamSubForm.this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private SanPham getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        String maSP = String.valueOf(model.getValueAt(row, 0));
        return service.findById(maSP).orElse(null);
    }

    private void deleteSelected() {
        SanPham sp = getSelected();
        if (sp == null) {
            JOptionPane.showMessageDialog(this, "Chọn 1 sản phẩm để xóa.", "Thiếu chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Xóa sản phẩm " + sp.getMaSP() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            service.deleteById(sp.getMaSP());
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEditor(SanPham editing) {
        if (!editable) return;
        boolean isNew = editing == null;
        SanPham sp = isNew ? new SanPham() : editing;

        List<LoaiSanPham> loais = service.listLoai();
        List<NhaCungCap> nccs = service.listNcc();

        JTextField maSP = new JTextField(15);
        JTextField tenSP = new JTextField(25);
        JTextField giaBan = new JTextField(10);
        JTextField donViTinh = new JTextField(10);
        JTextField soLuongTon = new JTextField(10);
        JComboBox<LoaiSanPham> loaiBox = new JComboBox<>(loais.toArray(new LoaiSanPham[0]));
        JComboBox<NhaCungCap> nccBox = new JComboBox<>(nccs.toArray(new NhaCungCap[0]));

        loaiBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lb = new JLabel(value != null ? value.getMaLoai() + " - " + value.getTenLoai() : "");
            if (isSelected) lb.setOpaque(true);
            return lb;
        });
        nccBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lb = new JLabel(value != null ? value.getMaNCC() + " - " + value.getTenNCC() : "");
            if (isSelected) lb.setOpaque(true);
            return lb;
        });

        if (!isNew) {
            maSP.setText(sp.getMaSP());
            maSP.setEnabled(false);
            tenSP.setText(sp.getTenSP());
            giaBan.setText(String.valueOf(sp.getGiaBan()));
            donViTinh.setText(sp.getDonViTinh());
            soLuongTon.setText(String.valueOf(sp.getSoLuongTon()));
            selectById(loaiBox, sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getMaLoai() : null);
            selectById(nccBox, sp.getNhaCungCap() != null ? sp.getNhaCungCap().getMaNCC() : null);
        }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("MaSP"), gbc);
        gbc.gridx = 1;
        form.add(maSP, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("TenSP"), gbc);
        gbc.gridx = 1;
        form.add(tenSP, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("GiaBan"), gbc);
        gbc.gridx = 1;
        form.add(giaBan, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("DonViTinh"), gbc);
        gbc.gridx = 1;
        form.add(donViTinh, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("SoLuongTon"), gbc);
        gbc.gridx = 1;
        form.add(soLuongTon, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Loai"), gbc);
        gbc.gridx = 1;
        form.add(loaiBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("NCC"), gbc);
        gbc.gridx = 1;
        form.add(nccBox, gbc);

        JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.setTitle(isNew ? "Thêm sản phẩm" : "Sửa sản phẩm");
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
                    String id = maSP.getText().trim();
                    if (id.isBlank()) throw new IllegalArgumentException("MaSP không được rỗng");
                    sp.setMaSP(id);
                }
                String name = tenSP.getText().trim();
                if (name.isBlank()) throw new IllegalArgumentException("TenSP không được rỗng");
                sp.setTenSP(name);
                sp.setGiaBan(Double.parseDouble(giaBan.getText().trim()));
                sp.setDonViTinh(donViTinh.getText().trim());
                sp.setSoLuongTon(Integer.parseInt(soLuongTon.getText().trim()));
                sp.setLoaiSanPham((LoaiSanPham) loaiBox.getSelectedItem());
                sp.setNhaCungCap((NhaCungCap) nccBox.getSelectedItem());
                service.saveOrUpdate(sp);
                dialog.dispose();
                load();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private static <T> void selectById(JComboBox<T> box, String id) {
        if (id == null) return;
        for (int i = 0; i < box.getItemCount(); i++) {
            T item = box.getItemAt(i);
            if (item instanceof LoaiSanPham l && id.equals(l.getMaLoai())) {
                box.setSelectedIndex(i);
                return;
            }
            if (item instanceof NhaCungCap n && id.equals(n.getMaNCC())) {
                box.setSelectedIndex(i);
                return;
            }
        }
    }
}
