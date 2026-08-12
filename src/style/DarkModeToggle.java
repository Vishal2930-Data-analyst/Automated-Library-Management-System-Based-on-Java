package style;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * DarkModeToggle — animated sliding toggle switch.
 * Fully self-contained, always visible, no external dependencies.
 *
 * Usage:  panel.add(new DarkModeToggle());
 */
public class DarkModeToggle extends JComponent {

    // ── Fixed sizes ───────────────────────────────────────────────────
    private static final int TRACK_W  = 56;   // track width
    private static final int TRACK_H  = 28;   // track height
    private static final int THUMB_D  = 22;   // thumb diameter
    private static final int THUMB_PAD = 3;   // padding around thumb
    private static final int TOTAL_W  = 56 + 60; // track + label gap
    private static final int TOTAL_H  = 28;

    // ── Animation ────────────────────────────────────────────────────
    private float   position  = 0f;   // 0.0 = light (left), 1.0 = dark (right)
    private Timer   animTimer;

    // ── Colors ───────────────────────────────────────────────────────
    private static final Color TRACK_OFF = new Color(203, 213, 225);   // light mode track
    private static final Color TRACK_ON  = new Color( 79,  70, 229);   // dark mode track  (indigo)
    private static final Color THUMB_CLR = Color.WHITE;
    private static final Color SHADOW    = new Color(0, 0, 0, 40);

    public DarkModeToggle() {
        setOpaque(false);
        setPreferredSize(new Dimension(TOTAL_W, TOTAL_H));
        setMinimumSize(new Dimension(TOTAL_W, TOTAL_H));
        setMaximumSize(new Dimension(TOTAL_W, TOTAL_H));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Sync with current state
        position = ThemeManager.getInstance().isDark() ? 1f : 0f;

        // Smooth animation timer ~60fps
        animTimer = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                float target = ThemeManager.getInstance().isDark() ? 1f : 0f;
                float diff   = target - position;
                if (Math.abs(diff) < 0.015f) {
                    position = target;
                    ((Timer) e.getSource()).stop();
                } else {
                    position += diff * 0.18f;
                }
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                ThemeManager.getInstance().toggleTheme();
                animTimer.restart();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int ty = (getHeight() - TRACK_H) / 2;   // vertical center of track
        boolean dark = ThemeManager.getInstance().isDark();

        // ── Track ────────────────────────────────────────────────────
        Color trackColor = blend(TRACK_OFF, TRACK_ON, position);
        g2.setColor(trackColor);
        g2.fillRoundRect(0, ty, TRACK_W, TRACK_H, TRACK_H, TRACK_H);

        // Track inner shadow (top)
        g2.setColor(new Color(0, 0, 0, 20));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, ty, TRACK_W - 1, TRACK_H - 1, TRACK_H, TRACK_H);

        // ── Sun icon (left side of track) ────────────────────────────
        float sunAlpha = 1f - position;
        if (sunAlpha > 0.05f) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sunAlpha));
            paintSun(g2, 10, ty + TRACK_H / 2);
        }

        // ── Moon icon (right side of track) ──────────────────────────
        float moonAlpha = position;
        if (moonAlpha > 0.05f) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, moonAlpha));
            paintMoon(g2, TRACK_W - 10, ty + TRACK_H / 2);
        }

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // ── Thumb shadow ─────────────────────────────────────────────
        int travel = TRACK_W - THUMB_D - THUMB_PAD * 2;
        int tx     = (int)(THUMB_PAD + position * travel);
        int thumbY = ty + (TRACK_H - THUMB_D) / 2;

        for (int i = 3; i > 0; i--) {
            g2.setColor(new Color(0, 0, 0, 14 * i));
            g2.fillOval(tx - i + 1, thumbY + i, THUMB_D + i, THUMB_D + i);
        }

        // ── Thumb ────────────────────────────────────────────────────
        g2.setColor(THUMB_CLR);
        g2.fillOval(tx, thumbY, THUMB_D, THUMB_D);

        // Thumb border
        g2.setColor(new Color(0, 0, 0, 15));
        g2.setStroke(new BasicStroke(1f));
        g2.drawOval(tx, thumbY, THUMB_D - 1, THUMB_D - 1);

        // ── Label to the right of track ──────────────────────────────
        g2.setColor(dark
            ? new Color(160, 170, 210)
            : new Color(100, 116, 139));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        String label = dark ? "Dark" : "Light";
        int lx = TRACK_W + 8;
        int ly = ty + (TRACK_H + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2;
        g2.drawString(label, lx, ly);

        g2.dispose();
    }

    // ── Sun ───────────────────────────────────────────────────────────
    private void paintSun(Graphics2D g2, int cx, int cy) {
        g2.setColor(new Color(255, 230, 180));
        g2.fillOval(cx - 4, cy - 4, 8, 8);
        g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 8; i++) {
            double a = Math.toRadians(i * 45);
            g2.drawLine(
                (int)(cx + 6  * Math.cos(a)), (int)(cy + 6  * Math.sin(a)),
                (int)(cx + 9  * Math.cos(a)), (int)(cy + 9  * Math.sin(a)));
        }
    }

    // ── Moon ─────────────────────────────────────────────────────────
    private void paintMoon(Graphics2D g2, int cx, int cy) {
        // Full circle
        g2.setColor(new Color(230, 235, 255));
        g2.fillOval(cx - 5, cy - 5, 10, 10);
        // Cut-out crescent
        g2.setColor(blend(TRACK_OFF, TRACK_ON, position));
        g2.fillOval(cx - 2, cy - 6, 10, 12);
    }

    // ── Color blend utility ───────────────────────────────────────────
    private Color blend(Color a, Color b, float t) {
        float s = 1f - t;
        return new Color(
            clamp((int)(a.getRed()   * s + b.getRed()   * t)),
            clamp((int)(a.getGreen() * s + b.getGreen() * t)),
            clamp((int)(a.getBlue()  * s + b.getBlue()  * t))
        );
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}