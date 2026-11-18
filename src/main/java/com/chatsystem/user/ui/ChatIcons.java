package com.chatsystem.user.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Biểu tượng nhỏ cho khung chat, vẽ bằng Java2D.
 * Font Segoe UI không có sẵn các ký tự 🔒 👥 ✨ 🔍 ⋮, nên vẽ tay để máy nào cũng hiện giống nhau.
 * Màu truyền vào lấy từ Theme, ví dụ: {@code ChatIcons.lock(16, Theme.WARNING)}.
 */
final class ChatIcons {

    private ChatIcons() {
    }

    /** Ổ khoá: nhóm đã mã hoá. */
    static Icon lock(int size, Color color) {
        return new BaseIcon(size, color) {
            @Override
            void draw(Graphics2D g, float s) {
                g.setStroke(new BasicStroke(s * 0.11f));
                g.draw(new Arc2D.Float(s * 0.30f, s * 0.08f, s * 0.40f, s * 0.44f, 0, 180, Arc2D.OPEN));
                g.draw(new Line2D.Float(s * 0.30f, s * 0.30f, s * 0.30f, s * 0.46f));
                g.draw(new Line2D.Float(s * 0.70f, s * 0.30f, s * 0.70f, s * 0.46f));
                g.fill(new RoundRectangle2D.Float(s * 0.16f, s * 0.44f, s * 0.68f, s * 0.50f, s * 0.16f, s * 0.16f));
            }
        };
    }

    /** Hai người: nhóm chat thường. */
    static Icon group(int size, Color color) {
        return new BaseIcon(size, color) {
            @Override
            void draw(Graphics2D g, float s) {
                g.fill(new Ellipse2D.Float(s * 0.52f, s * 0.14f, s * 0.30f, s * 0.30f));
                g.fill(new Arc2D.Float(s * 0.42f, s * 0.50f, s * 0.54f, s * 0.70f, 0, 180, Arc2D.CHORD));
                g.fill(new Ellipse2D.Float(s * 0.14f, s * 0.20f, s * 0.32f, s * 0.32f));
                g.fill(new Arc2D.Float(s * 0.02f, s * 0.58f, s * 0.56f, s * 0.70f, 0, 180, Arc2D.CHORD));
            }
        };
    }

    /** Ngôi sao lấp lánh: nút gợi ý bằng LLM. */
    static Icon sparkle(int size, Color color) {
        return new BaseIcon(size, color) {
            @Override
            void draw(Graphics2D g, float s) {
                g.fill(star(s * 0.42f, s * 0.56f, s * 0.40f));
                g.fill(star(s * 0.80f, s * 0.20f, s * 0.18f));
            }
        };
    }

    /** Kính lúp: tìm trong cuộc chat. */
    static Icon search(int size, Color color) {
        return new BaseIcon(size, color) {
            @Override
            void draw(Graphics2D g, float s) {
                g.setStroke(new BasicStroke(s * 0.12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(new Ellipse2D.Float(s * 0.12f, s * 0.12f, s * 0.52f, s * 0.52f));
                g.draw(new Line2D.Float(s * 0.58f, s * 0.58f, s * 0.88f, s * 0.88f));
            }
        };
    }

    /** Ba chấm dọc: menu thêm của cuộc chat. */
    static Icon more(int size, Color color) {
        return new BaseIcon(size, color) {
            @Override
            void draw(Graphics2D g, float s) {
                float d = s * 0.18f;
                for (int i = 0; i < 3; i++) {
                    g.fill(new Ellipse2D.Float((s - d) / 2, s * 0.10f + i * s * 0.32f, d, d));
                }
            }
        };
    }

    /** Ngôi sao 4 cánh, tâm (cx, cy), bán kính r. */
    private static Shape star(float cx, float cy, float r) {
        float k = r * 0.28f;
        Path2D.Float p = new Path2D.Float();
        p.moveTo(cx, cy - r);
        p.lineTo(cx + k, cy - k);
        p.lineTo(cx + r, cy);
        p.lineTo(cx + k, cy + k);
        p.lineTo(cx, cy + r);
        p.lineTo(cx - k, cy + k);
        p.lineTo(cx - r, cy);
        p.lineTo(cx - k, cy - k);
        p.closePath();
        return p;
    }

    private abstract static class BaseIcon implements Icon {
        private final int size;
        private final Color color;

        BaseIcon(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        abstract void draw(Graphics2D g, float s);

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);
            g2.setColor(color);
            draw(g2, size);
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return size; }

        @Override
        public int getIconHeight() { return size; }
    }
}
