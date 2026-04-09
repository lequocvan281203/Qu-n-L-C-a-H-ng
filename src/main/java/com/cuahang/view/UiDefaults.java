package com.cuahang.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.UIManager;

public final class UiDefaults {
    private UiDefaults() {
    }

    public static void apply() {
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.rowHeight", 28);
        UIManager.put("TitlePane.unifiedBackground", true);

        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Component.innerFocusWidth", 0);

        UIManager.put("Button.margin", new Insets(7, 14, 7, 14));

        UIManager.put("Component.accentColor", new Color(0x2563EB));
        UIManager.put("Component.focusColor", new Color(0x2563EB));

        Font font = UIManager.getFont("defaultFont");
        if (font != null) {
            UIManager.put("defaultFont", font.deriveFont(Font.PLAIN, 13f));
        }
    }

    public static void styleActionButton(JButton b) {
        String text = b.getText() != null ? b.getText().trim() : "";

        b.setFocusPainted(false);
        b.putClientProperty("FlatLaf.style", "arc: 12; margin: 7,14,7,14");

        if (text.equalsIgnoreCase("Xóa") || text.equalsIgnoreCase("Xoa")) {
            b.setForeground(Color.WHITE);
            b.setBackground(new Color(0xDC2626));
            b.putClientProperty("FlatLaf.style", "arc: 12; margin: 7,14,7,14; fontStyle: bold");
        } else if (text.equalsIgnoreCase("Thêm") || text.equalsIgnoreCase("Them")) {
            b.setForeground(Color.WHITE);
            b.setBackground(new Color(0x16A34A));
            b.putClientProperty("FlatLaf.style", "arc: 12; margin: 7,14,7,14; fontStyle: bold");
        } else if (text.equalsIgnoreCase("Sửa") || text.equalsIgnoreCase("Sua")) {
            b.setForeground(Color.WHITE);
            b.setBackground(new Color(0xF59E0B));
            b.putClientProperty("FlatLaf.style", "arc: 12; margin: 7,14,7,14; fontStyle: bold");
        }
    }
}
