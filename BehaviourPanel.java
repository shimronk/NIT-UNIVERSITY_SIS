package com.mycompany.sis.admin;

import com.mycompany.sis.db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BehaviourPanel extends JPanel {

    private JTextField txtStudentId = new JTextField();
    private JTextField txtChange = new JTextField();
    private JTextField txtReason = new JTextField();

    private JLabel lblScore = new JLabel("-");
    private JLabel lblStatus = new JLabel("-");
    private JTextArea txtHistory = new JTextArea();

    public BehaviourPanel() {
        setLayout(new BorderLayout(10, 10));

        // ===== TOP FORM =====
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Behaviour Management"));

        JButton btnLoad = new JButton("Load Student");
        JButton btnApply = new JButton("Apply Change");

        form.add(new JLabel("Student ID"));
        form.add(txtStudentId);

        form.add(new JLabel("Change (+ / -)"));
        form.add(txtChange);

        form.add(new JLabel("Reason"));
        form.add(txtReason);

        form.add(btnLoad);
        form.add(btnApply);

        form.add(new JLabel("Score"));
        form.add(lblScore);

        form.add(new JLabel("Status"));
        form.add(lblStatus);

        // ===== HISTORY =====
        txtHistory.setEditable(false);
        JScrollPane scroll = new JScrollPane(txtHistory);
        scroll.setBorder(BorderFactory.createTitledBorder("Behaviour History"));

        add(form, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // ===== ACTIONS =====
        btnLoad.addActionListener(e -> loadStudent());
        btnApply.addActionListener(e -> applyChange());
    }

    // ================= LOAD STUDENT =================
    private void loadStudent() {
        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "SELECT behaviour_score, behaviour_status FROM students WHERE student_id=?"
            );
            ps.setString(1, txtStudentId.getText());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lblScore.setText(String.valueOf(rs.getInt("behaviour_score")));
                lblStatus.setText(rs.getString("behaviour_status"));
                updateColor(lblStatus.getText());
                loadHistory(); // ✅ NOW THIS EXISTS
            } else {
                JOptionPane.showMessageDialog(this, "Student not found");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ================= APPLY CHANGE =================
    private void applyChange() {
        try (Connection con = DBConnection.getConnection()) {

            int change = Integer.parseInt(txtChange.getText());

            // update score
            PreparedStatement ps1 = con.prepareStatement(
                "UPDATE students SET behaviour_score = behaviour_score + ? WHERE student_id=?"
            );
            ps1.setInt(1, change);
            ps1.setString(2, txtStudentId.getText());
            ps1.executeUpdate();

            // calculate status
            String status = calculateStatus(con);

            PreparedStatement ps2 = con.prepareStatement(
                "UPDATE students SET behaviour_status=? WHERE student_id=?"
            );
            ps2.setString(1, status);
            ps2.setString(2, txtStudentId.getText());
            ps2.executeUpdate();

            // insert history
            PreparedStatement ps3 = con.prepareStatement(
                "INSERT INTO behaviour_records(student_id, change_value, reason) VALUES (?,?,?)"
            );
            ps3.setString(1, txtStudentId.getText());
            ps3.setInt(2, change);
            ps3.setString(3, txtReason.getText());
            ps3.executeUpdate();

            loadStudent();
            txtChange.setText("");
            txtReason.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ================= LOAD HISTORY (FIXED) =================
    private void loadHistory() {
        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "SELECT change_value, reason, created_at FROM behaviour_records WHERE student_id=? ORDER BY created_at DESC"
            );
            ps.setString(1, txtStudentId.getText());

            ResultSet rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder();

            while (rs.next()) {
                sb.append(rs.getTimestamp("created_at"))
                  .append(" | ")
                  .append(rs.getInt("change_value"))
                  .append(" | ")
                  .append(rs.getString("reason"))
                  .append("\n");
            }

            txtHistory.setText(sb.toString());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ================= STATUS RULES =================
    private String calculateStatus(Connection con) throws Exception {

        PreparedStatement ps = con.prepareStatement(
            "SELECT behaviour_score FROM students WHERE student_id=?"
        );
        ps.setString(1, txtStudentId.getText());

        ResultSet rs = ps.executeQuery();
        rs.next();

        int score = rs.getInt("behaviour_score");

        if (score > 75) return "EXCELLENT";
        if (score >= 50) return "GOOD";
        if (score >= 10) return "BAD";
        if (score >= 0) return "READY_TO_SUSPEND";
        return "SUSPENDED";
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
