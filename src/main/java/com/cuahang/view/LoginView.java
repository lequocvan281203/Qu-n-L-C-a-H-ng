package com.cuahang.view;

import com.cuahang.entity.TaiKhoan;
import com.cuahang.service.AuthService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Insets;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Optional;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

public class LoginView extends JFrame {
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JButton loginButton = new JButton("Đăng nhập");
    private final JButton quickAdminButton = new JButton("Admin demo");
    private final JButton quickUserButton = new JButton("User demo");
    private final JCheckBox showPassword = new JCheckBox("Hiện mật khẩu");
    private final JLabel statusLabel = new JLabel(" ");

    private final AuthService authService = new AuthService();

    public LoginView() {
        setTitle("Đăng nhập - Quản lý cửa hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(0xF1F5F9));
        root.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel shell = new JPanel(new BorderLayout());
        shell.putClientProperty("FlatLaf.style", "arc: 20");
        shell.setBorder(new EmptyBorder(0, 0, 0, 0));
        shell.setPreferredSize(new Dimension(820, 440));
        shell.setBackground(Color.WHITE);

        JPanel brand = new BrandPanel();
        brand.setPreferredSize(new Dimension(300, 440));

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(28, 28, 28, 28));
        card.setLayout(new javax.swing.BoxLayout(card, javax.swing.BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Đăng nhập");
        title.setAlignmentX(LEFT_ALIGNMENT);
        title.putClientProperty("FlatLaf.style", "font: +8");
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        JLabel subtitle = new JLabel("Chào mừng bạn quay lại");
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        subtitle.putClientProperty("FlatLaf.style", "foreground: #64748B");

        usernameField.putClientProperty("JTextField.placeholderText", "Tên đăng nhập (vd: admin, user)");
        passwordField.putClientProperty("JTextField.placeholderText", "Mật khẩu");
        loginButton.putClientProperty("FlatLaf.style", "arc: 12; minimumWidth: 140");
        loginButton.setFont(loginButton.getFont().deriveFont(Font.BOLD));
        quickAdminButton.putClientProperty("FlatLaf.style", "arc: 10");
        quickUserButton.putClientProperty("FlatLaf.style", "arc: 10");

        statusLabel.setForeground(new Color(0xDC2626));

        char echoChar = passwordField.getEchoChar();
        showPassword.addActionListener(e -> passwordField.setEchoChar(showPassword.isSelected() ? (char) 0 : echoChar));

        JLabel demo = new JLabel("<html><b>Demo:</b> admin / 123456, user / 123456</html>");
        demo.putClientProperty("FlatLaf.style", "foreground: #64748B");

        quickAdminButton.addActionListener(e -> {
            usernameField.setText("admin");
            passwordField.setText("123456");
            usernameField.requestFocus();
        });
        quickUserButton.addActionListener(e -> {
            usernameField.setText("user");
            passwordField.setText("123456");
            usernameField.requestFocus();
        });

        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel quickWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        quickWrap.setAlignmentX(LEFT_ALIGNMENT);
        quickWrap.setOpaque(false);
        quickWrap.add(quickAdminButton);
        quickWrap.add(quickUserButton);

        JLabel userLabel = new JLabel("Tài khoản");
        userLabel.setAlignmentX(LEFT_ALIGNMENT);
        JLabel passLabel = new JLabel("Mật khẩu");
        passLabel.setAlignmentX(LEFT_ALIGNMENT);
        showPassword.setAlignmentX(LEFT_ALIGNMENT);
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);
        demo.setAlignmentX(LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(14));
        card.add(quickWrap);
        card.add(Box.createVerticalStrut(14));
        card.add(userLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(10));
        card.add(passLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(8));
        card.add(showPassword);
        card.add(Box.createVerticalStrut(12));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(8));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(demo);

        shell.add(brand, BorderLayout.WEST);
        shell.add(card, BorderLayout.CENTER);
        root.add(shell, BorderLayout.CENTER);
        add(root, BorderLayout.CENTER);

        loginButton.addActionListener(e -> doLogin());
        getRootPane().setDefaultButton(loginButton);
    }

    private void doLogin() {
        statusLabel.setText(" ");
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isBlank() || password.isBlank()) {
            statusLabel.setText("Vui lòng nhập tài khoản và mật khẩu.");
            return;
        }

        setBusy(true, "Đang đăng nhập...");

        new SwingWorker<Optional<TaiKhoan>, Void>() {
            @Override
            protected Optional<TaiKhoan> doInBackground() {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                setBusy(false, " ");
                try {
                    Optional<TaiKhoan> tk = get();
                    if (tk.isPresent()) {
                        dispose();
                        new MainForm(tk.get()).setVisible(true);
                    } else {
                        statusLabel.setText("Sai tài khoản hoặc mật khẩu.");
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginView.this, "Lỗi khi đăng nhập: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void setBusy(boolean busy, String message) {
        statusLabel.setText(message != null ? message : " ");
        loginButton.setEnabled(!busy);
        usernameField.setEnabled(!busy);
        passwordField.setEnabled(!busy);
        showPassword.setEnabled(!busy);
        quickAdminButton.setEnabled(!busy);
        quickUserButton.setEnabled(!busy);
    }

    private static final class BrandPanel extends JPanel {
        private BrandPanel() {
            setOpaque(true);
            setBackground(new Color(0x1D4ED8));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, new Color(0x1D4ED8), 0, h, new Color(0x0EA5E9));
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            int cx = 56;
            int cy = 64;
            int r = 44;
            g2.setColor(new Color(255, 255, 255, 32));
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            g2.setColor(new Color(255, 255, 255, 220));
            g2.fillOval(cx - 34, cy - 34, 68, 68);

            g2.setColor(new Color(0x1D4ED8));
            Font logoFont = getFont().deriveFont(Font.BOLD, 26f);
            g2.setFont(logoFont);
            FontMetrics fm = g2.getFontMetrics();
            String logo = "SM";
            int lw = fm.stringWidth(logo);
            int lh = fm.getAscent();
            g2.drawString(logo, cx - lw / 2, cy + lh / 2 - 2);

            g2.setColor(Color.WHITE);
            Font tFont = getFont().deriveFont(Font.BOLD, 22f);
            g2.setFont(tFont);
            g2.drawString("SMART-MART", 28, 150);

            g2.setColor(new Color(255, 255, 255, 210));
            g2.setFont(getFont().deriveFont(Font.PLAIN, 13f));
            g2.drawString("Quản lý cửa hàng tiện lợi", 28, 176);
            g2.drawString("POS • CRM • AI Text-to-SQL", 28, 196);

            g2.setColor(new Color(255, 255, 255, 190));
            g2.drawString("Demo nhanh:", 28, 240);
            g2.drawString("- admin / 123456", 28, 262);
            g2.drawString("- user / 123456", 28, 282);

            g2.dispose();
        }
    }
}
