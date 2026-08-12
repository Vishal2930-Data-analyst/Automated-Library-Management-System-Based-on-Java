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
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class IssuedBooksUI {

    // ── Palette ──────────────────────────────────────────────────────
    private static final Color BG_PAGE      = new Color(244, 245, 251);
    private static final Color BG_CARD      = new Color(255, 255, 255);
    private static final Color INDIGO       = new Color( 79,  70, 229);
    private static final Color INDIGO_LIGHT = new Color(238, 237, 255);
    private static final Color INDIGO_MED   = new Color(165, 180, 252);
    private static final Color VIOLET       = new Color(124,  58, 237);
    private static final Color EMERALD      = new Color(  5, 150, 105);
    private static final Color ROSE         = new Color(225,  29,  72);
    private static final Color AMBER        = new Color(217, 119,   6);
    private static final Color SKY          = new Color(  2, 132, 199);
    private static final Color SKY_LIGHT    = new Color(224, 242, 254);
    private static final Color TEXT_H       = new Color( 17,  24,  39);
    private static final Color TEXT_BODY    = new Color( 55,  65,  81);
    private static final Color TEXT_MUTED   = new Color(107, 114, 128);
    private static final Color TEXT_SUBTLE  = new Color(156, 163, 175);
    private static final Color BORDER       = new Color(226, 232, 240);
    private static final Color ROW_STRIPE   = new Color(249, 249, 255);
    private static final Color ROW_SELECT   = new Color(224, 221, 255);

    private static final String SEARCH_HINT = "  Search by member name...";

    // ── State ─────────────────────────────────────────────────────────
    private DefaultTableModel model;
    private JTable            table;
    private JTextField        searchField;
    private JLabel            pageLbl;
    private JButton           prevBtn;
    private JButton           nextBtn;
    private int               currentPage = 0;
    private static final int  PAGE_SIZE   = 10;

    // Keep frame reference for export dialogs
    private JFrame frame;

    public IssuedBooksUI() {

        frame = new JFrame("LibraryPro \u2014 Issued Books Records");
        frame.setSize(1300, 760);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BG_PAGE);

        // ── Wrapper ──────────────────────────────────────────────────
        JPanel wrapper = new JPanel(new BorderLayout(0, 18));
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
        leftTop.setPreferredSize(new Dimension(460, 44));

        // Sky-blue icon badge (Issued = sky/outgoing)
        JPanel iconBadge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, SKY, getWidth(), getHeight(), INDIGO);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Clipboard with arrow-out
                g2.drawRoundRect(3, 4, 14, 18, 3, 3);
                g2.drawLine(3, 9, 17, 9);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(5, 12, 11, 12);
                g2.drawLine(5, 15, 9,  15);
                // Arrow out right side
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(21, 14, 32, 14);
                int[] ax = {28, 32, 28}; int[] ay = {10, 14, 18};
                g2.fillPolygon(ax, ay, 3);
                g2.dispose();
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setBounds(0, 4, 36, 36);

        JLabel titleLbl = new JLabel("Issued Books Records");
        titleLbl.setFont(new Font("Georgia", Font.BOLD, 20));
        titleLbl.setForeground(TEXT_H);
        titleLbl.setBounds(46, 4, 340, 26);

        JLabel subLbl = new JLabel("");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLbl.setForeground(TEXT_MUTED);
        subLbl.setBounds(47, 30, 380, 14);

        leftTop.add(iconBadge);
        leftTop.add(titleLbl);
        leftTop.add(subLbl);

        // Right
        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTop.setOpaque(false);

        searchField = buildSearchField();
        final JButton pdfBtn   = buildOutlineBtn("Export PDF",   ROSE,    makeIconPDF());
        final JButton excelBtn = buildOutlineBtn("Export Excel", AMBER,   makeIconExcel());
        final JButton csvBtn   = buildOutlineBtn("Export CSV",   EMERALD, makeIconCSV());

        rightTop.add(searchField);
        rightTop.add(pdfBtn);
        rightTop.add(excelBtn);
        rightTop.add(csvBtn);

        topBar.add(leftTop,  BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);
        wrapper.add(topBar, BorderLayout.NORTH);

        // ════════════════════════════════════════════════════════════
        //  TABLE CARD
        // ════════════════════════════════════════════════════════════
        JPanel tableCard = buildCard(16);
        tableCard.setLayout(new BorderLayout());

        // Model (unchanged columns)
        model = new DefaultTableModel(
            new String[]{"Issue ID","Book ID","Book Title","Member ID","Member Name","Issue Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Table
        table = new JTable(model) {
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
                // Issue ID column — bold indigo
                if (col == 0) {
                    c.setFont(new Font("Georgia", Font.BOLD, 13));
                    c.setForeground(isRowSelected(row) ? INDIGO : INDIGO);
                }
                // Center-align all
                if (c instanceof JLabel) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                    ((JLabel) c).setBorder(new EmptyBorder(0, 8, 0, 8));
                }
                return c;
            }
        };

        table.setRowHeight(48);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 242, 248));
        table.setSelectionBackground(ROW_SELECT);
        table.setSelectionForeground(INDIGO);
        table.setFocusable(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        // Custom header — sky-blue theme (matches issued = sky accent)
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                final String text = (v != null) ? v.toString() : "";
                JLabel lbl = new JLabel(text) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        GradientPaint gp = new GradientPaint(0, 0, new Color(240, 248, 255),
                            0, getHeight(), new Color(224, 242, 254));
                        g2.setPaint(gp);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        // Bottom border
                        g2.setColor(new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(), 160));
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                        // Vertical divider
                        g2.setColor(new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(), 60));
                        g2.setStroke(new BasicStroke(0.8f));
                        g2.drawLine(getWidth()-1, 4, getWidth()-1, getHeight()-4);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lbl.setForeground(SKY);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(new EmptyBorder(12, 8, 12, 8));
                lbl.setOpaque(false);
                return lbl;
            }
        });
        table.getTableHeader().setPreferredSize(new Dimension(0, 46));
        table.getTableHeader().setReorderingAllowed(false);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(75);
        table.getColumnModel().getColumn(2).setPreferredWidth(340);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(220);
        table.getColumnModel().getColumn(5).setPreferredWidth(130);

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

        // Left — record count badge
        JPanel leftBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftBottom.setOpaque(false);

        JPanel countBadge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SKY_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(), 80));
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
            }
        };
        countBadge.setOpaque(false);
        countBadge.setPreferredSize(new Dimension(160, 34));
        countBadge.setBorder(new EmptyBorder(0, 10, 0, 10));

        JLabel countLbl = new JLabel("Showing issued books");
        countLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        countLbl.setForeground(SKY);
        countBadge.setLayout(new BorderLayout());
        countBadge.add(countLbl, BorderLayout.CENTER);

        leftBottom.add(countBadge);

        // Right — pagination
        JPanel pagination = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pagination.setOpaque(false);

        prevBtn = buildPagBtn("\u2190 Prev");
        pageLbl = new JLabel("Page 1");
        pageLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pageLbl.setForeground(TEXT_MUTED);
        nextBtn = buildPagBtn("Next \u2192");

        pagination.add(prevBtn);
        pagination.add(pageLbl);
        pagination.add(nextBtn);

        bottomBar.add(leftBottom, BorderLayout.WEST);
        bottomBar.add(pagination, BorderLayout.EAST);

        JPanel centerStack = new JPanel(new BorderLayout(0, 14));
        centerStack.setOpaque(false);
        centerStack.add(tableCard, BorderLayout.CENTER);
        centerStack.add(bottomBar, BorderLayout.SOUTH);
        wrapper.add(centerStack, BorderLayout.CENTER);

        // ── Initial load ──────────────────────────────────────────────
        goToPage(0);

        // ════════════════════════════════════════════════════════════
        //  LISTENERS  (no lambdas)
        // ════════════════════════════════════════════════════════════

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { goToPage(0); }
            public void removeUpdate(DocumentEvent e)  { goToPage(0); }
            public void changedUpdate(DocumentEvent e) { goToPage(0); }
        });

        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(SEARCH_HINT)) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_H);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(SEARCH_HINT);
                    searchField.setForeground(TEXT_SUBTLE);
                }
            }
        });

        prevBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { goToPage(currentPage - 1); }
        });
        nextBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { goToPage(currentPage + 1); }
        });

        pdfBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { exportPDF(); }
        });
        excelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { exportExcel(); }
        });
        csvBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { exportCSV(); }
        });

        frame.setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════
    //  PAGINATION (goToPage pattern — same as ViewMemberUI)
    // ════════════════════════════════════════════════════════════════
    private void goToPage(int target) {
        if (target < 0) return;
        String kw = getKeyword();

        int total = 0;
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement cnt;
            if (kw.isEmpty()) {
                cnt = con.prepareStatement("SELECT COUNT(*) FROM transactions WHERE status='Issued'");
            } else {
                cnt = con.prepareStatement(
                    "SELECT COUNT(*) FROM transactions WHERE status='Issued' AND member_name LIKE ?");
                cnt.setString(1, "%" + kw + "%");
            }
            ResultSet rs = cnt.executeQuery();
            if (rs.next()) total = rs.getInt(1);
        } catch (Exception ex) { ex.printStackTrace(); }

        int maxPage = Math.max(0, (total - 1) / PAGE_SIZE);
        if (target > maxPage) target = maxPage;
        currentPage = target;

        loadIssuedBooks(kw);

        pageLbl.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
        prevBtn.setEnabled(currentPage > 0);
        nextBtn.setEnabled(currentPage < maxPage);
    }

    private String getKeyword() {
        String t = searchField.getText().trim();
        return t.equals(SEARCH_HINT) ? "" : t;
    }

    // ════════════════════════════════════════════════════════════════
    //  LOAD DATA (unchanged logic)
    // ════════════════════════════════════════════════════════════════
    private void loadIssuedBooks(String keyword) {
        try {
            model.setRowCount(0);
            Connection con = DBConnection.getConnection();
            PreparedStatement ps;

            if (keyword == null || keyword.trim().isEmpty()) {
                ps = con.prepareStatement(
                    "SELECT transaction_id, book_id, book_title, member_id, member_name, issue_date " +
                    "FROM transactions WHERE status='Issued' ORDER BY transaction_id DESC LIMIT ? OFFSET ?");
                ps.setInt(1, PAGE_SIZE);
                ps.setInt(2, currentPage * PAGE_SIZE);
            } else {
                ps = con.prepareStatement(
                    "SELECT transaction_id, book_id, book_title, member_id, member_name, issue_date " +
                    "FROM transactions WHERE status='Issued' AND member_name LIKE ? " +
                    "ORDER BY transaction_id DESC LIMIT ? OFFSET ?");
                ps.setString(1, "%" + keyword + "%");
                ps.setInt(2, PAGE_SIZE);
                ps.setInt(3, currentPage * PAGE_SIZE);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("transaction_id"),
                    rs.getInt("book_id"),
                    rs.getString("book_title"),
                    rs.getInt("member_id"),
                    rs.getString("member_name"),
                    rs.getDate("issue_date")
                });
            }
            rs.close(); ps.close(); con.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading data: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  EXPORTS (unchanged logic)
    // ════════════════════════════════════════════════════════════════
    private void exportPDF() {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream("Issued_Books_Report.pdf"));
            document.open();

            com.itextpdf.text.Font tf = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Library Issued Books Report", tf);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            com.itextpdf.text.Font df = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph date = new Paragraph("Generated on: " + new java.util.Date().toString(), df);
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);

            for (int i = 0; i < model.getRowCount(); i++) {
                document.add(new Paragraph(
                    "Issue ID: " + model.getValueAt(i,0) +
                    " | Book: " + model.getValueAt(i,2) +
                    " | Member: " + model.getValueAt(i,4) +
                    " | Date: " + model.getValueAt(i,5)));
            }
            document.close();
            showToast("PDF exported as Issued_Books_Report.pdf", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error exporting PDF: " + ex.getMessage());
        }
    }

    private void exportExcel() {
        try {
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("IssuedBooks");

            CellStyle headerStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font hFont = wb.createFont();
            hFont.setBold(true);
            headerStyle.setFont(hFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < table.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(table.getColumnName(i));
                cell.setCellStyle(headerStyle);
            }
            for (int i = 0; i < table.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < table.getColumnCount(); j++) {
                    Object v = table.getValueAt(i, j);
                    row.createCell(j).setCellValue(v != null ? v.toString() : "");
                }
            }
            for (int i = 0; i < table.getColumnCount(); i++) sheet.autoSizeColumn(i);

            FileOutputStream fos = new FileOutputStream("IssuedBooks.xlsx");
            wb.write(fos); wb.close(); fos.close();
            showToast("Excel exported as IssuedBooks.xlsx", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error exporting Excel: " + ex.getMessage());
        }
    }

    private void exportCSV() {
        try {
            FileWriter fw = new FileWriter("IssuedBooks.csv");
            for (int i = 0; i < table.getColumnCount(); i++) {
                fw.write("\"" + table.getColumnName(i) + "\"");
                if (i < table.getColumnCount() - 1) fw.write(",");
            }
            fw.write("\n");
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    Object v = table.getValueAt(i, j);
                    fw.write("\"" + (v != null ? v.toString() : "") + "\"");
                    if (j < table.getColumnCount() - 1) fw.write(",");
                }
                fw.write("\n");
            }
            fw.close();
            showToast("CSV exported as IssuedBooks.csv", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error exporting CSV: " + ex.getMessage());
        }
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
                    g2.setColor(new Color(0, 0, 0, 4 * i));
                    g2.fillRoundRect(i, i, getWidth() - i, getHeight() - i, radius, radius);
                }
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, radius, radius);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, radius, radius);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    private JTextField buildSearchField() {
        JTextField f = new JTextField(SEARCH_HINT) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean foc = hasFocus();
                g2.setColor(foc ? Color.WHITE : new Color(249, 249, 255));
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(foc ? SKY : BORDER);
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(TEXT_SUBTLE);
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(8, 10, 12, 12);
                g2.drawLine(17, 19, 22, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(9, 30, 9, 12));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setForeground(TEXT_SUBTLE);
        f.setCaretColor(SKY);
        f.setPreferredSize(new Dimension(250, 38));
        return f;
    }

    private JButton buildOutlineBtn(final String label, final Color accent, final Image icon) {
        final Color tint = new Color(
            Math.min(255, 220 + accent.getRed()   / 10),
            Math.min(255, 220 + accent.getGreen() / 10),
            Math.min(255, 220 + accent.getBlue()  / 10));
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover(), p = getModel().isPressed();
                g2.setColor((h||p) ? tint : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),(h||p)?200:120));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                if (icon != null) g2.drawImage(recolor(icon, accent), 10, (getHeight()-16)/2, 16, 16, null);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(accent);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, 32, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 38));
        return btn;
    }

    private JButton buildPagBtn(final String label) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover();
                boolean dis = !isEnabled();
                g2.setColor(dis ? new Color(245,245,250) : (h ? new Color(224,242,254) : BG_CARD));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(dis ? new Color(210,210,220) : (h ? SKY : BORDER));
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(dis ? TEXT_SUBTLE : (h ? SKY : TEXT_BODY));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, (getWidth()-fm.stringWidth(label))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
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
            @Override protected void configureScrollBarColors() { thumbColor = new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(), 140); trackColor = BG_PAGE; }
            @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(SKY.getRed(), SKY.getGreen(), SKY.getBlue(), 160));
                g2.fillRoundRect(r.x+2, r.y+2, r.width-4, r.height-4, 6, 6);
                g2.dispose();
            }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
                g.setColor(BG_PAGE); g.fillRect(r.x, r.y, r.width, r.height);
            }
            private JButton zeroBtn() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0,0)); b.setMinimumSize(new Dimension(0,0)); b.setMaximumSize(new Dimension(0,0));
                return b;
            }
        };
    }

    private void showToast(final String msg, final boolean success) {
        final JWindow toast = new JWindow(frame);
        final Color bg = success ? EMERALD : ROSE;
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 18, 10, 18));
        JLabel lbl = new JLabel((success ? "\u2713  " : "\u2715  ") + msg);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        p.add(lbl);
        toast.add(p);
        toast.pack();
        toast.setLocation(frame.getX()+(frame.getWidth()-toast.getWidth())/2, frame.getY()+frame.getHeight()-90);
        toast.setVisible(true);
        Timer t = new Timer(2400, new ActionListener() {
            public void actionPerformed(ActionEvent e) { toast.dispose(); }
        });
        t.setRepeats(false);
        t.start();
    }

    // ════════════════════════════════════════════════════════════════
    //  ICONS
    // ════════════════════════════════════════════════════════════════
    private Image makeIconPDF() {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawRoundRect(2,1,12,17,3,3); g.drawLine(10,1,14,5); g.drawLine(14,5,14,9);
        g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(4,8,10,8); g.drawLine(4,11,10,11); g.drawLine(4,14,8,14);
        g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(16,10,16,17);
        int[] ax={13,16,19}; int[] ay={14,17,14}; g.fillPolygon(ax,ay,3);
        g.dispose();
        return img;
    }

    private Image makeIconExcel() {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawRoundRect(2,1,12,17,3,3); g.drawLine(10,1,14,5); g.drawLine(14,5,14,9);
        g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(4,8,12,8); g.drawLine(4,11,12,11); g.drawLine(4,14,12,14); g.drawLine(8,8,8,17);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(15,11,19,17); g.drawLine(19,11,15,17);
        g.dispose();
        return img;
    }

    private Image makeIconCSV() {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawRoundRect(2,1,12,17,3,3); g.drawLine(10,1,14,5); g.drawLine(14,5,14,9);
        g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(4,8,10,8); g.drawLine(4,11,10,11); g.drawLine(4,14,8,14);
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawRect(13,10,5,7); g.drawLine(16,10,16,17); g.drawLine(13,13,18,13);
        g.dispose();
        return img;
    }

    private Image recolor(Image src, Color color) {
        int w = src.getWidth(null), h = src.getHeight(null);
        if (w<=0||h<=0) return src;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics(); g.drawImage(src,0,0,null); g.dispose();
        int r=color.getRed(), gn=color.getGreen(), b=color.getBlue();
        for (int x=0;x<w;x++) for (int y=0;y<h;y++) {
            int a=(out.getRGB(x,y)>>24)&0xFF;
            if (a>10) out.setRGB(x,y,(a<<24)|(r<<16)|(gn<<8)|b);
        }
        return out;
    }
}