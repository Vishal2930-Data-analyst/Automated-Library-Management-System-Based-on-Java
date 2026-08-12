package gui;

import javax.swing.*;
import util.UserSession;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.sql.*;

import db.DBConnection;

import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import style.DarkModeToggle;
import style.ThemeManager;
public class DashboardUI {

    // =========================================================================
    //  PALETTE - These will be updated dynamically for dark mode
    // =========================================================================
    private static Color BG_PRIMARY       = new Color(245, 246, 252);
    private static Color BG_SECONDARY     = new Color(237, 240, 250);
    private static Color BG_CARD          = new Color(255, 255, 255);

    private static Color INDIGO_PRIMARY   = new Color( 79,  70, 229);
    private static Color INDIGO_SECONDARY = new Color(129, 140, 248);
    private static Color INDIGO_GLOW      = new Color(224, 231, 255);

    private static Color EMERALD_PRIMARY  = new Color( 16, 185, 129);
    private static Color EMERALD_SECONDARY= new Color(110, 231, 183);

    private static Color ROSE_PRIMARY     = new Color(244,  63,  94);
    private static Color ROSE_SECONDARY   = new Color(251, 146, 158);

    private static Color AMBER_PRIMARY    = new Color(245, 158,  11);
    private static Color AMBER_SECONDARY  = new Color(252, 211,  77);

    private static Color SKY_PRIMARY      = new Color( 14, 165, 233);
    private static Color SKY_SECONDARY    = new Color(125, 211, 252);

    private static Color VIOLET_PRIMARY   = new Color(139,  92, 246);
    private static Color VIOLET_SECONDARY = new Color(196, 181, 253);

    private static Color TEXT_PRIMARY     = new Color( 15,  23,  42);
    private static Color TEXT_SECONDARY   = new Color( 51,  65,  85);
    private static Color TEXT_TERTIARY    = new Color(100, 116, 139);
    private static Color TEXT_DISABLED    = new Color(148, 163, 184);

    private static Color BORDER_LIGHT     = new Color(226, 232, 240);
    private static Color BORDER_MEDIUM    = new Color(203, 213, 225);
    private static Color SHADOW_LIGHT     = new Color(  0,   0,   0,  15);
    private static Color GLASS_WHITE      = new Color(255, 255, 255, 210);
    private static Color GLASS_BORDER     = new Color(255, 255, 255, 110);
    
    // Improved Dark Mode Colors
    private static final Color DARK_BG_PRIMARY = new Color(15, 15, 20);
    private static final Color DARK_BG_SECONDARY = new Color(20, 20, 28);
    private static final Color DARK_BG_CARD = new Color(28, 28, 36);
    private static final Color DARK_GLASS = new Color(35, 35, 45, 220);
    private static final Color DARK_GLASS_BORDER = new Color(80, 80, 100, 150);
    private static final Color DARK_TEXT_PRIMARY = new Color(237, 242, 247);
    private static final Color DARK_TEXT_SECONDARY = new Color(203, 213, 225);
    private static final Color DARK_TEXT_TERTIARY = new Color(148, 163, 184);
    private static final Color DARK_BORDER = new Color(51, 65, 85);
    private static final Color DARK_INDIGO_GLOW = new Color(79, 70, 229, 40);

    // =========================================================================
    //  FONTS  — Georgia (display) + Segoe UI (body)
    // =========================================================================
    private static final Font FONT_DISPLAY_LG  = new Font("Georgia",   Font.BOLD,  24);
    private static final Font FONT_DISPLAY_MD  = new Font("Georgia",   Font.BOLD,  20);
    private static final Font FONT_DISPLAY_SM  = new Font("Georgia",   Font.BOLD,  16);
    private static final Font FONT_BODY_BOLD   = new Font("Segoe UI",  Font.BOLD,  13);
    private static final Font FONT_BODY        = new Font("Segoe UI",  Font.PLAIN, 13);
    private static final Font FONT_SMALL_BOLD  = new Font("Segoe UI",  Font.BOLD,  11);
    private static final Font FONT_SMALL       = new Font("Segoe UI",  Font.PLAIN, 11);
    private static final Font FONT_CAPTION     = new Font("Segoe UI",  Font.PLAIN, 10);
    private static final Font FONT_VALUE       = new Font("Georgia",   Font.BOLD,  30);
    private static final Font FONT_MONO        = new Font("Monospaced",Font.PLAIN, 10);

    // =========================================================================
    //  COMPONENT REFERENCES
    // =========================================================================
    private JLabel booksLabel;
    private JLabel membersLabel;
    private JLabel issuedLabel;
    private JLabel overdueLabel;
    private JButton activeNav = null;
    private JFrame mainFrame;
    private JPanel contentPane;
    private JPanel sidebar;
    private JPanel mainPanel;
    private JPanel cardsRow;
    private JPanel chartsPanel;
    private JPanel topBar;
    private JLabel titleLabel;
    private JLabel dateLabel;
    private boolean isDarkMode = false;
    private JButton[] navButtons;  // Store navigation buttons for keyboard shortcuts

    private static final String[] MONTHS =
        {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    // =========================================================================
    //  CONSTRUCTOR
    // =========================================================================
    public DashboardUI() {

        JFrame frame = new JFrame("LibraryPro \u2014 Dashboard");
        frame.setSize(1600, 980);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        mainFrame = frame;

        contentPane = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, BG_PRIMARY, 0, getHeight(), BG_SECONDARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Subtle dot grid
                g2.setColor(new Color(100, 116, 139, isDarkMode ? 30 : 18));
                for (int x = 0; x < getWidth(); x += 32)
                    for (int y = 0; y < getHeight(); y += 32)
                        g2.fillOval(x, y, 2, 2);
                g2.dispose();
            }
        };
        frame.setContentPane(contentPane);

        sidebar = buildSidebar();
        mainPanel = buildMainPanel();
        
        contentPane.add(sidebar,   BorderLayout.WEST);
        contentPane.add(mainPanel, BorderLayout.CENTER);

        // Setup keyboard shortcuts after frame is fully created
        if (navButtons != null && navButtons.length >= 9) {
            setupKeyboardShortcuts(frame,
                navButtons[0], navButtons[1], navButtons[2], navButtons[3],
                navButtons[4], navButtons[5], navButtons[6], navButtons[7], navButtons[8]);
        }

        frame.setVisible(true);
        startBackgroundServices();
    }

    // =========================================================================
    //  SIDEBAR
    // =========================================================================
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Base
                g2.setColor(BG_CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Faint indigo gradient on left edge
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(79, 70, 229, isDarkMode ? 30 : 14),
                    getWidth(), 0, new Color(255, 255, 255, 0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Right divider
                g2.setColor(BORDER_LIGHT);
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(278, 980));

        sidebar.add(buildLogo(),     BorderLayout.NORTH);
        sidebar.add(buildNavMenu(),  BorderLayout.CENTER);
        sidebar.add(buildUserCard(), BorderLayout.SOUTH);
        return sidebar;
    }

    // ── Logo ──────────────────────────────────────────────────────────────────
    private JPanel buildLogo() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                // Bottom separator line with gradient
                GradientPaint gp = new GradientPaint(
                    20, 0, INDIGO_SECONDARY,
                    getWidth() - 20, 0, new Color(INDIGO_SECONDARY.getRed(), INDIGO_SECONDARY.getGreen(), INDIGO_SECONDARY.getBlue(), 0));
                g2.setPaint(gp);
                g2.fillRect(20, getHeight() - 1, getWidth() - 40, 1);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(278, 96));

        // Gradient badge
        JLabel badge = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, INDIGO_PRIMARY, getWidth(), getHeight(), VIOLET_PRIMARY);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                // Inner shine
                g2.setColor(GLASS_BORDER);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
                // Book icon
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(7, 5, 22, 26, 4, 4);
                g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(11, 13, 25, 13);
                g2.drawLine(11, 18, 25, 18);
                g2.drawLine(11, 23, 20, 23);
                g2.dispose();
            }
        };
        badge.setBounds(18, 24, 44, 44);

        JLabel title = new JLabel("LibraryPro");
        title.setFont(new Font("Georgia", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);
        title.setBounds(72, 24, 190, 28);

        JLabel sub = new JLabel("Management System");
        sub.setFont(FONT_CAPTION);
        sub.setForeground(TEXT_TERTIARY);
        sub.setBounds(73, 52, 190, 16);

        panel.add(badge);
        panel.add(title);
        panel.add(sub);
        return panel;
    }

    // ── Nav menu ──────────────────────────────────────────────────────────────
    private JPanel buildNavMenu() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        addNavSection(inner, "LIBRARY");
        final JButton addBook   = navBtn("Add Book",       INDIGO_PRIMARY,  makeIcon_Book());
        final JButton viewBook  = navBtn("View Books",     INDIGO_PRIMARY,  makeIcon_Stack());
        addNavBtns(inner, addBook, viewBook);

        addNavSection(inner, "MEMBERS");
        final JButton addMember  = navBtn("Add Member",    EMERALD_PRIMARY, makeIcon_PersonAdd());
        final JButton viewMember = navBtn("View Members",  EMERALD_PRIMARY, makeIcon_People());
        addNavBtns(inner, addMember, viewMember);

        addNavSection(inner, "CIRCULATION");
        final JButton issueBook  = navBtn("Issue Book",    SKY_PRIMARY,     makeIcon_ArrowOut());
        final JButton returnBook = navBtn("Return Book",   VIOLET_PRIMARY,  makeIcon_ArrowIn());
        addNavBtns(inner, issueBook, returnBook);

        addNavSection(inner, "RECORDS");
        final JButton transactions = navBtn("Transactions",    AMBER_PRIMARY,  makeIcon_Receipt());
        final JButton issuedRec    = navBtn("Issued Records",  ROSE_PRIMARY,   makeIcon_Clipboard());
        final JButton entryReg     = navBtn("Entry Register",  EMERALD_PRIMARY,makeIcon_Pencil());
        addNavBtns(inner, transactions, issuedRec, entryReg);

        outer.add(inner, BorderLayout.NORTH);

        // Store button references for keyboard shortcuts
        navButtons = new JButton[]{addBook, viewBook, addMember, viewMember, 
                                    issueBook, returnBook, transactions, issuedRec, entryReg};

        // Actions
        addBook.addActionListener(new ActionListener()   { public void actionPerformed(ActionEvent e) { new AddBookUI(); } });
        viewBook.addActionListener(new ActionListener()  { public void actionPerformed(ActionEvent e) { new ViewBooksUI(); } });
        addMember.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { new AddMemberUI(); } });
        viewMember.addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e) { new ViewMemberUI(); } });
        issueBook.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { new IssueBookUI(); } });
        returnBook.addActionListener(new ActionListener(){ public void actionPerformed(ActionEvent e) { new ReturnBookUI(); } });
        transactions.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { new TransactionsUI(); } });
        issuedRec.addActionListener(new ActionListener()    { public void actionPerformed(ActionEvent e) { new IssuedBooksUI(); } });
        entryReg.addActionListener(new ActionListener()     { public void actionPerformed(ActionEvent e) { new EntryRegisterUI(); } });

        return outer;
    }

    private void addNavSection(JPanel p, String label) {
        p.add(Box.createVerticalStrut(18));
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_MONO);
        lbl.setForeground(TEXT_DISABLED);
        lbl.setBorder(new EmptyBorder(0, 10, 6, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(254, 20));
        p.add(lbl);
    }

    private void addNavBtns(JPanel p, JButton... btns) {
        for (JButton b : btns) {
            p.add(b);
            p.add(Box.createVerticalStrut(4));
        }
    }

    private JButton navBtn(final String label, final Color accent, final Image icon) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                boolean active  = (DashboardUI.this.activeNav == this);
                boolean hovered = getModel().isRollover();

                if (active) {
                    // Gradient fill
                    GradientPaint gp = new GradientPaint(0, 0, accent, getWidth(), 0,
                        new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 200));
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    // Shine
                    g2.setColor(new Color(255, 255, 255, 25));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 12, 12);
                    // Right active pip
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(getWidth() - 6, getHeight() / 2 - 12, 4, 24, 4, 4);
                } else if (hovered) {
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 18));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60));
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                }

                // Icon badge
                int by = (getHeight() - 28) / 2;
                boolean iconFilled = active || hovered;
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), iconFilled ? (active ? 255 : 180) : 28));
                g2.fillRoundRect(12, by, 28, 28, 8, 8);
                if (icon != null) {
                    Image ic = recolor(icon, (active || hovered) ? Color.WHITE : accent);
                    g2.drawImage(ic, 16, by + 4, 20, 20, null);
                }

                // Label
                g2.setFont(active ? FONT_BODY_BOLD : FONT_BODY);
                g2.setColor(active ? Color.WHITE : (hovered ? TEXT_PRIMARY : TEXT_SECONDARY));
                g2.drawString(label, 48, getHeight() / 2 + 5);

                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(254, 46));
        btn.setMaximumSize(new Dimension(254, 46));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (DashboardUI.this.activeNav != null) DashboardUI.this.activeNav.repaint();
                DashboardUI.this.activeNav = (JButton) e.getSource();
                ((JButton) e.getSource()).repaint();
            }
            public void mouseEntered(MouseEvent e) { ((JButton) e.getSource()).repaint(); }
            public void mouseExited(MouseEvent e)  { ((JButton) e.getSource()).repaint(); }
        });
        return btn;
    }

    // ── User card ──────────────────────────────────────────────────────────────
    private JPanel buildUserCard() {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Top separator
                GradientPaint gp = new GradientPaint(
                    20, 0, INDIGO_GLOW,
                    getWidth() - 20, 0, new Color(INDIGO_GLOW.getRed(), INDIGO_GLOW.getGreen(), INDIGO_GLOW.getBlue(), 0));
                g2.setPaint(gp);
                g2.fillRect(16, 0, getWidth() - 32, 1);
                // Card background
                g2.setColor(INDIGO_GLOW);
                g2.fillRoundRect(12, 10, getWidth() - 24, getHeight() - 18, 14, 14);
                g2.setColor(INDIGO_SECONDARY);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(12, 10, getWidth() - 24, getHeight() - 18, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(278, 90));

        // Avatar
        JLabel avatar = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, INDIGO_PRIMARY, getWidth(), getHeight(), VIOLET_PRIMARY);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Georgia", Font.BOLD, 17));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("A", (getWidth() - fm.stringWidth("A")) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setBounds(24, 22, 40, 40);

        JLabel name = new JLabel("Admin User");
        name.setFont(new Font("Segoe UI", Font.BOLD, 13));
        name.setForeground(TEXT_PRIMARY);
        name.setBounds(74, 23, 170, 18);

        // Online dot + role
        JLabel role = new JLabel() {
            float pulse = 0;
            float dir   = 0.05f;
            {
                new Timer(55, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        pulse += dir;
                        if (pulse >= 1f || pulse <= 0f) dir = -dir;
                        repaint();
                    }
                }).start();
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Pulsing dot
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f + pulse * 0.6f));
                g2.setColor(EMERALD_PRIMARY);
                g2.fillOval(0, 5, 8, 8);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2.setColor(EMERALD_PRIMARY);
                g2.fillOval(1, 6, 6, 6);
                // Role text
                g2.setFont(FONT_SMALL);
                g2.setColor(INDIGO_PRIMARY);
                g2.drawString("MCA Project", 14, 14);
                g2.dispose();
            }
        };
        role.setBounds(74, 44, 185, 20);

        card.add(avatar);
        card.add(name);
        card.add(role);
        return card;
    }

    // =========================================================================
    //  MAIN PANEL
    // =========================================================================
    private JPanel buildMainPanel() {
        JPanel main = new JPanel(new BorderLayout(0, 24));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(24, 28, 24, 28));

        main.add(buildTopBar(),     BorderLayout.NORTH);
        main.add(buildContent(),    BorderLayout.CENTER);
        return main;
    }

    // ── Top bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        topBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Multi-layer shadow
                for (int i = 4; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 4 * i));
                    g2.fillRoundRect(i, i, getWidth() - i, getHeight() - i, 20, 20);
                }
                // Glass body
                g2.setColor(GLASS_WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);
                // Glass border
                g2.setColor(GLASS_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
                // Indigo accent bar top
                GradientPaint gp = new GradientPaint(0, 0, INDIGO_PRIMARY,
                    (getWidth() - 4) * 2 / 5, 0,
                    new Color(INDIGO_PRIMARY.getRed(), INDIGO_PRIMARY.getGreen(), INDIGO_PRIMARY.getBlue(), 0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 4, 4, 4, 4);
                g2.dispose();
            }
        };
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(18, 28, 18, 28));
        topBar.setPreferredSize(new Dimension(0, 88));

        // Left
        JPanel left = new JPanel(new BorderLayout(0, 4));
        left.setOpaque(false);

        titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(FONT_DISPLAY_LG);
        titleLabel.setForeground(TEXT_PRIMARY);

        dateLabel = new JLabel(
            new java.text.SimpleDateFormat("EEEE, MMMM dd yyyy").format(new java.util.Date()));
        dateLabel.setFont(FONT_SMALL);
        dateLabel.setForeground(TEXT_TERTIARY);

        left.add(titleLabel, BorderLayout.NORTH);
        left.add(dateLabel,  BorderLayout.SOUTH);

        // Right
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        right.add(buildLivePill());
        right.add(buildDarkModeToggle());
        right.add(buildSearchBar());

        topBar.add(left,  BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);
        return topBar;
    }

    // ── Dark Mode Toggle Button ───────────────────────────────────────────────
    private JPanel buildDarkModeToggle() {
        JPanel togglePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background
                g2.setColor(new Color(255, 255, 255, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER_LIGHT);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        togglePanel.setOpaque(false);
        togglePanel.setPreferredSize(new Dimension(42, 42));
        togglePanel.setLayout(new GridBagLayout());
        
        JButton toggleBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isDarkMode) {
                    // Moon icon for dark mode
                    g2.setColor(VIOLET_PRIMARY);
                    g2.fillOval(8, 8, 24, 24);
                    g2.setColor(Color.WHITE);
                    g2.fillOval(14, 10, 18, 18);
                    g2.setColor(VIOLET_PRIMARY);
                    g2.fillOval(16, 12, 14, 14);
                } else {
                    // Sun icon for light mode
                    g2.setColor(AMBER_PRIMARY);
                    g2.fillOval(13, 13, 16, 16);
                    g2.setColor(AMBER_SECONDARY);
                    for (int i = 0; i < 8; i++) {
                        double angle = i * Math.PI * 2 / 8;
                        int x = (int)(21 + 14 * Math.cos(angle));
                        int y = (int)(21 + 14 * Math.sin(angle));
                        g2.fillOval(x - 3, y - 3, 6, 6);
                    }
                }
                g2.dispose();
            }
        };
        toggleBtn.setOpaque(false);
        toggleBtn.setFocusPainted(false);
        toggleBtn.setBorderPainted(false);
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleBtn.setPreferredSize(new Dimension(42, 42));
        
        toggleBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isDarkMode = !isDarkMode;
                applyTheme(isDarkMode);
                toggleBtn.repaint();
            }
        });
        
        togglePanel.add(toggleBtn);
        return togglePanel;
    }
    
    private void applyTheme(boolean dark) {
        if (dark) {
            // Apply dark theme colors
            BG_PRIMARY = DARK_BG_PRIMARY;
            BG_SECONDARY = DARK_BG_SECONDARY;
            BG_CARD = DARK_BG_CARD;
            GLASS_WHITE = DARK_GLASS;
            GLASS_BORDER = DARK_GLASS_BORDER;
            TEXT_PRIMARY = DARK_TEXT_PRIMARY;
            TEXT_SECONDARY = DARK_TEXT_SECONDARY;
            TEXT_TERTIARY = DARK_TEXT_TERTIARY;
            TEXT_DISABLED = DARK_TEXT_TERTIARY;
            BORDER_LIGHT = DARK_BORDER;
            INDIGO_GLOW = DARK_INDIGO_GLOW;
            // Slightly brighten accent colors for better visibility
            INDIGO_PRIMARY = new Color(99, 102, 241);
            INDIGO_SECONDARY = new Color(165, 180, 252);
            EMERALD_PRIMARY = new Color(52, 211, 153);
            EMERALD_SECONDARY = new Color(110, 231, 183);
            ROSE_PRIMARY = new Color(251, 113, 133);
            ROSE_SECONDARY = new Color(251, 146, 158);
            AMBER_PRIMARY = new Color(251, 191, 36);
            AMBER_SECONDARY = new Color(252, 211, 77);
            SKY_PRIMARY = new Color(56, 189, 248);
            SKY_SECONDARY = new Color(125, 211, 252);
            VIOLET_PRIMARY = new Color(167, 139, 250);
            VIOLET_SECONDARY = new Color(196, 181, 253);
        } else {
            // Apply light theme colors
            BG_PRIMARY = new Color(245, 246, 252);
            BG_SECONDARY = new Color(237, 240, 250);
            BG_CARD = new Color(255, 255, 255);
            GLASS_WHITE = new Color(255, 255, 255, 210);
            GLASS_BORDER = new Color(255, 255, 255, 110);
            TEXT_PRIMARY = new Color(15, 23, 42);
            TEXT_SECONDARY = new Color(51, 65, 85);
            TEXT_TERTIARY = new Color(100, 116, 139);
            TEXT_DISABLED = new Color(148, 163, 184);
            BORDER_LIGHT = new Color(226, 232, 240);
            INDIGO_GLOW = new Color(224, 231, 255);
            // Reset accents to original
            INDIGO_PRIMARY = new Color(79, 70, 229);
            INDIGO_SECONDARY = new Color(129, 140, 248);
            EMERALD_PRIMARY = new Color(16, 185, 129);
            EMERALD_SECONDARY = new Color(110, 231, 183);
            ROSE_PRIMARY = new Color(244, 63, 94);
            ROSE_SECONDARY = new Color(251, 146, 158);
            AMBER_PRIMARY = new Color(245, 158, 11);
            AMBER_SECONDARY = new Color(252, 211, 77);
            SKY_PRIMARY = new Color(14, 165, 233);
            SKY_SECONDARY = new Color(125, 211, 252);
            VIOLET_PRIMARY = new Color(139, 92, 246);
            VIOLET_SECONDARY = new Color(196, 181, 253);
        }
        
        // Refresh all UI components
        contentPane.repaint();
        sidebar.repaint();
        mainPanel.repaint();
        if (topBar != null) topBar.repaint();
        if (titleLabel != null) titleLabel.setForeground(TEXT_PRIMARY);
        if (dateLabel != null) dateLabel.setForeground(TEXT_TERTIARY);
        
        // Refresh charts
        refreshDashboard();
        // Recreate charts to apply new colors
        refreshCharts();
    }
    
    private void refreshCharts() {
        // Remove old charts and add new ones with updated theme
        if (chartsPanel != null) {
            chartsPanel.removeAll();
            chartsPanel.add(buildChartCard("Monthly Issue Trend",  createMonthlyIssuesChart(),  INDIGO_PRIMARY,  "Book circulation over time"));
            chartsPanel.add(buildChartCard("Top Issued Books",     createTopBooksChart(),       EMERALD_PRIMARY, "Most popular titles"));
            chartsPanel.add(buildChartCard("Overdue Members",      createOverdueMembersChart(), ROSE_PRIMARY,    "Pending returns"));
            chartsPanel.add(buildChartCard("Fine Collection",      createFineChart(),           AMBER_PRIMARY,   "Revenue from fines"));
            chartsPanel.add(buildChartCard("Weekly Activity",      createWeeklyChart(),         VIOLET_PRIMARY,  "Day-wise issue pattern"));
            chartsPanel.add(buildChartCard("Category Popularity",  createCategoryChart(),       SKY_PRIMARY,     "Genre distribution"));
            chartsPanel.revalidate();
            chartsPanel.repaint();
        }
    }

    private JPanel buildLivePill() {
        JPanel pill = new JPanel() {
            float pulse = 0;
            float dir   = 0.05f;
            {
                new Timer(55, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        pulse += dir;
                        if (pulse >= 1f || pulse <= 0f) dir = -dir;
                        repaint();
                    }
                }).start();
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Pill background
                g2.setColor(new Color(209, 250, 229, (int)(120 + pulse * 80)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.setColor(new Color(EMERALD_PRIMARY.getRed(), EMERALD_PRIMARY.getGreen(), EMERALD_PRIMARY.getBlue(), 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                // Dot
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f + pulse * 0.5f));
                g2.setColor(EMERALD_PRIMARY);
                g2.fillOval(10, (getHeight() - 9) / 2, 9, 9);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                // Text
                g2.setColor(EMERALD_PRIMARY);
                g2.setFont(FONT_SMALL_BOLD);
                g2.drawString("LIVE", 26, getHeight() / 2 + 4);
                g2.dispose();
            }
        };
        pill.setOpaque(false);
        pill.setPreferredSize(new Dimension(74, 36));
        return pill;
    }

    private JPanel buildSearchBar() {
        JPanel panel = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER_LIGHT);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(290, 42));
        panel.setBorder(new EmptyBorder(0, 14, 0, 14));

        JTextField field = new JTextField("Search books, members...");
        field.setBorder(null);
        field.setOpaque(false);
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_TERTIARY);
        field.setCaretColor(INDIGO_PRIMARY);

        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    // ── Content ───────────────────────────────────────────────────────────────
    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(0, 22));
        content.setOpaque(false);

        // Stat cards
        cardsRow = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsRow.setOpaque(false);
        cardsRow.setPreferredSize(new Dimension(0, 140));

        booksLabel   = makeValueLabel();
        membersLabel = makeValueLabel();
        issuedLabel  = makeValueLabel();
        overdueLabel = makeValueLabel();

        cardsRow.add(buildStatCard("Total Books",    booksLabel,   INDIGO_PRIMARY,  makeIcon_Book_Lg(),   "\u2191 12% from last month"));
        cardsRow.add(buildStatCard("Active Members", membersLabel, EMERALD_PRIMARY, makeIcon_People_Lg(), "\u2191 8 new this week"));
        cardsRow.add(buildStatCard("Issued Books",   issuedLabel,  ROSE_PRIMARY,    makeIcon_Issue_Lg(),  "Currently in circulation"));
        cardsRow.add(buildStatCard("Overdue Books",  overdueLabel, AMBER_PRIMARY,   makeIcon_Alert_Lg(),  "\u2193 3 less than yesterday"));

        content.add(cardsRow, BorderLayout.NORTH);
        refreshDashboard();

        // Charts
        chartsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        chartsPanel.setOpaque(false);

        chartsPanel.add(buildChartCard("Monthly Issue Trend",  createMonthlyIssuesChart(),  INDIGO_PRIMARY,  "Book circulation over time"));
        chartsPanel.add(buildChartCard("Top Issued Books",     createTopBooksChart(),       EMERALD_PRIMARY, "Most popular titles"));
        chartsPanel.add(buildChartCard("Overdue Members",      createOverdueMembersChart(), ROSE_PRIMARY,    "Pending returns"));
        chartsPanel.add(buildChartCard("Fine Collection",      createFineChart(),           AMBER_PRIMARY,   "Revenue from fines"));
        chartsPanel.add(buildChartCard("Weekly Activity",      createWeeklyChart(),         VIOLET_PRIMARY,  "Day-wise issue pattern"));
        chartsPanel.add(buildChartCard("Category Popularity",  createCategoryChart(),       SKY_PRIMARY,     "Genre distribution"));

        content.add(chartsPanel, BorderLayout.CENTER);
        return content;
    }

    // ── Stat card ──────────────────────────────────────────────────────────────
    private JPanel buildStatCard(final String title, final JLabel valLabel,
                                  final Color accent, final Image icon, final String trend) {
        JPanel card = new JPanel(null) {
            private float hover = 0f;
            private boolean in  = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        in = true;
                        new Timer(14, new ActionListener() {
                            public void actionPerformed(ActionEvent ev) {
                                if (in && hover < 1f) { hover = Math.min(1f, hover + 0.1f); repaint(); }
                                else ((Timer) ev.getSource()).stop();
                            }
                        }).start();
                    }
                    public void mouseExited(MouseEvent e) {
                        in = false;
                        new Timer(14, new ActionListener() {
                            public void actionPerformed(ActionEvent ev) {
                                if (!in && hover > 0f) { hover = Math.max(0f, hover - 0.1f); repaint(); }
                                else ((Timer) ev.getSource()).stop();
                            }
                        }).start();
                    }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                for (int i = 5; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 4 * i));
                    g2.fillRoundRect(i, i, getWidth() - i, getHeight() - i, 18, 18);
                }
                // Body
                g2.setColor(new Color(BG_CARD.getRed(), BG_CARD.getGreen(), BG_CARD.getBlue(), (int)(255 - hover * 20)));
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 18, 18);
                // Hover border
                if (hover > 0.05f) {
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (int)(hover * 120)));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, getWidth() - 6, getHeight() - 6, 16, 16);
                } else {
                    g2.setColor(BORDER_LIGHT);
                    g2.setStroke(new BasicStroke(0.8f));
                    g2.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 18, 18);
                }
                // Top accent gradient
                GradientPaint gp = new GradientPaint(0, 0, accent,
                    (getWidth() - 4) * 3 / 5, 0,
                    new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 4, 5, 4, 4);
                // Decorative orb
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 14));
                g2.fillOval(getWidth() - 74, -22, 88, 88);
                g2.dispose();
            }
        };
        card.setOpaque(false);

        // Icon badge
        JLabel iconLbl = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, accent, getWidth(), getHeight(),
                    new Color(Math.min(accent.getRed() + 50, 255),
                              Math.min(accent.getGreen() + 50, 255),
                              Math.min(accent.getBlue()  + 50, 255)));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                if (icon != null)
                    g2.drawImage(recolor(icon, Color.WHITE), 10, 10, getWidth() - 20, getHeight() - 20, null);
                g2.dispose();
            }
        };
        iconLbl.setBounds(18, 22, 52, 52);

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(FONT_CAPTION);
        titleLbl.setForeground(TEXT_TERTIARY);
        titleLbl.setBounds(84, 20, 200, 14);

        valLabel.setBounds(84, 36, 190, 40);
        valLabel.setForeground(TEXT_PRIMARY);

        JLabel trendLbl = new JLabel(trend);
        trendLbl.setFont(FONT_SMALL);
        trendLbl.setForeground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 200));
        trendLbl.setBounds(84, 82, 200, 16);

        card.add(iconLbl);
        card.add(titleLbl);
        card.add(valLabel);
        card.add(trendLbl);
        return card;
    }

    // ── Chart card ─────────────────────────────────────────────────────────────
    private JPanel buildChartCard(final String title, final ChartPanel cp,
                                   final Color accent, final String subtitle) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                for (int i = 4; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 4 * i));
                    g2.fillRoundRect(i, i, getWidth() - i, getHeight() - i, 16, 16);
                }
                // Glass body
                g2.setColor(GLASS_WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 16, 16);
                // Border
                g2.setColor(BORDER_LIGHT);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 16, 16);
                // Left accent strip
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 200));
                g2.fillRoundRect(0, 28, 4, 32, 4, 4);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 18, 14, 14));

        // Header
        JPanel header = new JPanel(new BorderLayout(0, 3));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);

        // Gradient dot
        JLabel dot = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, accent, getWidth(), getHeight(),
                    new Color(Math.min(accent.getRed() + 60, 255),
                              Math.min(accent.getGreen() + 60, 255),
                              Math.min(accent.getBlue()  + 60, 255)));
                g2.setPaint(gp);
                g2.fillOval(0, (getHeight() - 11) / 2, 11, 11);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(13, 22));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_DISPLAY_SM);
        titleLbl.setForeground(TEXT_PRIMARY);

        titleRow.add(dot);
        titleRow.add(titleLbl);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(FONT_SMALL);
        subLbl.setForeground(TEXT_TERTIARY);

        header.add(titleRow, BorderLayout.NORTH);
        header.add(subLbl,   BorderLayout.SOUTH);

        cp.setBackground(isDarkMode ? DARK_BG_CARD : Color.WHITE);
        cp.setOpaque(true);
        cp.setBorder(new CompoundBorder(
            new LineBorder(BORDER_LIGHT, 1, true),
            new EmptyBorder(8, 8, 8, 8)));

        card.add(header, BorderLayout.NORTH);
        card.add(cp,     BorderLayout.CENTER);
        return card;
    }

    private JLabel makeValueLabel() {
        JLabel l = new JLabel("0");
        l.setFont(FONT_VALUE);
        l.setForeground(TEXT_PRIMARY);
        return l;
    }

    // =========================================================================
    //  REFRESH  (unchanged logic)
    // =========================================================================
    private void refreshDashboard() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst;

         // 🔥 Total Books
         pst = con.prepareStatement("SELECT COUNT(*) FROM books WHERE admin_id=?");
         pst.setInt(1, UserSession.adminId);
         ResultSet r1 = pst.executeQuery();
         if (r1.next()) booksLabel.setText(String.valueOf(r1.getInt(1)));

         // 🔥 Total Members
         pst = con.prepareStatement("SELECT COUNT(*) FROM members WHERE admin_id=?");
         pst.setInt(1, UserSession.adminId);
         ResultSet r2 = pst.executeQuery();
         if (r2.next()) membersLabel.setText(String.valueOf(r2.getInt(1)));

         // 🔥 Issued Books
         pst = con.prepareStatement(
             "SELECT COUNT(*) FROM transactions WHERE status='Issued' AND admin_id=?"
         );
         pst.setInt(1, UserSession.adminId);
         ResultSet r3 = pst.executeQuery();
         if (r3.next()) issuedLabel.setText(String.valueOf(r3.getInt(1)));

         // 🔥 Overdue Books
         pst = con.prepareStatement(
             "SELECT COUNT(*) FROM transactions WHERE status='Issued' AND due_date < CURDATE() AND admin_id=?"
         );
         pst.setInt(1, UserSession.adminId);
         ResultSet r4 = pst.executeQuery();
         if (r4.next()) overdueLabel.setText(String.valueOf(r4.getInt(1)));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void startBackgroundServices() {
        java.util.Timer bgTimer = new java.util.Timer();
        bgTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            public void run() { util.ReminderService.checkDueReminders(); }
        }, 0, 86400000);
        new javax.swing.Timer(5000, new ActionListener() {
            public void actionPerformed(ActionEvent e) { refreshDashboard(); }
        }).start();
    }

    // =========================================================================
    //  CHARTS  (unchanged logic but using dynamic colors)
    // =========================================================================
    private ChartPanel createMonthlyIssuesChart() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT MONTH(issue_date) as m, COUNT(*) as total FROM transactions WHERE admin_id=? GROUP BY MONTH(issue_date)");
        ) {
            pst.setInt(1, UserSession.adminId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) ds.setValue(rs.getInt("total"), "Issues", MONTHS[rs.getInt("m") - 1]);
        } catch (Exception e) { e.printStackTrace(); }
        JFreeChart c = ChartFactory.createLineChart("", "", "", ds);
        styleLineChart(c, INDIGO_PRIMARY);
        return chartPanel(c);
    }

    private ChartPanel createTopBooksChart() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT book_title, COUNT(*) as total FROM transactions WHERE admin_id=? GROUP BY book_title ORDER BY COUNT(*) DESC LIMIT 5");
        ) {
            pst.setInt(1, UserSession.adminId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) ds.setValue(rs.getInt("total"), "Issues", rs.getString("book_title"));
        } catch (Exception e) { e.printStackTrace(); }
        JFreeChart c = ChartFactory.createBarChart("", "", "", ds);
        styleBarChart(c, EMERALD_PRIMARY);
        return chartPanel(c);
    }

    private ChartPanel createOverdueMembersChart() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT member_name, COUNT(*) as total FROM transactions WHERE status='Issued' AND due_date<CURDATE() AND admin_id=? GROUP BY member_name");
        ) {
            pst.setInt(1, UserSession.adminId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) ds.setValue(rs.getInt("total"), "Overdue", rs.getString("member_name"));
        } catch (Exception e) { e.printStackTrace(); }
        JFreeChart c = ChartFactory.createBarChart("", "", "", ds);
        styleBarChart(c, ROSE_PRIMARY);
        return chartPanel(c);
    }

    private ChartPanel createFineChart() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT MONTH(return_date) as m, SUM(fine) as total FROM transactions WHERE admin_id=? AND return_date IS NOT NULL GROUP BY MONTH(return_date)");
        ) {
            pst.setInt(1, UserSession.adminId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) ds.setValue(rs.getInt("total"), "Fine", MONTHS[rs.getInt("m") - 1]);
        } catch (Exception e) { e.printStackTrace(); }
        JFreeChart c = ChartFactory.createLineChart("", "", "", ds);
        styleLineChart(c, AMBER_PRIMARY);
        return chartPanel(c);
    }

    private ChartPanel createWeeklyChart() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT DAYNAME(issue_date) as d, COUNT(*) as total FROM transactions WHERE admin_id=? GROUP BY DAYNAME(issue_date)");
        ) {
            pst.setInt(1, UserSession.adminId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String d = rs.getString("d");
                ds.setValue(rs.getInt("total"), "Issues", d.length() >= 3 ? d.substring(0, 3) : d);
            }
        } catch (Exception e) { e.printStackTrace(); }
        JFreeChart c = ChartFactory.createBarChart("", "", "", ds);
        styleBarChart(c, VIOLET_PRIMARY);
        return chartPanel(c);
    }

    private ChartPanel createCategoryChart() {
        DefaultPieDataset ds = new DefaultPieDataset();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT category, COUNT(*) as total FROM books WHERE admin_id=? GROUP BY category");
        ) {
            pst.setInt(1, UserSession.adminId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) ds.setValue(rs.getString("category"), rs.getInt("total"));
        } catch (Exception e) { e.printStackTrace(); }
        JFreeChart c = ChartFactory.createPieChart("", ds, true, true, false);
        PiePlot plot = (PiePlot) c.getPlot();
        plot.setBackgroundPaint(isDarkMode ? DARK_BG_CARD : Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setShadowPaint(null);
        plot.setLabelFont(FONT_SMALL);
        plot.setLabelBackgroundPaint(isDarkMode ? DARK_BG_CARD : new Color(248, 250, 252));
        plot.setLabelOutlinePaint(BORDER_LIGHT);
        plot.setLabelShadowPaint(null);
        plot.setLabelPaint(TEXT_PRIMARY);
        plot.setInteriorGap(0.04);
        Color[] pc = {INDIGO_PRIMARY, EMERALD_PRIMARY, ROSE_PRIMARY, AMBER_PRIMARY, VIOLET_PRIMARY, SKY_PRIMARY};
        for (int i = 0; i < ds.getItemCount(); i++) plot.setSectionPaint(ds.getKey(i), pc[i % pc.length]);
        c.setBackgroundPaint(isDarkMode ? DARK_BG_CARD : Color.WHITE);
        if (c.getLegend() != null) {
            c.getLegend().setBackgroundPaint(isDarkMode ? DARK_BG_CARD : Color.WHITE);
            c.getLegend().setItemFont(FONT_SMALL);
            c.getLegend().setItemPaint(TEXT_PRIMARY);
            c.getLegend().setBorder(0, 0, 0, 0);
        }
        return chartPanel(c);
    }

    private void styleBarChart(JFreeChart chart, Color color) {
        chart.setBackgroundPaint(isDarkMode ? DARK_BG_CARD : Color.WHITE);
        chart.setAntiAlias(true);
        chart.setBorderVisible(false);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(isDarkMode ? DARK_BG_SECONDARY : new Color(248, 250, 252));
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinePaint(isDarkMode ? new Color(51, 65, 85) : new Color(226, 232, 240, 100));
        plot.setRangeGridlineStroke(new BasicStroke(0.8f));
        plot.setOutlinePaint(null);
        plot.setInsets(new org.jfree.chart.ui.RectangleInsets(10, 6, 6, 10));
        styleAxis(plot);
        BarRenderer r = (BarRenderer) plot.getRenderer();
        r.setBarPainter(new StandardBarPainter());
        r.setShadowVisible(false);
        r.setMaximumBarWidth(0.10);
        r.setItemMargin(0.15);
        Color lighter = new Color(Math.min(color.getRed() + 60, 255),
            Math.min(color.getGreen() + 60, 255), Math.min(color.getBlue() + 60, 255));
        r.setSeriesPaint(0, new GradientPaint(0, 80, color, 0, 0, lighter));
        r.setSeriesItemLabelFont(0, FONT_CAPTION);
    }

    private void styleLineChart(JFreeChart chart, Color color) {
        chart.setBackgroundPaint(isDarkMode ? DARK_BG_CARD : Color.WHITE);
        chart.setAntiAlias(true);
        chart.setBorderVisible(false);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(isDarkMode ? DARK_BG_SECONDARY : new Color(248, 250, 252));
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinePaint(isDarkMode ? new Color(51, 65, 85) : new Color(226, 232, 240, 100));
        plot.setRangeGridlineStroke(new BasicStroke(0.8f));
        plot.setOutlinePaint(null);
        plot.setInsets(new org.jfree.chart.ui.RectangleInsets(10, 6, 6, 10));
        styleAxis(plot);
        LineAndShapeRenderer r = (LineAndShapeRenderer) plot.getRenderer();
        r.setSeriesPaint(0, color);
        r.setSeriesStroke(0, new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        r.setDefaultShapesVisible(true);
        r.setSeriesShape(0, new Ellipse2D.Double(-5, -5, 10, 10));
        r.setSeriesFillPaint(0, isDarkMode ? DARK_BG_CARD : Color.WHITE);
        r.setUseFillPaint(true);
        r.setDrawOutlines(true);
        r.setSeriesOutlinePaint(0, color);
        r.setSeriesOutlineStroke(0, new BasicStroke(2.5f));
    }

    private void styleAxis(CategoryPlot plot) {
        plot.getDomainAxis().setTickLabelFont(FONT_CAPTION);
        plot.getDomainAxis().setTickLabelPaint(TEXT_TERTIARY);
        plot.getDomainAxis().setAxisLinePaint(BORDER_LIGHT);
        plot.getDomainAxis().setTickMarksVisible(false);
        plot.getDomainAxis().setLabelFont(FONT_CAPTION);
        plot.getDomainAxis().setLabelPaint(TEXT_TERTIARY);
        plot.getRangeAxis().setTickLabelFont(FONT_CAPTION);
        plot.getRangeAxis().setTickLabelPaint(TEXT_TERTIARY);
        plot.getRangeAxis().setAxisLinePaint(BORDER_LIGHT);
        plot.getRangeAxis().setTickMarksVisible(false);
        plot.getRangeAxis().setLabelFont(FONT_CAPTION);
        plot.getRangeAxis().setLabelPaint(TEXT_TERTIARY);
    }

    private ChartPanel chartPanel(JFreeChart chart) {
        ChartPanel cp = new ChartPanel(chart);
        cp.setOpaque(false);
        cp.setBackground(isDarkMode ? DARK_BG_CARD : Color.WHITE);
        cp.setMinimumDrawWidth(0);
        cp.setMinimumDrawHeight(0);
        cp.setPopupMenu(null);
        return cp;
    }

    // =========================================================================
    //  KEYBOARD SHORTCUTS
    // =========================================================================
    private void setupKeyboardShortcuts(JFrame frame,
        final JButton addBook,   final JButton viewBook,
        final JButton addMember, final JButton viewMember,
        final JButton issueBook, final JButton returnBook,
        final JButton transactions, final JButton issuedRec,
        final JButton entryReg) {

        JRootPane root = frame.getRootPane();

        // Ctrl+B → Add Book
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK), "addBook");
        root.getActionMap().put("addBook", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { addBook.doClick(); }
        });

        // Ctrl+Shift+B → View Books
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "viewBook");
        root.getActionMap().put("viewBook", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { viewBook.doClick(); }
        });

        // Ctrl+M → Add Member
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK), "addMember");
        root.getActionMap().put("addMember", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { addMember.doClick(); }
        });

        // Ctrl+Shift+M → View Members
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), "viewMember");
        root.getActionMap().put("viewMember", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { viewMember.doClick(); }
        });

        // Ctrl+I → Issue Book
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK), "issueBook");
        root.getActionMap().put("issueBook", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { issueBook.doClick(); }
        });

        // Ctrl+R → Return Book
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK), "returnBook");
        root.getActionMap().put("returnBook", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { returnBook.doClick(); }
        });

        // Ctrl+T → Transactions
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK), "transactions");
        root.getActionMap().put("transactions", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { transactions.doClick(); }
        });

        // Ctrl+L → Issued Records
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK), "issuedRec");
        root.getActionMap().put("issuedRec", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { issuedRec.doClick(); }
        });

        // Ctrl+E → Entry Register
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "entryReg");
        root.getActionMap().put("entryReg", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { entryReg.doClick(); }
        });

        // F5 → Refresh Dashboard
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        root.getActionMap().put("refresh", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { refreshDashboard(); }
        });

        // Ctrl+D → Toggle Dark Mode
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "darkMode");
        root.getActionMap().put("darkMode", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // Find and click the dark mode toggle button
                isDarkMode = !isDarkMode;
                applyTheme(isDarkMode);
                // Update the toggle button's appearance
                if (topBar != null) {
                    Component[] components = topBar.getComponents();
                    for (Component comp : components) {
                        if (comp instanceof JPanel) {
                            JPanel panel = (JPanel) comp;
                            if (panel.getComponentCount() > 0 && panel.getComponent(0) instanceof JButton) {
                                panel.getComponent(0).repaint();
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    // =========================================================================
    //  ICONS  (all pure geometry, no lambdas, no Consumer)
    // =========================================================================
    private Image makeIcon(int size, float stroke,  IconPainter painter) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        painter.paint(g);
        g.dispose();
        return img;
    }

    /** Functional interface — no java.util.function needed, works in Java 7+ */
    interface IconPainter { void paint(Graphics2D g); }

    private Image makeIcon_Book() {
        return makeIcon(20, 1.8f, new IconPainter() { public void paint(Graphics2D g) {
            g.drawRoundRect(3, 1, 13, 17, 3, 3);
            g.drawLine(6, 7, 12, 7); g.drawLine(6, 10, 12, 10); g.drawLine(6, 13, 10, 13);
        }});
    }
    private Image makeIcon_Stack() {
        return makeIcon(20, 1.8f, new IconPainter() { public void paint(Graphics2D g) {
            g.drawRoundRect(1, 4, 13, 14, 3, 3); g.drawRoundRect(5, 1, 13, 14, 3, 3);
            g.drawLine(8, 7, 15, 7); g.drawLine(8, 10, 15, 10);
        }});
    }
    private Image makeIcon_PersonAdd() {
        return makeIcon(20, 1.8f, new IconPainter() { public void paint(Graphics2D g) {
            g.drawOval(3, 1, 9, 9); g.drawArc(1, 11, 10, 7, 0, 180);
            g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(14, 12, 14, 18); g.drawLine(11, 15, 17, 15);
        }});
    }
    private Image makeIcon_People() {
        return makeIcon(20, 1.8f, new IconPainter() { public void paint(Graphics2D g) {
            g.drawOval(1, 2, 7, 7); g.drawArc(0, 10, 9, 6, 0, 180);
            g.drawOval(11, 1, 7, 7); g.drawArc(9, 9, 10, 7, 0, 180);
        }});
    }
    private Image makeIcon_ArrowOut() {
        return makeIcon(20, 1.8f, new IconPainter() { public void paint(Graphics2D g) {
            g.drawRoundRect(2, 3, 11, 14, 3, 3);
            g.drawLine(6, 7, 9, 7); g.drawLine(6, 10, 9, 10);
            int[] x = {15, 18, 15}; int[] y = {7, 10, 13}; g.fillPolygon(x, y, 3);
            g.drawLine(10, 6, 18, 10);
        }});
    }
    private Image makeIcon_ArrowIn() {
        return makeIcon(20, 1.8f, new IconPainter() { public void paint(Graphics2D g) {
            g.drawRoundRect(7, 3, 11, 14, 3, 3);
            g.drawLine(11, 7, 14, 7); g.drawLine(11, 10, 14, 10);
            int[] x = {5, 2, 5}; int[] y = {7, 10, 13}; g.fillPolygon(x, y, 3);
            g.drawLine(2, 10, 10, 6);
        }});
    }
    private Image makeIcon_Receipt() {
        return makeIcon(20, 1.8f, new IconPainter() { public void paint(Graphics2D g) {
            g.drawRoundRect(3, 2, 14, 16, 2, 2); g.drawLine(3, 6, 17, 6);
            g.drawLine(6, 10, 9, 10); g.drawLine(11, 10, 14, 10);
            g.drawLine(6, 13, 9, 13); g.drawLine(11, 13, 14, 13);
        }});
    }
    private Image makeIcon_Clipboard() {
        return makeIcon(20, 1.8f, new IconPainter() { public void paint(Graphics2D g) {
            g.drawRoundRect(3, 3, 14, 15, 3, 3);
            g.drawLine(6, 8, 14, 8); g.drawLine(6, 11, 14, 11); g.drawLine(6, 14, 11, 14);
            g.drawRoundRect(6, 1, 8, 5, 3, 3);
        }});
    }
    private Image makeIcon_Pencil() {
        return makeIcon(20, 1.8f, new IconPainter() { public void paint(Graphics2D g) {
            g.drawRoundRect(3, 10, 10, 7, 2, 2);
            int[] x = {3, 12, 14, 5}; int[] y = {10, 1, 3, 12}; g.drawPolygon(x, y, 4);
            g.drawLine(12, 1, 14, 3);
        }});
    }

    // Large 28px stat card icons
    private Image makeIcon_Book_Lg() {
        return makeIcon(28, 2f, new IconPainter() { public void paint(Graphics2D g) {
            g.drawRoundRect(4, 2, 18, 24, 4, 4); g.drawLine(4, 14, 22, 14);
            g.drawLine(9, 8, 19, 8); g.drawLine(9, 19, 19, 19);
        }});
    }
    private Image makeIcon_People_Lg() {
        return makeIcon(28, 2f, new IconPainter() { public void paint(Graphics2D g) {
            g.drawOval(5, 2, 10, 10); g.drawArc(2, 14, 11, 9, 0, 180);
            g.drawOval(14, 3, 9, 9);  g.drawArc(11, 13, 13, 8, 0, 180);
        }});
    }
    private Image makeIcon_Issue_Lg() {
        return makeIcon(28, 2f, new IconPainter() { public void paint(Graphics2D g) {
            g.drawRoundRect(3, 3, 14, 22, 4, 4);
            g.drawLine(6, 10, 13, 10); g.drawLine(6, 14, 13, 14);
            int[] x = {17, 24, 17}; int[] y = {9, 14, 19}; g.fillPolygon(x, y, 3);
            g.drawLine(12, 14, 24, 14);
        }});
    }
    private Image makeIcon_Alert_Lg() {
        return makeIcon(28, 2f, new IconPainter() { public void paint(Graphics2D g) {
            int[] x = {14, 3, 25}; int[] y = {3, 25, 25}; g.drawPolygon(x, y, 3);
            g.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(14, 11, 14, 17); g.drawLine(14, 20, 14, 22);
        }});
    }

    private Image recolor(Image src, Color color) {
        int w = src.getWidth(null), h = src.getHeight(null);
        if (w <= 0 || h <= 0) return src;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        int r = color.getRed(), gn = color.getGreen(), b = color.getBlue();
        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++) {
                int a = (out.getRGB(x, y) >> 24) & 0xFF;
                if (a > 10) out.setRGB(x, y, (a << 24) | (r << 16) | (gn << 8) | b);
            }
        return out;
    }
}