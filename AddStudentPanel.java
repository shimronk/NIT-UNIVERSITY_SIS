package com.mycompany.sis.admin;

import com.mycompany.sis.db.DBConnection;
import com.mycompany.sis.auth.model.Student;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class AddStudentPanel extends JPanel {

    // ===== Fields =====
    private JTextField txtId = new JTextField(15);
    private JTextField txtName = new JTextField(15);
    private JTextField txtPhone = new JTextField(15);

    private JComboBox<String> cmbCourse =
            new JComboBox<>(new String[]{"IT", "CS", "SE", "DS","TM"});

    private JDateChooser dobChooser = new JDateChooser();

    private JTextField txtAge = new JTextField(15);
    private JTextField txtEmail = new JTextField(15);
    private JTextField txtPassword = new JTextField(15);

    private JButton btnAdd = new JButton("Add Student");

    public AddStudentPanel() {

        setLayout(new GridLayout(0, 2, 10, 10));

        dobChooser.setDateFormatString("yyyy-MM-dd");

        txtAge.setEditable(false);
        txtEmail.setEditable(false);
        txtPassword.setEditable(false);

        // ===== UI =====
        add(new JLabel("Student ID"));
        add(txtId);

        add(new JLabel("Student Name"));
        add(txtName);

        add(new JLabel("Date of Birth"));
        add(dobChooser);

        add(new JLabel("Course"));
        add(cmbCourse);

        add(new JLabel("Phone"));
        add(txtPhone);

        add(new JLabel("Age (Auto)"));
        add(txtAge);

        add(new JLabel("Email (Auto)"));
        add(txtEmail);

        add(new JLabel("Password (Auto)"));
        add(txtPassword);

        add(new JLabel(""));
        add(btnAdd);

        // ===== Events =====
        btnAdd.addActionListener(e -> saveStudent());
        dobChooser.addPropertyChangeListener(e -> generateAutoFields());
        txtName.addCaretListener(e -> generateAutoFields());
        txtId.addCaretListener(e -> generateAutoFields());
    }

    // ===== Auto Generation =====
    private void generateAutoFields() {

        if (txtId.getText().isEmpty()
                || txtName.getText().isEmpty()
                || dobChooser.getDate() == null) {
            return;
        }

        LocalDate dob = dobChooser.getDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        int age = Period.between(dob, LocalDate.now()).getYears();
        txtAge.setText(String.valueOf(age));

        String email = txtId.getText().toLowerCase() + "@nituniversity.lk";
        txtEmail.setText(email);

        String[] names = txtName.getText().split(" ");
        StringBuilder initials = new StringBuilder();
        for (String n : names) {
            initials.append(n.charAt(0));
        }

        String password = initials.toString().toUpperCase()
                + dob.getYear() % 10000;
        txtPassword.setText(password);
    }

    // ===== Save Student =====
    private void saveStudent() {

        if (dobChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Please select Date of Birth");
            return;
        }

        try {
            Student s = new Student(
                    txtId.getText(),
                    txtName.getText(),
                    dobChooser.getDate()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate(),
                    cmbCourse.getSelectedItem().toString(),
                    txtPhone.getText()
            );

            Connection con = DBConnection.getConnection();

            String sql = """
                INSERT INTO students
                (student_id, full_name, dob, age, course, phone, email, password, behaviour_score, behaviour_status)
                VALUES (?,?,?,?,?,?,?,?,?,?)
                """;

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, s.getStudentId());
            ps.setString(2, s.getFullName());
            ps.setDate(3, Date.valueOf(s.getDob()));
            ps.setInt(4, s.getAge());
            ps.setString(5, s.getCourse());
            ps.setString(6, s.getPhone());
            ps.setString(7, s.getEmail());
            ps.setString(8, s.getPassword());
            ps.setInt(9, 0);              // default behaviour score
            ps.setString(10, "GOOD");     // default behaviour status

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student Added Successfully");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}
