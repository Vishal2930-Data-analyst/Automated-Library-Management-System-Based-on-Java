package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import db.DBConnection;
import style.Theme;

public class SignUpUI {

    public SignUpUI() {

        JFrame frame = new JFrame("Create Admin Account");
        frame.setSize(600,500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(Theme.BACKGROUND);

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(400,350));
        card.setLayout(null);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel title = new JLabel("Create Admin Account");
        title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        title.setBounds(70,30,300,30);

        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds(60,90,100,20);

        JTextField username = new JTextField();
        username.setBounds(60,115,280,35);

        JLabel passLabel = new JLabel("Password");
        passLabel.setBounds(60,160,100,20);

        JPasswordField password = new JPasswordField();
        password.setBounds(60,185,280,35);

        JButton createBtn = new JButton("CREATE ACCOUNT");
        createBtn.setBounds(60,250,280,40);
        createBtn.setBackground(Theme.PRIMARY);
        createBtn.setForeground(Color.WHITE);
        createBtn.setFocusPainted(false);

        createBtn.addActionListener(e -> {

            try {
                Connection con = DBConnection.getConnection();

                PreparedStatement check = con.prepareStatement(
                        "SELECT * FROM admin WHERE username=?"
                );
                check.setString(1, username.getText());
                ResultSet rs = check.executeQuery();

                if(rs.next()){
                    JOptionPane.showMessageDialog(frame,
                            "Username already exists!");
                    return;
                }

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO admin VALUES (?,?)"
                );

                ps.setString(1, username.getText());
                ps.setString(2,
                        String.valueOf(password.getPassword()));

                ps.executeUpdate();

                JOptionPane.showMessageDialog(frame,
                        "Account Created Successfully!");

                frame.dispose();

            } catch(Exception ex){
                ex.printStackTrace();
            }
        });

        card.add(title);
        card.add(userLabel);
        card.add(username);
        card.add(passLabel);
        card.add(password);
        card.add(createBtn);

        frame.add(card);
        frame.setVisible(true);
    }
}