package com.cuahang.view;

import com.cuahang.entity.TaiKhoan;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainForm extends JFrame {
    private final JDesktopPane desktop = new JDesktopPane();

    /**
     * Khởi tạo màn hình chính và dựng menu theo quyền của tài khoản.
     *
     * @param taiKhoan tài khoản đăng nhập (có thể null)
     */
    public MainForm(TaiKhoan taiKhoan) {
        setTitle("Quản lý cửa hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        JPanel leftMenu = new JPanel();
        leftMenu.setLayout(new javax.swing.BoxLayout(leftMenu, javax.swing.BoxLayout.Y_AXIS));
        leftMenu.setPreferredSize(new Dimension(240, 800));

        String role = taiKhoan != null ? taiKhoan.getQuyen() : "User";
        boolean isAdmin = "Admin".equalsIgnoreCase(role);
        
        JLabel userLabel = new JLabel("Xin chào: " + (taiKhoan != null ? taiKhoan.getUsername() : "Khách") + " (" + role + ")");
        userLabel.setAlignmentX(LEFT_ALIGNMENT);
        leftMenu.add(userLabel);

        // Buttons
        JButton posButton = new JButton("Bán hàng (POS)");
        JButton aiButton = new JButton("AI (Text-to-SQL)");
        JButton khButton = new JButton("Khách hàng");
        JButton spButton = new JButton("Sản phẩm");
        JButton lspButton = new JButton("Loại sản phẩm");
        JButton hdButton = new JButton("Hóa đơn");
        JButton cthdButton = new JButton("Chi tiết hóa đơn");
        JButton nccButton = new JButton("Nhà cung cấp");
        JButton nvButton = new JButton("Nhân viên");
        JButton tkButton = new JButton("Tài khoản");
        JButton reportButton = new JButton("Thống kê (JPQL)");

        // Alignment
        posButton.setAlignmentX(LEFT_ALIGNMENT);
        aiButton.setAlignmentX(LEFT_ALIGNMENT);
        khButton.setAlignmentX(LEFT_ALIGNMENT);
        spButton.setAlignmentX(LEFT_ALIGNMENT);
        lspButton.setAlignmentX(LEFT_ALIGNMENT);
        hdButton.setAlignmentX(LEFT_ALIGNMENT);
        cthdButton.setAlignmentX(LEFT_ALIGNMENT);
        nccButton.setAlignmentX(LEFT_ALIGNMENT);
        nvButton.setAlignmentX(LEFT_ALIGNMENT);
        tkButton.setAlignmentX(LEFT_ALIGNMENT);
        reportButton.setAlignmentX(LEFT_ALIGNMENT);

        // Add to menu based on role
        leftMenu.add(posButton);
        leftMenu.add(aiButton);
        leftMenu.add(khButton);
        leftMenu.add(spButton);
        leftMenu.add(lspButton);
        leftMenu.add(hdButton);
        leftMenu.add(cthdButton);
        
        if (isAdmin) {
            leftMenu.add(nccButton);
            leftMenu.add(nvButton);
            leftMenu.add(tkButton);
            leftMenu.add(reportButton);
        }

        setLayout(new BorderLayout());
        add(leftMenu, BorderLayout.WEST);
        add(desktop, BorderLayout.CENTER);

        // Action Listeners
        posButton.addActionListener(e -> openSingleton(POSSubForm.class, () -> new POSSubForm()));
        aiButton.addActionListener(e -> openSingleton(AiChatSubForm.class, () -> new AiChatSubForm()));
        khButton.addActionListener(e -> openSingleton(KhachHangSubForm.class, () -> new KhachHangSubForm(isAdmin)));
        spButton.addActionListener(e -> openSingleton(SanPhamSubForm.class, () -> new SanPhamSubForm(isAdmin)));
        lspButton.addActionListener(e -> openSingleton(LoaiSanPhamSubForm.class, () -> new LoaiSanPhamSubForm(isAdmin)));
        hdButton.addActionListener(e -> openSingleton(HoaDonSubForm.class, () -> new HoaDonSubForm(isAdmin)));
        cthdButton.addActionListener(e -> openSingleton(ChiTietHoaDonSubForm.class, () -> new ChiTietHoaDonSubForm(isAdmin)));
        
        if (isAdmin) {
            nccButton.addActionListener(e -> openSingleton(NhaCungCapSubForm.class, () -> new NhaCungCapSubForm(true)));
            nvButton.addActionListener(e -> openSingleton(NhanVienSubForm.class, () -> new NhanVienSubForm(true)));
            tkButton.addActionListener(e -> openSingleton(TaiKhoanSubForm.class, () -> new TaiKhoanSubForm(true)));
            reportButton.addActionListener(e -> openSingleton(ReportSubForm.class, () -> new ReportSubForm()));
        }

        // Open POS by default
        openSingleton(POSSubForm.class, () -> new POSSubForm());
    }

    private interface FormFactory {
        SubForm create();
    }

    /**
     * Mở 1 SubForm theo kiểu singleton trong desktop (đã mở thì focus thay vì tạo mới).
     *
     * @param clazz loại SubForm
     * @param factory hàm tạo instance nếu chưa tồn tại
     */
    private void openSingleton(Class<? extends SubForm> clazz, FormFactory factory) {
        for (var f : desktop.getAllFrames()) {
            if (clazz.isInstance(f)) {
                try {
                    f.setSelected(true);
                } catch (Exception ignored) {
                }
                f.toFront();
                return;
            }
        }
        SubForm form = factory.create();
        desktop.add(form);
        form.setLocation(10, 10);
        try {
            form.setSelected(true);
        } catch (Exception ignored) {
        }
    }
}
