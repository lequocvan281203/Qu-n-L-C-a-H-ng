package com.cuahang.main;

import com.cuahang.view.LoginView;
import com.cuahang.service.BootstrapService;
import com.formdev.flatlaf.FlatLightLaf;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.hibernate.Session;
import com.cuahang.util.HibernateUtil;

public class MainApp {
    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            try (Session ignored = HibernateUtil.getSessionFactory().openSession()) {
                boolean created = new BootstrapService().ensureDefaultAdmin();
                if (created) {
                    JOptionPane.showMessageDialog(
                        null,
                        "Đã tạo tài khoản mặc định: admin / 123456",
                        "Tài khoản mặc định",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
                new LoginView().setVisible(true);
            } catch (Throwable e) {
                JOptionPane.showMessageDialog(
                    null,
                    buildStartupErrorMessage(e),
                    "Lỗi kết nối",
                    JOptionPane.ERROR_MESSAGE
                );
                HibernateUtil.shutdown();
            }
        });
    }

    private static String buildStartupErrorMessage(Throwable e) {
        Throwable root = rootCause(e);
        String msg = root.getMessage() != null ? root.getMessage() : e.toString();

        if (containsIgnoreCase(msg, "Unknown database") || hasSqlErrorCode(root, 1049)) {
            return String.join(
                "\n",
                "Không tìm thấy database 'quanlycuahang'.",
                "- Hãy tạo DB bằng MySQL Workbench và chạy script: _workspace/QUANLYCUAHANG.txt",
                "- Kiểm tra lại user/pass trong src/main/resources/hibernate.cfg.xml",
                "\nChi tiết: " + msg
            );
        }

        return "Không thể kết nối CSDL. Vui lòng kiểm tra MySQL/hibernate.cfg.xml.\n\nChi tiết: " + msg;
    }

    private static boolean hasSqlErrorCode(Throwable t, int code) {
        Throwable cur = t;
        while (cur != null) {
            if (cur instanceof SQLException sqlEx) {
                if (sqlEx.getErrorCode() == code) return true;
            }
            cur = cur.getCause();
        }
        return false;
    }

    private static Throwable rootCause(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur;
    }

    private static boolean containsIgnoreCase(String haystack, String needle) {
        return haystack.toLowerCase().contains(needle.toLowerCase());
    }
}
