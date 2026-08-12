package util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * EmailSender — sends emails via Gmail SMTP (TLS port 587)
 *
 * SETUP STEPS:
 * 1. Go to your Gmail account → Google Account → Security
 * 2. Enable 2-Step Verification (required for App Passwords)
 * 3. Go to Security → App passwords → Select app: Mail → Select device: Other
 * 4. Copy the 16-character app password
 * 5. Paste it into APP_PASSWORD below (replace "your_app_password_here")
 * 6. Replace SENDER_EMAIL with your Gmail address
 *
 * REQUIRED JAR: javax.mail (jakarta.mail) — download from:
 *   https://mvnrepository.com/artifact/com.sun.mail/javax.mail
 *   or add to pom.xml:
 *   <dependency>
 *     <groupId>com.sun.mail</groupId>
 *     <artifactId>javax.mail</artifactId>
 *     <version>1.6.2</version>
 *   </dependency>
 */
public class EmailSender {

    // ── CONFIGURE THESE TWO VALUES ────────────────────────────────────
    private static final String SENDER_EMAIL  = "vishalborse8881@gmail.com";
    private static final String APP_PASSWORD  = "mhrtkaadsrttzunv";
    // ─────────────────────────────────────────────────────────────────

    private static final String SMTP_HOST     = "smtp.gmail.com";
    private static final int    SMTP_PORT     = 587;
    private static final String LIBRARY_NAME  = "LibraryPro";

    /**
     * Sends a plain-text email.
     *
     * @param toEmail   recipient email address
     * @param subject   email subject
     * @param body      plain-text email body
     * @throws Exception if sending fails
     */
    public static void sendEmail(String toEmail, String subject, String body) throws Exception {

        if (toEmail == null || toEmail.trim().isEmpty()) {
            System.out.println("[EmailSender] Skipped — no email address for recipient.");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",            SMTP_HOST);
        props.put("mail.smtp.port",            String.valueOf(SMTP_PORT));
        props.put("mail.smtp.ssl.trust",       SMTP_HOST);
        props.put("mail.smtp.connectiontimeout", "8000");
        props.put("mail.smtp.timeout",           "8000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        // Build HTML email for a nicer look
        String htmlBody = buildHtmlEmail(subject, body);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SENDER_EMAIL, LIBRARY_NAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);

        // Send as multipart (plain text fallback + HTML)
        MimeMultipart multipart = new MimeMultipart("alternative");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body, "utf-8");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlBody, "text/html; charset=utf-8");

        multipart.addBodyPart(textPart);
        multipart.addBodyPart(htmlPart);

        message.setContent(multipart);

        Transport.send(message);
        System.out.println("[EmailSender] Email sent to: " + toEmail + " | Subject: " + subject);
    }

    /**
     * Wraps the plain-text body in a clean HTML template.
     */
    private static String buildHtmlEmail(String subject, String body) {
        // Convert line breaks to <br>
        String htmlBody = body
            .replace("&",  "&amp;")
            .replace("<",  "&lt;")
            .replace(">",  "&gt;")
            .replace("\n", "<br>");

        return "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'>" +
            "<style>" +
            "  body { font-family: 'Segoe UI', Arial, sans-serif; background: #f4f5fb; margin:0; padding:0; }" +
            "  .wrapper { max-width:580px; margin:30px auto; background:#fff; border-radius:16px;" +
            "             border:1px solid #e2e8f0; overflow:hidden; box-shadow:0 4px 24px rgba(0,0,0,0.08); }" +
            "  .header  { background:linear-gradient(135deg,#4f46e5,#7c3aed); padding:28px 32px; }" +
            "  .header h1 { color:#fff; margin:0; font-size:20px; font-weight:700; letter-spacing:0.5px; }" +
            "  .header p  { color:rgba(255,255,255,0.75); margin:4px 0 0; font-size:12px; }" +
            "  .body    { padding:28px 32px; color:#374151; font-size:14px; line-height:1.7; }" +
            "  .footer  { background:#f8fafc; padding:16px 32px; border-top:1px solid #e2e8f0;" +
            "             color:#9ca3af; font-size:11px; text-align:center; }" +
            "  .badge   { display:inline-block; background:#fef3c7; color:#d97706; border-radius:6px;" +
            "             padding:4px 12px; font-size:12px; font-weight:700; margin:10px 0; }" +
            "  .badge.red { background:#fff1f2; color:#e11d48; }" +
            "  .badge.green { background:#d1fae5; color:#059669; }" +
            "</style></head><body>" +
            "<div class='wrapper'>" +
            "  <div class='header'>" +
            "    <h1>&#128218; " + LIBRARY_NAME + "</h1>" +
            "    <p>" + subject + "</p>" +
            "  </div>" +
            "  <div class='body'>" + htmlBody + "</div>" +
            "  <div class='footer'>" +
            "    This is an automated message from " + LIBRARY_NAME + ". Please do not reply to this email." +
            "  </div>" +
            "</div></body></html>";
    }
}