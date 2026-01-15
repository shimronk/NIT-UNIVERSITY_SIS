package com.mycompany.sis.student;

import com.mycompany.sis.db.DBConnection;
import com.mycompany.sis.utill.Session;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class MarksChartPanel extends JPanel {

    private Map<String, Integer> marksMap = new LinkedHashMap<>();

    public MarksChartPanel() {
        setPreferredSize(new Dimension(800, 250));
        loadMarks();
    }

    private void loadMarks() {
        try (Connection con = DBConnection.getConnection()) {

            String sql = """
                SELECT subject, marks
                FROM marks
                WHERE student_id = ?
            """;

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, Session.getStudentId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                marksMap.put(rs.getString("subject"),
                             rs.getInt("marks"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (marksMap.isEmpty()) return;

        int x = 50;
        int barWidth = 60;

        for (Map.Entry<String, Integer> e : marksMap.entrySet()) {
            int barHeight = e.getValue() * 2;

            g.setColor(new Color(33, 150, 243));
            g.fillRect(x, 200 - barHeight, barWidth, barHeight);

            g.setColor(Color.BLACK);
            g.drawString(e.getKey(), x, 215);
            g.drawString(String.valueOf(e.getValue()), x + 15, 190 - barHeight);

            x += 90;
        }
    }
}
