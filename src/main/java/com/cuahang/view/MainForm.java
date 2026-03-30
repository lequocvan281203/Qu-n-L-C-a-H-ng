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

    public MainForm(TaiKhoan taiKhoan) {
        setTitle("Quản lý cửa hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        JPanel leftMenu = new JPanel();
        leftMenu.setLayout(new javax.swing.BoxLayout(leftMenu, javax.swing.BoxLayout.Y_AXIS));
        leftMenu.setPreferredSize(new Dimension(240, 800));

        String role = taiKhoan != null ? taiKhoan.getQuyen() : "";
        JLabel userLabel = new JLabel("Xin chào: " + (taiKhoan != null ? taiKhoan.getUsername() : "") + " (" + role + ")");
        userLabel.setAlignmentX(LEFT_ALIGNMENT);
        leftMenu.add(userLabel);

        JButton posButton = new JButton("Bán hàng (POS)");
        JButton aiButton = new JButton("AI (Text-to-SQL)");
        JButton spButton = new JButton("Sản phẩm (CRUD)");
        JButton khButton = new JButton("Khách hàng (CRUD)");
        JButton reportButton = new JButton("Thống kê (JPQL)");

        posButton.setAlignmentX(LEFT_ALIGNMENT);
        aiButton.setAlignmentX(LEFT_ALIGNMENT);
        spButton.setAlignmentX(LEFT_ALIGNMENT);
        khButton.setAlignmentX(LEFT_ALIGNMENT);
        reportButton.setAlignmentX(LEFT_ALIGNMENT);

        leftMenu.add(posButton);
        leftMenu.add(aiButton);
        leftMenu.add(spButton);
        leftMenu.add(khButton);
        leftMenu.add(reportButton);

        setLayout(new BorderLayout());
        add(leftMenu, BorderLayout.WEST);
        add(desktop, BorderLayout.CENTER);

        posButton.addActionListener(e -> openSingleton(POSSubForm.class, () -> new POSSubForm()));
        aiButton.addActionListener(e -> openSingleton(AiChatSubForm.class, () -> new AiChatSubForm()));
        spButton.addActionListener(e -> openSingleton(SanPhamSubForm.class, () -> new SanPhamSubForm()));
        khButton.addActionListener(e -> openSingleton(KhachHangSubForm.class, () -> new KhachHangSubForm()));
        reportButton.addActionListener(e -> openSingleton(ReportSubForm.class, () -> new ReportSubForm()));

        openSingleton(POSSubForm.class, () -> new POSSubForm());
    }

    private interface FormFactory {
        SubForm create();
    }

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

