package com.mycompany.sis.admin;

import com.mycompany.sis.db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewStudentsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtStudentId, txtFullName, txtDob, txtAge, txtCourse, txtPhone, txtSearch;
    private JButton btnSearch, btnUpdate, btnDelete, btnClear;

    public ViewStudentsPanel() {

        setLayout(new BorderLayout(10, 10));

        // ================= SEARCH PANEL =================
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        btnSearch = new JButton("Search");

        searchPanel.add(new JLabel("Search (ID / Name):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        add(searchPanel, BorderLayout.NORTH);

        // ================= TABLE =================
        model = new DefaultTableModel(
                new String[]{"student_id", "full_name", "dob", "age", "course", "phone"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 🔒 read only
            }
        };

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ================= FORM =================
        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));

        txtStudentId = new JTextField();
        txtStudentId.setEditable(false); // PK

        txtFullName = new JTextField();
        txtDob = new JTextField(); // yyyy-MM-dd
        txtAge = new JTextField();
        txtCourse = new JTextField();
        txtPhone = new JTextField();

        form.add(new JLabel("Student ID"));
        form.add(txtStudentId);
        form.add(new JLabel("Full Name"));
        form.add(txtFullName);
        form.add(new JLabel("Date of Birth"));
        form.add(txtDob);
        form.add(new JLabel("Age"));
        form.add(txtAge);
        form.add(new JLabel("Course"));
        form.add(txtCourse);
        form.add(new JLabel("Phone"));
        form.add(txtPhone);

        add(form, BorderLayout.EAST);

        // ================= BUTTONS =================
        JPanel buttons = new JPanel();

        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");

        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        add(buttons, BorderLayout.SOUTH);

        // ================= EVENTS =================
        loadStudents();

        btnSearch.addActionListener(e -> searchStudents());
        table.getSelectionModel().addListSelectionListener(e -> loadSelectedStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearForm());
    }

    // ================= LOAD ALL =================
    private void loadStudents() {

        model.setRowCount(0);

        String sql = "SELECT student_id, full_name, dob, age, course, phone FROM students";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("student_id"),
                        rs.getString("full_name"),
                        rs.getDate("dob"),
                        rs.getInt("age"),
                        rs.getString("course"),
                        rs.getString("phone")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= SEARCH =================
    private void searchStudents() {

        model.setRowCount(0);
        String key = txtSearch.getText().trim();

        String sql = """
                SELECT student_id, full_name, dob, age, course, phone
                FROM students
                WHERE student_id LIKE ? OR full_name LIKE ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, "%" + key + "%");
            pst.setString(2, "%" + key + "%");

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("student_id"),
                        rs.getString("full_name"),
                        rs.getDate("dob"),
                        rs.getInt("age"),
                        rs.getString("course"),
                        rs.getString("phone")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= LOAD SELECTED =================
    private void loadSelectedStudent() {

        int row = table.getSelectedRow();
        if (row == -1) return;

        txtStudentId.setText(model.getValueAt(row, 0).toString());
        txtFullName.setText(model.getValueAt(row, 1).toString());
        txtDob.setText(model.getValueAt(row, 2).toString());
        txtAge.setText(model.getValueAt(row, 3).toString());
        txtCourse.setText(model.getValueAt(row, 4).toString());
        txtPhone.setText(model.getValueAt(row, 5).toString());
    }

    // ================= UPDATE =================
    private void updateStudent() {

        if (txtStudentId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a student first");
            return;
        }

        String sql = """
                UPDATE students
                SET full_name=?, dob=?, age=?, course=?, phone=?
                WHERE student_id=?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, txtFullName.getText());
            pst.setDate(2, Date.valueOf(txtDob.getText()));
            pst.setInt(3, Integer.parseInt(txtAge.getText()));
            pst.setString(4, txtCourse.getText());
            pst.setString(5, txtPhone.getText());
            pst.setString(6, txtStudentId.getText());

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student updated");

            loadStudents();
            clearForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= DELETE =================
    private void deleteStudent() {

        if (txtStudentId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a student first");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this student?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM students WHERE student_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, txtStudentId.getText());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student deleted");

            loadStudents();
            clearForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= CLEAR =================
    private void clearForm() {
        txtStudentId.setText("");
        txtFullName.setText("");
        txtDob.setText("");
        txtAge.setText("");
        txtCourse.setText("");
        txtPhone.setText("");
        txtSearch.setText("");
    }
}
