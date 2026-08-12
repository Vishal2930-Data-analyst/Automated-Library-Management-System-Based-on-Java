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
import util.UserSession;
public class IssueBookUI extends JFrame {

    // ── Palette ──────────────────────────────────────────────────────
    private static final Color BG_PAGE      = new Color(244, 245, 251);
    private static final Color BG_CARD      = new Color(255, 255, 255);
    private static final Color INDIGO       = new Color( 79,  70, 229);
    private static final Color INDIGO_LIGHT = new Color(238, 237, 255);
    private static final Color INDIGO_MED   = new Color(165, 180, 252);
    private static final Color VIOLET       = new Color(124,  58, 237);
    private static final Color SKY          = new Color(  2, 132, 199);
    private static final Color SKY_LIGHT    = new Color(224, 242, 254);
    private static final Color TEXT_H       = new Color( 17,  24,  39);
    private static final Color TEXT_BODY    = new Color( 55,  65,  81);
    private static final Color TEXT_MUTED   = new Color(107, 114, 128);
    private static final Color TEXT_SUBTLE  = new Color(156, 163, 175);
    private static final Color BORDER       = new Color(226, 232, 240);
    private static final Color FIELD_BG     = new Color(249, 250, 255);

    // ── Fields ───────────────────────────────────────────────────────
    private JTextField bookIdField;
    private JTextField memberIdField;

    public IssueBookUI() {

        setTitle("LibraryPro \u2014 Issue Book");
        setSize(520, 580);
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
                // Drop shadow
                for (int i = 5; i > 0; i--) {
                    g2.setColor(new Color(17, 24, 39, 5 * i));
                    g2.fillRoundRect(i, i, getWidth() - i, getHeight() - i, 20, 20);
                }
                // White body
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
                // Sky-blue top accent bar (issue = sky color)
                GradientPaint gp = new GradientPaint(0, 0, SKY,
                    (getWidth() - 5) * 2 / 3, 0,
                    new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(), 0));
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
        card.setPreferredSize(new Dimension(450, 500));
        card.setBorder(new EmptyBorder(30, 36, 28, 36));

        // ── Header ───────────────────────────────────────────────────
        JPanel header = new JPanel(null);
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 72));

        // Sky-blue icon badge
        JPanel iconBadge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, SKY, getWidth(), getHeight(),
                    new Color(79, 70, 229));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Arrow-out icon (issue = send book out)
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Book
                g2.drawRoundRect(5, 6, 16, 22, 4, 4);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(8, 12, 17, 12);
                g2.drawLine(8, 16, 17, 16);
                // Arrow out
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(26, 14, 34, 14);
                int[] ax = {31, 34, 31}; int[] ay = {10, 14, 18};
                g2.fillPolygon(ax, ay, 3);
                g2.dispose();
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setBounds(0, 10, 44, 44);

        JLabel titleLbl = new JLabel("Issue New Book");
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 21));
        titleLbl.setForeground(TEXT_H);
        titleLbl.setBounds(56, 10, 310, 28);

        JLabel subLbl = new JLabel("Assign a library book to a registered member");
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
                GradientPaint gp = new GradientPaint(0, 0, new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(), 180),
                    getWidth() * 2 / 3, 0,
                    new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(), 0));
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
        form.setBorder(new EmptyBorder(16, 0, 16, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx   = 0;
        gbc.gridy   = 0;
        gbc.gridwidth = 2;

        // Book ID + Member ID side by side
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

        // Row 0: labels
        form.add(makeLabel("Book ID", true, SKY),     left);
        form.add(makeLabel("Member ID", true, SKY),   right);

        // Row 1: fields
        left.gridy  = 1; left.insets  = new Insets(0, 0, 16, 8);
        right.gridy = 1; right.insets = new Insets(0, 0, 16, 0);

        bookIdField   = buildField("e.g.  101");
        memberIdField = buildField("e.g.  1001");

        form.add(bookIdField,   left);
        form.add(memberIdField, right);

        // Issue Date — full width
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 4, 0);
        form.add(makeLabel("Issue Date", false, SKY), gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 4, 0);
        LocalDate today = LocalDate.now();
        JPanel dateRow = buildDateRow(today.toString());
        form.add(dateRow, gbc);

        // Due Date info row
        gbc.gridy = 4; gbc.insets = new Insets(6, 0, 4, 0);
        form.add(buildDueInfo(today.plusDays(7).toString()), gbc);

        card.add(form, BorderLayout.CENTER);

        // ── Button panel ─────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new BorderLayout(0, 10));
        btnPanel.setOpaque(false);

        // Two-button row
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 14, 0));
        btnRow.setOpaque(false);

        // Issue button — Sky gradient
        JButton issueBtn = new JButton("Issue Book") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover();
                boolean p = getModel().isPressed();
                Color c1 = p ? SKY.darker()         : (h ? new Color(7, 100, 160) : SKY);
                Color c2 = p ? new Color(79, 70, 229): (h ? INDIGO                : new Color(79, 70, 229));
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (!p) {
                    g2.setColor(new Color(255, 255, 255, 28));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 12, 12);
                }
                // Arrow icon
                g2.setColor(new Color(255, 255, 255, 160));
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int ix = 18, iy = getHeight() / 2 - 7;
                g2.drawRoundRect(ix, iy - 2, 10, 14, 3, 3);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(ix + 2, iy + 3, ix + 8, iy + 3);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(ix + 14, iy + 5, ix + 20, iy + 5);
                int[] ax = {ix + 17, ix + 20, ix + 17}; int[] ay = {iy + 2, iy + 5, iy + 8};
                g2.fillPolygon(ax, ay, 3);
                // Label
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "Issue Book";
                g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2 + 8,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        issueBtn.setContentAreaFilled(false);
        issueBtn.setBorderPainted(false);
        issueBtn.setFocusPainted(false);
        issueBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        issueBtn.setPreferredSize(new Dimension(0, 48));

        // Cancel button — outlined
        JButton cancelBtn = new JButton("Cancel") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover();
                g2.setColor(h ? new Color(241, 245, 249) : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(h ? new Color(100, 116, 139) : BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(h ? TEXT_H : TEXT_MUTED);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "Cancel";
                g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        cancelBtn.setContentAreaFilled(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.setPreferredSize(new Dimension(0, 48));

        // Clear link
        JLabel clearLbl = new JLabel("Clear all fields", SwingConstants.CENTER);
        clearLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        clearLbl.setForeground(TEXT_MUTED);
        clearLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearLbl.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { clearLbl.setForeground(SKY); }
            public void mouseExited(MouseEvent e)  { clearLbl.setForeground(TEXT_MUTED); }
            public void mouseClicked(MouseEvent e) {
                bookIdField.setText("");
                memberIdField.setText("");
                bookIdField.requestFocus();
            }
        });

        btnRow.add(issueBtn);
        btnRow.add(cancelBtn);
        btnPanel.add(btnRow,    BorderLayout.CENTER);
        btnPanel.add(clearLbl,  BorderLayout.SOUTH);
        card.add(btnPanel, BorderLayout.SOUTH);

        page.add(card);
        setVisible(true);

        // ── Listeners (unchanged logic) ──────────────────────────────
        issueBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { issueBook(); }
        });

        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { dispose(); }
        });
    }

    // ── Issue book logic (completely unchanged) ───────────────────────
    private void issueBook() {
        try {
            Connection con = DBConnection.getConnection();

            int bookId   = Integer.parseInt(bookIdField.getText().trim());
            int memberId = Integer.parseInt(memberIdField.getText().trim());

            PreparedStatement bookQuery = con.prepareStatement(
            	    "SELECT title FROM books WHERE id=? AND admin_id=?"
            	);

            	bookQuery.setInt(1, bookId);
            	bookQuery.setInt(2, UserSession.adminId);
            ResultSet bookRs = bookQuery.executeQuery();
            if (!bookRs.next()) {
                JOptionPane.showMessageDialog(this, "Book not found");
                return;
            }
            String bookTitle = bookRs.getString("title");
         
            PreparedStatement checkQty = con.prepareStatement(
                "SELECT quantity FROM books WHERE id=? AND admin_id=?"
            );

            checkQty.setInt(1, bookId);
            checkQty.setInt(2, UserSession.adminId);

            ResultSet qtyRs = checkQty.executeQuery();

            if(qtyRs.next()){
                int qty = qtyRs.getInt("quantity");

                if(qty <= 0){
                    JOptionPane.showMessageDialog(this, "Book out of stock!");
                    return;   
                }
            }

            PreparedStatement memberQuery = con.prepareStatement(
            	    "SELECT name,email FROM members WHERE id=? AND admin_id=?"
            	);

            	memberQuery.setInt(1, memberId);
            	memberQuery.setInt(2, UserSession.adminId);
            ResultSet memberRs = memberQuery.executeQuery();
            if (!memberRs.next()) {
                JOptionPane.showMessageDialog(this, "Member not found");
                return;
            }
            String memberName  = memberRs.getString("name");
            String memberEmail = memberRs.getString("email");

            LocalDate issueDate = LocalDate.now();
            LocalDate dueDate   = issueDate.plusDays(7);

            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO transactions \r\n"
                + "(book_id,book_title,member_id,member_name,issue_date,due_date,status,admin_id)\r\n"
                + "VALUES (?,?,?,?,?,?,?,?)");
            pst.setInt(1, bookId);
            pst.setString(2, bookTitle);
            pst.setInt(3, memberId);
            pst.setString(4, memberName);
            pst.setDate(5, Date.valueOf(issueDate));
            pst.setDate(6, Date.valueOf(dueDate));
            pst.setString(7, "Issued");
            pst.setInt(8, UserSession.adminId);
            pst.executeUpdate();
         
            PreparedStatement updateBook = con.prepareStatement(
                "UPDATE books SET quantity = quantity - 1 WHERE id=? AND admin_id=?"
            );

            updateBook.setInt(1, bookId);
            updateBook.setInt(2, UserSession.adminId);
            updateBook.executeUpdate();

            try {
                String subject = "Book Issued - LibraryPro";
                String message = "Hello " + memberName + ",\n\n"
                    + "You have successfully issued a book from the library.\n\n"
                    + "Book Title: " + bookTitle + "\n"
                    + "Issue Date: " + issueDate + "\n"
                    + "Due Date: "   + dueDate   + "\n\n"
                    + "Please return the book before the due date.\n\n"
                    + "Thank you,\nLibraryPro Team";
                EmailSender.sendEmail(memberEmail, subject, message);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(this, "Book Issued Successfully!");
            bookIdField.setText("");
            memberIdField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric IDs", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while issuing the book", "Error", JOptionPane.ERROR_MESSAGE);
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
                        g2.setColor(new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(), 12 * i));
                        g2.setStroke(new BasicStroke(i * 1.2f));
                        g2.drawRoundRect(i, i, getWidth() - 1 - i*2, getHeight() - 1 - i*2, 10, 10);
                    }
                    g2.setColor(SKY);
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
        f.setCaretColor(SKY);
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

    /** Read-only date display row */
    private JPanel buildDateRow(final String dateStr) {
        JPanel row = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SKY_LIGHT);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(), 80));
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
                g2.setColor(SKY);
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
        dateLbl.setForeground(SKY);
        dateLbl.setBorder(new EmptyBorder(0, 8, 0, 0));

        JLabel todayBadge = new JLabel("Today") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(), 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(SKY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("Today", (getWidth() - fm.stringWidth("Today")) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        todayBadge.setPreferredSize(new Dimension(46, 22));

        row.add(calIcon,   BorderLayout.WEST);
        row.add(dateLbl,   BorderLayout.CENTER);
        row.add(todayBadge,BorderLayout.EAST);
        return row;
    }

    /** Due date info banner */
    private JPanel buildDueInfo(final String dueStr) {
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(238, 237, 255));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setColor(INDIGO_MED);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(8, 12, 8, 12));
        banner.setPreferredSize(new Dimension(0, 40));

        JLabel infoIcon = new JLabel("\u2139");
        infoIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoIcon.setForeground(INDIGO);

        JLabel infoLbl = new JLabel("Due date: " + dueStr + "  \u2022  Return within 7 days");
        infoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLbl.setForeground(INDIGO);

        banner.add(infoIcon);
        banner.add(infoLbl);
        return banner;
    }
}