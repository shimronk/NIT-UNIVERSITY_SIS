package com.mycompany.sis.admin;

import com.mycompany.sis.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AddMarksPanel extends JPanel {

    private JTextField txtStudentId;
    private JTextField txtSubject;
    private JTextField txtMarks;
    private JTable marksTable;

    public AddMarksPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ---------- TOP FORM ----------
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Student Marks"));

        formPanel.add(new JLabel("Student ID:"));
        txtStudentId = new JTextField();
        formPanel.add(txtStudentId);

        formPanel.add(new JLabel("Subject:"));
        txtSubject = new JTextField();
        formPanel.add(txtSubject);

        formPanel.add(new JLabel("Marks:"));
        txtMarks = new JTextField();
        formPanel.add(txtMarks);

        JButton btnAddMarks = new JButton("Add / Update Marks");
        btnAddMarks.addActionListener(e -> addMarksToDatabase());

        formPanel.add(new JLabel());
        formPanel.add(btnAddMarks);

        add(formPanel, BorderLayout.NORTH);

        // ---------- TABLE ----------
        marksTable = new JTable(new DefaultTableModel(
                new Object[]{"Student ID", "Subject", "Marks"}, 0
        ));
        add(new JScrollPane(marksTable), BorderLayout.CENTER);

        // Load existing data
        loadMarksTable();
    }

    // =====================================================
    // ADD OR UPDATE MARKS
    // =====================================================
    private void addMarksToDatabase() {
        String studentId = txtStudentId.getText().trim();
        String subject = txtSubject.getText().trim();
        String marksText = txtMarks.getText().trim();

        if (studentId.isEmpty() || subject.isEmpty() || marksText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        try {
            int marks = Integer.parseInt(marksText);

            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO marks (student_id, subject, marks) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE marks = ?"
            );

            ps.setString(1, studentId);
            ps.setString(2, subject);
            ps.setInt(3, marks);
            ps.setInt(4, marks);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Marks saved successfully");

            clearFields();
            loadMarksTable();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Marks must be a number");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // =====================================================
    // LOAD TABLE DATA
    // =====================================================
    private void loadMarksTable() {
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                    "SELECT student_id, subject, marks FROM marks"
            );

            DefaultTableModel model =
                    (DefaultTableModel) marksTable.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("student_id"),
                        rs.getString("subject"),
                        rs.getInt("marks")
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // =====================================================
    // CLEAR INPUTS
    // =====================================================
    private void clearFields() {
        txtStudentId.setText("");
        txtSubject.setText("");
        txtMarks.setText("");
    }
}
