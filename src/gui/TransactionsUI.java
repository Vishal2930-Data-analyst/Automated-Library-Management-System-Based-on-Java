package gui;

import db.DBConnection;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.*;
import java.util.Vector;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.UserSession;
public class TransactionsUI extends JFrame {

    private static final long serialVersionUID = 1L;

    // ── Palette ──────────────────────────────────────────────────────
    private static final Color BG_PAGE      = new Color(244, 245, 251);
    private static final Color BG_CARD      = new Color(255, 255, 255);
    private static final Color INDIGO       = new Color( 79,  70, 229);
    private static final Color INDIGO_LIGHT = new Color(238, 237, 255);
    private static final Color INDIGO_MED   = new Color(165, 180, 252);
    private static final Color VIOLET       = new Color(124,  58, 237);
    private static final Color EMERALD      = new Color(  5, 150, 105);
    private static final Color EMERALD_LIGHT= new Color(209, 250, 229);
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

    private static final String SEARCH_HINT = "  Search by book or member...";

    // ── State ─────────────────────────────────────────────────────────
    private JTable            table;
    private DefaultTableModel model;
    private JTextField        searchField;
    private JLabel            pageLbl;
    private JButton           prevBtn;
    private JButton           nextBtn;
    private int               currentPage = 0;
    private static final int  PAGE_SIZE   = 10;

    public TransactionsUI() {

        setTitle("LibraryPro \u2014 Transaction Records");
        setSize(1420, 820);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_PAGE);

        // ── Wrapper ──────────────────────────────────────────────────
        JPanel wrapper = new JPanel(new BorderLayout(0, 18));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(22, 26, 22, 26));
        add(wrapper, BorderLayout.CENTER);

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
        leftTop.setPreferredSize(new Dimension(480, 44));

        JPanel iconBadge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, INDIGO, getWidth(), getHeight(), VIOLET);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(3, 4, 14, 18, 3, 3); // receipt
                g2.drawLine(3, 8, 17, 8);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(6, 12, 10, 12);
                g2.drawLine(12, 12, 15, 12);
                g2.drawLine(6, 15, 10, 15);
                g2.drawLine(12, 15, 15, 15);
                // Arrow bidirectional (transactions)
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(21, 10, 30, 10);
                int[] ax1 = {24, 21, 24}; int[] ay1 = {7, 10, 13}; g2.fillPolygon(ax1, ay1, 3);
                g2.drawLine(21, 18, 30, 18);
                int[] ax2 = {27, 30, 27}; int[] ay2 = {15, 18, 21}; g2.fillPolygon(ax2, ay2, 3);
                g2.dispose();
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setBounds(0, 4, 36, 36);

        JLabel titleLbl = new JLabel("Transaction Records");
        titleLbl.setFont(new java.awt.Font("Georgia", Font.BOLD, 20));
        titleLbl.setForeground(TEXT_H);
        titleLbl.setBounds(46, 4, 340, 26);

        JLabel subLbl = new JLabel("View, manage and export all library circulation records");
        subLbl.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 11));
        subLbl.setForeground(TEXT_MUTED);
        subLbl.setBounds(47, 30, 420, 14);

        leftTop.add(iconBadge);
        leftTop.add(titleLbl);
        leftTop.add(subLbl);

        // Right
        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTop.setOpaque(false);

        searchField = buildSearchField();
        final JButton exportPDF   = buildOutlineBtn("Export PDF",   ROSE,    makeIconPDF());
        final JButton exportCSV   = buildOutlineBtn("Export CSV",   EMERALD, makeIconCSV());

        rightTop.add(searchField);
        rightTop.add(exportPDF);
        rightTop.add(exportCSV);

        topBar.add(leftTop,  BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);
        wrapper.add(topBar, BorderLayout.NORTH);

        // ════════════════════════════════════════════════════════════
        //  TABLE CARD
        // ════════════════════════════════════════════════════════════
        JPanel tableCard = buildCard(16);
        tableCard.setLayout(new BorderLayout());

        // ── Model ────────────────────────────────────────────────────
        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        String[] cols = {"ID","Book ID","Book Title","Member ID","Member Name","Course/Department",
                         "Issue Date","Due Date","Return Date","Fine (\u20b9)","Status"};
        for (String c : cols) model.addColumn(c);

        // ── Table ────────────────────────────────────────────────────
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

                // Status column special coloring
                if (col == 10 && model.getValueAt(row, col) != null) {
                    String status = model.getValueAt(row, col).toString();
                    if ("Issued".equals(status)) {
                        c.setForeground(EMERALD);
                        if (c instanceof JLabel)
                            ((JLabel) c).setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12));
                    } else if ("Returned".equals(status)) {
                        c.setForeground(TEXT_MUTED);
                    } else if ("Overdue".equals(status)) {
                        c.setForeground(ROSE);
                        if (c instanceof JLabel)
                            ((JLabel) c).setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12));
                    } else if ("Lost".equals(status)) {
                        c.setForeground(AMBER);
                    }
                }

                // Center-align all cells
                if (c instanceof JLabel) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                    ((JLabel) c).setBorder(new EmptyBorder(0, 8, 0, 8));
                }

                return c;
            }
        };

        table.setRowHeight(48);
        table.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 242, 248));
        table.setSelectionBackground(ROW_SELECT);
        table.setSelectionForeground(INDIGO);
        table.setFocusable(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        // ── Custom header ────────────────────────────────────────────
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                final String text = (v != null) ? v.toString() : "";
                JLabel lbl = new JLabel(text) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        GradientPaint gp = new GradientPaint(0, 0, new Color(246, 245, 255),
                            0, getHeight(), new Color(238, 237, 255));
                        g2.setPaint(gp);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        g2.setColor(INDIGO_MED);
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                        // Vertical divider
                        g2.setColor(new Color(INDIGO_MED.getRed(), INDIGO_MED.getGreen(), INDIGO_MED.getBlue(), 80));
                        g2.setStroke(new BasicStroke(0.8f));
                        g2.drawLine(getWidth() - 1, 4, getWidth() - 1, getHeight() - 4);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                lbl.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 11));
                lbl.setForeground(INDIGO);
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
        table.getColumnModel().getColumn(1).setPreferredWidth(65);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(75);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);
        table.getColumnModel().getColumn(5).setPreferredWidth(95);
        table.getColumnModel().getColumn(6).setPreferredWidth(95);
        table.getColumnModel().getColumn(7).setPreferredWidth(95);
        table.getColumnModel().getColumn(8).setPreferredWidth(75);
        table.getColumnModel().getColumn(9).setPreferredWidth(95);

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

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionBtns.setOpaque(false);

        final JButton editBtn   = buildFillBtn("Edit Transaction",   INDIGO, makeIconEdit());
        final JButton deleteBtn = buildFillBtn("Delete Transaction",  ROSE,   makeIconDelete());
        actionBtns.add(editBtn);
        actionBtns.add(deleteBtn);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        paginationPanel.setOpaque(false);

        prevBtn = buildPagBtn("\u2190 Prev");
        pageLbl = new JLabel("Page 1");
        pageLbl.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12));
        pageLbl.setForeground(TEXT_MUTED);
        nextBtn = buildPagBtn("Next \u2192");

        paginationPanel.add(prevBtn);
        paginationPanel.add(pageLbl);
        paginationPanel.add(nextBtn);

        bottomBar.add(actionBtns,      BorderLayout.WEST);
        bottomBar.add(paginationPanel, BorderLayout.EAST);

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

            private void handle() {
                // ✅ Only when typing
                if (!searchField.isFocusOwner()) return;

                String text = searchField.getText().trim();

                // ✅ Ignore placeholder
                if (text.equals(SEARCH_HINT)) return;

                goToPage(0);
            }

            public void insertUpdate(DocumentEvent e)  { handle(); }
            public void removeUpdate(DocumentEvent e)  { handle(); }
            public void changedUpdate(DocumentEvent e) { handle(); }
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

        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { editTransaction(); }
        });
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteTransaction(); }
        });

        exportPDF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { exportPDF(); }
        });
      
        exportCSV.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { exportCSV(); }
        });

        setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════
    //  PAGINATION  (same pattern as ViewMemberUI)
    // ════════════════════════════════════════════════════════════════
    private void goToPage(int target) {
        if (target < 0) return;
        String kw = getKeyword();

        int total = 0;
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement cnt = con.prepareStatement(
            		"SELECT COUNT(*) FROM transactions WHERE admin_id=? AND (book_title LIKE ? OR member_name LIKE ? OR course LIKE ?)");
            cnt.setInt(1, UserSession.adminId);
            cnt.setString(2, "%" + kw + "%");
            cnt.setString(3, "%" + kw + "%");
            cnt.setString(4, "%" + kw + "%");
            ResultSet rs = cnt.executeQuery();
            if (rs.next()) total = rs.getInt(1);
        } catch (Exception ex) { ex.printStackTrace(); }

        int maxPage = Math.max(0, (total - 1) / PAGE_SIZE);
        if (target > maxPage) target = maxPage;
        currentPage = target;

        loadTransactions(kw);

        pageLbl.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
        prevBtn.setEnabled(currentPage > 0);
        nextBtn.setEnabled(currentPage < maxPage);
    }

    private String getKeyword() {
        String t = searchField.getText().trim();
        return t.equals(SEARCH_HINT) ? "" : t;
    }

    // ════════════════════════════════════════════════════════════════
    //  DATA  (unchanged logic)
    // ════════════════════════════════════════════════════════════════
    private void loadTransactions(String keyword) {
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(
            		"SELECT * FROM transactions WHERE admin_id=? AND (book_title LIKE ? OR member_name LIKE ? OR course LIKE ?) ORDER BY transaction_id DESC LIMIT ? OFFSET ?");
            pst.setInt(1, UserSession.adminId);
            pst.setString(2, "%" + keyword + "%");
            pst.setString(3, "%" + keyword + "%");
            pst.setString(4, "%" + keyword + "%");   
            pst.setInt(5, PAGE_SIZE);
            pst.setInt(6, currentPage * PAGE_SIZE);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<Object>();
                row.add(rs.getInt("transaction_id"));
                row.add(rs.getInt("book_id"));
                row.add(rs.getString("book_title"));
                row.add(rs.getInt("member_id"));
                row.add(rs.getString("member_name"));
                row.add(rs.getString("course"));
                row.add(rs.getDate("issue_date")  != null ? rs.getDate("issue_date").toString()  : "—");
                row.add(rs.getDate("due_date")    != null ? rs.getDate("due_date").toString()    : "—");
                row.add(rs.getDate("return_date") != null ? rs.getDate("return_date").toString() : "—");
                row.add(rs.getInt("fine"));
                row.add(rs.getString("status"));
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage());
        }
    }

    private void editTransaction() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a transaction to edit"); return; }
        int id = (Integer) model.getValueAt(row, 0);
        String current = (String) model.getValueAt(row, 10);
        String[] statuses = {"Issued", "Returned", "Overdue", "Lost"};
        String status = (String) JOptionPane.showInputDialog(this, "Select new status:",
            "Edit Transaction", JOptionPane.QUESTION_MESSAGE, null, statuses, current);
        if (status != null && !status.equals(current)) {
            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(
                    "UPDATE transactions SET status=? WHERE transaction_id=? AND admin_id=?");
                pst.setString(1, status);
                pst.setInt(2, id);
                pst.setInt(3, UserSession.adminId);
                pst.executeUpdate();
                goToPage(currentPage);
                JOptionPane.showMessageDialog(this, "Transaction updated successfully!");
            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
    }

    private void deleteTransaction() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Please select a transaction to delete"); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this transaction?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (Integer) model.getValueAt(row, 0);
            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement("DELETE FROM transactions WHERE transaction_id=? AND admin_id=?");
                pst.setInt(1, id);
                pst.setInt(2, UserSession.adminId);
                pst.executeUpdate();
                PreparedStatement updateBook = con.prepareStatement(
                	    "UPDATE books SET quantity = quantity + 1 WHERE id=? AND admin_id=?"
                	);

                	updateBook.setInt(1, (Integer) model.getValueAt(row, 1)); // book_id column
                	updateBook.setInt(2, UserSession.adminId);
                	updateBook.executeUpdate();
                goToPage(currentPage);
                JOptionPane.showMessageDialog(this, "Transaction deleted successfully!");
            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
    }

    private void exportPDF() {
        try {
            Document doc = new Document(PageSize.A4.rotate(), 36, 36, 50, 36);
            PdfWriter.getInstance(doc, new FileOutputStream("transactions.pdf"));
            doc.open();

            // 🎯 Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(70, 130, 200));
            Paragraph title = new Paragraph("LIBRARY TRANSACTION REPORT");
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            doc.add(title);

            // 📅 Date
            Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, currentPage, BaseColor.DARK_GRAY);
            String date = new java.text.SimpleDateFormat("dd MMM yyyy HH:mm").format(new java.util.Date());
            Paragraph datePara = new Paragraph("Generated on: " + date, dateFont);
            datePara.setAlignment(Element.ALIGN_RIGHT);
            datePara.setSpacingAfter(15);
            doc.add(datePara);

            // 📊 Table
            PdfPTable tablePDF = new PdfPTable(table.getColumnCount());
            tablePDF.setWidthPercentage(100);
            tablePDF.setSpacingBefore(10);

            // Header Font
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);

            // 🎨 Table Headers
            for (int i = 0; i < table.getColumnCount(); i++) {
                PdfPCell header = new PdfPCell(new Paragraph(table.getColumnName(i), headerFont));
                header.setBackgroundColor(new BaseColor(41, 128, 185));
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setPadding(8);
                header.setBorderColor(BaseColor.WHITE);
                tablePDF.addCell(header);
            }

            // Body Font
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // 🔁 Table Data with styling
            for (int r = 0; r < table.getRowCount(); r++) {

                BaseColor rowColor = (r % 2 == 0) ? BaseColor.WHITE : new BaseColor(245, 245, 245);

                for (int c = 0; c < table.getColumnCount(); c++) {
                    Object val = table.getValueAt(r, c);
                    String text = (val != null) ? val.toString() : "";

                    PdfPCell cell = new PdfPCell(new Paragraph(text, bodyFont));
                    cell.setPadding(6);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                    // 🎯 Status column styling
                    if (c == 9) { // Status column
                        if ("Issued".equals(text)) {
                            cell.setBackgroundColor(new BaseColor(212, 237, 218)); // green
                        } else if ("Overdue".equals(text)) {
                            cell.setBackgroundColor(new BaseColor(248, 215, 218)); // red
                        } else if ("Returned".equals(text)) {
                            cell.setBackgroundColor(new BaseColor(230, 230, 230)); // gray
                        }
                    } else {
                        cell.setBackgroundColor(rowColor);
                    }

                    tablePDF.addCell(cell);
                }
            }

            doc.add(tablePDF);

            // 📌 Footer
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY);
            Paragraph footer = new Paragraph("Generated by Library Management System", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(15);
            doc.add(footer);

            doc.close();

            showToast("PDF exported (Modern UI)", true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void exportCSV() {
        try {
            FileWriter fw = new FileWriter("transactions.csv");
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
            showToast("CSV exported as transactions.csv", true);
        } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
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
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(foc ? INDIGO : BORDER);
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
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
        f.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12));
        f.setForeground(TEXT_SUBTLE);
        f.setCaretColor(INDIGO);
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
                g2.setColor((h || p) ? tint : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (h||p)?200:120));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                if (icon != null) g2.drawImage(recolor(icon, accent), 10, (getHeight()-16)/2, 16, 16, null);
                g2.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(accent);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, 32, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 38));
        return btn;
    }

    private JButton buildFillBtn(final String label, final Color accent, final Image icon) {
        final Color lighter = new Color(
            Math.min(255, accent.getRed()   + 40),
            Math.min(255, accent.getGreen() + 40),
            Math.min(255, accent.getBlue()  + 40));
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover(), p = getModel().isPressed();
                Color c1 = p ? accent.darker() : (h ? lighter : accent);
                Color c2 = p ? accent          : (h ? accent  : lighter);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (!p) { g2.setColor(new Color(255,255,255,30)); g2.fillRoundRect(0,0,getWidth(),getHeight()/2,10,10); }
                if (icon != null) g2.drawImage(recolor(icon, Color.WHITE), 10, (getHeight()-16)/2, 16, 16, null);
                g2.setColor(Color.WHITE);
                g2.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, 32, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
        return btn;
    }

    private JButton buildPagBtn(final String label) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover();
                boolean dis = !isEnabled();
                g2.setColor(dis ? new Color(245,245,250) : (h ? INDIGO_LIGHT : BG_CARD));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(dis ? new Color(210,210,220) : (h ? INDIGO : BORDER));
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(dis ? TEXT_SUBTLE : (h ? INDIGO : TEXT_BODY));
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
            @Override protected void configureScrollBarColors() { thumbColor = INDIGO_MED; trackColor = BG_PAGE; }
            @Override protected JButton createDecreaseButton(int o) { return zeroBtn(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroBtn(); }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INDIGO_MED);
                g2.fillRoundRect(r.x+2, r.y+2, r.width-4, r.height-4, 6, 6);
                g2.dispose();
            }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
                g.setColor(BG_PAGE); g.fillRect(r.x, r.y, r.width, r.height);
            }
            private JButton zeroBtn() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0,0));
                b.setMinimumSize(new Dimension(0,0));
                b.setMaximumSize(new Dimension(0,0));
                return b;
            }
        };
    }

    private void showToast(final String msg, final boolean success) {
        final JWindow toast = new JWindow(this);
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
        lbl.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        p.add(lbl);
        toast.add(p);
        toast.pack();
        toast.setLocation(getX()+(getWidth()-toast.getWidth())/2, getY()+getHeight()-90);
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
    private Image makeIconEdit() {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int[] px = {3,13,16,6}; int[] py = {13,2,5,16};
        g.drawPolygon(px, py, 4);
        g.drawLine(3,13,2,18); g.drawLine(6,16,2,18); g.drawLine(10,4,14,8);
        g.dispose();
        return img;
    }

    private Image makeIconDelete() {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawRoundRect(3,5,14,13,3,3); g.drawLine(1,5,19,5); g.drawRoundRect(6,2,8,4,2,2);
        g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(7,9,7,14); g.drawLine(10,9,10,14); g.drawLine(13,9,13,14);
        g.dispose();
        return img;
    }

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