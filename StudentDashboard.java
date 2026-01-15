package com.mycompany.sis.student;

import com.mycompany.sis.auth.LoginFrame;
import com.mycompany.sis.utill.Session;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {

    private JPanel contentPanel;
    private JButton btnLogout;

    public StudentDashboard(String studentId) {

        setTitle("Student Dashboard");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= HEADER =================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        header.setPreferredSize(new Dimension(0, 60));

        JLabel lblStudent = new JLabel(" Student ID: " + studentId);
        lblStudent.setForeground(Color.WHITE);
        lblStudent.setFont(new Font("Segoe UI", Font.BOLD, 16));

        btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnLogout.addActionListener(e -> {
            Session.clear();
            new LoginFrame().setVisible(true);
            dispose();
        });

        header.add(lblStudent, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ================= SIDEBAR =================
        JPanel sidebar = new JPanel(new GridLayout(3, 1, 10, 10));
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBackground(new Color(240, 248, 255));

        JButton btnProfile = createMenuButton("Profile");
        JButton btnMarks = createMenuButton("Marks");
        JButton btnBehaviour = createMenuButton("My Behaviour");

        sidebar.add(btnProfile);
        sidebar.add(btnMarks);
        sidebar.add(btnBehaviour);

        add(sidebar, BorderLayout.WEST);

        // ================= CONTENT =================
        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);

        // DEFAULT VIEW
        showMarksPanel();

        // ACTIONS
        btnMarks.addActionListener(e -> showMarksPanel());
        btnProfile.addActionListener(e -> showProfilePanel());
        btnBehaviour.addActionListener(e -> showBehaviourPanel(studentId));
    }

    // ================= HELPERS =================

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(224, 242, 254));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ================= PANELS =================

    private void showProfilePanel() {
        contentPanel.removeAll();
        contentPanel.add(new ProfilePanel(), BorderLayout.CENTER);
        refresh();
    }

    private void showBehaviourPanel(String studentId) {
        contentPanel.removeAll();
        contentPanel.add(new StudentBehaviourPanel(studentId), BorderLayout.CENTER);
        refresh();
    }

    private void showMarksPanel() {
        contentPanel.removeAll();
        contentPanel.add(new MarksPanel(), BorderLayout.CENTER); // ✅ DB-BASED PANEL
        refresh();
    }

    private void refresh() {
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
