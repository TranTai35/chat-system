package com.chatsystem.user.ui;

import com.chatsystem.common.ui.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Hiển thị trạng thái Online / Offline trong bảng, dùng chung cho các màn bạn bè.
 * Giá trị ô là chuỗi {@link #ONLINE} hoặc {@link #OFFLINE}.
 * <pre>
 * table.getColumnModel().getColumn(0).setCellRenderer(StatusCellRenderer.dot());   // "●" xanh / xám
 * table.getColumnModel().getColumn(3).setCellRenderer(StatusCellRenderer.text());  // chữ "Online" xanh / "Offline" xám
 * </pre>
 */
public class StatusCellRenderer extends DefaultTableCellRenderer {

    public static final String ONLINE = "Online";
    public static final String OFFLINE = "Offline";

    /** Độ rộng cột chấm trạng thái. */
    public static final int DOT_COLUMN_WIDTH = 40;

    private final boolean dotOnly;

    private StatusCellRenderer(boolean dotOnly) {
        this.dotOnly = dotOnly;
    }

    /** Cột chỉ có chấm "●", căn giữa. */
    public static StatusCellRenderer dot() {
        return new StatusCellRenderer(true);
    }

    /** Cột chữ "Online" / "Offline" có màu. */
    public static StatusCellRenderer text() {
        return new StatusCellRenderer(false);
    }

    /** Chuyển true/false thành giá trị ô. */
    public static String of(boolean online) {
        return online ? ONLINE : OFFLINE;
    }

    /** Gắn renderer chấm vào cột và giữ cột hẹp, không giãn theo cửa sổ. */
    public static void installDotColumn(JTable table, int column) {
        var col = table.getColumnModel().getColumn(column);
        col.setCellRenderer(dot());
        col.setPreferredWidth(DOT_COLUMN_WIDTH);
        col.setMaxWidth(DOT_COLUMN_WIDTH + 8);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected,
                                                   boolean focused, int row, int column) {
        boolean online = ONLINE.equals(value);
        super.getTableCellRendererComponent(table, dotOnly ? "●" : value, selected, focused, row, column);
        setHorizontalAlignment(dotOnly ? CENTER : LEFT);
        if (!selected) {
            setForeground(online ? Theme.ONLINE : Theme.OFFLINE);
        }
        return this;
    }
}
