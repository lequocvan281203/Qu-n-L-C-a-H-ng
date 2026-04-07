package com.cuahang.view;

import java.awt.BorderLayout;

public class POSSubForm extends SubForm {
    public POSSubForm() {
        super("Bán hàng (POS)");
        setLayout(new BorderLayout());
        add(new POSPanel(), BorderLayout.CENTER);
    }
}

