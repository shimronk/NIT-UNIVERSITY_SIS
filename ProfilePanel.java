package com.mycompany.sis.student;

import com.mycompany.sis.db.DBConnection;
import com.mycompany.sis.utill.Session;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProfilePanel extends JPanel {

    private JLabel lblName, lblEmail, lblCourse, lblPhone, lblBehaviour;

    public ProfilePanel() {
        setLayout(new GridLayout(5, 2, 15, 15));
        setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        add(new JLabel("Full Name:"));
        lblName = new JLabel("-");
        add(lblName);

        add(new JLabel("Email:"));
        lblEmail = new JLabel("-");
        add(lblEmail);

        add(new JLabel("Course:"));
        lblCourse = new JLabel("-");
        add(lblCourse);

        add(new JLabel("Telephone:"));
        lblPhone = new JLabel("-");
        add(lblPhone);

        add(new JLabel("Behaviour Points:"));
        lblBehaviour = new JLabel("-");
        add(lblBehaviour);

        loadProfile();
    }

    private void loadProfile() {
        try (Connection con = DBConnection.getConnection()) {

            String sql = """
                SELECT full_name, email, course, phone, behaviour_score
                FROM students
                WHERE student_id = ?
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, Session.getStudentId());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lblName.setText(rs.getString("full_name"));
                lblEmail.setText(rs.getString("email"));
                lblCourse.setText(rs.getString("course"));
                lblPhone.setText(rs.getString("phone"));
                lblBehaviour.setText(String.valueOf(rs.getInt("behaviour_score")));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Profile Load Error: " + e.getMessage());
            System.out.println("LOGGED STUDENT ID = " + Session.getStudentId());

        }
    }
}
