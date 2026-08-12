package gui;

import db.DBConnection;
import util.EmailSender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import util.UserSession;
public class ReturnBookUI extends JFrame {

    // ── Palette ──────────────────────────────────────────────────────
    private static final Color BG_PAGE      = new Color(244, 245, 251);
    private static final Color BG_CARD      = new Color(255, 255, 255);
    private static final Color INDIGO       = new Color( 79,  70, 229);
    private static final Color INDIGO_LIGHT = new Color(238, 237, 255);
    private static final Color INDIGO_MED   = new Color(165, 180, 252);
    private static final Color EMERALD      = new Color(  5, 150, 105);
    private static final Color EMERALD_LIGHT= new Color(209, 250, 229);
    private static final Color EMERALD_TIP  = new Color( 52, 211, 153);
    private static final Color VIOLET       = new Color(124,  58, 237);
    private static final Color SKY          = new Color(  2, 132, 199);
    private static final Color SKY_LIGHT    = new Color(224, 242, 254);
    private static final Color ROSE         = new Color(225,  29,  72);
    private static final Color ROSE_LIGHT   = new Color(255, 228, 230);
    private static final Color AMBER        = new Color(217, 119,   6);
    private static final Color AMBER_LIGHT  = new Color(254, 243, 199);
    private static final Color TEXT_H       = new Color( 17,  24,  39);
    private static final Color TEXT_BODY    = new Color( 55,  65,  81);
    private static final Color TEXT_MUTED   = new Color(107, 114, 128);
    private static final Color TEXT_SUBTLE  = new Color(156, 163, 175);
    private static final Color BORDER       = new Color(226, 232, 240);
    private static final Color FIELD_BG     = new Color(249, 250, 255);

    // ── State ─────────────────────────────────────────────────────────
    private JTextField bookIdField;
    private JTextField memberIdField;
    private JLabel     fineLabel;
    private JPanel     fineBanner;
    private int        fineAmount = 0;

    public ReturnBookUI() {

        setTitle("LibraryPro \u2014 Return Book");
        setSize(530, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(BG_PAGE);

        // ── Page wrapper ─────────────────────────────────────────────
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
                for (int i = 5; i > 0; i--) {
                    g2.setColor(new Color(17, 24, 39, 5 * i));
                    g2.fillRoundRect(i, i, getWidth() - i, getHeight() - i, 20, 20);
                }
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
                // Emerald top accent bar
                GradientPaint gp = new GradientPaint(0, 0, EMERALD,
                    (getWidth() - 5) * 2 / 3, 0,
                    new Color(EMERALD.getRed(), EMERALD.getGreen(), EMERALD.getBlue(), 0));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth() - 5, 5, 4, 4);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(462, 570));
        card.setBorder(new EmptyBorder(30, 36, 28, 36));

        // ── Header ───────────────────────────────────────────────────
        JPanel header = new JPanel(null);
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 72));

        // Emerald → Violet icon badge
        JPanel iconBadge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, EMERALD, getWidth(), getHeight(), VIOLET);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Arrow-in + book icon
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(14, 6, 16, 22, 4, 4);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(17, 12, 26, 12);
                g2.drawLine(17, 16, 26, 16);
                // Arrow in (from left)
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(2, 18, 12, 18);
                int[] ax = {5, 2, 5}; int[] ay = {14, 18, 22};
                g2.fillPolygon(ax, ay, 3);
                g2.dispose();
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setBounds(0, 10, 44, 44);

        JLabel titleLbl = new JLabel("Return Book");
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 21));
        titleLbl.setForeground(TEXT_H);
        titleLbl.setBounds(56, 10, 310, 28);

        JLabel subLbl = new JLabel("Process book returns and calculate overdue fines");
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
                GradientPaint gp = new GradientPaint(0, 0,
                    new Color(EMERALD.getRed(), EMERALD.getGreen(), EMERALD.getBlue(), 180),
                    getWidth() * 2 / 3, 0,
                    new Color(EMERALD.getRed(), EMERALD.getGreen(), EMERALD.getBlue(), 0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        divider.setOpaque(false);
        divider.setPreferredSize(new Dimension(0, 1));

        JPanel headerWrap = new JPanel(new BorderLayout());
        headerWrap.setOpaque(false);
        headerWrap.add(header,  BorderLayout.CENTER);
        headerWrap.add(divider, BorderLayout.SOUTH);
        card.add(headerWrap, BorderLayout.NORTH);

        // ── Form ─────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 0, 12, 0));

        // Row 0/1 — Book ID + Member ID side by side
        GridBagConstraints left = new GridBagConstraints();
        left.fill    = GridBagConstraints.HORIZONTAL;
        left.weightx = 0.5;
        left.gridx   = 0; left.gridy = 0;
        left.insets  = new Insets(0, 0, 4, 8);

        GridBagConstraints right = new GridBagConstraints();
        right.fill    = GridBagConstraints.HORIZONTAL;
        right.weightx = 0.5;
        right.gridx   = 1; right.gridy = 0;
        right.insets  = new Insets(0, 0, 4, 0);

        form.add(makeLabel("Book ID",   true, EMERALD), left);
        form.add(makeLabel("Member ID", true, EMERALD), right);

        left.gridy  = 1; left.insets  = new Insets(0, 0, 16, 8);
        right.gridy = 1; right.insets = new Insets(0, 0, 16, 0);

        bookIdField   = buildField("e.g.  101");
        memberIdField = buildField("e.g.  1001");
        form.add(bookIdField,   left);
        form.add(memberIdField, right);

        // Row 2 — Return Date label (full width)
        GridBagConstraints full = new GridBagConstraints();
        full.fill      = GridBagConstraints.HORIZONTAL;
        full.weightx   = 1.0;
        full.gridx     = 0; full.gridy = 2;
        full.gridwidth = 2;
        full.insets    = new Insets(0, 0, 4, 0);
        form.add(makeLabel("Return Date", false, EMERALD), full);

        // Row 3 — Return date display
        full.gridy = 3; full.insets = new Insets(0, 0, 14, 0);
        LocalDate today = LocalDate.now();
        form.add(buildReturnDateRow(today.toString()), full);

        // Row 4 — Fine Amount label
        full.gridy = 4; full.insets = new Insets(0, 0, 4, 0);
        form.add(makeLabel("Fine Amount", false, ROSE), full);

        // Row 5 — Fine display panel
        full.gridy = 5; full.insets = new Insets(0, 0, 14, 0);
        fineBanner = buildFineBanner();
        form.add(fineBanner, full);

        // Row 6 — Two action buttons
        full.gridy = 6; full.insets = new Insets(0, 0, 0, 0);
        JPanel twoBtn = new JPanel(new GridLayout(1, 2, 14, 0));
        twoBtn.setOpaque(false);

        final JButton checkBtn  = buildOutlineBtn("Check Fine",     SKY);
        final JButton returnBtn = buildGradientBtn("Confirm Return", EMERALD, VIOLET);

        twoBtn.add(checkBtn);
        twoBtn.add(returnBtn);
        form.add(twoBtn, full);

        card.add(form, BorderLayout.CENTER);

        // ── Clear link ───────────────────────────────────────────────
        JLabel clearLbl = new JLabel("Clear all fields", SwingConstants.CENTER);
        clearLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        clearLbl.setForeground(TEXT_MUTED);
        clearLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearLbl.setBorder(new EmptyBorder(4, 0, 0, 0));
        clearLbl.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { clearLbl.setForeground(EMERALD); }
            public void mouseExited(MouseEvent e)  { clearLbl.setForeground(TEXT_MUTED); }
            public void mouseClicked(MouseEvent e) {
                bookIdField.setText("");
                memberIdField.setText("");
                fineAmount = 0;
                updateFineBanner(0, false);
                bookIdField.requestFocus();
            }
        });

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(clearLbl, BorderLayout.CENTER);
        card.add(south, BorderLayout.SOUTH);

        page.add(card);
        setVisible(true);

        // ── Listeners (unchanged logic) ──────────────────────────────
        checkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { checkFine(); }
        });
        returnBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { returnBook(); }
        });
    }

    // ── Fine banner builder + updater ────────────────────────────────
    private JPanel buildFineBanner() {
        JPanel banner = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg   = (fineAmount > 0) ? ROSE_LIGHT   : EMERALD_LIGHT;
                Color edge = (fineAmount > 0) ? new Color(ROSE.getRed(), ROSE.getGreen(), ROSE.getBlue(), 80)
                                              : new Color(EMERALD.getRed(), EMERALD.getGreen(), EMERALD.getBlue(), 80);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(edge);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(10, 14, 10, 14));
        banner.setPreferredSize(new Dimension(0, 48));

        // Icon area
        JLabel iconLbl = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = (fineAmount > 0) ? ROSE : EMERALD;
                g2.setColor(c);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                if (fineAmount > 0) {
                    // Warning triangle
                    int[] x = {10, 2, 18}; int[] y = {1, 17, 17};
                    g2.drawPolygon(x, y, 3);
                    g2.drawLine(10, 7, 10, 11);
                    g2.drawLine(10, 13, 10, 14);
                } else {
                    // Checkmark
                    g2.drawLine(2, 9, 7, 14);
                    g2.drawLine(7, 14, 17, 4);
                }
                g2.dispose();
            }
        };
        iconLbl.setPreferredSize(new Dimension(22, 22));

        fineLabel = new JLabel("\u20b90") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(getFont());
                g2.setColor(getForeground());
                g2.drawString(getText(), 0, g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        };
        fineLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        fineLabel.setForeground(EMERALD);

        JLabel statusLbl = new JLabel("No fine — returned on time");
        statusLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLbl.setForeground(EMERALD);
        statusLbl.setName("statusLbl");

        JPanel textCol = new JPanel(new BorderLayout(0, 2));
        textCol.setOpaque(false);
        textCol.add(fineLabel, BorderLayout.NORTH);
        textCol.add(statusLbl, BorderLayout.SOUTH);

        banner.add(iconLbl, BorderLayout.WEST);
        banner.add(textCol, BorderLayout.CENTER);
        return banner;
    }

    private void updateFineBanner(int amount, boolean checked) {
        fineAmount = amount;
        fineLabel.setText("\u20b9" + amount);
        fineLabel.setForeground(amount > 0 ? ROSE : EMERALD);

        // Find statusLbl
        for (Component c : ((JPanel)((BorderLayout)fineBanner.getLayout()).getLayoutComponent(BorderLayout.CENTER)).getComponents()) {
            if (c instanceof JLabel && "statusLbl".equals(c.getName())) {
                JLabel sl = (JLabel) c;
                if (!checked) {
                    sl.setText("Click \u2018Check Fine\u2019 to calculate");
                    sl.setForeground(TEXT_MUTED);
                } else if (amount > 0) {
                    sl.setText("Overdue fine: \u20b92 per day");
                    sl.setForeground(ROSE);
                } else {
                    sl.setText("No fine \u2014 returned on time");
                    sl.setForeground(EMERALD);
                }
            }
        }
        fineBanner.repaint();
    }

    // ── Original logic — completely unchanged ─────────────────────────

    private void checkFine() {
        try {
            Connection con = DBConnection.getConnection();
            int bookId   = Integer.parseInt(bookIdField.getText().trim());
            int memberId = Integer.parseInt(memberIdField.getText().trim());

            PreparedStatement pst = con.prepareStatement(
                "SELECT due_date FROM transactions \r\n"
                + "WHERE book_id=? AND member_id=? AND status='Issued' AND admin_id=?");
            pst.setInt(1, bookId);
            pst.setInt(2, memberId);
            pst.setInt(3, UserSession.adminId);
            ResultSet rs = pst.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "No issued book found for this Member ID and Book ID combination");
                return;
            }

            LocalDate dueDate = rs.getDate("due_date").toLocalDate();
            LocalDate today   = LocalDate.now();
            fineAmount = 0;

            if (today.isAfter(dueDate)) {
                long lateDays = ChronoUnit.DAYS.between(dueDate, today);
                fineAmount = (int) lateDays * 2;
            }

            updateFineBanner(fineAmount, true);
            if (fineAmount > 0) fineLabel.setToolTipText("Late fine: \u20b92 per day");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric IDs", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while checking fine", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnBook() {
        try {
            Connection con = DBConnection.getConnection();
            int bookId   = Integer.parseInt(bookIdField.getText().trim());
            int memberId = Integer.parseInt(memberIdField.getText().trim());

            PreparedStatement bookQuery = con.prepareStatement(
                "SELECT book_title FROM transactions \r\n"
                + "WHERE book_id=? AND member_id=? AND status='Issued' AND admin_id=?");
            bookQuery.setInt(1, bookId);
            bookQuery.setInt(2, memberId);
            bookQuery.setInt(3, UserSession.adminId);
            ResultSet bookRs = bookQuery.executeQuery();
            String bookTitle = bookRs.next() ? bookRs.getString("book_title") : "";

            PreparedStatement memberQuery = con.prepareStatement(
                "SELECT name, email FROM members WHERE id=? AND admin_id=?");
            memberQuery.setInt(1, memberId);
            memberQuery.setInt(2, UserSession.adminId);
            ResultSet memberRs = memberQuery.executeQuery();
            String memberName  = "";
            String memberEmail = "";
            if (memberRs.next()) {
                memberName  = memberRs.getString("name");
                memberEmail = memberRs.getString("email");
            }

            PreparedStatement pst = con.prepareStatement(
                "UPDATE transactions \r\n"
                + "SET return_date=?, fine=?, status='Returned' \r\n"
                + "WHERE book_id=? AND member_id=? AND status='Issued' AND admin_id=?");
            pst.setDate(1, Date.valueOf(LocalDate.now()));
            pst.setInt(2, fineAmount);
            pst.setInt(3, bookId);
            pst.setInt(4, memberId);
            pst.setInt(5, UserSession.adminId);
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated == 0) {
                JOptionPane.showMessageDialog(this, "No issued book found to return");
                return;
            }

          
            PreparedStatement updateBook = con.prepareStatement(
                "UPDATE books SET quantity = quantity + 1 WHERE id=? AND admin_id=?"
            );

            updateBook.setInt(1, bookId);
            updateBook.setInt(2, UserSession.adminId);
            updateBook.executeUpdate();

            try {
                String subject = "Book Returned - LibraryPro";
                String message = "Hello " + memberName + ",\n\n"
                    + "Your book has been returned successfully.\n\n"
                    + "Book Title: " + bookTitle + "\n"
                    + "Return Date: " + LocalDate.now() + "\n";
                if (fineAmount > 0) message += "Late Fine: \u20b9" + fineAmount + "\n";
                message += "\nThank you for using LibraryPro!";
                EmailSender.sendEmail(memberEmail, subject, message);
            } catch (Exception ex) { ex.printStackTrace(); }

            String msg = "Book Returned Successfully!";
            if (fineAmount > 0) msg += "\nFine to Pay: \u20b9" + fineAmount;
            JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);

            bookIdField.setText("");
            memberIdField.setText("");
            fineAmount = 0;
            updateFineBanner(0, false);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric IDs", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while returning the book", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── UI helpers ────────────────────────────────────────────────────

    private JLabel makeLabel(final String text, final boolean required, final Color accent) {
        JLabel lbl = new JLabel(required ? text + " *" : text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(TEXT_BODY);
                g2.drawString(text, 0, g2.getFontMetrics().getAscent());
                if (required) {
                    g2.setColor(accent);
                    g2.drawString(" *", g2.getFontMetrics().stringWidth(text), g2.getFontMetrics().getAscent());
                }
                g2.dispose();
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_BODY);
        return lbl;
    }

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
                        g2.setColor(new Color(EMERALD.getRed(), EMERALD.getGreen(), EMERALD.getBlue(), 12 * i));
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

    private JPanel buildReturnDateRow(final String dateStr) {
        JPanel row = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(EMERALD_LIGHT);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(new Color(EMERALD.getRed(), EMERALD.getGreen(), EMERALD.getBlue(), 80));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(11, 14, 11, 14));
        row.setPreferredSize(new Dimension(0, 44));

        // Calendar icon
        JLabel calIcon = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(EMERALD);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(1, 2, 16, 14, 3, 3);
                g2.drawLine(1, 6, 17, 6);
                g2.drawLine(5, 0, 5, 4);
                g2.drawLine(13, 0, 13, 4);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawLine(5, 9,  7,  9);
                g2.drawLine(9, 9,  11, 9);
                g2.drawLine(13, 9, 15, 9);
                g2.drawLine(5, 12, 7,  12);
                g2.drawLine(9, 12, 11, 12);
                g2.dispose();
            }
        };
        calIcon.setPreferredSize(new Dimension(20, 20));

        JLabel dateLbl = new JLabel(dateStr);
        dateLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dateLbl.setForeground(EMERALD);
        dateLbl.setBorder(new EmptyBorder(0, 8, 0, 0));

        JLabel badge = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(EMERALD.getRed(), EMERALD.getGreen(), EMERALD.getBlue(), 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(EMERALD);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                FontMetrics fm = g2.getFontMetrics();
                String s = "Today";
                g2.drawString(s, (getWidth() - fm.stringWidth(s)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(46, 22));

        row.add(calIcon, BorderLayout.WEST);
        row.add(dateLbl, BorderLayout.CENTER);
        row.add(badge,   BorderLayout.EAST);
        return row;
    }

    /** Outlined button (Check Fine — SKY accent) */
    private JButton buildOutlineBtn(final String label, final Color accent) {
        final Color tint = new Color(
            Math.min(255, 220 + accent.getRed()   / 10),
            Math.min(255, 220 + accent.getGreen() / 10),
            Math.min(255, 220 + accent.getBlue()  / 10));
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover(), p = getModel().isPressed();
                g2.setColor((h || p) ? tint : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (h||p) ? 200 : 120));
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.setColor(accent);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, (getWidth() - fm.stringWidth(label)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 48));
        return btn;
    }

    /** Gradient fill button (Confirm Return — Emerald → Violet) */
    private JButton buildGradientBtn(final String label, final Color c1, final Color c2) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover(), p = getModel().isPressed();
                Color a = p ? c1.darker() : (h ? EMERALD_TIP : c1);
                Color b = p ? c2          : (h ? VIOLET      : c2);
                GradientPaint gp = new GradientPaint(0, 0, a, getWidth(), getHeight(), b);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (!p) {
                    g2.setColor(new Color(255, 255, 255, 28));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 12, 12);
                }
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, (getWidth() - fm.stringWidth(label)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 48));
        return btn;
    }
}