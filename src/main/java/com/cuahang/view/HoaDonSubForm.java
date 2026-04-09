package com.cuahang.view;

import com.cuahang.entity.HoaDon;
import com.cuahang.entity.KhachHang;
import com.cuahang.entity.NhanVien;
import com.cuahang.service.HoaDonService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;
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

public class HoaDonSubForm extends SubForm {
    private final HoaDonService service = new HoaDonService();
    private final boolean editable;

    private final JTextField searchField = new JTextField(30);
    private final JButton searchButton = new JButton("Tìm");
    private final JButton addButton = new JButton("Thêm");
    private final JButton editButton = new JButton("Sửa");
    private final JButton deleteButton = new JButton("Xóa");
    private final JButton refreshButton = new JButton("Tải lại");

    private final DefaultTableModel model = new DefaultTableModel(
        new Object[] {"MaHD", "NgayLap", "TongTien", "TrangThai", "MaKH", "MaNV"},
        0
    );
    private final JTable table = new JTable(model);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public HoaDonSubForm(boolean editable) {
        super("Hóa đơn (CRUD)");
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

        new SwingWorker<List<HoaDon>, Void>() {
            @Override
            protected List<HoaDon> doInBackground() {
                return service.search(searchField.getText());
            }

            @Override
            protected void done() {
                searchButton.setEnabled(true);
                refreshButton.setEnabled(true);
                model.setRowCount(0);
                try {
                    for (HoaDon hd : get()) {
                        model.addRow(
                            new Object[] {
                                hd.getMaHD(),
                                hd.getNgayLap() != null ? dateFormat.format(hd.getNgayLap()) : "",
                                hd.getTongTien(),
                                hd.getTrangThai(),
                                hd.getKhachHang() != null ? hd.getKhachHang().getMaKH() : null,
                                hd.getNhanVien() != null ? hd.getNhanVien().getMaNV() : null
                            }
                        );
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(HoaDonSubForm.this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private HoaDon getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        String maHD = String.valueOf(model.getValueAt(row, 0));
        return service.findById(maHD).orElse(null);
    }

    private void deleteSelected() {
        HoaDon hd = getSelected();
        if (hd == null) {
            JOptionPane.showMessageDialog(this, "Chọn 1 hóa đơn để xóa.", "Thiếu chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Xóa hóa đơn " + hd.getMaHD() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            service.deleteById(hd.getMaHD());
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEditor(HoaDon editing) {
        if (!editable) return;
        boolean isNew = editing == null;
        HoaDon hd = isNew ? new HoaDon() : editing;

        List<KhachHang> khachHangs = service.listKhachHang();
        List<NhanVien> nhanViens = service.listNhanVien();

        JTextField maHD = new JTextField(15);
        JTextField trangThai = new JTextField(15);
        JComboBox<KhachHang> khBox = new JComboBox<>(khachHangs.toArray(new KhachHang[0]));
        JComboBox<NhanVien> nvBox = new JComboBox<>(nhanViens.toArray(new NhanVien[0]));

        khBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lb = new JLabel(value != null ? value.getMaKH() + " - " + value.getTenKH() : "");
            if (isSelected) lb.setOpaque(true);
            return lb;
        });

        nvBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lb = new JLabel(value != null ? value.getMaNV() + " - " + value.getHoTen() : "");
            if (isSelected) lb.setOpaque(true);
            return lb;
        });

        if (!isNew) {
            maHD.setText(hd.getMaHD());
            maHD.setEnabled(false);
            trangThai.setText(hd.getTrangThai());
            
            if (hd.getKhachHang() != null) {
                for (int i = 0; i < khBox.getItemCount(); i++) {
                    if (khBox.getItemAt(i).getMaKH().equals(hd.getKhachHang().getMaKH())) {
                        khBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
            if (hd.getNhanVien() != null) {
                for (int i = 0; i < nvBox.getItemCount(); i++) {
                    if (nvBox.getItemAt(i).getMaNV().equals(hd.getNhanVien().getMaNV())) {
                        nvBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } else {
            trangThai.setText("Pending");
        }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("MaHD"), gbc);
        gbc.gridx = 1;
        form.add(maHD, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("TrangThai"), gbc);
        gbc.gridx = 1;
        form.add(trangThai, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Khách hàng"), gbc);
        gbc.gridx = 1;
        form.add(khBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Nhân viên"), gbc);
        gbc.gridx = 1;
        form.add(nvBox, gbc);

        JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.setTitle(isNew ? "Thêm hóa đơn" : "Sửa hóa đơn");
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
                    String id = maHD.getText().trim();
                    if (id.isBlank()) throw new IllegalArgumentException("MaHD không được rỗng");
                    hd.setMaHD(id);
                    hd.setNgayLap(new Date());
                    hd.setTongTien(0.0);
                }
                hd.setTrangThai(trangThai.getText().trim());
                hd.setKhachHang((KhachHang) khBox.getSelectedItem());
                hd.setNhanVien((NhanVien) nvBox.getSelectedItem());
                
                service.saveOrUpdate(hd);
                dialog.dispose();
                load();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }
}
