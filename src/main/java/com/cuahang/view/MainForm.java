package com.cuahang.view;

import com.cuahang.entity.TaiKhoan;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

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
        leftMenu.setBorder(new EmptyBorder(16, 12, 16, 12));
        leftMenu.setBackground(new Color(0x0F172A));

        String role = taiKhoan != null ? taiKhoan.getQuyen() : "User";
        boolean isAdmin = "Admin".equalsIgnoreCase(role);
        
        JLabel userLabel = new JLabel("<html><b>" + (taiKhoan != null ? taiKhoan.getUsername() : "Khách") + "</b><br/>" + role + "</html>");
        userLabel.setAlignmentX(LEFT_ALIGNMENT);
        userLabel.setForeground(Color.WHITE);
        userLabel.setBorder(new EmptyBorder(0, 6, 12, 6));
        leftMenu.add(userLabel);
        leftMenu.add(Box.createVerticalStrut(8));

        // Buttons
        JButton posButton = createMenuButton("Bán hàng (POS)");
        JButton aiButton = createMenuButton("AI (Text-to-SQL)");
        JButton khButton = createMenuButton("Khách hàng");
        JButton spButton = createMenuButton("Sản phẩm");
        JButton lspButton = createMenuButton("Loại sản phẩm");
        JButton hdButton = createMenuButton("Hóa đơn");
        JButton cthdButton = createMenuButton("Chi tiết hóa đơn");
        JButton nccButton = createMenuButton("Nhà cung cấp");
        JButton nvButton = createMenuButton("Nhân viên");
        JButton tkButton = createMenuButton("Tài khoản");
        JButton reportButton = createMenuButton("Thống kê (JPQL)");

        markPrimary(posButton);
        markPrimary(aiButton);

        // Add to menu based on role
        leftMenu.add(posButton);
        leftMenu.add(aiButton);
        leftMenu.add(Box.createVerticalStrut(8));
        leftMenu.add(sectionLabel("Nghiệp vụ"));
        leftMenu.add(khButton);
        leftMenu.add(spButton);
        leftMenu.add(lspButton);
        leftMenu.add(hdButton);
        leftMenu.add(cthdButton);
        
        if (isAdmin) {
            leftMenu.add(Box.createVerticalStrut(8));
            leftMenu.add(sectionLabel("Quản trị"));
            leftMenu.add(nccButton);
            leftMenu.add(nvButton);
            leftMenu.add(tkButton);
            leftMenu.add(Box.createVerticalStrut(8));
            leftMenu.add(sectionLabel("Báo cáo"));
            leftMenu.add(reportButton);
        }

        setLayout(new BorderLayout());
        add(leftMenu, BorderLayout.WEST);
        add(desktop, BorderLayout.CENTER);
        desktop.setBackground(new Color(0xF1F5F9));

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

    private static JLabel sectionLabel(String text) {
        JLabel lb = new JLabel(text.toUpperCase());
        lb.setAlignmentX(LEFT_ALIGNMENT);
        lb.setForeground(new Color(0x94A3B8));
        lb.setBorder(new EmptyBorder(8, 6, 6, 6));
        return lb;
    }

    private static JButton createMenuButton(String text) {
        JButton b = new JButton(text);
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 12, 10, 12));
        b.setOpaque(true);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(0x111827));
        b.setMargin(new Insets(0, 0, 0, 0));
        b.putClientProperty("FlatLaf.style", "arc: 12");
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return b;
    }

    private static void markPrimary(JButton b) {
        b.setBackground(new Color(0x1D4ED8));
        b.putClientProperty("FlatLaf.style", "arc: 12; font: +1; fontStyle: bold");
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
