package com.mycompany.sis.auth;

import com.mycompany.sis.utill.Session;
import com.mycompany.sis.admin.AdminDashboard;
import com.mycompany.sis.db.DBConnection;
import com.mycompany.sis.student.StudentDashboard;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;

    public LoginFrame() {

        setTitle("Student Information System - Login");
        setSize(420, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Student Information System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        cmbRole = new JComboBox<>(new String[]{"Admin", "Student"});

        JButton btnLogin = new JButton("Login");

        c.gridx = 0; c.gridy = 0;
        formPanel.add(new JLabel("Username / Student ID"), c);
        c.gridx = 1;
        formPanel.add(txtUsername, c);

        c.gridx = 0; c.gridy = 1;
        formPanel.add(new JLabel("Password"), c);
        c.gridx = 1;
        formPanel.add(txtPassword, c);

        c.gridx = 0; c.gridy = 2;
        formPanel.add(new JLabel("Role"), c);
        c.gridx = 1;
        formPanel.add(cmbRole, c);

        c.gridx = 1; c.gridy = 3;
        c.anchor = GridBagConstraints.EAST;
        formPanel.add(btnLogin, c);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        btnLogin.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {

        String username = txtUsername.getText().trim(); // Student ID
        String password = new String(txtPassword.getPassword());
        String role = cmbRole.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter ID and password");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            // ================= ADMIN LOGIN =================
            if (role.equals("Admin")) {

                String sql = "SELECT * FROM admin WHERE username=? AND password=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    dispose();
                    new AdminDashboard().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid admin credentials");
                }

            }

            // ================= STUDENT LOGIN =================
            else {

                String sql = "SELECT student_id FROM students WHERE student_id=? AND password=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    String studentId = rs.getString("student_id");

                    //  SET SESSION
                    Session.setStudent(studentId);

                    //  OPEN DASHBOARD
                    dispose();
                    new StudentDashboard(studentId).setVisible(true);

                } else {
                    JOptionPane.showMessageDialog(this, "Invalid student credentials");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
