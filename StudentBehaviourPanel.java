package com.mycompany.sis.student;

import com.mycompany.sis.db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class StudentBehaviourPanel extends JPanel {

    private String studentId;

    private JLabel lblScore = new JLabel("-");
    private JLabel lblStatus = new JLabel("-");
    private JTextArea txtHistory = new JTextArea();

    public StudentBehaviourPanel(String studentId) {
        this.studentId = studentId;
        setLayout(new BorderLayout(10, 10));

        // ===== SUMMARY =====
        JPanel top = new JPanel(new GridLayout(2, 2, 10, 10));
        top.setBorder(BorderFactory.createTitledBorder("Behaviour Summary"));

        top.add(new JLabel("Behaviour Score"));
        top.add(lblScore);
        top.add(new JLabel("Status"));
        top.add(lblStatus);

        // ===== HISTORY =====
        txtHistory.setEditable(false);
        JScrollPane scroll = new JScrollPane(txtHistory);
        scroll.setBorder(BorderFactory.createTitledBorder("Behaviour History"));

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        loadBehaviour();
    }

    private void loadBehaviour() {
        try (Connection con = DBConnection.getConnection()) {

            // ===== LOAD SCORE & STATUS =====
            PreparedStatement ps = con.prepareStatement(
                "SELECT behaviour_score, behaviour_status FROM students WHERE student_id=?"
            );
            ps.setString(1, studentId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lblScore.setText(String.valueOf(rs.getInt("behaviour_score")));
                lblStatus.setText(rs.getString("behaviour_status"));
                updateColor(lblStatus.getText());
            }

            // ===== LOAD HISTORY =====
            PreparedStatement ps2 = con.prepareStatement(
                "SELECT change_value, reason, created_at FROM behaviour_records WHERE student_id=? ORDER BY created_at DESC"
            );
            ps2.setString(1, studentId);

            ResultSet rs2 = ps2.executeQuery();
            StringBuilder sb = new StringBuilder();

            while (rs2.next()) {
                sb.append(rs2.getTimestamp("created_at"))
                  .append(" | ")
                  .append(rs2.getInt("change_value"))
                  .append(" | ")
                  .append(rs2.getString("reason"))
                  .append("\n");
            }

            txtHistory.setText(sb.toString());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void updateColor(String status) {
        switch (status) {
            case "EXCELLENT" -> lblStatus.setForeground(Color.GREEN);
            case "GOOD" -> lblStatus.setForeground(Color.BLUE);
            case "BAD" -> lblStatus.setForeground(Color.ORANGE);
            default -> lblStatus.setForeground(Color.RED);
        }
    }
}
