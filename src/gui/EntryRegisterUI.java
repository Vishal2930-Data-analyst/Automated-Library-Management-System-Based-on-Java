package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.*;

import db.DBConnection;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import util.UserSession;
public class EntryRegisterUI {

    // ── Palette ──────────────────────────────────────────────────────
    private static final Color BG_PAGE      = new Color(244, 245, 251);
    private static final Color BG_CARD      = new Color(255, 255, 255);
    private static final Color INDIGO       = new Color( 79,  70, 229);
    private static final Color INDIGO_LIGHT = new Color(238, 237, 255);
    private static final Color INDIGO_MED   = new Color(165, 180, 252);
    private static final Color VIOLET       = new Color(124,  58, 237);
    private static final Color EMERALD      = new Color(  5, 150, 105);
    private static final Color EMERALD_LIGHT= new Color(209, 250, 229);
    private static final Color EMERALD_TIP  = new Color( 52, 211, 153);
    private static final Color ROSE         = new Color(225,  29,  72);
    private static final Color ROSE_LIGHT   = new Color(255, 228, 230);
    private static final Color AMBER        = new Color(217, 119,   6);
    private static final Color AMBER_LIGHT  = new Color(254, 243, 199);
    private static final Color SKY          = new Color(  2, 132, 199);
    private static final Color SKY_LIGHT    = new Color(224, 242, 254);
    private static final Color TEXT_H       = new Color( 17,  24,  39);
    private static final Color TEXT_BODY    = new Color( 55,  65,  81);
    private static final Color TEXT_MUTED   = new Color(107, 114, 128);
    private static final Color TEXT_SUBTLE  = new Color(156, 163, 175);
    private static final Color BORDER       = new Color(226, 232, 240);
    private static final Color ROW_STRIPE   = new Color(249, 249, 255);
    private static final Color ROW_SELECT   = new Color(224, 221, 255);

    private static final String SEARCH_HINT   = "  Search by member name...";
    private static final String FROM_HINT      = "  YYYY-MM-DD";
    private static final String TO_HINT        = "  YYYY-MM-DD";
    private static final String MEMBERID_HINT  = "  Member ID";

    // ── State ─────────────────────────────────────────────────────────
    DefaultTableModel model;
    int               page  = 0;
    int               limit = 10;

    JTextField searchField;
    JTextField fromDate;
    JTextField toDate;

    private JFrame  frame;
    private JLabel  pageLbl;
    private JButton prevBtn;
    private JButton nextBtn;
    private JTextField memberIdField;

    public EntryRegisterUI() {

        frame = new JFrame("LibraryPro \u2014 Entry Register");
        frame.setSize(1320, 760);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BG_PAGE);

        // ── Wrapper ──────────────────────────────────────────────────
        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(22, 26, 22, 26));
        frame.add(wrapper, BorderLayout.CENTER);

        // ════════════════════════════════════════════════════════════
        //  TOP BAR
        // ════════════════════════════════════════════════════════════
        JPanel topBar = buildCard(16);
        topBar.setLayout(new BorderLayout());
        topBar.setBorder(new EmptyBorder(16, 24, 16, 24));
        topBar.setPreferredSize(new Dimension(0, 72));

        // Left
        JPanel leftTop = new JPanel(null);
        leftTop.setOpaque(false);
        leftTop.setPreferredSize(new Dimension(380, 44));

        // Violet icon badge (entry register = pencil/log)
        JPanel iconBadge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, VIOLET, getWidth(), getHeight(), INDIGO);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Clipboard / register icon
                g2.drawRoundRect(3, 4, 14, 18, 3, 3);
                g2.drawLine(3, 9, 17, 9);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(5, 12, 15, 12);
                g2.drawLine(5, 15, 13, 15);
                // Pencil
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int[] px = {21, 29, 31, 23}; int[] py = {22, 14, 16, 24};
                g2.drawPolygon(px, py, 4);
                g2.drawLine(21, 22, 20, 26);
                g2.drawLine(23, 24, 20, 26);
                g2.dispose();
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setBounds(0, 4, 36, 36);

        JLabel titleLbl = new JLabel("Library Entry Register");
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 20));
        titleLbl.setForeground(TEXT_H);
        titleLbl.setBounds(46, 4, 320, 26);

        
        leftTop.add(iconBadge);
        leftTop.add(titleLbl);
        

        // Right — search + date filters + exports
        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightTop.setOpaque(false);

        searchField = buildHintField(SEARCH_HINT, 190);
        fromDate    = buildDateField(FROM_HINT, 116);
        toDate      = buildDateField(TO_HINT,   116);

        final JButton filterBtn  = buildFillBtn2("Filter",     INDIGO,  makeIconFilter());
        final JButton exportPDF  = buildOutlineBtn2("PDF",     ROSE,    makeIconPDF());
        final JButton exportCSV  = buildOutlineBtn2("CSV",     EMERALD, makeIconCSV());

        // Date range labels
        JLabel fromLbl = buildFieldLabel("From:");
        JLabel toLbl   = buildFieldLabel("To:");

        rightTop.add(searchField);
        rightTop.add(fromLbl);
        rightTop.add(fromDate);
        rightTop.add(toLbl);
        rightTop.add(toDate);
        rightTop.add(filterBtn);
        rightTop.add(exportPDF);
        rightTop.add(exportCSV);

        topBar.add(leftTop,  BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);
        wrapper.add(topBar, BorderLayout.NORTH);

        // ════════════════════════════════════════════════════════════
        //  ENTRY CONTROL CARD
        // ════════════════════════════════════════════════════════════
        JPanel entryCard = buildCard(14);
        entryCard.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 0));
        entryCard.setBorder(new EmptyBorder(12, 20, 12, 20));
        entryCard.setPreferredSize(new Dimension(0, 62));

        // Member ID field
        memberIdField = buildHintField(MEMBERID_HINT, 160);

        // Entry / Exit buttons
        final JButton entryBtn = buildFillBtn2("Mark Entry", EMERALD, makeIconEntry());
        final JButton exitBtn  = buildFillBtn2("Mark Exit",  ROSE,    makeIconExit());

        // Status label
        JLabel statusBadge = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                if (getText().isEmpty()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(EMERALD.getRed(), EMERALD.getGreen(), EMERALD.getBlue(), 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(EMERALD.getRed(), EMERALD.getGreen(), EMERALD.getBlue(), 80));
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(EMERALD);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        statusBadge.setPreferredSize(new Dimension(0, 36));
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusBadge.setForeground(EMERALD);

        // Divider between input and table
        JLabel sectionLbl = new JLabel("Record Entry");
        sectionLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sectionLbl.setForeground(VIOLET);

        entryCard.add(sectionLbl);
        entryCard.add(Box.createHorizontalStrut(10));
        entryCard.add(memberIdField);
        entryCard.add(entryBtn);
        entryCard.add(exitBtn);

        // ════════════════════════════════════════════════════════════
        //  TABLE CARD
        // ════════════════════════════════════════════════════════════
        JPanel tableCard = buildCard(16);
        tableCard.setLayout(new BorderLayout());

        model = new DefaultTableModel(
            new String[]{"ID","Member ID","Name","Course","Entry Time","Exit Time","Duration","Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(ROW_SELECT);
                    c.setForeground(INDIGO);
                } else if (row % 2 == 0) {
                    c.setBackground(BG_CARD);
                    c.setForeground(TEXT_BODY);
                } else {
                    c.setBackground(ROW_STRIPE);
                    c.setForeground(TEXT_BODY);
                }
                // Duration column — color "Inside Library" green, duration violet
                if (col == 5 && model.getValueAt(row, col) != null) {
                    String val = model.getValueAt(row, col).toString();
                    if ("Inside Library".equals(val)) {
                        c.setForeground(EMERALD);
                        if (c instanceof JLabel)
                            ((JLabel) c).setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else {
                        c.setForeground(VIOLET);
                    }
                }
                // Center all columns
                if (c instanceof JLabel) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                    ((JLabel) c).setBorder(new EmptyBorder(0, 8, 0, 8));
                }
                return c;
            }
        };

        table.setRowHeight(46);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 242, 248));
        table.setSelectionBackground(ROW_SELECT);
        table.setSelectionForeground(INDIGO);
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        // Custom header — violet theme (matches entry register / pencil badge)
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                final String text = (v != null) ? v.toString() : "";
                JLabel lbl = new JLabel(text) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        GradientPaint gp = new GradientPaint(0, 0, new Color(248, 245, 255),
                            0, getHeight(), new Color(237, 233, 254));
                        g2.setPaint(gp);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        g2.setColor(new Color(VIOLET.getRed(), VIOLET.getGreen(), VIOLET.getBlue(), 160));
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                        g2.setColor(new Color(VIOLET.getRed(), VIOLET.getGreen(), VIOLET.getBlue(), 60));
                        g2.setStroke(new BasicStroke(0.8f));
                        g2.drawLine(getWidth()-1, 4, getWidth()-1, getHeight()-4);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setForeground(VIOLET);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(new EmptyBorder(12, 8, 12, 8));
                lbl.setOpaque(false);
                return lbl;
            }
        });
        table.getTableHeader().setPreferredSize(new Dimension(0, 46));
        table.getTableHeader().setReorderingAllowed(false);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);
        table.getColumnModel().getColumn(4).setPreferredWidth(160);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.getVerticalScrollBar().setUI(buildSlimScrollBar());
        tableCard.add(scroll, BorderLayout.CENTER);

        // ════════════════════════════════════════════════════════════
        //  BOTTOM BAR
        // ════════════════════════════════════════════════════════════
        JPanel bottomBar = buildCard(14);
        bottomBar.setLayout(new BorderLayout());
        bottomBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        bottomBar.setPreferredSize(new Dimension(0, 62));

        JPanel pagination = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pagination.setOpaque(false);

        prevBtn = buildPagBtn("\u2190 Prev",  VIOLET);
        pageLbl = new JLabel("Page 1");
        pageLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pageLbl.setForeground(TEXT_MUTED);
        nextBtn = buildPagBtn("Next \u2192", VIOLET);

        pagination.add(prevBtn);
        pagination.add(pageLbl);
        pagination.add(nextBtn);

        // Left — live count indicator
        JPanel leftBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftBottom.setOpaque(false);

        JPanel todayBadge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(237, 233, 254));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(VIOLET.getRed(), VIOLET.getGreen(), VIOLET.getBlue(), 80));
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
            }
        };
        todayBadge.setOpaque(false);
        todayBadge.setPreferredSize(new Dimension(170, 34));
        todayBadge.setBorder(new EmptyBorder(0, 10, 0, 10));
        todayBadge.setLayout(new BorderLayout());

        JLabel todayLbl = new JLabel("Entry Register Log");
        todayLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        todayLbl.setForeground(VIOLET);
        todayBadge.add(todayLbl, BorderLayout.CENTER);

        leftBottom.add(todayBadge);

        bottomBar.add(leftBottom, BorderLayout.WEST);
        bottomBar.add(pagination, BorderLayout.EAST);

        // Assemble center stack
        JPanel topSection = new JPanel(new BorderLayout(0, 14));
        topSection.setOpaque(false);
        topSection.add(entryCard, BorderLayout.NORTH);
        topSection.add(tableCard, BorderLayout.CENTER);

        JPanel centerStack = new JPanel(new BorderLayout(0, 14));
        centerStack.setOpaque(false);
        centerStack.add(topSection, BorderLayout.CENTER);
        centerStack.add(bottomBar,  BorderLayout.SOUTH);

        wrapper.add(centerStack, BorderLayout.CENTER);

        // ── Initial load ──────────────────────────────────────────────
        loadEntries("", "", "", 0);
        updatePageLabel();

        // ════════════════════════════════════════════════════════════
        //  LISTENERS  (no lambdas)
        // ════════════════════════════════════════════════════════════

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { onSearchChange(); }
            public void removeUpdate(DocumentEvent e)  { onSearchChange(); }
            public void changedUpdate(DocumentEvent e) { onSearchChange(); }
            private void onSearchChange() {
                // Don't fire while hint text is still showing
                String t = searchField.getText();
                if (SEARCH_HINT.equals(t)) return;
                page = 0;
                loadEntries(t.trim(), getFrom(), getTo(), page);
                updatePageLabel();
            }
        });

        filterBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                page = 0;
                loadEntries(getSearch(), getFrom(), getTo(), page);
                updatePageLabel();
            }
        });

        entryBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                	Connection con = DBConnection.getConnection();

                	// Step 1: Get name + course
                	PreparedStatement psMember = con.prepareStatement(
                	    "SELECT name, course FROM members WHERE id=? AND admin_id=?"
                	);

                	psMember.setInt(1, Integer.parseInt(getMemberId()));
                	psMember.setInt(2, UserSession.adminId);

                	ResultSet rs = psMember.executeQuery();

                	if (rs.next()) {
                	    String name = rs.getString("name");
                	    String course = rs.getString("course");   // ✅ GET COURSE

                	    // Step 2: Insert correctly
                	    PreparedStatement ps = con.prepareStatement(
                	        "INSERT INTO library_entry(member_id, member_name, course, entry_time, date, admin_id) " +
                	        "VALUES(?, ?, ?, NOW(), CURDATE(), ?)"
                	    );

                	    ps.setInt(1, Integer.parseInt(getMemberId()));
                	    ps.setString(2, name);
                	    ps.setString(3, course);                  // ✅ FIXED
                	    ps.setInt(4, UserSession.adminId);        // ✅ shifted

                	    ps.executeUpdate();

                	    JOptionPane.showMessageDialog(frame, "Entry Recorded");
                	
                        loadEntries(getSearch(), getFrom(), getTo(), page);
                        updatePageLabel();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Member not found");
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        exitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement(
                        "UPDATE library_entry \r\n"
                        + "SET exit_time=NOW() \r\n"
                        + "WHERE member_id=? AND exit_time IS NULL AND admin_id=?");
                    ps.setInt(1, Integer.parseInt(getMemberId()));
                    ps.setInt(2, UserSession.adminId);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(frame, "Exit Recorded");
                    loadEntries(getSearch(), getFrom(), getTo(), page);
                    updatePageLabel();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        exportPDF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream("Entry_Register_Report.pdf"));
                    document.open();
                    document.add(new Paragraph("Library Entry Register\n\n"));
                    for (int i = 0; i < model.getRowCount(); i++) {
                        document.add(new Paragraph(
                            model.getValueAt(i,1) + " | " + model.getValueAt(i,2) + " | " +
                            model.getValueAt(i,3) + " | " + model.getValueAt(i,4) + " | " + model.getValueAt(i,5)));
                    }
                    document.close();
                    showToast("PDF exported as Entry_Register_Report.pdf", true);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        exportCSV.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    FileWriter writer = new FileWriter("Entry_Register_Report.csv");
                    writer.append("Member ID,Name,Course,Entry,Exit,Duration,Date\n");
                    for (int i = 0; i < model.getRowCount(); i++) {
                        writer.append(model.getValueAt(i,1)+","+model.getValueAt(i,2)+","+
                                      model.getValueAt(i,3)+","+model.getValueAt(i,4)+","+
                                      model.getValueAt(i,5)+","+model.getValueAt(i,6)+"\n");
                    }
                    writer.flush();
                    writer.close();
                    showToast("CSV exported as Entry_Register_Report.csv", true);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        prevBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (page > 0) {
                    page--;
                    loadEntries(getSearch(), getFrom(), getTo(), page);
                    updatePageLabel();
                }
            }
        });

        nextBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                page++;
                loadEntries(getSearch(), getFrom(), getTo(), page);
                updatePageLabel();
            }
        });

        frame.setVisible(true);
    }

    // ── Helpers for reading fields ────────────────────────────────────
    private String getSearch() {
        String t = searchField.getText();
        if (t == null || t.trim().isEmpty() || t.equals(SEARCH_HINT)) return "";
        return t.trim();
    }
    private String getFrom() {
        String t = fromDate.getText();
        if (t == null || t.trim().isEmpty() || t.equals(FROM_HINT)) return "";
        return t.trim();
    }
    private String getTo() {
        String t = toDate.getText();
        if (t == null || t.trim().isEmpty() || t.equals(TO_HINT)) return "";
        return t.trim();
    }
    private String getMemberId() {
        String t = memberIdField.getText();
        if (t == null || t.trim().isEmpty() || t.equals(MEMBERID_HINT)) return "";
        return t.trim();
    }
    private void updatePageLabel() {
        pageLbl.setText("Page " + (page + 1));
        prevBtn.setEnabled(page > 0);
    }

    // ════════════════════════════════════════════════════════════════
    //  LOAD ENTRIES  (unchanged logic)
    // ════════════════════════════════════════════════════════════════
    private void loadEntries(String keyword, String from, String to, int pg) {
        try {
            model.setRowCount(0);
            Connection con = DBConnection.getConnection();
            PreparedStatement ps;

            // Sanitize — strip hints and whitespace
            String kw  = (keyword == null || keyword.trim().isEmpty()
                          || keyword.equals(SEARCH_HINT)) ? "" : keyword.trim();
            String fr  = (from    == null || from.trim().isEmpty()
                          || from.equals(FROM_HINT))      ? "" : from.trim();
            String toe = (to      == null || to.trim().isEmpty()
                          || to.equals(TO_HINT))          ? "" : to.trim();

            if (kw.isEmpty() && fr.isEmpty() && toe.isEmpty()) {
            	ps = con.prepareStatement(
            		    "SELECT *, TIMESTAMPDIFF(MINUTE, entry_time, exit_time) AS duration " +
            		    "FROM library_entry WHERE admin_id=? ORDER BY id DESC LIMIT ? OFFSET ?"
            		);

            		ps.setInt(1, UserSession.adminId);
            		ps.setInt(2, limit);
            		ps.setInt(3, pg * limit);
            } else {
            	String sql = "SELECT *, TIMESTAMPDIFF(MINUTE, entry_time, exit_time) AS duration " +
                        "FROM library_entry WHERE admin_id=? ";

           if (!kw.isEmpty())  sql += "AND (member_name LIKE ? OR course LIKE ?) ";
           if (!fr.isEmpty())  sql += "AND date >= ? ";
           if (!toe.isEmpty()) sql += "AND date <= ? ";

           sql += "ORDER BY id DESC LIMIT ? OFFSET ?";

           ps = con.prepareStatement(sql);

           int idx = 1;
           ps.setInt(idx++, UserSession.adminId);

           if (!kw.isEmpty())  ps.setString(idx++, "%" + kw + "%");
           if (!kw.isEmpty())  ps.setString(idx++, "%" + kw + "%");
           if (!fr.isEmpty())  ps.setString(idx++, fr);
           if (!toe.isEmpty()) ps.setString(idx++, toe);

           ps.setInt(idx++, limit);
           ps.setInt(idx, pg * limit);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String duration;
                if (rs.getString("exit_time") == null) {
                    duration = "Inside Library";
                } else {
                    int minutes = rs.getInt("duration");
                    int hours = minutes / 60;
                    int mins  = minutes % 60;
                    duration = hours + "h " + mins + "m";
                }
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getInt("member_id"),
                    rs.getString("member_name"),
                    rs.getString("course"),
                    rs.getString("entry_time"),
                    rs.getString("exit_time"),
                    duration,
                    rs.getString("date")
                });
            }
            rs.close(); ps.close(); con.close();

        } catch (Exception e) { e.printStackTrace(); }
    }

    // ════════════════════════════════════════════════════════════════
    //  UI BUILDERS
    // ════════════════════════════════════════════════════════════════

    private JPanel buildCard(final int radius) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int i = 4; i > 0; i--) {
                    g2.setColor(new Color(0,0,0,4*i));
                    g2.fillRoundRect(i,i,getWidth()-i,getHeight()-i,radius,radius);
                }
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0,0,getWidth()-4,getHeight()-4,radius,radius);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0,0,getWidth()-5,getHeight()-5,radius,radius);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    /** Plain search/hint field */
    private JTextField buildHintField(final String hint, int width) {
        JTextField f = new JTextField(hint) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean foc = hasFocus();
                g2.setColor(foc ? Color.WHITE : new Color(249,249,255));
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(foc ? VIOLET : BORDER);
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                if (getText().equals(hint) || getText().isEmpty()) {
                    g2.setColor(TEXT_SUBTLE);
                    g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawOval(8,10,11,11); g2.drawLine(16,18,20,22);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(8, 28, 8, 10));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setForeground(TEXT_SUBTLE);
        f.setCaretColor(VIOLET);
        f.setPreferredSize(new Dimension(width, 36));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if(f.getText().equals(hint)){f.setText("");f.setForeground(TEXT_H);} }
            public void focusLost(FocusEvent e)   { if(f.getText().isEmpty()){f.setText(hint);f.setForeground(TEXT_SUBTLE);} }
        });
        return f;
    }

    /** Date field — calendar-icon style */
    private JTextField buildDateField(final String hint, int width) {
        JTextField f = new JTextField(hint) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean foc = hasFocus();
                g2.setColor(foc ? Color.WHITE : new Color(249,249,255));
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(foc ? VIOLET : BORDER);
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                // Calendar icon
                g2.setColor(TEXT_SUBTLE);
                g2.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(7,9,12,10,2,2); g2.drawLine(7,13,19,13);
                g2.drawLine(11,7,11,11); g2.drawLine(15,7,15,11);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(8, 26, 8, 8));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setForeground(TEXT_SUBTLE);
        f.setCaretColor(VIOLET);
        f.setPreferredSize(new Dimension(width, 36));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if(f.getText().equals(hint)){f.setText("");f.setForeground(TEXT_H);} }
            public void focusLost(FocusEvent e)   { if(f.getText().isEmpty()){f.setText(hint);f.setForeground(TEXT_SUBTLE);} }
        });
        return f;
    }

    private JLabel buildFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    /** Gradient fill button */
    private JButton buildFillBtn2(final String label, final Color accent, final Image icon) {
        final Color lighter = new Color(
            Math.min(255, accent.getRed()+50),
            Math.min(255, accent.getGreen()+50),
            Math.min(255, accent.getBlue()+50));
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover(), p = getModel().isPressed();
                Color c1 = p ? accent.darker() : (h ? lighter : accent);
                Color c2 = p ? accent           : (h ? accent  : lighter);
                GradientPaint gp = new GradientPaint(0,0,c1,getWidth(),getHeight(),c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                if (!p){ g2.setColor(new Color(255,255,255,28)); g2.fillRoundRect(0,0,getWidth(),getHeight()/2,10,10); }
                if (icon!=null) g2.drawImage(recolor(icon,Color.WHITE),10,(getHeight()-16)/2,16,16,null);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, 32, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(icon!=null ? label.length()*8+40 : label.length()*8+20, 36));
        return btn;
    }

    /** Outlined export button */
    private JButton buildOutlineBtn2(final String label, final Color accent, final Image icon) {
        final Color tint = new Color(
            Math.min(255, 220+accent.getRed()/10),
            Math.min(255, 220+accent.getGreen()/10),
            Math.min(255, 220+accent.getBlue()/10));
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover(), p = getModel().isPressed();
                g2.setColor((h||p) ? tint : BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),(h||p)?200:120));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                if (icon!=null) g2.drawImage(recolor(icon,accent),10,(getHeight()-16)/2,16,16,null);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(accent);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, 32, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(80, 36));
        return btn;
    }

    private JButton buildPagBtn(final String label, final Color accent) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover();
                boolean dis = !isEnabled();
                Color light = new Color(237,233,254);
                g2.setColor(dis ? new Color(245,245,250) : (h ? light : BG_CARD));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(dis ? new Color(210,210,220) : (h ? accent : BORDER));
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(dis ? TEXT_SUBTLE : (h ? accent : TEXT_BODY));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label,(getWidth()-fm.stringWidth(label))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 36));
        return btn;
    }

    private javax.swing.plaf.basic.BasicScrollBarUI buildSlimScrollBar() {
        return new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { thumbColor = new Color(VIOLET.getRed(),VIOLET.getGreen(),VIOLET.getBlue(),140); trackColor = BG_PAGE; }
            @Override protected JButton createDecreaseButton(int o) { return zero(); }
            @Override protected JButton createIncreaseButton(int o) { return zero(); }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(VIOLET.getRed(),VIOLET.getGreen(),VIOLET.getBlue(),160));
                g2.fillRoundRect(r.x+2,r.y+2,r.width-4,r.height-4,6,6);
                g2.dispose();
            }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) { g.setColor(BG_PAGE); g.fillRect(r.x,r.y,r.width,r.height); }
            private JButton zero() { JButton b=new JButton(); b.setPreferredSize(new Dimension(0,0)); b.setMinimumSize(new Dimension(0,0)); b.setMaximumSize(new Dimension(0,0)); return b; }
        };
    }

    private void showToast(final String msg, final boolean success) {
        final JWindow toast = new JWindow(frame);
        final Color bg = success ? EMERALD : ROSE;
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.dispose();
            }
        };
        p.setOpaque(false); p.setBorder(new EmptyBorder(10,18,10,18));
        JLabel l = new JLabel((success?"\u2713  ":"\u2715  ")+msg);
        l.setFont(new Font("Segoe UI",Font.BOLD,12)); l.setForeground(Color.WHITE);
        p.add(l); toast.add(p); toast.pack();
        toast.setLocation(frame.getX()+(frame.getWidth()-toast.getWidth())/2, frame.getY()+frame.getHeight()-90);
        toast.setVisible(true);
        Timer t = new Timer(2400, new ActionListener() { public void actionPerformed(ActionEvent e){toast.dispose();} });
        t.setRepeats(false); t.start();
    }

    // ════════════════════════════════════════════════════════════════
    //  ICONS
    // ════════════════════════════════════════════════════════════════
    private Image makeIconFilter() {
        BufferedImage img = new BufferedImage(20,20,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.8f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        int[] fx={2,10,18}; int[] fy={4,4,4}; g.drawLine(2,4,18,4);
        g.drawLine(5,9,15,9); g.drawLine(8,14,12,14);
        g.dispose(); return img;
    }
    private Image makeIconPDF() {
        BufferedImage img = new BufferedImage(20,20,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.7f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawRoundRect(2,1,12,17,3,3); g.drawLine(10,1,14,5); g.drawLine(14,5,14,9);
        g.setStroke(new BasicStroke(1.3f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawLine(4,8,10,8); g.drawLine(4,11,10,11); g.drawLine(4,14,8,14);
        g.setStroke(new BasicStroke(1.6f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawLine(16,10,16,17); int[]ax={13,16,19}; int[]ay={14,17,14}; g.fillPolygon(ax,ay,3);
        g.dispose(); return img;
    }
    private Image makeIconCSV() {
        BufferedImage img = new BufferedImage(20,20,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.7f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawRoundRect(2,1,12,17,3,3); g.drawLine(10,1,14,5); g.drawLine(14,5,14,9);
        g.setStroke(new BasicStroke(1.3f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawLine(4,8,10,8); g.drawLine(4,11,10,11); g.drawLine(4,14,8,14);
        g.setStroke(new BasicStroke(1.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawRect(13,10,5,7); g.drawLine(16,10,16,17); g.drawLine(13,13,18,13);
        g.dispose(); return img;
    }
    private Image makeIconEntry() {
        BufferedImage img = new BufferedImage(20,20,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.8f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawRoundRect(8,3,10,14,3,3);
        g.drawLine(1,10,8,10); int[]x={4,1,4}; int[]y={7,10,13}; g.fillPolygon(x,y,3);
        g.dispose(); return img;
    }
    private Image makeIconExit() {
        BufferedImage img = new BufferedImage(20,20,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.8f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawRoundRect(2,3,10,14,3,3);
        g.drawLine(12,10,19,10); int[]x={16,19,16}; int[]y={7,10,13}; g.fillPolygon(x,y,3);
        g.dispose(); return img;
    }

    private Image recolor(Image src, Color color) {
        int w=src.getWidth(null),h=src.getHeight(null);
        if(w<=0||h<=0) return src;
        BufferedImage out=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=out.createGraphics(); g.drawImage(src,0,0,null); g.dispose();
        int r=color.getRed(),gn=color.getGreen(),b=color.getBlue();
        for(int x=0;x<w;x++) for(int y=0;y<h;y++){
            int a=(out.getRGB(x,y)>>24)&0xFF;
            if(a>10) out.setRGB(x,y,(a<<24)|(r<<16)|(gn<<8)|b);
        }
        return out;
    }
}