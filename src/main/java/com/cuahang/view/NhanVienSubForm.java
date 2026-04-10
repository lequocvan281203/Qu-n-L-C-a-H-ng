package com.cuahang.view;

import com.cuahang.entity.NhanVien;
import com.cuahang.service.NhanVienService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class NhanVienSubForm extends SubForm {
    private final NhanVienService service = new NhanVienService();
    private final boolean editable;

    private final JTextField searchField = new JTextField(30);
    private final JButton searchButton = new JButton("Tìm");
    private final JButton addButton = new JButton("Thêm");
    private final JButton editButton = new JButton("Sửa");
    private final JButton deleteButton = new JButton("Xóa");
    private final JButton refreshButton = new JButton("Tải lại");

    private final DefaultTableModel model = new DefaultTableModel(
        new Object[] {"MaNV", "HoTen", "NgaySinh", "SoDT", "ChucVu"},
        0
    );
    private final JTable table = new JTable(model);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public NhanVienSubForm(boolean editable) {
        super("Nhân viên (CRUD)");
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

        new SwingWorker<List<NhanVien>, Void>() {
            @Override
            protected List<NhanVien> doInBackground() {
                return service.search(searchField.getText());
            }

            @Override
            protected void done() {
                searchButton.setEnabled(true);
                refreshButton.setEnabled(true);
                model.setRowCount(0);
                try {
                    for (NhanVien nv : get()) {
                        model.addRow(
                            new Object[] {
                                nv.getMaNV(),
                                nv.getHoTen(),
                                nv.getNgaySinh() != null ? dateFormat.format(nv.getNgaySinh()) : "",
                                nv.getSoDT(),
                                nv.getChucVu()
                            }
                        );
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(NhanVienSubForm.this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private NhanVien getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        String maNV = String.valueOf(model.getValueAt(row, 0));
        return service.findById(maNV).orElse(null);
    }

    private void deleteSelected() {
        NhanVien nv = getSelected();
        if (nv == null) {
            JOptionPane.showMessageDialog(this, "Chọn 1 nhân viên để xóa.", "Thiếu chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Xóa nhân viên " + nv.getMaNV() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            service.deleteById(nv.getMaNV());
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEditor(NhanVien editing) {
        if (!editable) return;
        boolean isNew = editing == null;
        NhanVien nv = isNew ? new NhanVien() : editing;

        JTextField maNV = new JTextField(15);
        JTextField hoTen = new JTextField(25);
        JTextField ngaySinh = new JTextField(15);
        JTextField soDT = new JTextField(15);
        JTextField chucVu = new JTextField(15);

        if (!isNew) {
            maNV.setText(nv.getMaNV());
            maNV.setEnabled(false);
            hoTen.setText(nv.getHoTen());
            if (nv.getNgaySinh() != null) {
                ngaySinh.setText(dateFormat.format(nv.getNgaySinh()));
            }
            soDT.setText(nv.getSoDT());
            chucVu.setText(nv.getChucVu());
        } else {
            ngaySinh.setText("2000-01-01");
        }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("MaNV"), gbc);
        gbc.gridx = 1;
        form.add(maNV, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("HoTen"), gbc);
        gbc.gridx = 1;
        form.add(hoTen, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("NgaySinh (yyyy-MM-dd)"), gbc);
        gbc.gridx = 1;
        form.add(ngaySinh, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("SoDT"), gbc);
        gbc.gridx = 1;
        form.add(soDT, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("ChucVu"), gbc);
        gbc.gridx = 1;
        form.add(chucVu, gbc);

        JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.setTitle(isNew ? "Thêm nhân viên" : "Sửa nhân viên");
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
                    String id = maNV.getText().trim();
                    if (id.isBlank()) throw new IllegalArgumentException("MaNV không được rỗng");
                    nv.setMaNV(id);
                }
                String name = hoTen.getText().trim();
                if (name.isBlank()) throw new IllegalArgumentException("HoTen không được rỗng");
                nv.setHoTen(name);
                
                String dateStr = ngaySinh.getText().trim();
                if (!dateStr.isBlank()) {
                    try {
                        nv.setNgaySinh(dateFormat.parse(dateStr));
                    } catch (ParseException ex) {
                        throw new IllegalArgumentException("Ngày sinh không đúng định dạng yyyy-MM-dd");
                    }
                } else {
                    nv.setNgaySinh(null);
                }
                
                nv.setSoDT(soDT.getText().trim());
                nv.setChucVu(chucVu.getText().trim());
                
                service.saveOrUpdate(nv);
                dialog.dispose();
                load();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }
}
