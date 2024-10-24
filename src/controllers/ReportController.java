package controllers;

import db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportController {
    public List<String> getDailySalesReport() {
        List<String> report = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM tickets WHERE DATE(created_at) = CURDATE()";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    report.add("Ticket ID: " + rs.getInt("id") + ", Total Price: " + rs.getDouble("total_price"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }

    public List<String> getMonthlySalesReport() {
        List<String> report = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM tickets WHERE MONTH(created_at) = MONTH(CURDATE()) AND YEAR(created_at) = YEAR(CURDATE())";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    report.add("Ticket ID: " + rs.getInt("id") + ", Total Price: " + rs.getDouble("total_price"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return report;
    }
}
