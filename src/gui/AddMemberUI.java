package gui;

import javax.swing.*;

import javax.swing.border.EmptyBorder;

import com.mysql.cj.Session;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import util.UserSession;
import db.DBConnection;
public class AddMemberUI extends JFrame {

    // ── Palette ──────────────────────────────────────────────────────
    private static final Color BG_PAGE      = new Color(244, 245, 251);
    private static final Color BG_CARD      = new Color(255, 255, 255);
    private static final Color INDIGO       = new Color( 79,  70, 229);
    private static final Color INDIGO_LIGHT = new Color(238, 237, 255);
    private static final Color INDIGO_MED   = new Color(165, 180, 252);
    private static final Color VIOLET       = new Color(124,  58, 237);
    private static final Color EMERALD      = new Color(  5, 150, 105);
    private static final Color ROSE         = new Color(225,  29,  72);
    private static final Color TEXT_H       = new Color( 17,  24,  39);
    private static final Color TEXT_BODY    = new Color( 55,  65,  81);
    private static final Color TEXT_MUTED   = new Color(107, 114, 128);
    private static final Color TEXT_SUBTLE  = new Color(156, 163, 175);
    private static final Color BORDER       = new Color(226, 232, 240);
    private static final Color FIELD_BG     = new Color(249, 250, 255);

    // ── Fields ───────────────────────────────────────────────────────
    private JTextField idField, nameField, courseField, emailField, phoneField;

    public AddMemberUI() {

        setTitle("Add Member");
        setSize(520, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(BG_PAGE);

        // ── Page background ──────────────────────────────────────────
        JPanel page = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(BG_PAGE);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        page.setOpaque(false);
        add(page, BorderLayout.CENTER);

        // ── Card ─────────────────────────────────────────────────────
        JPanel card = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow layers
                for (int i = 5; i > 0; i--) {
                    g2.setColor(new Color(17, 24, 39, 5 * i));
                    g2.fillRoundRect(i, i, getWidth() - i, getHeight() - i, 20, 20);
                }
                // White body
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
                // Top accent gradient bar
                GradientPaint gp = new GradientPaint(0, 0, EMERALD,
                    (getWidth() - 5) * 2 / 3, 0,
                    new Color(EMERALD.getRed(), EMERALD.getGreen(), EMERALD.getBlue(), 0));
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
        card.setPreferredSize(new Dimension(450, 590));
        card.setBorder(new EmptyBorder(30, 36, 28, 36));

        // ── Header ───────────────────────────────────────────────────
        JPanel header = new JPanel(null);
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 76));

        // Emerald gradient icon badge
        JPanel iconBadge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, EMERALD, getWidth(), getHeight(),
                    new Color(6, 182, 212));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Person + plus icon
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Head
                g2.drawOval(7, 5, 12, 12);
                // Shoulders
                g2.drawArc(3, 19, 16, 10, 0, 180);
                // Plus sign
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(26, 14, 34, 14);
                g2.drawLine(30, 10, 30, 18);
                g2.dispose();
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setBounds(0, 10, 44, 44);

        JLabel titleLbl = new JLabel("Add New Member");
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 21));
        titleLbl.setForeground(TEXT_H);
        titleLbl.setBounds(56, 10, 310, 28);

        JLabel subLbl = new JLabel("Register a new patron in the library system");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLbl.setForeground(TEXT_MUTED);
        subLbl.setBounds(56, 40, 340, 16);

        header.add(iconBadge);
        header.add(titleLbl);
        header.add(subLbl);

        // Gradient divider
        JPanel divider = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, EMERALD, getWidth() * 2 / 3, 0,
                    new Color(EMERALD.getRed(), EMERALD.getGreen(), EMERALD.getBlue(), 0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        divider.setOpaque(false);
        divider.setPreferredSize(new Dimension(0, 1));

        JPanel headerWrap = new JPanel(new BorderLayout(0, 0));
        headerWrap.setOpaque(false);
        headerWrap.add(header,  BorderLayout.CENTER);
        headerWrap.add(divider, BorderLayout.SOUTH);
        card.add(headerWrap, BorderLayout.NORTH);

        // ── Form ─────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 0, 16, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.weightx   = 1.0;
        gbc.gridx     = 0;
        gbc.gridy     = 0;

        idField     = createField("e.g.  1001");
        nameField   = createField("e.g.  John Doe");
        courseField = createField("e.g.  B.Sc Computer Science");
        emailField  = createField("e.g.  john@email.com");
        phoneField  = createField("e.g.  9876543210");

        // Two-column layout: ID (left) + Name (right)
        GridBagConstraints left = new GridBagConstraints();
        left.fill    = GridBagConstraints.HORIZONTAL;
        left.weightx = 0.4;
        left.gridx   = 0;
        left.gridy   = 0;
        left.insets  = new Insets(0, 0, 4, 8);

        GridBagConstraints right = new GridBagConstraints();
        right.fill    = GridBagConstraints.HORIZONTAL;
        right.weightx = 0.6;
        right.gridx   = 1;
        right.gridy   = 0;
        right.insets  = new Insets(0, 0, 4, 0);

        // Row 0: labels
        form.add(makeLabel("Member ID", true,  EMERALD), left);
        form.add(makeLabel("Full Name",  true,  EMERALD), right);

        // Row 1: fields
        left.gridy  = 1; left.insets  = new Insets(0, 0, 14, 8);
        right.gridy = 1; right.insets = new Insets(0, 0, 14, 0);
        form.add(idField,   left);
        form.add(nameField, right);

        // Full-width rows
        gbc.gridy  = 2; gbc.insets = new Insets(0, 0, 4, 0); gbc.gridwidth = 2;
        form.add(makeLabel("Course / Department", true, EMERALD), gbc);
        gbc.gridy  = 3; gbc.insets = new Insets(0, 0, 14, 0);
        form.add(courseField, gbc);

        gbc.gridy  = 4; gbc.insets = new Insets(0, 0, 4, 0);
        form.add(makeLabel("Email Address", false, EMERALD), gbc);
        gbc.gridy  = 5; gbc.insets = new Insets(0, 0, 14, 0);
        form.add(emailField, gbc);

        gbc.gridy  = 6; gbc.insets = new Insets(0, 0, 4, 0);
        form.add(makeLabel("Phone Number", false, EMERALD), gbc);
        gbc.gridy  = 7; gbc.insets = new Insets(0, 0, 4, 0);
        form.add(phoneField, gbc);

        card.add(form, BorderLayout.CENTER);

        // ── Button panel ─────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new BorderLayout(0, 8));
        btnPanel.setOpaque(false);

        JButton addBtn = new JButton("Add Member") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover();
                boolean p = getModel().isPressed();
                Color c1 = p ? EMERALD.darker() : (h ? new Color(4, 120, 87) : EMERALD);
                Color c2 = p ? new Color(6,182,212) : (h ? new Color(8,145,178) : new Color(6, 182, 212));
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (!p) {
                    g2.setColor(new Color(255, 255, 255, 28));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 12, 12);
                }
                // Person + plus icon
                g2.setColor(new Color(255,255,255,180));
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int ix = 18, iy = getHeight()/2 - 9;
                g2.drawOval(ix, iy, 8, 8);
                g2.drawArc(ix-3, iy+9, 14, 7, 0, 180);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(ix+14, iy+3, ix+20, iy+3);
                g2.drawLine(ix+17, iy, ix+17, iy+6);
                // Label
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "Add Member";
                g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2 + 4,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        addBtn.setContentAreaFilled(false);
        addBtn.setBorderPainted(false);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setPreferredSize(new Dimension(0, 48));

        // Clear link
        JLabel clearLbl = new JLabel("Clear all fields", SwingConstants.CENTER);
        clearLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        clearLbl.setForeground(TEXT_MUTED);
        clearLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearLbl.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { clearLbl.setForeground(EMERALD); }
            public void mouseExited(MouseEvent e)  { clearLbl.setForeground(TEXT_MUTED); }
            public void mouseClicked(MouseEvent e) { clearAll(); }
        });

        btnPanel.add(addBtn,   BorderLayout.CENTER);
        btnPanel.add(clearLbl, BorderLayout.SOUTH);
        card.add(btnPanel, BorderLayout.SOUTH);

        page.add(card);
        setVisible(true);

        // ── Add Member Action (unchanged logic) ──────────────────────
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addMember();
            }
        });
    }

    // ── Add member DB logic (unchanged) ──────────────────────────────
    private void addMember() {
        String idTxt    = idField.getText().trim();
        String nameTxt  = nameField.getText().trim();
        String courseTxt= courseField.getText().trim();
        String emailTxt = emailField.getText().trim();
        String phoneTxt = phoneField.getText().trim();

        if (idTxt.isEmpty() || nameTxt.isEmpty() || courseTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields (*).", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            String query = "INSERT INTO members(id,name,course,email,phone,admin_id) VALUES(?,?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, Integer.parseInt(idTxt));
            pst.setString(2, nameTxt);
            pst.setString(3, courseTxt);
            pst.setString(4, emailTxt);
            pst.setString(5, phoneTxt);
            pst.setInt(5, UserSession.adminId);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "\"" + nameTxt + "\" added successfully!", "Member Added", JOptionPane.INFORMATION_MESSAGE);
            clearAll();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Member ID must be a whole number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "A member with this ID already exists.", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearAll() {
        idField.setText("");     nameField.setText("");
        courseField.setText(""); emailField.setText("");
        phoneField.setText("");
        idField.requestFocus();
    }

    // ── Label helper ─────────────────────────────────────────────────
    private JLabel makeLabel(final String text, final boolean required, final Color accent) {
        JLabel lbl = new JLabel(required ? text + " *" : text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(TEXT_BODY);
                g2.drawString(text, 0, g2.getFontMetrics().getAscent());
                if (required) {
                    FontMetrics fm = g2.getFontMetrics();
                    g2.setColor(accent);
                    g2.drawString(" *", fm.stringWidth(text), fm.getAscent());
                }
                g2.dispose();
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_BODY);
        return lbl;
    }

    // ── Styled text field ─────────────────────────────────────────────
    private JTextField createField(final String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean focused = hasFocus();
                g2.setColor(focused ? Color.WHITE : FIELD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                if (focused) {
                    for (int i = 3; i > 0; i--) {
                        g2.setColor(new Color(EMERALD.getRed(), EMERALD.getGreen(), EMERALD.getBlue(), 14 * i));
                        g2.setStroke(new BasicStroke(i * 1.2f));
                        g2.drawRoundRect(i, i, getWidth()-1-i*2, getHeight()-1-i*2, 10, 10);
                    }
                    g2.setColor(EMERALD);
                } else {
                    g2.setColor(BORDER);
                }
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(11, 14, 11, 14));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(TEXT_SUBTLE);
        f.setCaretColor(EMERALD);
        f.setText(placeholder);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(TEXT_H); }
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(TEXT_SUBTLE); }
            }
        });
        f.setPreferredSize(new Dimension(0, 44));
        return f;
    }
}