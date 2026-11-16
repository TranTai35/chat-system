package com.chatsystem.common.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Form 2 cột (nhãn | ô nhập) dùng cho dialog thêm/sửa, đăng ký, profile...
 *
 * <pre>
 * FormPanel form = new FormPanel();
 * JTextField username = form.addField("Tên đăng nhập", new JTextField(20));
 * JComboBox&lt;String&gt; gender = form.addField("Giới tính", new JComboBox&lt;&gt;(new String[]{"Nam", "Nữ"}));
 * form.addButtons(UiUtil.button("Huỷ"), UiUtil.primaryButton("Lưu"));
 * </pre>
 */
public class FormPanel extends JPanel {

    private int row = 0;

    public FormPanel() {
        super(new GridBagLayout());
        setOpaque(false);
    }

    /** Thêm 1 dòng: nhãn bên trái, ô nhập bên phải. Trả về chính ô nhập. */
    public <T extends JComponent> T addField(String label, T field) {
        GridBagConstraints c = base();
        c.gridx = 0;
        c.weightx = 0;
        c.anchor = GridBagConstraints.LINE_START;
        JLabel l = new JLabel(label);
        l.setLabelFor(field);
        add(l, c);

        c = base();
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(field, c);
        row++;
        return field;
    }

    /** Thêm 1 component chiếm cả 2 cột (ghi chú, checkbox, link...). */
    public <T extends JComponent> T addFullRow(T comp) {
        GridBagConstraints c = base();
        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(comp, c);
        row++;
        return comp;
    }

    /** Hàng nút căn phải ở cuối form. */
    public void addButtons(JButton... buttons) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.GAP, 0));
        bar.setOpaque(false);
        for (JButton b : buttons) {
            bar.add(b);
        }
        GridBagConstraints c = base();
        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(Theme.PADDING, 0, 0, 0);
        add(bar, c);
        row++;
    }

    private GridBagConstraints base() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.insets = new Insets(Theme.GAP / 2, Theme.GAP / 2, Theme.GAP / 2, Theme.GAP / 2);
        return c;
    }
}
