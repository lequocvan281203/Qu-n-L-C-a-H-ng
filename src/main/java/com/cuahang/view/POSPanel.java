package com.cuahang.view;

import com.cuahang.dao.SanPhamDAO;
import com.cuahang.entity.SanPham;
import com.cuahang.service.BanHangService;
import com.cuahang.service.dto.PurchaseItem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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
    private final JTextField productSearchField = new JTextField(24);

    private final JTextField maHDField = new JTextField(12);
    private final JTextField maKHField = new JTextField(12);
    private final JTextField maNVField = new JTextField(12);

    private final JTextField soLuongField = new JTextField(6);

    private final DefaultTableModel cartTableModel = new DefaultTableModel(
        new Object[] {"MaSP", "TenSP", "DonGia", "SoLuong", "ThanhTien"},
        0
    );
    private final JTable cartTable = new JTable(cartTableModel);
    private final List<PurchaseItem> cartItems = new ArrayList<>();

    private final JButton refreshButton = new JButton("Tải lại");
    private final JButton addToCartButton = new JButton("Thêm");
    private final JButton removeFromCartButton = new JButton("Xóa");
    private final JButton createInvoiceButton = new JButton("Tạo hóa đơn");
    private final JButton clearCartButton = new JButton("Xóa giỏ");
    private final JLabel totalLabel = new JLabel("Tổng tiền: 0");
    private final NumberFormat currency = NumberFormat.getInstance();

    public POSPanel() {
        setLayout(new BorderLayout());

        productSearchField.putClientProperty("JTextField.placeholderText", "Tìm sản phẩm (mã/tên)...");
        productSearchField.setPreferredSize(new Dimension(280, 32));

        productTable.setAutoCreateRowSorter(true);
        productTable.setFillsViewportHeight(true);
        productTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(90);

        cartTable.setFillsViewportHeight(true);
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(110);

        UiDefaults.styleActionButton(addToCartButton);
        UiDefaults.styleActionButton(removeFromCartButton);
        UiDefaults.styleActionButton(clearCartButton);
        createInvoiceButton.putClientProperty("FlatLaf.style", "arc: 12");
        createInvoiceButton.setFont(createInvoiceButton.getFont().deriveFont(java.awt.Font.BOLD));

        JPanel left = new JPanel(new BorderLayout());
        left.add(buildProductToolbar(), BorderLayout.NORTH);
        left.add(new JScrollPane(productTable), BorderLayout.CENTER);
        left.add(buildAddToCartBar(), BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout());
        right.add(buildOrderHeader(), BorderLayout.NORTH);
        right.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        right.add(buildCheckoutBar(), BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.6);
        add(split, BorderLayout.CENTER);

        refreshButton.addActionListener(e -> loadProducts());
        addToCartButton.addActionListener(e -> addSelectedToCart());
        removeFromCartButton.addActionListener(e -> removeSelectedFromCart());
        clearCartButton.addActionListener(e -> clearCart());
        createInvoiceButton.addActionListener(e -> createInvoice());
        productSearchField.addActionListener(e -> loadProducts());

        productTable.getSelectionModel().addListSelectionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                soLuongField.requestFocus();
            }
        });

        loadProducts();
    }

    private JPanel buildProductToolbar() {
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        left.add(new JLabel("Sản phẩm"));
        left.add(productSearchField);
        left.add(refreshButton);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(left, BorderLayout.WEST);
        return wrap;
    }

    private JPanel buildAddToCartBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        soLuongField.putClientProperty("JTextField.placeholderText", "SL");
        soLuongField.setPreferredSize(new Dimension(80, 32));
        bar.add(new JLabel("Số lượng"));
        bar.add(soLuongField);
        bar.add(addToCartButton);
        bar.add(removeFromCartButton);
        return bar;
    }

    private JPanel buildOrderHeader() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        maHDField.putClientProperty("JTextField.placeholderText", "HDxxx");
        maKHField.putClientProperty("JTextField.placeholderText", "KHxx");
        maNVField.putClientProperty("JTextField.placeholderText", "NVxx");

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("MaHD"), gbc);
        gbc.gridx = 1;
        form.add(maHDField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("MaKH"), gbc);
        gbc.gridx = 1;
        form.add(maKHField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("MaNV"), gbc);
        gbc.gridx = 1;
        form.add(maNVField, gbc);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(form, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildCheckoutBar() {
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        totalLabel.putClientProperty("FlatLaf.style", "font: +1");
        totalLabel.setFont(totalLabel.getFont().deriveFont(java.awt.Font.BOLD));
        left.add(totalLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        right.add(clearCartButton);
        right.add(createInvoiceButton);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(left, BorderLayout.WEST);
        wrap.add(right, BorderLayout.EAST);
        return wrap;
    }

    private void loadProducts() {
        refreshButton.setEnabled(false);
        new SwingWorker<List<SanPham>, Void>() {
            @Override
            protected List<SanPham> doInBackground() {
                String keyword = productSearchField.getText() != null ? productSearchField.getText().trim() : "";
                if (keyword.isBlank()) {
                    return sanPhamDAO.findAll();
                }
                return sanPhamDAO.search(keyword);
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

    private void addSelectedToCart() {
        int viewRow = productTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 sản phẩm.", "Thiếu chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = productTable.convertRowIndexToModel(viewRow);
        String maSP = String.valueOf(productTableModel.getValueAt(row, 0));
        String tenSP = String.valueOf(productTableModel.getValueAt(row, 1));
        double gia = Double.parseDouble(String.valueOf(productTableModel.getValueAt(row, 2)));
        int ton = Integer.parseInt(String.valueOf(productTableModel.getValueAt(row, 4)));

        String soLuongText = soLuongField.getText().trim();
        if (soLuongText.isBlank()) {
            JOptionPane.showMessageDialog(this, "Nhập số lượng.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
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

        if (soLuong > ton) {
            JOptionPane.showMessageDialog(this, "Vượt tồn kho (" + ton + ").", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean merged = false;
        for (int i = 0; i < cartItems.size(); i++) {
            PurchaseItem item = cartItems.get(i);
            if (item.maSP().equals(maSP)) {
                int newQty = item.soLuong() + soLuong;
                if (newQty > ton) {
                    JOptionPane.showMessageDialog(this, "Vượt tồn kho (" + ton + ").", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                cartItems.set(i, new PurchaseItem(maSP, newQty));
                updateCartRow(i, maSP, tenSP, gia, newQty);
                merged = true;
                break;
            }
        }
        if (!merged) {
            cartItems.add(new PurchaseItem(maSP, soLuong));
            cartTableModel.addRow(new Object[] {maSP, tenSP, gia, soLuong, gia * soLuong});
        }

        recalcTotal();
        soLuongField.setText("");
    }

    private void updateCartRow(int index, String maSP, String tenSP, double gia, int soLuong) {
        cartTableModel.setValueAt(maSP, index, 0);
        cartTableModel.setValueAt(tenSP, index, 1);
        cartTableModel.setValueAt(gia, index, 2);
        cartTableModel.setValueAt(soLuong, index, 3);
        cartTableModel.setValueAt(gia * soLuong, index, 4);
    }

    private void removeSelectedFromCart() {
        int viewRow = cartTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 dòng trong giỏ.", "Thiếu chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = cartTable.convertRowIndexToModel(viewRow);
        String maSP = String.valueOf(cartTableModel.getValueAt(row, 0));
        cartTableModel.removeRow(row);
        cartItems.removeIf(i -> i.maSP().equals(maSP));
        recalcTotal();
    }

    private void clearCart() {
        cartItems.clear();
        cartTableModel.setRowCount(0);
        recalcTotal();
    }

    private void recalcTotal() {
        double sum = 0;
        for (int r = 0; r < cartTableModel.getRowCount(); r++) {
            Object v = cartTableModel.getValueAt(r, 4);
            if (v != null) sum += Double.parseDouble(String.valueOf(v));
        }
        totalLabel.setText("Tổng tiền: " + currency.format(sum));
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
