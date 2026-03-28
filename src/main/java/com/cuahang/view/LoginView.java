package com.cuahang.view;

import com.cuahang.entity.TaiKhoan;
import com.cuahang.service.AuthService;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class LoginView extends JFrame {
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JButton loginButton = new JButton("Đăng nhập");

    private final AuthService authService = new AuthService();

    public LoginView() {
        setTitle("Đăng nhập - Quản lý cửa hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 220);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Tài khoản:"), gbc);
        gbc.gridx = 1;
        form.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        form.add(passwordField, gbc);

        JPanel actions = new JPanel();
        actions.add(loginButton);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> doLogin());
        getRootPane().setDefaultButton(loginButton);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản và mật khẩu.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        loginButton.setEnabled(false);

        new SwingWorker<Optional<TaiKhoan>, Void>() {
            @Override
            protected Optional<TaiKhoan> doInBackground() {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                loginButton.setEnabled(true);
                try {
                    Optional<TaiKhoan> tk = get();
                    if (tk.isPresent()) {
                        dispose();
                        new MainDashboard(tk.get()).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(LoginView.this, "Sai tài khoản hoặc mật khẩu.", "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginView.this, "Lỗi khi đăng nhập: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}

