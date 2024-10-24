package controllers;

import db.DatabaseConnection;
import models.Ticket;
import models.Journey;
import models.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketController {
    public void bookTicket(Ticket ticket) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Add customer if not exist and get the customer ID
            int customerId = getOrAddCustomer(conn, ticket.getCustomer());

            String query = "INSERT INTO tickets (journey_id, customer_id, quantity, total_price, booking_datetime) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, ticket.getJourney().getId());
                stmt.setInt(2, customerId);
                stmt.setInt(3, ticket.getQuantity());
                stmt.setDouble(4, ticket.getTotalPrice());
                stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis())); // Set current timestamp
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getOrAddCustomer(Connection conn, Customer customer) throws SQLException {
        String selectQuery = "SELECT id FROM customers WHERE name = ? AND contact = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
            selectStmt.setString(1, customer.getName());
            selectStmt.setString(2, customer.getContact());
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        String insertQuery = "INSERT INTO customers (name, contact) VALUES (?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, customer.getName());
            insertStmt.setString(2, customer.getContact());
            insertStmt.executeUpdate();

            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        }
    }

    public List<Ticket> getAllTickets() {
        return getSalesReport("SELECT tickets.id, journeys.origin, journeys.destination, journeys.schedule, customers.name, customers.contact, tickets.quantity, journeys.price, tickets.total_price, tickets.booking_datetime FROM tickets " +
                "JOIN journeys ON tickets.journey_id = journeys.id " +
                "JOIN customers ON tickets.customer_id = customers.id ORDER BY tickets.id");
    }

    public List<Ticket> getDailySalesReport() {
        return getSalesReport("SELECT tickets.id, journeys.origin, journeys.destination, journeys.schedule, customers.name, customers.contact, tickets.quantity, journeys.price, tickets.total_price, tickets.booking_datetime FROM tickets " +
                "JOIN journeys ON tickets.journey_id = journeys.id " +
                "JOIN customers ON tickets.customer_id = customers.id " +
                "WHERE DATE(tickets.booking_datetime) = CURDATE() ORDER BY tickets.id");
    }

    public List<Ticket> getMonthlySalesReport() {
        return getSalesReport("SELECT tickets.id, journeys.origin, journeys.destination, journeys.schedule, customers.name, customers.contact, tickets.quantity, journeys.price, tickets.total_price, tickets.booking_datetime FROM tickets " +
                "JOIN journeys ON tickets.journey_id = journeys.id " +
                "JOIN customers ON tickets.customer_id = customers.id " +
                "WHERE MONTH(tickets.booking_datetime) = MONTH(CURDATE()) AND YEAR(tickets.booking_datetime) = YEAR(CURDATE()) ORDER BY tickets.id");
    }

    private List<Ticket> getSalesReport(String query) {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Ticket ticket = new Ticket();
                Journey journey = new Journey();
                Customer customer = new Customer();

                ticket.setId(rs.getInt("id"));
                journey.setOrigin(rs.getString("origin"));
                journey.setDestination(rs.getString("destination"));
                journey.setSchedule(rs.getString("schedule"));
                customer.setName(rs.getString("name"));
                customer.setContact(rs.getString("contact"));
                ticket.setQuantity(rs.getInt("quantity"));
                journey.setPrice(rs.getDouble("price"));
                ticket.setTotalPrice(rs.getDouble("total_price"));
                ticket.setBookingDatetime(rs.getTimestamp("booking_datetime"));

                ticket.setJourney(journey);
                ticket.setCustomer(customer);

                tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public List<Ticket> getTicketsByBookingDateRange(Timestamp startDate, Timestamp endDate) {
        return getTicketsByDateRange(startDate, endDate, "booking_datetime");
    }

    public List<Ticket> getTicketsByScheduleDateRange(Timestamp startDate, Timestamp endDate) {
        return getTicketsByDateRange(startDate, endDate, "schedule");
    }

    public List<Ticket> getTicketsByDateRange(Timestamp startDate, Timestamp endDate, String dateColumn) {
    List<Ticket> tickets = new ArrayList<>();
    // Adjust endDate to include the entire day
    endDate = new Timestamp(endDate.getTime() + 24 * 60 * 60 * 1000 - 1);
    String query = "SELECT tickets.id, journeys.origin, journeys.destination, journeys.schedule, customers.name, customers.contact, tickets.quantity, journeys.price, tickets.total_price, tickets.booking_datetime FROM tickets " +
                   "JOIN journeys ON tickets.journey_id = journeys.id " +
                   "JOIN customers ON tickets.customer_id = customers.id " +
                   "WHERE " + dateColumn + " BETWEEN ? AND ? ORDER BY tickets.id";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setTimestamp(1, startDate);
        stmt.setTimestamp(2, endDate);
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Ticket ticket = new Ticket();
                Journey journey = new Journey();
                Customer customer = new Customer();

                ticket.setId(rs.getInt("id"));
                journey.setOrigin(rs.getString("origin"));
                journey.setDestination(rs.getString("destination"));
                journey.setSchedule(rs.getString("schedule"));
                customer.setName(rs.getString("name"));
                customer.setContact(rs.getString("contact"));
                ticket.setQuantity(rs.getInt("quantity"));
                journey.setPrice(rs.getDouble("price"));
                ticket.setTotalPrice(rs.getDouble("total_price"));
                ticket.setBookingDatetime(rs.getTimestamp("booking_datetime"));

                ticket.setJourney(journey);
                ticket.setCustomer(customer);

                tickets.add(ticket);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return tickets;
}


    public void deleteTicket(int ticketId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String deleteQuery = "DELETE FROM tickets WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, ticketId);
                deleteStmt.executeUpdate();
            }

            // Update the IDs to be sequential after deletion
            String updateQuery = "SET @num := 0; " +
                                 "UPDATE tickets SET id = @num := (@num+1); " +
                                 "ALTER TABLE tickets AUTO_INCREMENT = 1;";
            try (Statement updateStmt = conn.createStatement()) {
                updateStmt.execute(updateQuery);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
