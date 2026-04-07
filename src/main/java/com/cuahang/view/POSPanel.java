package com.cuahang.view;

import com.cuahang.dao.SanPhamDAO;
import com.cuahang.entity.SanPham;
import com.cuahang.service.BanHangService;
import com.cuahang.service.dto.PurchaseItem;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class POSPanel extends JPanel {
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final BanHangService banHangService = new BanHangService();

    private final DefaultTableModel productTableModel = new DefaultTableModel(
        new Object[] {"MaSP", "TenSP", "GiaBan", "DonViTinh", "SoLuongTon"},
        0
    );
    private final JTable productTable = new JTable(productTableModel);

    private final JTextField maHDField = new JTextField(12);
    private final JTextField maKHField = new JTextField(12);
    private final JTextField maNVField = new JTextField(12);

    private final JTextField maSPField = new JTextField(12);
    private final JTextField soLuongField = new JTextField(6);

    private final DefaultListModel<String> cartListModel = new DefaultListModel<>();
    private final JList<String> cartList = new JList<>(cartListModel);
    private final List<PurchaseItem> cartItems = new ArrayList<>();

    private final JButton refreshButton = new JButton("Tải lại sản phẩm");
    private final JButton addToCartButton = new JButton("Thêm vào giỏ");
    private final JButton createInvoiceButton = new JButton("Tạo hóa đơn");
    private final JButton clearCartButton = new JButton("Xóa giỏ");

    public POSPanel() {
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(refreshButton);
        add(top, BorderLayout.NORTH);

        add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.add(buildOrderForm(), BorderLayout.NORTH);
        right.add(new JScrollPane(cartList), BorderLayout.CENTER);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rightActions.add(createInvoiceButton);
        rightActions.add(clearCartButton);
        right.add(rightActions, BorderLayout.SOUTH);

        add(right, BorderLayout.EAST);

        refreshButton.addActionListener(e -> loadProducts());
        addToCartButton.addActionListener(e -> addToCart());
        clearCartButton.addActionListener(e -> clearCart());
        createInvoiceButton.addActionListener(e -> createInvoice());

        productTable.getSelectionModel().addListSelectionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                Object ma = productTableModel.getValueAt(row, 0);
                if (ma != null) maSPField.setText(ma.toString());
            }
        });

        loadProducts();
    }

    private JPanel buildOrderForm() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;

        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("MaHD:"), gbc);
        gbc.gridx = 1;
        form.add(maHDField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("MaKH:"), gbc);
        gbc.gridx = 1;
        form.add(maKHField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("MaNV:"), gbc);
        gbc.gridx = 1;
        form.add(maNVField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("MaSP:"), gbc);
        gbc.gridx = 1;
        form.add(maSPField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1;
        form.add(soLuongField, gbc);

        y++;
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        form.add(addToCartButton, gbc);

        return form;
    }

    private void loadProducts() {
        refreshButton.setEnabled(false);
        new SwingWorker<List<SanPham>, Void>() {
            @Override
            protected List<SanPham> doInBackground() {
                return sanPhamDAO.findAll();
            }

            @Override
            protected void done() {
                refreshButton.setEnabled(true);
                productTableModel.setRowCount(0);
                try {
                    for (SanPham sp : get()) {
                        productTableModel.addRow(
                            new Object[] {
                                sp.getMaSP(),
                                sp.getTenSP(),
                                sp.getGiaBan(),
                                sp.getDonViTinh(),
                                sp.getSoLuongTon()
                            }
                        );
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(POSPanel.this, "Không tải được danh sách sản phẩm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void addToCart() {
        String maSP = maSPField.getText().trim();
        String soLuongText = soLuongField.getText().trim();
        if (maSP.isBlank() || soLuongText.isBlank()) {
            JOptionPane.showMessageDialog(this, "Nhập MaSP và số lượng.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int soLuong;
        try {
            soLuong = Integer.parseInt(soLuongText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (soLuong <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng phải > 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PurchaseItem item = new PurchaseItem(maSP, soLuong);
        cartItems.add(item);
        cartListModel.addElement(maSP + " x" + soLuong);
        soLuongField.setText("");
    }

    private void clearCart() {
        cartItems.clear();
        cartListModel.clear();
    }

    private void createInvoice() {
        String maHD = maHDField.getText().trim();
        if (maHD.isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập MaHD.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ đang trống.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        createInvoiceButton.setEnabled(false);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                banHangService.taoHoaDon(maHD, maKHField.getText().trim(), maNVField.getText().trim(), List.copyOf(cartItems));
                return null;
            }

            @Override
            protected void done() {
                createInvoiceButton.setEnabled(true);
                try {
                    get();
                    JOptionPane.showMessageDialog(POSPanel.this, "Tạo hóa đơn thành công.", "OK", JOptionPane.INFORMATION_MESSAGE);
                    clearCart();
                    loadProducts();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(POSPanel.this, "Tạo hóa đơn thất bại: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}

