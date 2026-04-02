package com.cuahang.view;

import com.cuahang.entity.NhanVien;
import com.cuahang.entity.TaiKhoan;
import com.cuahang.service.TaiKhoanService;
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

public class TaiKhoanSubForm extends SubForm {
    private final TaiKhoanService service = new TaiKhoanService();
    private final boolean editable;

    private final JTextField searchField = new JTextField(30);
    private final JButton searchButton = new JButton("Tìm");
    private final JButton addButton = new JButton("Thêm");
    private final JButton editButton = new JButton("Sửa");
    private final JButton deleteButton = new JButton("Xóa");
    private final JButton refreshButton = new JButton("Tải lại");

    private final DefaultTableModel model = new DefaultTableModel(
        new Object[] {"Username", "Quyền", "Mã NV"},
        0
    );
    private final JTable table = new JTable(model);

    public TaiKhoanSubForm(boolean editable) {
        super("Tài khoản (CRUD)");
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

        new SwingWorker<List<TaiKhoan>, Void>() {
            @Override
            protected List<TaiKhoan> doInBackground() {
                return service.search(searchField.getText());
            }

            @Override
            protected void done() {
                searchButton.setEnabled(true);
                refreshButton.setEnabled(true);
                model.setRowCount(0);
                try {
                    for (TaiKhoan tk : get()) {
                        model.addRow(
                            new Object[] {
                                tk.getUsername(),
                                tk.getQuyen(),
                                tk.getNhanVien() != null ? tk.getNhanVien().getMaNV() : null
                            }
                        );
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TaiKhoanSubForm.this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private TaiKhoan getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        String username = String.valueOf(model.getValueAt(row, 0));
        return service.findById(username).orElse(null);
    }

    private void deleteSelected() {
        TaiKhoan tk = getSelected();
        if (tk == null) {
            JOptionPane.showMessageDialog(this, "Chọn 1 tài khoản để xóa.", "Thiếu chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Xóa tài khoản " + tk.getUsername() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            service.deleteById(tk.getUsername());
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEditor(TaiKhoan editing) {
        if (!editable) return;
        boolean isNew = editing == null;
        TaiKhoan tk = isNew ? new TaiKhoan() : editing;

        List<NhanVien> nhanViens = service.listNhanVien();

        JTextField username = new JTextField(15);
        JTextField password = new JTextField(15);
        JComboBox<String> quyenBox = new JComboBox<>(new String[]{"Admin", "User"});
        JComboBox<NhanVien> nvBox = new JComboBox<>(nhanViens.toArray(new NhanVien[0]));

        nvBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lb = new JLabel(value != null ? value.getMaNV() + " - " + value.getHoTen() : "");
            if (isSelected) lb.setOpaque(true);
            return lb;
        });

        if (!isNew) {
            username.setText(tk.getUsername());
            username.setEnabled(false);
            password.setText(tk.getPassword());
            quyenBox.setSelectedItem(tk.getQuyen());
            
            if (tk.getNhanVien() != null) {
                for (int i = 0; i < nvBox.getItemCount(); i++) {
                    if (nvBox.getItemAt(i).getMaNV().equals(tk.getNhanVien().getMaNV())) {
                        nvBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        form.add(username, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        form.add(password, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Quyền"), gbc);
        gbc.gridx = 1;
        form.add(quyenBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Nhân viên"), gbc);
        gbc.gridx = 1;
        form.add(nvBox, gbc);

        JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.setTitle(isNew ? "Thêm tài khoản" : "Sửa tài khoản");
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
                    String id = username.getText().trim();
                    if (id.isBlank()) throw new IllegalArgumentException("Username không được rỗng");
                    tk.setUsername(id);
                }
                String pwd = password.getText().trim();
                if (pwd.isBlank()) throw new IllegalArgumentException("Password không được rỗng");
                tk.setPassword(pwd);
                
                tk.setQuyen((String) quyenBox.getSelectedItem());
                tk.setNhanVien((NhanVien) nvBox.getSelectedItem());
                
                service.saveOrUpdate(tk);
                dialog.dispose();
                load();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }
}
