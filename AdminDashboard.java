package com.mycompany.sis.admin;

import com.mycompany.sis.auth.LoginFrame;
import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private JPanel contentPanel;
    private JButton btnEditStudent;   // ✅ declaration only

    public AdminDashboard() {

        setTitle("Admin Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel menu = new JPanel(new GridLayout(7, 1, 5, 5)); // 7 rows now

        JButton btnAddStudent = new JButton("Add Student");
        JButton btnViewStudents = new JButton("View Students");
        JButton btnAddMarks = new JButton("Add Marks");
        JButton btnBehaviour = new JButton("Behaviour");
        btnEditStudent = new JButton("Edit Student"); // ✅ initialized here
        JButton btnLogout = new JButton("Logout");

        // add buttons to menu
        menu.add(btnAddStudent);
        menu.add(btnViewStudents);
        menu.add(btnEditStudent);
        menu.add(btnAddMarks);
        menu.add(btnBehaviour);
        menu.add(btnLogout);

        add(menu, BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);

        // default panel
        showPanel(new AddStudentPanel());

        // action listeners
        btnAddStudent.addActionListener(e -> showPanel(new AddStudentPanel()));
        btnViewStudents.addActionListener(e -> showPanel(new ViewStudentsPanel()));
       
        btnAddMarks.addActionListener(e -> showPanel(new AddMarksPanel()));
        btnBehaviour.addActionListener(e -> showPanel(new BehaviourPanel()));
        btnLogout.addActionListener(e -> logout());

        setVisible(true);
    }

    // ---------- panel switcher ----------
    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ---------- logout ----------
    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }
}