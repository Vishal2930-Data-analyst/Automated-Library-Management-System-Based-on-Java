package gui;

import javax.swing.*;
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

import db.DBConnection;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import util.UserSession;

public class ViewBooksUI {

    private DefaultTableModel model;
    private JTable table; // ← made field so listeners can access directly
    private int page         = 0;
    private int limit        = 10;
    private int totalRecords = 0;

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
    private static final Color ROW_STRIPE   = new Color(249, 249, 255);
    private static final Color ROW_SELECT   = new Color(224, 221, 255);

    public ViewBooksUI() {

        JFrame frame = new JFrame("View Books");
        frame.setSize(1080, 680);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BG_PAGE);

        JPanel wrapper = new JPanel(new BorderLayout(0, 18));
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(22, 26, 22, 26));
        frame.add(wrapper, BorderLayout.CENTER);

        // ── Top Bar ──────────────────────────────────────────────────
        JPanel topBar = buildCard(16);
        topBar.setLayout(new BorderLayout(0, 0));
        topBar.setBorder(new EmptyBorder(16, 24, 16, 24));
        topBar.setPreferredSize(new Dimension(0, 72));

        JPanel leftTop = new JPanel(null);
        leftTop.setOpaque(false);
        leftTop.setPreferredSize(new Dimension(400, 44));

        JPanel iconBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, INDIGO, getWidth(), getHeight(), VIOLET);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawRoundRect(4, 3, 14, 18, 3, 3);
                g2.drawRoundRect(8, 6, 14, 18, 3, 3);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(11, 11, 18, 11);
                g2.drawLine(11, 14, 18, 14);
                g2.dispose();
            }
        };
        iconBadge.setOpaque(false);
        iconBadge.setBounds(0, 4, 36, 36);

        JLabel titleLbl = new JLabel("Books Management");
        titleLbl.setFont(new java.awt.Font("Georgia", java.awt.Font.BOLD, 20));
        titleLbl.setForeground(TEXT_H);
        titleLbl.setBounds(46, 4, 320, 26);

        JLabel subLbl = new JLabel("Browse, search, edit and export your library catalog");
        subLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
        subLbl.setForeground(TEXT_MUTED);
        subLbl.setBounds(47, 30, 380, 14);

        leftTop.add(iconBadge);
        leftTop.add(titleLbl);
        leftTop.add(subLbl);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTop.setOpaque(false);

        final JTextField searchField = buildSearchField();
        final JButton exportPDF = buildOutlineButton("Export PDF", ROSE,    makeIcon_PDF());
        final JButton exportCSV = buildOutlineButton("Export CSV", EMERALD, makeIcon_CSV());

        rightTop.add(searchField);
        rightTop.add(exportPDF);
        rightTop.add(exportCSV);

        topBar.add(leftTop,  BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);
        wrapper.add(topBar,  BorderLayout.NORTH);

        // ── Table ────────────────────────────────────────────────────
        JPanel tableCard = buildCard(16);
        tableCard.setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Quantity"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return (c == 0 || c == 3) ? Integer.class : String.class; }
        };

        // ── FIX: table is now a field ─────────────────────────────────
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
        table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(241, 242, 248));
        table.setSelectionBackground(ROW_SELECT);
        table.setSelectionForeground(INDIGO);
        // ── FIX: keep focusable so selection model works properly ─────
        table.setFocusable(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        // ── FIX: single row selection ─────────────────────────────────
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom header renderer
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                final String text = (v != null) ? v.toString() : "";
                JLabel lbl = new JLabel(text) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        GradientPaint gp = new GradientPaint(0, 0, new Color(248, 247, 255), 0, getHeight(), new Color(241, 239, 254));
                        g2.setPaint(gp);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        g2.setColor(INDIGO_MED);
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                lbl.setForeground(INDIGO);
                lbl.setBorder(new EmptyBorder(12, 14, 12, 14));
                lbl.setOpaque(false);
                return lbl;
            }
        });
        table.getTableHeader().setPreferredSize(new Dimension(0, 44));
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(360);
        table.getColumnModel().getColumn(2).setPreferredWidth(240);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);

        // ── FIX: removed separate mouseListener; table selection model handles it ──

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(BG_CARD);
        tableCard.add(scroll, BorderLayout.CENTER);

        // ── Bottom Bar ───────────────────────────────────────────────
        JPanel bottomBar = buildCard(14);
        bottomBar.setLayout(new BorderLayout());
        bottomBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        bottomBar.setPreferredSize(new Dimension(0, 62));

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionBtns.setOpaque(false);

        final JButton editBtn   = buildFillButton("Edit Book",   INDIGO, makeIcon_Edit());
        final JButton deleteBtn = buildFillButton("Delete Book", ROSE,   makeIcon_Delete());

        actionBtns.add(editBtn);
        actionBtns.add(deleteBtn);

        JPanel pagination = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pagination.setOpaque(false);

        final JLabel pageInfoLbl = new JLabel("Page 1");
        pageInfoLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        pageInfoLbl.setForeground(TEXT_MUTED);

        final JButton prevBtn = buildOutlinedPagBtn("← Prev");
        final JButton nextBtn = buildOutlinedPagBtn("Next →");

        pagination.add(prevBtn);
        pagination.add(pageInfoLbl);
        pagination.add(nextBtn);

        bottomBar.add(actionBtns, BorderLayout.WEST);
        bottomBar.add(pagination, BorderLayout.EAST);

        JPanel centerStack = new JPanel(new BorderLayout(0, 14));
        centerStack.setOpaque(false);
        centerStack.add(tableCard, BorderLayout.CENTER);
        centerStack.add(bottomBar, BorderLayout.SOUTH);
        wrapper.add(centerStack, BorderLayout.CENTER);

        // ── Load initial data ────────────────────────────────────────
        loadBooks("", 0);
        pageInfoLbl.setText("Page 1");

        // ── Listeners ────────────────────────────────────────────────

        searchField.getDocument().addDocumentListener(new DocumentListener() {

            private void handle() {
                // ✅ Only run when user is actually typing (field focused)
                if (!searchField.isFocusOwner()) return;

                String text = searchField.getText().trim();

                // ✅ Ignore placeholder text
                if (text.equals(SEARCH_HINT)) return;

                page = 0;
                loadBooks(text, page);
                pageInfoLbl.setText("Page 1");
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

        nextBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int nextPage = page + 1;
                if (nextPage * limit < totalRecords) {
                    page = nextPage;
                    loadBooks(getSearchQuery(searchField), page);
                    pageInfoLbl.setText("Page " + (page + 1));
                }
            }
        });

        prevBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (page > 0) {
                    page--;
                    loadBooks(getSearchQuery(searchField), page);
                    pageInfoLbl.setText("Page " + (page + 1));
                }
            }
        });

        // ── FIX: Edit — read selected row directly from table ─────────
        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int viewRow = table.getSelectedRow(); // ← always fresh from selection model
                if (viewRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Please select a book to edit.");
                    return;
                }

                int modelRow = table.convertRowIndexToModel(viewRow);

                int    id     = (Integer) model.getValueAt(modelRow, 0);
                String ttVal  = (String)  model.getValueAt(modelRow, 1);
                String auVal  = (String)  model.getValueAt(modelRow, 2);
                int    qtyVal = (Integer) model.getValueAt(modelRow, 3);

                JTextField tf = new JTextField(ttVal);
                JTextField af = new JTextField(auVal);
                JTextField qf = new JTextField(String.valueOf(qtyVal));

                Object[] fields = {"Title:", tf, "Author:", af, "Quantity:", qf};
                int opt = JOptionPane.showConfirmDialog(frame, fields, "Edit Book", JOptionPane.OK_CANCEL_OPTION);
                if (opt == JOptionPane.OK_OPTION) {
                    try {
                        String qtyText = qf.getText().trim();
                        if (tf.getText().trim().isEmpty() || af.getText().trim().isEmpty() || qtyText.isEmpty()) {
                            JOptionPane.showMessageDialog(frame, "All fields are required.");
                            return;
                        }
                        int newQty = Integer.parseInt(qtyText);
                        Connection con = DBConnection.getConnection();
                        PreparedStatement ps = con.prepareStatement(
                            "UPDATE books SET title=?, author=?, quantity=? WHERE id=? AND admin_id=?");
                        ps.setString(1, tf.getText().trim());
                        ps.setString(2, af.getText().trim());
                        ps.setInt(3, newQty);
                        ps.setInt(4, id);
                        ps.setInt(5, UserSession.adminId);
                        ps.executeUpdate();
                        loadBooks(getSearchQuery(searchField), page);
                        showToast(frame, "Book updated successfully.", true);
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(frame, "Quantity must be a valid number.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showToast(frame, "Error updating book: " + ex.getMessage(), false);
                    }
                }
            }
        });

        // ── FIX: Delete — read selected row directly from table ───────
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int viewRow = table.getSelectedRow(); // ← always fresh from selection model
                if (viewRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Please select a book to delete.");
                    return;
                }

                int modelRow  = table.convertRowIndexToModel(viewRow);
                int    id        = (Integer) model.getValueAt(modelRow, 0);
                String bookTitle = (String)  model.getValueAt(modelRow, 1);

                int confirm = JOptionPane.showConfirmDialog(frame,
                    "Delete \"" + bookTitle + "\"?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        Connection con = DBConnection.getConnection();
                        PreparedStatement ps = con.prepareStatement(
                            "DELETE FROM books WHERE id=? AND admin_id=?");
                        ps.setInt(1, id);
                        ps.setInt(2, UserSession.adminId);
                        ps.executeUpdate();
                        // If last item on page, go back one page
                        if (model.getRowCount() == 1 && page > 0) page--;
                        loadBooks(getSearchQuery(searchField), page);
                        showToast(frame, "Book deleted.", true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showToast(frame, "Error deleting book: " + ex.getMessage(), false);
                    }
                }
            }
        });

        exportPDF.addActionListener(e -> {
            try {
                Connection con = DBConnection.getConnection();
                String query = "SELECT * FROM books WHERE admin_id=?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, UserSession.adminId);
                ResultSet rs = pst.executeQuery();

                Document doc = new Document(PageSize.A4, 36, 36, 54, 36);
                PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("Books_Report.pdf"));
                doc.open();

                Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(70, 130, 200));
                Paragraph title = new Paragraph("LIBRARY BOOKS REPORT", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                doc.add(title);

                Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY);
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
                java.util.Date currentDate = new java.util.Date();
                Paragraph dateTime = new Paragraph("Generated on: " + sdf.format(currentDate), dateFont);
                dateTime.setAlignment(Element.ALIGN_RIGHT);
                dateTime.setSpacingAfter(15);
                doc.add(dateTime);

                Paragraph line = new Paragraph("__________________________________________________");
                line.setAlignment(Element.ALIGN_CENTER);
                line.setSpacingAfter(10);
                doc.add(line);
                doc.add(new Paragraph(" "));

                PdfPTable pdftable = new PdfPTable(4);
                pdftable.setWidthPercentage(100);
                pdftable.setSpacingBefore(15);
                pdftable.setSpacingAfter(15);
                try { pdftable.setWidths(new float[]{0.8f, 3.5f, 3f, 1.2f}); } catch (Exception ex) {}

                Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
                String[] headers = {"ID", "Title", "Author", "Quantity"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Paragraph(h, headFont));
                    cell.setBackgroundColor(new BaseColor(52, 152, 219));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setPadding(10);
                    cell.setBorderColor(BaseColor.WHITE);
                    cell.setBorderWidth(1);
                    pdftable.addCell(cell);
                }

                Font bodyFont     = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
                Font quantityFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, new BaseColor(46, 204, 113));
                boolean hasData = false;
                int rowCount = 0;

                while (rs.next()) {
                    hasData = true;
                    rowCount++;
                    BaseColor rowColor = (rowCount % 2 == 0) ? new BaseColor(245, 245, 245) : BaseColor.WHITE;

                    PdfPCell idCell = new PdfPCell(new Paragraph(String.valueOf(rs.getInt("id")), bodyFont));
                    idCell.setBackgroundColor(rowColor);
                    idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    idCell.setPadding(8);
                    idCell.setBorderColor(BaseColor.LIGHT_GRAY);
                    pdftable.addCell(idCell);

                    PdfPCell titleCell = new PdfPCell(new Paragraph(rs.getString("title"), bodyFont));
                    titleCell.setBackgroundColor(rowColor);
                    titleCell.setPadding(8);
                    titleCell.setBorderColor(BaseColor.LIGHT_GRAY);
                    pdftable.addCell(titleCell);

                    PdfPCell authorCell = new PdfPCell(new Paragraph(rs.getString("author"), bodyFont));
                    authorCell.setBackgroundColor(rowColor);
                    authorCell.setPadding(8);
                    authorCell.setBorderColor(BaseColor.LIGHT_GRAY);
                    pdftable.addCell(authorCell);

                    int quantity = rs.getInt("quantity");
                    PdfPCell qtyCell;
                    if (quantity < 3 && quantity > 0) {
                        qtyCell = new PdfPCell(new Paragraph(String.valueOf(quantity) + " (Low)",
                            new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, new BaseColor(241, 196, 15))));
                        qtyCell.setBackgroundColor(new BaseColor(255, 243, 205));
                    } else if (quantity == 0) {
                        qtyCell = new PdfPCell(new Paragraph("OUT OF STOCK",
                            new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, new BaseColor(231, 76, 60))));
                        qtyCell.setBackgroundColor(new BaseColor(255, 225, 225));
                    } else {
                        qtyCell = new PdfPCell(new Paragraph(String.valueOf(quantity), quantityFont));
                        qtyCell.setBackgroundColor(rowColor);
                    }
                    qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    qtyCell.setPadding(8);
                    qtyCell.setBorderColor(BaseColor.LIGHT_GRAY);
                    pdftable.addCell(qtyCell);
                }

                if (!hasData) {
                    PdfPCell noDataCell = new PdfPCell(new Paragraph("No records found in the library",
                        new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.GRAY)));
                    noDataCell.setColspan(4);
                    noDataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    noDataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    noDataCell.setPadding(20);
                    noDataCell.setBackgroundColor(new BaseColor(248, 249, 250));
                    pdftable.addCell(noDataCell);
                } else {
                    PdfPCell summaryLabel = new PdfPCell(new Paragraph("Total Books:",
                        new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)));
                    summaryLabel.setColspan(3);
                    summaryLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    summaryLabel.setPadding(8);
                    summaryLabel.setBackgroundColor(new BaseColor(230, 244, 255));
                    pdftable.addCell(summaryLabel);

                    PdfPCell totalCount = new PdfPCell(new Paragraph(String.valueOf(rowCount),
                        new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, new BaseColor(52, 152, 219))));
                    totalCount.setHorizontalAlignment(Element.ALIGN_CENTER);
                    totalCount.setPadding(8);
                    totalCount.setBackgroundColor(new BaseColor(230, 244, 255));
                    pdftable.addCell(totalCount);
                }

                doc.add(pdftable);
                doc.add(new Paragraph(" "));

                Font footerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);
                Paragraph footer = new Paragraph("Generated by Library Management System", footerFont);
                footer.setAlignment(Element.ALIGN_CENTER);
                footer.setSpacingBefore(20);
                doc.add(footer);

                PdfContentByte cb = writer.getDirectContent();
                cb.setColorStroke(BaseColor.LIGHT_GRAY);
                cb.setLineWidth(1);
                cb.rectangle(30, 30, doc.getPageSize().getWidth() - 60, doc.getPageSize().getHeight() - 60);
                cb.stroke();

                doc.close();
                showToast(frame, "PDF Exported Successfully!", true);

            } catch (Exception ex) {
                ex.printStackTrace();
                showToast(frame, "Error generating PDF: " + ex.getMessage(), false);
            }
        });

        exportCSV.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    SimpleDateFormat fileSdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String timestamp = fileSdf.format(new java.util.Date());
                    String fileName = "Books_Report_" + timestamp + ".csv";

                    FileWriter w = new FileWriter(fileName);

                    Connection con = DBConnection.getConnection();
                    String query = "SELECT * FROM books WHERE admin_id=?";
                    PreparedStatement pst = con.prepareStatement(query);
                    pst.setInt(1, UserSession.adminId);
                    ResultSet rs = pst.executeQuery();

                    w.append("# Library Books Report\n");
                    w.append("# Generated on: ")
                     .append(new SimpleDateFormat("dd MMMM yyyy HH:mm:ss").format(new java.util.Date()))
                     .append("\n");
                    w.append("#\n");
                    w.append("ID,Title,Author,Quantity,Status\n");

                    boolean hasData = false;
                    int rowCount = 0, totalQuantity = 0, lowStockCount = 0, outOfStockCount = 0;

                    while (rs.next()) {
                        hasData = true;
                        rowCount++;

                        String id     = String.valueOf(rs.getInt("id"));
                        String ttl    = rs.getString("title");
                        String auth   = rs.getString("author");
                        int    qty    = rs.getInt("quantity");
                        totalQuantity += qty;

                        String status;
                        if (qty <= 0)      { status = "OUT OF STOCK"; outOfStockCount++; }
                        else if (qty < 3)  { status = "LOW STOCK";    lowStockCount++;   }
                        else               { status = "AVAILABLE"; }

                        ttl  = ttl.contains(",")  ? "\"" + ttl  + "\"" : ttl;
                        auth = auth.contains(",") ? "\"" + auth + "\"" : auth;

                        w.append(id).append(",")
                         .append(ttl).append(",")
                         .append(auth).append(",")
                         .append(String.valueOf(qty)).append(",")
                         .append(status).append("\n");
                    }

                    if (!hasData) {
                        w.append("\n# No records found in the library\n");
                    } else {
                        w.append("\n# SUMMARY\n");
                        w.append("# Total Books (Records): ").append(String.valueOf(rowCount)).append("\n");
                        w.append("# Total Quantity (Copies): ").append(String.valueOf(totalQuantity)).append("\n");
                        w.append("# Low Stock Items (<3 copies): ").append(String.valueOf(lowStockCount)).append("\n");
                        w.append("# Out of Stock Items: ").append(String.valueOf(outOfStockCount)).append("\n");
                        w.append("#\n# Report generated by Library Management System\n");
                    }

                    rs.close(); pst.close(); con.close();
                    w.flush(); w.close();

                    showToast(frame, "CSV exported: " + fileName + " (" + rowCount + " records)", true);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    showToast(frame, "Error exporting CSV: " + ex.getMessage(), false);
                }
            }
        });

        frame.setVisible(true);
    }

    // ── Search placeholder constant ──────────────────────────────────
    private static final String SEARCH_HINT = "  Search by title...";

    // ── Data loading ──────────────────────────────────────────────────
    private void loadBooks(String keyword, int pg) {
        try {
            model.setRowCount(0);
            Connection con = DBConnection.getConnection();

            PreparedStatement countPs;
            if (keyword == null || keyword.trim().isEmpty()) {
                countPs = con.prepareStatement("SELECT COUNT(*) FROM books WHERE admin_id=?");
                countPs.setInt(1, UserSession.adminId);
            } else {
                countPs = con.prepareStatement("SELECT COUNT(*) FROM books WHERE admin_id=? AND title LIKE ?");
                countPs.setInt(1, UserSession.adminId);
                countPs.setString(2, "%" + keyword + "%");
            }
            ResultSet countRs = countPs.executeQuery();
            if (countRs.next()) totalRecords = countRs.getInt(1);

            int maxPage = (totalRecords > 0) ? (int) Math.ceil((double) totalRecords / limit) - 1 : 0;
            if (pg < 0)       pg = 0;
            else if (pg > maxPage) pg = maxPage;
            page = pg;

            PreparedStatement ps;
            if (keyword == null || keyword.trim().isEmpty()) {
                ps = con.prepareStatement("SELECT * FROM books WHERE admin_id=? LIMIT ? OFFSET ?");
                ps.setInt(1, UserSession.adminId);
                ps.setInt(2, limit);
                ps.setInt(3, pg * limit);
            } else {
                ps = con.prepareStatement("SELECT * FROM books WHERE admin_id=? AND title LIKE ? LIMIT ? OFFSET ?");
                ps.setInt(1, UserSession.adminId);
                ps.setString(2, "%" + keyword + "%");
                ps.setInt(3, limit);
                ps.setInt(4, pg * limit);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4)});

        } catch (Exception e) { e.printStackTrace(); }
    }

    private String getSearchQuery(JTextField f) {
        String t = f.getText().trim();
        return t.equals(SEARCH_HINT) ? "" : t;
    }

    // ── Card panel ───────────────────────────────────────────────────
    private JPanel buildCard(final int radius) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
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

    // ── Search field ─────────────────────────────────────────────────
    private JTextField buildSearchField() {
        JTextField f = new JTextField(SEARCH_HINT) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean focused = hasFocus();
                g2.setColor(focused ? Color.WHITE : new Color(249, 249, 255));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(focused ? INDIGO : BORDER);
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
        f.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        f.setForeground(TEXT_SUBTLE);
        f.setCaretColor(INDIGO);
        f.setPreferredSize(new Dimension(220, 38));
        return f;
    }

    // ── FIX: recolor helper — tints a BufferedImage ───────────────────
    private Image recolor(Image src, Color tint) {
        if (src == null) return null;
        int w = src.getWidth(null), h = src.getHeight(null);
        if (w <= 0 || h <= 0) return src;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.setComposite(AlphaComposite.SrcIn);
        g2.setColor(tint);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
        return out;
    }

    // ── Outlined button (Export PDF / CSV) ───────────────────────────
    private JButton buildOutlineButton(final String label, final Color accent, final Image icon) {
        final Color tint = new Color(
            Math.min(255, 220 + (accent.getRed()   / 10)),
            Math.min(255, 220 + (accent.getGreen() / 10)),
            Math.min(255, 220 + (accent.getBlue()  / 10))
        );
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover();
                boolean p = getModel().isPressed();
                g2.setColor((h || p) ? tint : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                int alpha = (h || p) ? 200 : 120;
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), alpha));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                if (icon != null) {
                    Image tinted = recolor(icon, accent);
                    if (tinted != null)
                        g2.drawImage(tinted, 10, (getHeight() - 16) / 2, 16, 16, null);
                }
                g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                g2.setColor(accent);
                FontMetrics fm = g2.getFontMetrics();
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(label, 32, ty);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 38));
        return btn;
    }

    // ── Gradient fill button (Edit / Delete) ─────────────────────────
    private JButton buildFillButton(final String label, final Color accent, final Image icon) {
        final Color lighter = new Color(
            Math.min(255, accent.getRed()   + 40),
            Math.min(255, accent.getGreen() + 40),
            Math.min(255, accent.getBlue()  + 40)
        );
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover();
                boolean p = getModel().isPressed();
                Color c1 = p ? accent.darker() : (h ? lighter : accent);
                Color c2 = p ? accent          : (h ? accent  : lighter);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (!p) {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight() / 2, 10, 10);
                }
                if (icon != null) {
                    Image tinted = recolor(icon, Color.WHITE);
                    if (tinted != null)
                        g2.drawImage(tinted, 10, (getHeight() - 16) / 2, 16, 16, null);
                }
                g2.setColor(Color.WHITE);
                g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(label, 32, ty);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 38));
        return btn;
    }

    // ── Pagination button ────────────────────────────────────────────
    private JButton buildOutlinedPagBtn(final String label) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean h = getModel().isRollover();
                g2.setColor(h ? INDIGO_LIGHT : BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(h ? INDIGO : BORDER);
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
                g2.setColor(h ? INDIGO : TEXT_BODY);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth()  - fm.stringWidth(label)) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(label, tx, ty);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(88, 36));
        return btn;
    }

    // ── Toast notification ───────────────────────────────────────────
    private void showToast(final JFrame frame, final String msg, final boolean success) {
        final JWindow toast = new JWindow(frame);
        final Color bg = success ? EMERALD : ROSE;
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 18, 10, 18));
        JLabel lbl = new JLabel((success ? "✓  " : "✕  ") + msg);
        lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        p.add(lbl);
        toast.add(p);
        toast.pack();
        toast.setLocation(
            frame.getX() + (frame.getWidth()  - toast.getWidth())  / 2,
            frame.getY() +  frame.getHeight() - 90
        );
        toast.setVisible(true);
        Timer t = new Timer(2400, new ActionListener() {
            public void actionPerformed(ActionEvent e) { toast.dispose(); }
        });
        t.setRepeats(false);
        t.start();
    }

    // ── Icons ────────────────────────────────────────────────────────

    private Image makeIcon_Edit() {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int[] px = {3, 13, 16, 6};
        int[] py = {13, 2,  5, 16};
        g.drawPolygon(px, py, 4);
        g.drawLine(3, 13, 2, 18);
        g.drawLine(6, 16, 2, 18);
        g.drawLine(10, 4, 14, 8);
        g.dispose();
        return img;
    }

    private Image makeIcon_Delete() {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawRoundRect(3, 5, 14, 13, 3, 3);
        g.drawLine(1, 5, 19, 5);
        g.drawRoundRect(6, 2, 8, 4, 2, 2);
        g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(7,  9, 7,  14);
        g.drawLine(10, 9, 10, 14);
        g.drawLine(13, 9, 13, 14);
        g.dispose();
        return img;
    }

    private Image makeIcon_PDF() {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawRoundRect(2, 1, 12, 17, 3, 3);
        g.drawLine(10, 1, 14, 5);
        g.drawLine(14, 5, 14, 9);
        g.setStroke(new BasicStroke(1.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(4, 8,  10, 8);
        g.drawLine(4, 11, 10, 11);
        g.drawLine(4, 14, 8,  14);
        g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(16, 10, 16, 17);
        int[] ax = {13, 16, 19};
        int[] ay = {14, 17, 14};
        g.fillPolygon(ax, ay, 3);
        g.dispose();
        return img;
    }

    // ── FIX: completed makeIcon_CSV ───────────────────────────────────
    private Image makeIcon_CSV() {
        BufferedImage img = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Document outline
        g.drawRoundRect(2, 1, 12, 17, 3, 3);
        // Fold corner
        g.drawLine(10, 1, 14, 5);
        g.drawLine(14, 5, 14, 9);
        // Grid lines (CSV look)
        g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(4, 7,  10, 7);   // row 1
        g.drawLine(4, 10, 10, 10);  // row 2
        g.drawLine(4, 13, 10, 13);  // row 3
        g.drawLine(7, 7,  7,  15);  // vertical divider
        // Download arrow
        g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(16, 10, 16, 17);
        int[] ax = {13, 16, 19};
        int[] ay = {14, 17, 14};
        g.fillPolygon(ax, ay, 3);
        g.dispose();
        return img;
    }
}