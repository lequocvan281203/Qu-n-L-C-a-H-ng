package com.cuahang.view;

import com.cuahang.entity.ChiTietHoaDon;
import com.cuahang.entity.HoaDon;
import com.cuahang.entity.SanPham;
import com.cuahang.service.ChiTietHoaDonService;
import java.awt.BorderLayout;
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
import javax.swing.table.DefaultTableModel;

/**
 * Màn hình CRUD cho ChiTietHoaDon.
 * Cho phép tìm theo MaHD và thao tác thêm/sửa/xóa dòng chi tiết.
 */
public class ChiTietHoaDonSubForm extends SubForm {
    private final ChiTietHoaDonService service = new ChiTietHoaDonService();
    private final boolean editable;

    private final JTextField maHDField = new JTextField(15);
    private final JButton searchButton = new JButton("Tải");
    private final JButton addButton = new JButton("Thêm");
    private final JButton editButton = new JButton("Sửa");
    private final JButton deleteButton = new JButton("Xóa");
    private final JButton refreshButton = new JButton("Tải lại");

    private final DefaultTableModel model = new DefaultTableModel(
        new Object[] {"MaCTHD", "MaHD", "MaSP", "SoLuong", "DonGia"},
        0
    );
    private final JTable table = new JTable(model);

    /**
     * Khởi tạo form Chi tiết hóa đơn.
     *
     * @param editable true nếu được phép thêm/sửa/xóa, false nếu chỉ xem
     */
    public ChiTietHoaDonSubForm(boolean editable) {
        super("Chi tiết hóa đơn (CRUD)");
        this.editable = editable;
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("MaHD:"));
        top.add(maHDField);
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
        refreshButton.addActionListener(e -> load());
        addButton.addActionListener(e -> openEditor(null));
        editButton.addActionListener(e -> openEditor(getSelected()));
        deleteButton.addActionListener(e -> deleteSelected());

        addButton.setEnabled(editable);
        editButton.setEnabled(editable);
        deleteButton.setEnabled(editable);

        load();
    }

    /**
     * Tải danh sách chi tiết hóa đơn theo MaHD (nếu có).
     */
    private void load() {
        searchButton.setEnabled(false);
        refreshButton.setEnabled(false);

        String kw = maHDField.getText().trim();

        new SwingWorker<List<ChiTietHoaDon>, Void>() {
            @Override
            protected List<ChiTietHoaDon> doInBackground() {
                return service.searchByMaHD(kw);
            }

            @Override
            protected void done() {
                searchButton.setEnabled(true);
                refreshButton.setEnabled(true);
                model.setRowCount(0);
                try {
                    for (ChiTietHoaDon ct : get()) {
                        model.addRow(
                            new Object[] {
                                ct.getMaCTHD(),
                                ct.getHoaDon() != null ? ct.getHoaDon().getMaHD() : null,
                                ct.getSanPham() != null ? ct.getSanPham().getMaSP() : null,
                                ct.getSoLuong(),
                                ct.getDonGia()
                            }
                        );
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ChiTietHoaDonSubForm.this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Lấy bản ghi ChiTietHoaDon đang được chọn trên bảng.
     *
     * @return ChiTietHoaDon hoặc null nếu chưa chọn
     */
    private ChiTietHoaDon getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
        return service.findById(id).orElse(null);
    }

    /**
     * Xóa bản ghi ChiTietHoaDon đang chọn.
     */
    private void deleteSelected() {
        ChiTietHoaDon ct = getSelected();
        if (ct == null) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng chi tiết để xóa.", "Thiếu chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Xóa CTHD #" + ct.getMaCTHD() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            service.deleteById(ct.getMaCTHD());
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mở dialog thêm/sửa ChiTietHoaDon.
     *
     * @param editing null để thêm mới, khác null để sửa
     */
    private void openEditor(ChiTietHoaDon editing) {
        if (!editable) return;
        boolean isNew = editing == null;
        ChiTietHoaDon ct = isNew ? new ChiTietHoaDon() : editing;

        List<HoaDon> hoaDons = service.listHoaDon();
        List<SanPham> sanPhams = service.listSanPham();

        JComboBox<HoaDon> hdBox = new JComboBox<>(hoaDons.toArray(new HoaDon[0]));
        JComboBox<SanPham> spBox = new JComboBox<>(sanPhams.toArray(new SanPham[0]));
        JTextField soLuong = new JTextField(10);
        JTextField donGia = new JTextField(10);

        hdBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lb = new JLabel(value != null ? value.getMaHD() : "");
            if (isSelected) lb.setOpaque(true);
            return lb;
        });
        spBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lb = new JLabel(value != null ? value.getMaSP() + " - " + value.getTenSP() : "");
            if (isSelected) lb.setOpaque(true);
            return lb;
        });

        if (!isNew) {
            if (ct.getHoaDon() != null) {
                selectHoaDon(hdBox, ct.getHoaDon().getMaHD());
            }
            if (ct.getSanPham() != null) {
                selectSanPham(spBox, ct.getSanPham().getMaSP());
            }
            hdBox.setEnabled(false);
            spBox.setEnabled(false);
            soLuong.setText(String.valueOf(ct.getSoLuong()));
            donGia.setText(String.valueOf(ct.getDonGia()));
        } else {
            String maHD = maHDField.getText().trim();
            if (!maHD.isBlank()) selectHoaDon(hdBox, maHD);
        }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Hóa đơn"), gbc);
        gbc.gridx = 1;
        form.add(hdBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Sản phẩm"), gbc);
        gbc.gridx = 1;
        form.add(spBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Số lượng"), gbc);
        gbc.gridx = 1;
        form.add(soLuong, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Đơn giá"), gbc);
        gbc.gridx = 1;
        form.add(donGia, gbc);

        JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.setTitle(isNew ? "Thêm chi tiết hóa đơn" : "Sửa chi tiết hóa đơn");
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
                int qty = Integer.parseInt(soLuong.getText().trim());
                if (qty <= 0) throw new IllegalArgumentException("Số lượng phải > 0");
                double price = Double.parseDouble(donGia.getText().trim());
                if (price < 0) throw new IllegalArgumentException("Đơn giá phải >= 0");

                ct.setHoaDon((HoaDon) hdBox.getSelectedItem());
                ct.setSanPham((SanPham) spBox.getSelectedItem());
                ct.setSoLuong(qty);
                ct.setDonGia(price);

                if (isNew) {
                    service.create(ct);
                } else {
                    service.update(ct);
                }
                dialog.dispose();
                load();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    /**
     * Chọn hóa đơn theo mã trong combobox.
     *
     * @param box combobox hóa đơn
     * @param maHD mã hóa đơn cần chọn
     */
    private static void selectHoaDon(JComboBox<HoaDon> box, String maHD) {
        if (maHD == null) return;
        for (int i = 0; i < box.getItemCount(); i++) {
            HoaDon item = box.getItemAt(i);
            if (item != null && maHD.equals(item.getMaHD())) {
                box.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * Chọn sản phẩm theo mã trong combobox.
     *
     * @param box combobox sản phẩm
     * @param maSP mã sản phẩm cần chọn
     */
    private static void selectSanPham(JComboBox<SanPham> box, String maSP) {
        if (maSP == null) return;
        for (int i = 0; i < box.getItemCount(); i++) {
            SanPham item = box.getItemAt(i);
            if (item != null && maSP.equals(item.getMaSP())) {
                box.setSelectedIndex(i);
                return;
            }
        }
    }
}
