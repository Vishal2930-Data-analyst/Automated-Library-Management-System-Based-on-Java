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
public class LoginUI {

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

    public static void main(String[] args) {
        new LoginUI();
    }

    public LoginUI() {

        // ── Frame ────────────────────────────────────────────────────
        JFrame frame = new JFrame("LibraryPro — Login");
        frame.setSize(980, 620);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);

        // ── Split layout: left panel + right card ────────────────────
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BG_PAGE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setOpaque(false);
        frame.add(root, BorderLayout.CENTER);

        // ════════════════════════════════════════════════════════════
        //  LEFT BRANDING PANEL
        // ════════════════════════════════════════════════════════════
        JPanel leftPanel = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Deep indigo-violet gradient background
                GradientPaint bgGrad = new GradientPaint(
                    0, 0, new Color(67, 56, 202),
                    getWidth(), getHeight(), new Color(109, 40, 217)
                );
                g2.setPaint(bgGrad);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Decorative blurred circles
                drawGlow(g2, getWidth() - 60,  60, 160, new Color(255,255,255, 18));
                drawGlow(g2, 30, getHeight() - 80, 200, new Color(255,255,255, 12));
                drawGlow(g2, getWidth()/2, getHeight()/2, 250, new Color(255,255,255, 8));

                // Subtle dot grid
                g2.setColor(new Color(255, 255, 255, 14));
                for (int x = 20; x < getWidth(); x += 28) {
                    for (int y = 20; y < getHeight(); y += 28) {
                        g2.fillOval(x, y, 2, 2);
                    }
                }

                // Large book illustration (bottom-right decorative)
                g2.setColor(new Color(255, 255, 255, 20));
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int bx = getWidth() - 110, by = getHeight() - 130;
                g2.drawRoundRect(bx, by, 70, 90, 8, 8);
                g2.drawRoundRect(bx+10, by-10, 70, 90, 8, 8);
                g2.drawLine(bx+14, by+25, bx+56, by+25);
                g2.drawLine(bx+14, by+38, bx+56, by+38);
                g2.drawLine(bx+14, by+51, bx+44, by+51);
                g2.drawLine(bx+24, by+15, bx+66, by+15);
                g2.drawLine(bx+24, by+28, bx+66, by+28);

                g2.dispose();
            }

            private void drawGlow(Graphics2D g2, int cx, int cy, int r, Color c) {
                for (int i = r; i > 0; i -= 20) {
                    float alpha = c.getAlpha() / 255f * (1f - (float)(r - i) / r);
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, (int)(alpha * 255))));
                    g2.fillOval(cx - i/2, cy - i/2, i, i);
                }
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(420, 620));

        // Brand badge
        JPanel brandBadge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(255, 255, 255, 60));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                // Book icon
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(8, 6, 22, 28, 4, 4);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(13, 14, 25, 14);
                g2.drawLine(13, 19, 25, 19);
                g2.drawLine(13, 24, 21, 24);
                g2.dispose();
            }
        };
        brandBadge.setOpaque(false);
        brandBadge.setBounds(54, 64, 52, 48);

        JLabel brandName = new JLabel("LibraryPro");
        brandName.setFont(new Font("Georgia", Font.BOLD, 32));
        brandName.setForeground(Color.WHITE);
        brandName.setBounds(48, 126, 300, 40);

        JLabel brandTagline = new JLabel("Smart Library Management");
        brandTagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        brandTagline.setForeground(new Color(255, 255, 255, 180));
        brandTagline.setBounds(50, 166, 300, 20);

        // Feature bullets
        String[] features = {
            "Manage books & members",
            "Track issues & returns",
            "Automated fine collection",
            "Detailed reports & analytics"
        };
        int fy = 240;
        for (String feat : features) {
            JLabel dot = new JLabel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255,255,255,200));
                    g2.fillOval(0, 3, 7, 7);
                    g2.dispose();
                }
            };
            dot.setBounds(50, fy + 4, 12, 14);

            JLabel fl = new JLabel(feat);
            fl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fl.setForeground(new Color(255, 255, 255, 200));
            fl.setBounds(68, fy, 300, 22);

            leftPanel.add(dot);
            leftPanel.add(fl);
            fy += 32;
        }

        // Version badge bottom
        JLabel version = new JLabel("v2.0 Premium Edition");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setForeground(new Color(255, 255, 255, 100));
        version.setBounds(50, 560, 200, 14);

        leftPanel.add(brandBadge);
        leftPanel.add(brandName);
        leftPanel.add(brandTagline);
        leftPanel.add(version);

        // ════════════════════════════════════════════════════════════
        //  RIGHT LOGIN CARD
        // ════════════════════════════════════════════════════════════
        JPanel rightPanel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(BG_PAGE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        rightPanel.setOpaque(false);

        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Drop shadow
                for (int i = 5; i > 0; i--) {
                    g2.setColor(new Color(17, 24, 39, 5 * i));
                    g2.fillRoundRect(i, i, getWidth() - i, getHeight() - i, 20, 20);
                }
                // White body
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
                // Top accent bar (indigo → transparent)
                GradientPaint gp = new GradientPaint(0, 0, INDIGO, (getWidth()-5)*2/3, 0,
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
        card.setPreferredSize(new Dimension(380, 460));

        // ── Card Header ──────────────────────────────────────────────
        // Small indigo icon badge
        JPanel cardIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, INDIGO, getWidth(), getHeight(), VIOLET);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                // Lock icon
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(8, 14, 16, 13, 4, 4);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(10, 7, 12, 12, 0, 180);
                g2.setColor(Color.WHITE);
                g2.fillOval(14, 19, 4, 4);
                g2.dispose();
            }
        };
        cardIcon.setOpaque(false);
        cardIcon.setBounds(158, 30, 46, 46);

        JLabel cardTitle = new JLabel("Welcome Back");
        cardTitle.setFont(new Font("Georgia", Font.BOLD, 22));
        cardTitle.setForeground(TEXT_H);
        cardTitle.setHorizontalAlignment(SwingConstants.CENTER);
        cardTitle.setBounds(40, 86, 300, 28);

        JLabel cardSub = new JLabel("Sign in to your admin account");
        cardSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cardSub.setForeground(TEXT_MUTED);
        cardSub.setHorizontalAlignment(SwingConstants.CENTER);
        cardSub.setBounds(40, 116, 300, 18);

        // Thin divider
        JPanel divider = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0,
                    new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(), 0),
                    getWidth()/2, 0, INDIGO_MED);
                GradientPaint gp2 = new GradientPaint(getWidth()/2, 0, INDIGO_MED,
                    getWidth(), 0, new Color(INDIGO.getRed(), INDIGO.getGreen(), INDIGO.getBlue(), 0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth()/2, getHeight());
                g2.setPaint(gp2);
                g2.fillRect(getWidth()/2, 0, getWidth()/2, getHeight());
                g2.dispose();
            }
        };
        divider.setOpaque(false);
        divider.setBounds(40, 144, 300, 1);

        // ── Username field ───────────────────────────────────────────
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(TEXT_BODY);
        userLabel.setBounds(40, 160, 200, 16);

        JLabel userAsterisk = new JLabel("*");
        userAsterisk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userAsterisk.setForeground(INDIGO);
        userAsterisk.setBounds(110, 160, 20, 16);

        final JTextField username = buildField("Enter your username");
        username.setBounds(40, 180, 300, 44);

        // ── Password field ───────────────────────────────────────────
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(TEXT_BODY);
        passLabel.setBounds(40, 238, 200, 16);

        JLabel passAsterisk = new JLabel("*");
        passAsterisk.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passAsterisk.setForeground(INDIGO);
        passAsterisk.setBounds(108, 238, 20, 16);

        final JPasswordField password = buildPasswordField("Enter your password");
        password.setBounds(40, 258, 300, 44);

        // Show/hide password toggle
        JLabel eyeToggle = new JLabel("Show") {
            boolean shown = false;
            {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setFont(new Font("Segoe UI", Font.PLAIN, 10));
                setForeground(TEXT_MUTED);
                addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        shown = !shown;
                        password.setEchoChar(shown ? (char) 0 : '●');
                        setText(shown ? "Hide" : "Show");
                        setForeground(shown ? INDIGO : TEXT_MUTED);
                    }
                    public void mouseEntered(MouseEvent e) { setForeground(INDIGO); }
                    public void mouseExited(MouseEvent e)  { setForeground(shown ? INDIGO : TEXT_MUTED); }
                });
            }
        };
        eyeToggle.setBounds(296, 238, 44, 16);
        eyeToggle.setHorizontalAlignment(SwingConstants.RIGHT);

        // ── Login Button ─────────────────────────────────────────────
        JButton loginBtn = new JButton("Sign In") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover();
                boolean p = getModel().isPressed();
                Color c1 = p ? new Color(67,56,202) : (h ? new Color(99,90,240) : INDIGO);
                Color c2 = p ? new Color(109,40,217): (h ? new Color(139,92,246) : VIOLET);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (!p) {
                    g2.setColor(new Color(255, 255, 255, 28));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 12, 12);
                }
                // Arrow icon
                g2.setColor(new Color(255,255,255,160));
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() - 28;
                int cy = getHeight() / 2;
                g2.drawLine(cx - 6, cy, cx + 6, cy);
                g2.drawLine(cx + 2, cy - 4, cx + 6, cy);
                g2.drawLine(cx + 2, cy + 4, cx + 6, cy);
                // Text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "Sign In";
                g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2 - 8,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setBounds(40, 324, 300, 50);

        // ── Signup Link ──────────────────────────────────────────────
        JPanel signupRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        signupRow.setOpaque(false);
        signupRow.setBounds(40, 388, 300, 24);

        JLabel noAccount = new JLabel("Don't have an account?");
        noAccount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noAccount.setForeground(TEXT_MUTED);

        JLabel signupLink = new JLabel("Create one") {
            {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setForeground(INDIGO);
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { setForeground(VIOLET); }
                    public void mouseExited(MouseEvent e)  { setForeground(INDIGO); }
                    public void mouseClicked(MouseEvent e) { new SignUpUI(); }
                });
            }
        };

        signupRow.add(noAccount);
        signupRow.add(signupLink);

        // ── Error label ──────────────────────────────────────────────
        JLabel errorLbl = new JLabel("") {
            @Override protected void paintComponent(Graphics g) {
                if (getText() == null || getText().isEmpty()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 228, 230));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setColor(new Color(225, 29, 72, 80));
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        errorLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        errorLbl.setForeground(ROSE);
        errorLbl.setBorder(new EmptyBorder(6, 12, 6, 12));
        errorLbl.setHorizontalAlignment(SwingConstants.CENTER);
        errorLbl.setBounds(40, 316, 300, 0); // hidden initially

        // Add all to card
        card.add(cardIcon);
        card.add(cardTitle);
        card.add(cardSub);
        card.add(divider);
        card.add(userLabel);
        card.add(userAsterisk);
        card.add(username);
        card.add(passLabel);
        card.add(passAsterisk);
        card.add(eyeToggle);
        card.add(password);
        card.add(errorLbl);
        card.add(loginBtn);
        card.add(signupRow);

        rightPanel.add(card);

        root.add(leftPanel,  BorderLayout.WEST);
        root.add(rightPanel, BorderLayout.CENTER);

        // ════════════════════════════════════════════════════════════
        //  LOGIN ACTION (unchanged logic)
        // ════════════════════════════════════════════════════════════
        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String user = username.getText().trim();
                String pass = String.valueOf(password.getPassword()).trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    showError(errorLbl, loginBtn, signupRow, "Please enter both username and password.");
                    return;
                }

                try {
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM admin WHERE username=? AND password=?");
                    ps.setString(1, user);
                    ps.setString(2, pass);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {

                        
						// 🔥 ADD THIS LINE HERE
                        util.UserSession.adminId = rs.getInt("id");

                        new DashboardUI();
                        frame.dispose();

                    } else {
                        showError(errorLbl, loginBtn, signupRow, "Invalid username or password. Please try again.");
                        password.setText("");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(errorLbl, loginBtn, signupRow, "Connection error. Please check your database.");
                }
            }
        });

        // Enter key triggers login
        KeyAdapter enterKey = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginBtn.doClick();
                }
            }
        };
        username.addKeyListener(enterKey);
        password.addKeyListener(enterKey);

        frame.setVisible(true);
    }

    // ── Show inline error (shifts button/link down) ──────────────────
    private void showError(JLabel errorLbl, JButton loginBtn, JPanel signupRow, String msg) {
        errorLbl.setText("  ✕  " + msg);
        errorLbl.setBounds(40, 312, 300, 32);
        loginBtn.setBounds(40, 352, 300, 50);
        signupRow.setBounds(40, 416, 300, 24);
        errorLbl.getParent().revalidate();
        errorLbl.getParent().repaint();
    }

    // ── Styled text field ────────────────────────────────────────────
    private JTextField buildField(final String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean focused = hasFocus();
                g2.setColor(focused ? Color.WHITE : FIELD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                if (focused) {
                    for (int i = 3; i > 0; i--) {
                        g2.setColor(new Color(79, 70, 229, 12 * i));
                        g2.setStroke(new BasicStroke(i * 1.2f));
                        g2.drawRoundRect(i, i, getWidth()-1-i*2, getHeight()-1-i*2, 10, 10);
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
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(11, 14, 11, 14));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(TEXT_SUBTLE);
        f.setCaretColor(INDIGO);
        f.setText(placeholder);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(TEXT_H); }
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(TEXT_SUBTLE); }
            }
        });
        return f;
    }

    // ── Styled password field ────────────────────────────────────────
    private JPasswordField buildPasswordField(final String placeholder) {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean focused = hasFocus();
                g2.setColor(focused ? Color.WHITE : FIELD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                if (focused) {
                    for (int i = 3; i > 0; i--) {
                        g2.setColor(new Color(79, 70, 229, 12 * i));
                        g2.setStroke(new BasicStroke(i * 1.2f));
                        g2.drawRoundRect(i, i, getWidth()-1-i*2, getHeight()-1-i*2, 10, 10);
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
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(11, 14, 11, 14));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(TEXT_SUBTLE);
        f.setCaretColor(INDIGO);
        f.setEchoChar('●');
        f.setText(placeholder);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(f.getPassword()).equals(placeholder)) {
                    f.setText("");
                    f.setForeground(TEXT_H);
                }
            }
            public void focusLost(FocusEvent e) {
                if (String.valueOf(f.getPassword()).isEmpty()) {
                    f.setText(placeholder);
                    f.setForeground(TEXT_SUBTLE);
                    f.setEchoChar((char) 0);
                } else {
                    f.setEchoChar('●');
                }
            }
        });
        return f;
    }
}