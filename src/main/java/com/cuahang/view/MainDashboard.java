package com.cuahang.view;

import com.cuahang.entity.TaiKhoan;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainDashboard extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    public MainDashboard(TaiKhoan taiKhoan) {
        setTitle("Dashboard - Quản lý cửa hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel leftMenu = new JPanel();
        leftMenu.setLayout(new javax.swing.BoxLayout(leftMenu, javax.swing.BoxLayout.Y_AXIS));
        leftMenu.setPreferredSize(new Dimension(220, 700));

        JLabel userLabel = new JLabel("Xin chào: " + (taiKhoan != null ? taiKhoan.getUsername() : ""));
        userLabel.setAlignmentX(LEFT_ALIGNMENT);
        leftMenu.add(userLabel);

        JButton posButton = new JButton("Bán hàng");
        JButton aiButton = new JButton("AI (Text-to-SQL)");

        posButton.setAlignmentX(LEFT_ALIGNMENT);
        aiButton.setAlignmentX(LEFT_ALIGNMENT);

        leftMenu.add(posButton);
        leftMenu.add(aiButton);

        POSPanel posPanel = new POSPanel();
        AiChatPanel aiPanel = new AiChatPanel();

        contentPanel.add(posPanel, "POS");
        contentPanel.add(aiPanel, "AI");

        posButton.addActionListener(e -> cardLayout.show(contentPanel, "POS"));
        aiButton.addActionListener(e -> cardLayout.show(contentPanel, "AI"));

        setLayout(new BorderLayout());
        add(leftMenu, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "POS");
    }
}

