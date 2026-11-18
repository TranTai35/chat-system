package com.chatsystem.user.ui;

import com.chatsystem.common.ui.BaseDialog;
import com.chatsystem.common.ui.FormPanel;
import com.chatsystem.common.ui.Theme;
import com.chatsystem.common.ui.UiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * User. Dialog báo cáo spam (wireframe user/14-spam-report-dialog).
 * Chỉ 1 ô "Lý do" (tối đa 255 ký tự, theo CSDL spam_reports.reason).
 * Mở từ màn Tìm người, và sau này từ khung chat:
 * <pre>
 * new ReportSpamDialog(SwingUtilities.getWindowAncestor(this), "minhlq", "Lê Quang Minh").setVisible(true);
 * </pre>
 * Phiên bản 1: chỉ giao diện, chưa xử lý sự kiện.
 */
public class ReportSpamDialog extends BaseDialog {

    private final JTextField reasonField;

    public ReportSpamDialog(Window owner, String username, String fullName) {
        super(owner, "Báo cáo spam", "Admin sẽ xem xét và có thể khoá tài khoản này");

        FormPanel form = new FormPanel();
        JLabel account = new JLabel(username + " – " + fullName);
        account.setFont(Theme.FONT_BOLD);
        form.addField("Tài khoản", account);
        reasonField = form.addField("Lý do", UiUtil.textField("vd: Gửi tin rác", 20));

        setContent(form);
        setPrimaryText("Báo cáo");
        finish();
    }

    // Getter để gắn sự kiện ở Phiên bản 2
    public JTextField getReasonField() { return reasonField; }
}
