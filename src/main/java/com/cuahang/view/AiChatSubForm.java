package com.cuahang.view;

import java.awt.BorderLayout;

public class AiChatSubForm extends SubForm {
    public AiChatSubForm() {
        super("AI (Text-to-SQL)");
        setLayout(new BorderLayout());
        add(new AiChatPanel(), BorderLayout.CENTER);
    }
}

