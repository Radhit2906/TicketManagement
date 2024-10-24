package controllers;

import db.DatabaseConnection;
import models.Journey;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JourneyController {
    public List<Journey> getAllJourneys() {
        List<Journey> journeys = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM journeys")) {
            while (rs.next()) {
                Journey journey = new Journey();
                journey.setId(rs.getInt("id"));
                journey.setOrigin(rs.getString("origin"));
                journey.setDestination(rs.getString("destination"));
                journey.setSchedule(rs.getString("schedule"));
                journey.setPrice(rs.getDouble("price"));
                journey.setAvailableTickets(rs.getInt("available_tickets"));
                journeys.add(journey);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return journeys;
    }

    public List<Journey> getJourneysByOriginAndDestination(String origin, String destination) {
        List<Journey> journeys = new ArrayList<>();
        String query = "SELECT * FROM journeys WHERE origin = ? AND destination = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, origin);
            stmt.setString(2, destination);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Journey journey = new Journey();
                    journey.setId(rs.getInt("id"));
                    journey.setOrigin(rs.getString("origin"));
                    journey.setDestination(rs.getString("destination"));
                    journey.setSchedule(rs.getString("schedule"));
                    journey.setPrice(rs.getDouble("price"));
                    journey.setAvailableTickets(rs.getInt("available_tickets"));
                    journeys.add(journey);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return journeys;
    }

    public void addJourney(Journey journey) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO journeys (origin, destination, schedule, price, available_tickets) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, journey.getOrigin());
                stmt.setString(2, journey.getDestination());
                stmt.setString(3, journey.getSchedule());
                stmt.setDouble(4, journey.getPrice());
                stmt.setInt(5, journey.getAvailableTickets());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateJourney(Journey journey) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE journeys SET origin = ?, destination = ?, schedule = ?, price = ?, available_tickets = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, journey.getOrigin());
                stmt.setString(2, journey.getDestination());
                stmt.setString(3, journey.getSchedule());
                stmt.setDouble(4, journey.getPrice());
                stmt.setInt(5, journey.getAvailableTickets());
                stmt.setInt(6, journey.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAvailableTickets(int journeyId, int newAvailableTickets) {
        String query = "UPDATE journeys SET available_tickets = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newAvailableTickets);
            stmt.setInt(2, journeyId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteJourney(int journeyId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Hapus semua tiket yang terkait dengan perjalanan ini
            String deleteTicketsQuery = "DELETE FROM tickets WHERE journey_id = ?";
            try (PreparedStatement deleteTicketsStmt = conn.prepareStatement(deleteTicketsQuery)) {
                deleteTicketsStmt.setInt(1, journeyId);
                deleteTicketsStmt.executeUpdate();
            }

            // Hapus perjalanan
            String deleteJourneyQuery = "DELETE FROM journeys WHERE id = ?";
            try (PreparedStatement deleteJourneyStmt = conn.prepareStatement(deleteJourneyQuery)) {
                deleteJourneyStmt.setInt(1, journeyId);
                deleteJourneyStmt.executeUpdate();
            }

            // Update the IDs to be sequential after deletion
            String updateQuery = "SET @num := 0; " +
                                 "UPDATE journeys SET id = @num := (@num+1); " +
                                 "ALTER TABLE journeys AUTO_INCREMENT = 1;";
            try (Statement updateStmt = conn.createStatement()) {
                updateStmt.execute(updateQuery);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
