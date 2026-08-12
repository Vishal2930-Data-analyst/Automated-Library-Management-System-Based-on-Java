package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.sql.*;

import db.DBConnection;
import util.UserSession;
public class AddBookUI {

    // ══════════════════════════════════════════════════════════════════
    //  PALETTE  — matches DashboardUI exactly
    // ══════════════════════════════════════════════════════════════════
    private static final Color BG_PAGE      = new Color(244, 245, 251);
    private static final Color BG_CARD      = new Color(255, 255, 255);
    private static final Color INDIGO       = new Color( 79,  70, 229);
    private static final Color INDIGO_LIGHT = new Color(238, 237, 255);
    private static final Color INDIGO_MED   = new Color(165, 180, 252);
    private static final Color VIOLET       = new Color(124,  58, 237);
    private static final Color EMERALD      = new Color(  5, 150, 105);
    private static final Color TEXT_H       = new Color( 17,  24,  39);
    private static final Color TEXT_BODY    = new Color( 55,  65,  81);
    private static final Color TEXT_MUTED   = new Color(107, 114, 128);
    private static final Color TEXT_SUBTLE  = new Color(156, 163, 175);
    private static final Color BORDER       = new Color(226, 232, 240);
    private static final Color FIELD_BG     = new Color(249, 250, 255);

    public AddBookUI() {

        // ── Frame ────────────────────────────────────────────────────
        JFrame frame = new JFrame("Add New Book");
        frame.setSize(520, 640);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BG_PAGE);

        // ── Outer wrapper (page background) ─────────────────────────
        JPanel page = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BG_PAGE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        page.setOpaque(false);
        frame.add(page, BorderLayout.CENTER);

        // ── Card ─────────────────────────────────────────────────────
        JPanel card = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Drop shadow layers
                for (int i = 5; i > 0; i--) {
                    g2.setColor(new Color(17, 24, 39, 5 * i));
                    g2.fillRoundRect(i, i, getWidth() - i, getHeight() - i, 20, 20);
                }
                // White body
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
                // Top accent gradient bar
                GradientPaint gp = new GradientPaint(0, 0, INDIGO, (getWidth() - 5) * 2 / 3, 0,
                        new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(), 0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 5, 5, 4, 4);
                // Border
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(440, 570));
        card.setBorder(new EmptyBorder(32, 38, 32, 38));

        // ── Header ───────────────────────────────────────────────────
        JPanel header = new JPanel(null);
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 72));

        // Icon badge
        JPanel iconBadge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, INDIGO, getWidth(), getHeight(), VIOLET);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Book icon (white)
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(6, 4, 18, 22, 4, 4);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(10, 10, 20, 10);
                g2.drawLine(10, 14, 20, 14);
                g2.drawLine(10, 18, 16, 18);
                // Plus badge
                g2.setColor(new Color(255, 255, 255, 220));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.drawString("+", 20, 9);
                g2.dispose();
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setBounds(0, 10, 44, 44);

        JLabel titleLbl = new JLabel("Add New Book");
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 22));
        titleLbl.setForeground(TEXT_H);
        titleLbl.setBounds(56, 10, 310, 28);

        JLabel subtitleLbl = new JLabel("Fill in the details to add a book to the library");
        subtitleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLbl.setForeground(TEXT_MUTED);
        subtitleLbl.setBounds(56, 40, 340, 18);

        header.add(iconBadge);
        header.add(titleLbl);
        header.add(subtitleLbl);

        // Divider
        JPanel divider = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, INDIGO_MED, getWidth(), 0,
                        new Color(INDIGO_MED.getRed(), INDIGO_MED.getGreen(), INDIGO_MED.getBlue(), 0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        divider.setOpaque(false);
        divider.setPreferredSize(new Dimension(0, 1));

        JPanel headerWrap = new JPanel(new BorderLayout(0, 0));
        headerWrap.setOpaque(false);
        headerWrap.add(header, BorderLayout.CENTER);
        headerWrap.add(divider, BorderLayout.SOUTH);

        card.add(headerWrap, BorderLayout.NORTH);

        // ── Form ─────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(18, 0, 18, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JTextField idField     = createField("e.g.  1001");
        JTextField titleField  = createField("e.g.  The Great Gatsby");
        JTextField authorField = createField("e.g.  F. Scott Fitzgerald");
        JTextField qtyField    = createField("e.g.  5");

        // Row: Book ID
        gbc.insets = new Insets(0, 0, 4, 0);
        form.add(makeLabel("Book ID", true), gbc); gbc.gridy++;
        gbc.insets = new Insets(0, 0, 14, 0);
        form.add(idField, gbc); gbc.gridy++;

        // Row: Title
        gbc.insets = new Insets(0, 0, 4, 0);
        form.add(makeLabel("Book Title", true), gbc); gbc.gridy++;
        gbc.insets = new Insets(0, 0, 14, 0);
        form.add(titleField, gbc); gbc.gridy++;

        // Row: Author
        gbc.insets = new Insets(0, 0, 4, 0);
        form.add(makeLabel("Author", true), gbc); gbc.gridy++;
        gbc.insets = new Insets(0, 0, 14, 0);
        form.add(authorField, gbc); gbc.gridy++;

        // Row: Quantity
        gbc.insets = new Insets(0, 0, 4, 0);
        form.add(makeLabel("Quantity", true), gbc); gbc.gridy++;
        gbc.insets = new Insets(0, 0, 6, 0);
        form.add(qtyField, gbc);

        card.add(form, BorderLayout.CENTER);

        // ── Button ───────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new BorderLayout(0, 10));
        btnPanel.setOpaque(false);

        JButton addBtn = new JButton("Add Book") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean hovered = getModel().isRollover();
                boolean pressed = getModel().isPressed();
                // Gradient background
                Color c1 = pressed ? new Color(67, 56, 202) : (hovered ? new Color(99, 90, 240) : INDIGO);
                Color c2 = pressed ? new Color(109, 40, 217) : (hovered ? new Color(139, 92, 246) : VIOLET);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                // Shine strip on top
                if (!pressed) {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 12, 12);
                }
                // Icon + text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String txt = getText();
                int tw = fm.stringWidth(txt);
                int tx = (getWidth() - tw) / 2 + 10;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                // Small book icon before text
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int ix = tx - 22, iy = ty - 13;
                g2.drawRoundRect(ix, iy, 13, 16, 3, 3);
                g2.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(ix+3, iy+5, ix+10, iy+5);
                g2.drawLine(ix+3, iy+8, ix+10, iy+8);
                g2.drawString(txt, tx, ty);
                g2.dispose();
            }
        };
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addBtn.setForeground(Color.WHITE);
        addBtn.setContentAreaFilled(false);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setPreferredSize(new Dimension(0, 48));

        // Cancel/reset link
        JLabel resetLbl = new JLabel("Clear all fields", SwingConstants.CENTER);
        resetLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        resetLbl.setForeground(TEXT_MUTED);
        resetLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetLbl.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { resetLbl.setForeground(INDIGO); }
            public void mouseExited(MouseEvent e)  { resetLbl.setForeground(TEXT_MUTED); }
            public void mouseClicked(MouseEvent e) {
                idField.setText(""); titleField.setText(""); authorField.setText(""); qtyField.setText("");
                idField.requestFocus();
            }
        });

        btnPanel.add(addBtn, BorderLayout.CENTER);
        btnPanel.add(resetLbl, BorderLayout.SOUTH);
        card.add(btnPanel, BorderLayout.SOUTH);

        // ── Action Listener (unchanged logic) ────────────────────────
        addBtn.addActionListener(e -> {
            String idText  = idField.getText().trim();
            String ttText  = titleField.getText().trim();
            String auText  = authorField.getText().trim();
            String qtText  = qtyField.getText().trim();

            if (idText.isEmpty() || ttText.isEmpty() || auText.isEmpty() || qtText.isEmpty()) {
                showMsg(frame, "Please fill in all fields.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int id  = Integer.parseInt(idText);
                int qty = Integer.parseInt(qtText);

                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("INSERT INTO books VALUES (?,?,?,?,?)");
                ps.setInt(1, id);
                ps.setString(2, ttText);
                ps.setString(3, auText);
                ps.setInt(4, qty);
                ps.setInt(5, UserSession.adminId);
                int result = ps.executeUpdate();

                if (result > 0) {
                    showMsg(frame, "\"" + ttText + "\" added successfully!", "Book Added", JOptionPane.INFORMATION_MESSAGE);
                    idField.setText(""); titleField.setText(""); authorField.setText(""); qtyField.setText("");
                } else {
                    showMsg(frame, "Failed to add book. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                showMsg(frame, "Book ID and Quantity must be whole numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLIntegrityConstraintViolationException ex) {
                showMsg(frame, "A book with this ID already exists.", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                showMsg(frame, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        page.add(card);
        frame.setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════

    /** Styled label with optional required asterisk */
    private JLabel makeLabel(String text, boolean required) {
        JLabel lbl = new JLabel(required ? text + " *" : text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                // Draw main text in TEXT_H
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(TEXT_BODY);
                g2.drawString(text, 0, g2.getFontMetrics().getAscent());
                // Draw asterisk in INDIGO
                if (required) {
                    FontMetrics fm = g2.getFontMetrics();
                    g2.setColor(INDIGO);
                    g2.drawString(" *", fm.stringWidth(text), fm.getAscent());
                }
                g2.dispose();
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_BODY);
        return lbl;
    }

    /** Styled text field with rounded border and focus glow */
    private JTextField createField(String placeholder) {
        JTextField field = new JTextField() {
            boolean focused = false;

            {
                addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) { focused = true;  repaint(); }
                    public void focusLost(FocusEvent e)   { focused = false; repaint(); }
                });
                // Placeholder logic
                setForeground(TEXT_SUBTLE);
                addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        if (getText().equals(placeholder)) { setText(""); setForeground(TEXT_H); }
                    }
                    public void focusLost(FocusEvent e) {
                        if (getText().isEmpty()) { setText(placeholder); setForeground(TEXT_SUBTLE); }
                    }
                });
                setText(placeholder);
            }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background
                g2.setColor(focused ? Color.WHITE : FIELD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                // Focus glow
                if (focused) {
                    for (int i = 3; i > 0; i--) {
                        g2.setColor(new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(), 12 * i));
                        g2.setStroke(new BasicStroke(i * 1.2f));
                        g2.drawRoundRect(i, i, getWidth() - 1 - i * 2, getHeight() - 1 - i * 2, 10, 10);
                    }
                    g2.setColor(INDIGO);
                } else {
                    g2.setColor(BORDER);
                }
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(11, 14, 11, 14));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 44));
        return field;
    }

    private void showMsg(JFrame frame, String msg, String title, int type) {
        JOptionPane.showMessageDialog(frame, msg, title, type);
    }
}