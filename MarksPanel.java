package com.mycompany.sis.student;

import com.mycompany.sis.db.DBConnection;
import com.mycompany.sis.utill.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MarksPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public MarksPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        model = new DefaultTableModel(
                new String[]{"Subject", "Marks", "Grade"}, 0);

        table = new JTable(model);
        table.setRowHeight(28);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(new MarksChartPanel(), BorderLayout.SOUTH);

        loadMarks();
    }

    private void loadMarks() {
        model.setRowCount(0);

        try (Connection con = DBConnection.getConnection()) {

            String sql = """
                SELECT subject, marks, grade
                FROM marks
                WHERE student_id = ?
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, Session.getStudentId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("subject"),
                        rs.getInt("marks"),
                        rs.getString("grade")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Marks Load Error: " + e.getMessage());
            System.out.println("LOGGED STUDENT ID = " + Session.getStudentId());

        }
    }
}
