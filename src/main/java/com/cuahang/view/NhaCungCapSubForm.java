package com.cuahang.view;

import com.cuahang.entity.NhaCungCap;
import com.cuahang.service.NhaCungCapService;
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

public class NhaCungCapSubForm extends SubForm {
    private final NhaCungCapService service = new NhaCungCapService();
    private final boolean editable;

    private final JTextField searchField = new JTextField(30);
    private final JButton searchButton = new JButton("Tìm");
    private final JButton addButton = new JButton("Thêm");
    private final JButton editButton = new JButton("Sửa");
    private final JButton deleteButton = new JButton("Xóa");
    private final JButton refreshButton = new JButton("Tải lại");

    private final DefaultTableModel model = new DefaultTableModel(
        new Object[] {"MaNCC", "TenNCC", "DiaChi", "Email"},
        0
    );
    private final JTable table = new JTable(model);

    public NhaCungCapSubForm(boolean editable) {
        super("Nhà cung cấp (CRUD)");
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

        new SwingWorker<List<NhaCungCap>, Void>() {
            @Override
            protected List<NhaCungCap> doInBackground() {
                return service.search(searchField.getText());
            }

            @Override
            protected void done() {
                searchButton.setEnabled(true);
                refreshButton.setEnabled(true);
                model.setRowCount(0);
                try {
                    for (NhaCungCap ncc : get()) {
                        model.addRow(
                            new Object[] {
                                ncc.getMaNCC(),
                                ncc.getTenNCC(),
                                ncc.getDiaChi(),
                                ncc.getEmail()
                            }
                        );
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(NhaCungCapSubForm.this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private NhaCungCap getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        String maNCC = String.valueOf(model.getValueAt(row, 0));
        return service.findById(maNCC).orElse(null);
    }

    private void deleteSelected() {
        NhaCungCap ncc = getSelected();
        if (ncc == null) {
            JOptionPane.showMessageDialog(this, "Chọn 1 nhà cung cấp để xóa.", "Thiếu chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Xóa nhà cung cấp " + ncc.getMaNCC() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            service.deleteById(ncc.getMaNCC());
            load();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEditor(NhaCungCap editing) {
        if (!editable) return;
        boolean isNew = editing == null;
        NhaCungCap ncc = isNew ? new NhaCungCap() : editing;

        JTextField maNCC = new JTextField(15);
        JTextField tenNCC = new JTextField(25);
        JTextField diaChi = new JTextField(30);
        JTextField email = new JTextField(20);

        if (!isNew) {
            maNCC.setText(ncc.getMaNCC());
            maNCC.setEnabled(false);
            tenNCC.setText(ncc.getTenNCC());
            diaChi.setText(ncc.getDiaChi());
            email.setText(ncc.getEmail());
        }

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("MaNCC"), gbc);
        gbc.gridx = 1;
        form.add(maNCC, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("TenNCC"), gbc);
        gbc.gridx = 1;
        form.add(tenNCC, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("DiaChi"), gbc);
        gbc.gridx = 1;
        form.add(diaChi, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Email"), gbc);
        gbc.gridx = 1;
        form.add(email, gbc);

        JDialog dialog = new JDialog();
        dialog.setModal(true);
        dialog.setTitle(isNew ? "Thêm nhà cung cấp" : "Sửa nhà cung cấp");
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
                    String id = maNCC.getText().trim();
                    if (id.isBlank()) throw new IllegalArgumentException("MaNCC không được rỗng");
                    ncc.setMaNCC(id);
                }
                String name = tenNCC.getText().trim();
                if (name.isBlank()) throw new IllegalArgumentException("TenNCC không được rỗng");
                ncc.setTenNCC(name);
                
                ncc.setDiaChi(diaChi.getText().trim());
                ncc.setEmail(email.getText().trim());
                
                service.saveOrUpdate(ncc);
                dialog.dispose();
                load();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }
}
