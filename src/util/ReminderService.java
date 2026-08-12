package util;

import db.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * ReminderService — automatically checks for:
 *
 *   1. DUE TODAY        — books due today (sends "Due Today" reminder)
 *   2. DUE TOMORROW     — books due tomorrow (sends "Due Tomorrow" reminder)
 *   3. DUE IN 3 DAYS    — early warning reminder
 *   4. OVERDUE (1 day+) — books already past due date (sends overdue + fine notice)
 *
 * Called from DashboardUI every 24 hours via java.util.Timer.
 * Can also be triggered manually for testing.
 *
 * REQUIRED TABLE: transactions
 *   Columns used: book_title, member_name, member_id, due_date, status, fine
 *   Plus: members table for email lookup
 */
public class ReminderService {

    // ── Fine rate (must match ReturnBookUI) ──────────────────────────
    private static final int FINE_PER_DAY = 2;

    // ── Called from DashboardUI timer every 24 hours ──────────────────
    public static void checkDueReminders() {

        System.out.println("[ReminderService] Running reminder check at: " + LocalDate.now());

        try {
            sendDueTodayReminders();
            sendDueTomorrowReminders();
            sendDueIn3DaysReminders();
            sendOverdueReminders();
            System.out.println("[ReminderService] All reminders processed.");
        } catch (Exception e) {
            System.err.println("[ReminderService] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  1. DUE TODAY
    // ════════════════════════════════════════════════════════════════
    private static void sendDueTodayReminders() throws Exception {

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
            "SELECT t.book_title, t.member_name, t.due_date, m.email " +
            "FROM transactions t " +
            "JOIN members m ON t.member_id = m.id " +
            "WHERE t.status = 'Issued' AND t.due_date = CURDATE()");

        ResultSet rs = ps.executeQuery();
        int count = 0;

        while (rs.next()) {
            String bookTitle  = rs.getString("book_title");
            String memberName = rs.getString("member_name");
            String dueDate    = rs.getString("due_date");
            String email      = rs.getString("email");

            String subject = "\u26a0\ufe0f Book Due Today — " + bookTitle;
            String body    = buildDueTodayBody(memberName, bookTitle, dueDate);

            try {
                EmailSender.sendEmail(email, subject, body);
                count++;
            } catch (Exception ex) {
                System.err.println("[ReminderService] Failed to send due-today email to " + email + ": " + ex.getMessage());
            }
        }

        rs.close(); ps.close(); con.close();
        System.out.println("[ReminderService] Due-today reminders sent: " + count);
    }

    // ════════════════════════════════════════════════════════════════
    //  2. DUE TOMORROW
    // ════════════════════════════════════════════════════════════════
    private static void sendDueTomorrowReminders() throws Exception {

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
            "SELECT t.book_title, t.member_name, t.due_date, m.email " +
            "FROM transactions t " +
            "JOIN members m ON t.member_id = m.id " +
            "WHERE t.status = 'Issued' AND t.due_date = ?");
        ps.setDate(1, Date.valueOf(tomorrow));

        ResultSet rs = ps.executeQuery();
        int count = 0;

        while (rs.next()) {
            String bookTitle  = rs.getString("book_title");
            String memberName = rs.getString("member_name");
            String dueDate    = rs.getString("due_date");
            String email      = rs.getString("email");

            String subject = "\ud83d\udcd6 Reminder: Book Due Tomorrow — " + bookTitle;
            String body    = buildDueTomorrowBody(memberName, bookTitle, dueDate);

            try {
                EmailSender.sendEmail(email, subject, body);
                count++;
            } catch (Exception ex) {
                System.err.println("[ReminderService] Failed to send due-tomorrow email to " + email + ": " + ex.getMessage());
            }
        }

        rs.close(); ps.close(); con.close();
        System.out.println("[ReminderService] Due-tomorrow reminders sent: " + count);
    }

    // ════════════════════════════════════════════════════════════════
    //  3. DUE IN 3 DAYS
    // ════════════════════════════════════════════════════════════════
    private static void sendDueIn3DaysReminders() throws Exception {

        LocalDate in3Days = LocalDate.now().plusDays(3);

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
            "SELECT t.book_title, t.member_name, t.due_date, m.email " +
            "FROM transactions t " +
            "JOIN members m ON t.member_id = m.id " +
            "WHERE t.status = 'Issued' AND t.due_date = ?");
        ps.setDate(1, Date.valueOf(in3Days));

        ResultSet rs = ps.executeQuery();
        int count = 0;

        while (rs.next()) {
            String bookTitle  = rs.getString("book_title");
            String memberName = rs.getString("member_name");
            String dueDate    = rs.getString("due_date");
            String email      = rs.getString("email");

            String subject = "\ud83d\udcda Early Reminder: Book Due in 3 Days — " + bookTitle;
            String body    = buildDueIn3DaysBody(memberName, bookTitle, dueDate);

            try {
                EmailSender.sendEmail(email, subject, body);
                count++;
            } catch (Exception ex) {
                System.err.println("[ReminderService] Failed to send 3-day reminder to " + email + ": " + ex.getMessage());
            }
        }

        rs.close(); ps.close(); con.close();
        System.out.println("[ReminderService] 3-day reminders sent: " + count);
    }

    // ════════════════════════════════════════════════════════════════
    //  4. OVERDUE (past due date, not yet returned)
    // ════════════════════════════════════════════════════════════════
    private static void sendOverdueReminders() throws Exception {

        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
            "SELECT t.book_title, t.member_name, t.due_date, m.email " +
            "FROM transactions t " +
            "JOIN members m ON t.member_id = m.id " +
            "WHERE t.status = 'Issued' AND t.due_date < CURDATE()");

        ResultSet rs = ps.executeQuery();
        int count = 0;

        while (rs.next()) {
            String    bookTitle  = rs.getString("book_title");
            String    memberName = rs.getString("member_name");
            LocalDate dueDate    = rs.getDate("due_date").toLocalDate();
            String    email      = rs.getString("email");

            long overdueDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
            int  fine        = (int) overdueDays * FINE_PER_DAY;

            String subject = "\ud83d\udea8 Overdue Book Alert — " + bookTitle +
                             " (" + overdueDays + " day" + (overdueDays > 1 ? "s" : "") + " late)";
            String body    = buildOverdueBody(memberName, bookTitle, dueDate.toString(),
                                              (int) overdueDays, fine);

            try {
                EmailSender.sendEmail(email, subject, body);
                count++;
            } catch (Exception ex) {
                System.err.println("[ReminderService] Failed to send overdue email to " + email + ": " + ex.getMessage());
            }
        }

        rs.close(); ps.close(); con.close();
        System.out.println("[ReminderService] Overdue reminders sent: " + count);
    }

    // ════════════════════════════════════════════════════════════════
    //  EMAIL BODY TEMPLATES
    // ════════════════════════════════════════════════════════════════

    private static String buildDueTodayBody(String name, String book, String due) {
        return "Hello " + name + ",\n\n" +
               "This is a reminder that the following book is due for return TODAY.\n\n" +
               "Book Title : " + book + "\n" +
               "Due Date   : " + due + " (Today)\n\n" +
               "Please return the book to the library today to avoid any late fine charges.\n\n" +
               "Fine Policy: \u20b9" + FINE_PER_DAY + " per day after the due date.\n\n" +
               "Thank you,\n" +
               "LibraryPro Team";
    }

    private static String buildDueTomorrowBody(String name, String book, String due) {
        return "Hello " + name + ",\n\n" +
               "This is a friendly reminder that the following book is due for return TOMORROW.\n\n" +
               "Book Title : " + book + "\n" +
               "Due Date   : " + due + " (Tomorrow)\n\n" +
               "Please ensure you return the book by tomorrow to avoid any late fine charges.\n\n" +
               "Fine Policy: \u20b9" + FINE_PER_DAY + " per day after the due date.\n\n" +
               "Thank you,\n" +
               "LibraryPro Team";
    }

    private static String buildDueIn3DaysBody(String name, String book, String due) {
        return "Hello " + name + ",\n\n" +
               "This is an early reminder that the following book is due for return in 3 days.\n\n" +
               "Book Title : " + book + "\n" +
               "Due Date   : " + due + "\n\n" +
               "You have 3 days remaining. Please plan to return the book on time.\n\n" +
               "Fine Policy: \u20b9" + FINE_PER_DAY + " per day after the due date.\n\n" +
               "Thank you,\n" +
               "LibraryPro Team";
    }

    private static String buildOverdueBody(String name, String book, String due,
                                            int overdueDays, int fine) {
        return "Hello " + name + ",\n\n" +
               "IMPORTANT: The following book is OVERDUE and has not been returned.\n\n" +
               "Book Title   : " + book + "\n" +
               "Due Date     : " + due + "\n" +
               "Days Overdue : " + overdueDays + " day" + (overdueDays > 1 ? "s" : "") + "\n" +
               "Fine Accrued : \u20b9" + fine + " (\u20b9" + FINE_PER_DAY + " \u00d7 " + overdueDays + " days)\n\n" +
               "Please return the book to the library immediately.\n" +
               "The fine will increase by \u20b9" + FINE_PER_DAY + " for every additional day.\n\n" +
               "If you have already returned the book, please ignore this message.\n\n" +
               "Thank you,\n" +
               "LibraryPro Team";
    }

    // ════════════════════════════════════════════════════════════════
    //  MANUAL TEST METHOD — run this main() to test immediately
    // ════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        System.out.println("=== ReminderService Manual Test ===");
        checkDueReminders();
        System.out.println("=== Done ===");
    }
}