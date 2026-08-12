package gui;

import db.DBConnection;

import com.itextpdf.text.Element;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.*;
import java.text.SimpleDateFormat;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Vector;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.UserSession;
public class ViewMemberUI extends JFrame {

    // ── Palette ──────────────────────────────────────────────────────
    private static final Color BG_PAGE     = new Color(244, 245, 251);
    private static final Color BG_CARD     = new Color(255, 255, 255);
    private static final Color INDIGO      = new Color( 79,  70, 229);
    private static final Color INDIGO_LIGHT= new Color(238, 237, 255);
    private static final Color EMERALD     = new Color(  5, 150, 105);
    private static final Color ROSE        = new Color(225,  29,  72);
    private static final Color AMBER       = new Color(217, 119,   6);
    private static final Color TEXT_H      = new Color( 17,  24,  39);
    private static final Color TEXT_BODY   = new Color( 55,  65,  81);
    private static final Color TEXT_MUTED  = new Color(107, 114, 128);
    private static final Color TEXT_SUBTLE = new Color(156, 163, 175);
    private static final Color BORDER      = new Color(226, 232, 240);
    private static final Color ROW_STRIPE  = new Color(249, 249, 255);
    private static final Color ROW_SELECT  = new Color(224, 221, 255);
    private static final Color EMERALD_TIP = new Color( 52, 211, 153);

    private static final String SEARCH_HINT = "  Search by name or course...";

    // ── ALL instance fields ───────────────────────────────────────────
    private JTable            table;
    private DefaultTableModel model;
    private JTextField        searchField;
    private JLabel            pageLbl;
    private JButton           prevBtn;
    private JButton           nextBtn;
    private int               currentPage = 0;
    private static final int  PAGE_SIZE   = 10;

    public ViewMemberUI() {

        setTitle("Members Management");
        setSize(1100, 690);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_PAGE);

        JPanel wrapper = new JPanel(new BorderLayout(0, 18));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(22, 26, 22, 26));
        add(wrapper, BorderLayout.CENTER);

        // ── Top Bar ──────────────────────────────────────────────────
        JPanel topBar = buildCard(16);
        topBar.setLayout(new BorderLayout());
        topBar.setBorder(new EmptyBorder(16, 24, 16, 24));
        topBar.setPreferredSize(new Dimension(0, 72));

        JPanel leftTop = new JPanel(null);
        leftTop.setOpaque(false);
        leftTop.setPreferredSize(new Dimension(420, 44));

        JPanel iconBadge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, EMERALD, getWidth(), getHeight(), new Color(6, 182, 212));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(4,  3,  9, 9);
                g2.drawArc(1,  13, 13, 8, 0, 180);
                g2.drawOval(19, 2,  9, 9);
                g2.drawArc(15, 12, 14, 8, 0, 180);
                g2.dispose();
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setBounds(0, 4, 36, 36);

        JLabel titleLbl = new JLabel("Members Management");
        titleLbl.setFont(new java.awt.Font("Georgia", Font.BOLD, 20));
        titleLbl.setForeground(TEXT_H);
        titleLbl.setBounds(46, 4, 320, 26);

        JLabel subLbl = new JLabel("Browse, search, edit and export library members");
        subLbl.setFont(new java.awt.Font("Segoe UI", Font.NORMAL, 11));
        subLbl.setForeground(TEXT_MUTED);
        subLbl.setBounds(47, 30, 380, 14);

        leftTop.add(iconBadge);
        leftTop.add(titleLbl);
        leftTop.add(subLbl);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTop.setOpaque(false);

        searchField = buildSearchField();
        final JButton exportPDF   = buildOutlineButton("Export PDF",   ROSE,    makeIconPDF());
        final JButton exportExcel = buildOutlineButton("Export Excel",  AMBER,   makeIconExcel());
        final JButton exportCSV   = buildOutlineButton("Export CSV",   EMERALD, makeIconCSV());

        rightTop.add(searchField);
        rightTop.add(exportPDF);
        rightTop.add(exportExcel);
        rightTop.add(exportCSV);

        topBar.add(leftTop,  BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);
        wrapper.add(topBar, BorderLayout.NORTH);

        // ── Table Card ────────────────────────────────────────────────
        JPanel tableCard = buildCard(16);
        tableCard.setLayout(new BorderLayout());

        model = new DefaultTableModel() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return (c == 0) ? Integer.class : String.class;
            }
        };
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Course");
        model.addColumn("Email");
        model.addColumn("Phone");

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
                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(new EmptyBorder(0, 14, 0, 14));
                }
                return c;
            }
        };
        table.setRowHeight(44);
        table.setFont(new java.awt.Font("Segoe UI", Font.NORMAL, 13));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 242, 248));
        table.setSelectionBackground(ROW_SELECT);
        table.setSelectionForeground(INDIGO);
        table.setFocusable(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                final String text = (v != null) ? v.toString() : "";
                JLabel lbl = new JLabel(text) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        GradientPaint gp = new GradientPaint(
                            0, 0, new Color(240, 253, 244),
                            0, getHeight(), new Color(209, 250, 229));
                        g2.setPaint(gp);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        g2.setColor(EMERALD_TIP);
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                lbl.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12));
                lbl.setForeground(EMERALD);
                lbl.setBorder(new EmptyBorder(12, 14, 12, 14));
                lbl.setOpaque(false);
                return lbl;
            }
        });
        table.getTableHeader().setPreferredSize(new Dimension(0, 44));
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(220);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.getVerticalScrollBar().setUI(buildSlimScrollBar());
        tableCard.add(scroll, BorderLayout.CENTER);

        // ── Bottom Bar ────────────────────────────────────────────────
        JPanel bottomBar = buildCard(14);
        bottomBar.setLayout(new BorderLayout());
        bottomBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        bottomBar.setPreferredSize(new Dimension(0, 62));

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionBtns.setOpaque(false);

        JButton editBtn   = buildFillButton("Edit Member",   INDIGO, makeIconEdit());
        JButton deleteBtn = buildFillButton("Delete Member", ROSE,   makeIconDelete());
        actionBtns.add(editBtn);
        actionBtns.add(deleteBtn);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        paginationPanel.setOpaque(false);

        prevBtn = buildPagBtn("\u2190 Prev");
        pageLbl = new JLabel("Page 1");
        pageLbl.setFont(new java.awt.Font("Segoe UI", Font.NORMAL, 12));
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

        // ── Listeners ─────────────────────────────────────────────────
        searchField.getDocument().addDocumentListener(new DocumentListener() {

            private void handle() {
                // ✅ Only trigger when user is typing
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
            public void actionPerformed(ActionEvent e) {
                goToPage(currentPage - 1);
            }
        });

        nextBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goToPage(currentPage + 1);
            }
        });

        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { editMember(); }
        });

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteMember(); }
        });

        exportPDF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { exportPDF(); }
        });

        exportExcel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { exportExcel(); }
        });

        exportCSV.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { exportCSV(); }
        });

        setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════
    //  CORE PAGINATION METHOD — single source of truth
    // ════════════════════════════════════════════════════════════════
    private void goToPage(int targetPage) {
        if (targetPage < 0) return;

        // Get keyword safely
        String kw = searchField.getText().trim();
        if (kw.equals(SEARCH_HINT)) kw = "";

        // Count total matching rows
        int total = 0;
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement cnt = con.prepareStatement(
            	    "SELECT COUNT(*) FROM members WHERE admin_id=? AND (name LIKE ? OR course LIKE ?)"
            	);

            	cnt.setInt(1, UserSession.adminId);
            	cnt.setString(2, "%" + kw + "%");
            	cnt.setString(3, "%" + kw + "%");
            ResultSet rs = cnt.executeQuery();
            if (rs.next()) total = rs.getInt(1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        int maxPage = Math.max(0, (total - 1) / PAGE_SIZE);
        if (targetPage > maxPage) targetPage = maxPage;

        // Set current page
        currentPage = targetPage;

        // Load rows for this page
        model.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(
            	    "SELECT * FROM members WHERE admin_id=? AND (name LIKE ? OR course LIKE ?) LIMIT ? OFFSET ?"
            	);

            	pst.setInt(1, UserSession.adminId);
            	pst.setString(2, "%" + kw + "%");
            	pst.setString(3, "%" + kw + "%");
            	pst.setInt(4, PAGE_SIZE);
            	pst.setInt(5, currentPage * PAGE_SIZE);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<Object>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("course"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phone"));
                model.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Update UI
        pageLbl.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
        prevBtn.setEnabled(currentPage > 0);
        nextBtn.setEnabled(currentPage < maxPage);
    }

    // ════════════════════════════════════════════════════════════════
    //  DATA METHODS  (unchanged logic)
    // ════════════════════════════════════════════════════════════════

    private void editMember() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member to edit.");
            return;
        }
        int id = (Integer) model.getValueAt(row, 0);
        JTextField nf = new JTextField((String) model.getValueAt(row, 1));
        JTextField cf = new JTextField((String) model.getValueAt(row, 2));
        JTextField ef = new JTextField((String) model.getValueAt(row, 3));
        JTextField pf = new JTextField((String) model.getValueAt(row, 4));
        Object[] fields = {"Name:", nf, "Course:", cf, "Email:", ef, "Phone:", pf};
        int opt = JOptionPane.showConfirmDialog(this, fields, "Edit Member", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(
                    "UPDATE members \r\n"
                    + "SET name=?,course=?,email=?,phone=? \r\n"
                    + "WHERE id=? AND admin_id=?");
                pst.setString(1, nf.getText());
                pst.setString(2, cf.getText());
                pst.setString(3, ef.getText());
                pst.setString(4, pf.getText());
                pst.setInt(5, id);
                pst.setInt(6, UserSession.adminId);
                pst.executeUpdate();
                goToPage(currentPage);
                showToast("Member updated successfully.", true);
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void deleteMember() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member to delete.");
            return;
        }
        int    id   = (Integer) model.getValueAt(row, 0);
        String name = (String)  model.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete member \"" + name + "\"?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement("DELETE FROM members WHERE id=? AND admin_id=?");
                pst.setInt(1, id);
                pst.setInt(2, UserSession.adminId);
                pst.executeUpdate();
                goToPage(currentPage);
                showToast("Member deleted.", true);
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void exportPDF() {
        try {
            // Query database directly (like your books report)
            Connection con = DBConnection.getConnection();
            String query = "SELECT * FROM members WHERE admin_id=?"; // Adjust table name and condition as per your database
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, UserSession.adminId);
            ResultSet rs = pst.executeQuery();
            
            Document doc = new Document(PageSize.A4, 36, 36, 54, 36);
            PdfWriter.getInstance(doc, new FileOutputStream("members.pdf"));
            doc.open();
            
            // Add title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(70, 130, 200));
            Paragraph title = new Paragraph("MEMBERS REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            doc.add(title);
            
            // Add date
            Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
            Paragraph dateTime = new Paragraph("Generated on: " + sdf.format(new java.util.Date()), dateFont);
            dateTime.setAlignment(Element.ALIGN_RIGHT);
            dateTime.setSpacingAfter(15);
            doc.add(dateTime);
            
            // Add decorative line
            Paragraph line = new Paragraph("__________________________________________________");
            line.setAlignment(Element.ALIGN_CENTER);
            line.setSpacingAfter(10);
            doc.add(line);
            
            doc.add(new Paragraph(" "));
            
            // Get column count from ResultSet metadata
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            
            // Create table
            PdfPTable pdfTable = new PdfPTable(columnCount);
            pdfTable.setWidthPercentage(100);
            pdfTable.setSpacingBefore(10);
            
            // Header styling
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
            
            // Add headers
            for (int i = 1; i <= columnCount; i++) {
                PdfPCell headerCell = new PdfPCell(new Paragraph(rsmd.getColumnName(i), headerFont));
                headerCell.setBackgroundColor(new BaseColor(52, 152, 219));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPadding(8);
                pdfTable.addCell(headerCell);
            }
            
            // Add data
            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
            boolean hasData = false;
            int rowCount = 0;
            
            while (rs.next()) {
                hasData = true;
                rowCount++;
                BaseColor rowColor = (rowCount % 2 == 0) ? new BaseColor(245, 245, 245) : BaseColor.WHITE;
                
                for (int i = 1; i <= columnCount; i++) {
                    String cellValue = rs.getString(i);
                    if (cellValue == null) cellValue = "";
                    PdfPCell dataCell = new PdfPCell(new Paragraph(cellValue, bodyFont));
                    dataCell.setBackgroundColor(rowColor);
                    dataCell.setPadding(6);
                    dataCell.setBorderColor(BaseColor.LIGHT_GRAY);
                    pdfTable.addCell(dataCell);
                }
            }
            
            if (!hasData) {
                PdfPCell noDataCell = new PdfPCell(new Paragraph("No members found", bodyFont));
                noDataCell.setColspan(columnCount);
                noDataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                noDataCell.setPadding(20);
                pdfTable.addCell(noDataCell);
            }
            
            doc.add(pdfTable);
            
            // Add footer
            doc.add(new Paragraph(" "));
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);
            Paragraph footer = new Paragraph("Total Members: " + rowCount + " | Generated by Library Management System", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(15);
            doc.add(footer);
            
            rs.close();
            pst.close();
            con.close();
            
            doc.close();
            showToast("PDF exported as members.pdf (" + rowCount + " records)", true);
            
        } catch (Exception e) { 
            e.printStackTrace();
            showToast("Error exporting PDF: " + e.getMessage(), false);
        }
    }

    private void exportExcel() {
        try {
            System.out.println("Starting Excel Export...");
            
            // Get database connection - SAME AS PDF
            Connection con = DBConnection.getConnection();
            System.out.println("Connection obtained");
            
            // SAME QUERY AS PDF
            String query = "SELECT * FROM members WHERE admin_id=?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, UserSession.adminId);
            System.out.println("Query prepared for admin_id: " + UserSession.adminId);
            
            ResultSet rs = pst.executeQuery();
            System.out.println("Query executed");
            
            // Create Excel file
            String filePath = "members_report.xlsx";
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Members");
            
            // Get column count
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            System.out.println("Column count: " + columnCount);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 1; i <= columnCount; i++) {
                headerRow.createCell(i - 1).setCellValue(metaData.getColumnName(i));
            }
            
            // Fill data
            int rowIndex = 1;
            boolean hasData = false;
            
            while (rs.next()) {
                hasData = true;
                Row row = sheet.createRow(rowIndex++);
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    row.createCell(i - 1).setCellValue(value != null ? value : "");
                }
            }
            
            if (!hasData) {
                System.out.println("No data found");
                Row row = sheet.createRow(1);
                row.createCell(0).setCellValue("No members found");
            } else {
                System.out.println("Added " + (rowIndex - 1) + " records");
            }
            
            // Auto-size columns
            for (int i = 0; i < columnCount; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            FileOutputStream fos = new FileOutputStream(filePath);
            workbook.write(fos);
            fos.close();
            workbook.close();
            
            // Close database resources
            rs.close();
            pst.close();
            con.close();
            
            System.out.println("Excel file created: " + filePath);
            showToast("Excel exported as " + filePath + " (" + (rowIndex - 1) + " records)", true);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            showToast("Error: " + ex.getMessage(), false);
        }
    }
    
    private void exportCSV() {
        try {
            FileWriter fw = new FileWriter("members.csv");
            for (int i = 0; i < table.getColumnCount(); i++)
                fw.write(table.getColumnName(i) + ",");
            fw.write("\n");
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++)
                    fw.write(table.getValueAt(i, j) + ",");
                fw.write("\n");
            }
            fw.close();
            showToast("CSV exported as members.csv", true);
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
                boolean focused = hasFocus();
                g2.setColor(focused ? Color.WHITE : new Color(249, 249, 255));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(focused ? EMERALD : BORDER);
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(TEXT_SUBTLE);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(8, 10, 13, 13);
                g2.drawLine(18, 20, 23, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(9, 32, 9, 12));
        f.setFont(new java.awt.Font("Segoe UI", Font.NORMAL, 12));
        f.setForeground(TEXT_SUBTLE);
        f.setCaretColor(EMERALD);
        f.setPreferredSize(new Dimension(240, 38));
        return f;
    }

    private JButton buildOutlineButton(final String label, final Color accent, final Image icon) {
        final Color tint = new Color(
            Math.min(255, 220 + accent.getRed()   / 10),
            Math.min(255, 220 + accent.getGreen() / 10),
            Math.min(255, 220 + accent.getBlue()  / 10)
        );
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover(), p = getModel().isPressed();
                g2.setColor((h || p) ? tint : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (h || p) ? 200 : 120));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                if (icon != null)
                    g2.drawImage(recolor(icon, accent), 10, (getHeight() - 16) / 2, 16, 16, null);
                g2.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(accent);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, 32, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(136, 38));
        return btn;
    }

    private JButton buildFillButton(final String label, final Color accent, final Image icon) {
        final Color lighter = new Color(
            Math.min(255, accent.getRed()   + 40),
            Math.min(255, accent.getGreen() + 40),
            Math.min(255, accent.getBlue()  + 40)
        );
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover(), p = getModel().isPressed();
                Color c1 = p ? accent.darker() : (h ? lighter : accent);
                Color c2 = p ? accent : (h ? accent : lighter);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (!p) { g2.setColor(new Color(255, 255, 255, 30)); g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 10, 10); }
                if (icon != null) g2.drawImage(recolor(icon, Color.WHITE), 10, (getHeight() - 16) / 2, 16, 16, null);
                g2.setColor(Color.WHITE);
                g2.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label, 32, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 38));
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
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(dis ? TEXT_SUBTLE : (h ? INDIGO : TEXT_BODY));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(label,
                    (getWidth()  - fm.stringWidth(label)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
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
            @Override protected void configureScrollBarColors() { thumbColor = EMERALD_TIP; trackColor = BG_PAGE; }
            @Override protected JButton createDecreaseButton(int o) { return zeroButton(); }
            @Override protected JButton createIncreaseButton(int o) { return zeroButton(); }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(EMERALD_TIP);
                g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 6, 6);
                g2.dispose();
            }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
                g.setColor(BG_PAGE); g.fillRect(r.x, r.y, r.width, r.height);
            }
            private JButton zeroButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
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
        toast.setLocation(getX() + (getWidth() - toast.getWidth()) / 2, getY() + getHeight() - 90);
        toast.setVisible(true);
        Timer timer = new Timer(2400, new ActionListener() {
            public void actionPerformed(ActionEvent e) { toast.dispose(); }
        });
        timer.setRepeats(false);
        timer.start();
    }

    // ── Icons ────────────────────────────────────────────────────────

    private Image makeIconEdit() {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int[] px = {3, 13, 16, 6}; int[] py = {13, 2, 5, 16};
        g.drawPolygon(px, py, 4);
        g.drawLine(3, 13, 2, 18); g.drawLine(6, 16, 2, 18); g.drawLine(10, 4, 14, 8);
        g.dispose();
        return img;
    }

    private Image makeIconDelete() {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawRoundRect(3, 5, 14, 13, 3, 3); g.drawLine(1, 5, 19, 5); g.drawRoundRect(6, 2, 8, 4, 2, 2);
        g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(7, 9, 7, 14); g.drawLine(10, 9, 10, 14); g.drawLine(13, 9, 13, 14);
        g.dispose();
        return img;
    }

    private Image makeIconPDF() {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawRoundRect(2, 1, 12, 17, 3, 3); g.drawLine(10, 1, 14, 5); g.drawLine(14, 5, 14, 9);
        g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(4, 8, 10, 8); g.drawLine(4, 11, 10, 11); g.drawLine(4, 14, 8, 14);
        g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(16, 10, 16, 17);
        int[] ax = {13, 16, 19}; int[] ay = {14, 17, 14}; g.fillPolygon(ax, ay, 3);
        g.dispose();
        return img;
    }

    private Image makeIconExcel() {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawRoundRect(2, 1, 12, 17, 3, 3); g.drawLine(10, 1, 14, 5); g.drawLine(14, 5, 14, 9);
        g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(4, 8, 12, 8); g.drawLine(4, 11, 12, 11); g.drawLine(4, 14, 12, 14); g.drawLine(8, 8, 8, 17);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(15, 11, 19, 17); g.drawLine(19, 11, 15, 17);
        g.dispose();
        return img;
    }

    private Image makeIconCSV() {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawRoundRect(2, 1, 12, 17, 3, 3); g.drawLine(10, 1, 14, 5); g.drawLine(14, 5, 14, 9);
        g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(4, 8, 10, 8); g.drawLine(4, 11, 10, 11); g.drawLine(4, 14, 8, 14);
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawRect(13, 10, 5, 7); g.drawLine(16, 10, 16, 17); g.drawLine(13, 13, 18, 13);
        g.dispose();
        return img;
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