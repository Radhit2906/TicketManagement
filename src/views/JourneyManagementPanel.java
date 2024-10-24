package views;

import controllers.JourneyController;
import models.Journey;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JourneyManagementPanel extends JPanel {
    private JTable journeyTable;
    private JourneyController journeyController;
    private DefaultTableModel tableModel;
    private JDateChooser scheduleChooser;

    public JourneyManagementPanel() {
        journeyController = new JourneyController();
        setLayout(new BorderLayout());

        // Table for displaying journeys
        journeyTable = new JTable();
        tableModel = new DefaultTableModel(new String[]{"ID", "Origin", "Destination", "Schedule", "Price", "Available Tickets"}, 0);
        journeyTable.setModel(tableModel);
        loadJourneyData();

        add(new JScrollPane(journeyTable), BorderLayout.CENTER);

        // Add buttons and other UI elements for managing journeys
        JPanel controlPanel = new JPanel();
        JButton addButton = new JButton("Add Journey");
        addButton.addActionListener(e -> addJourney());
        JButton editButton = new JButton("Edit Journey");
        editButton.addActionListener(e -> editJourney());
        JButton deleteButton = new JButton("Delete Journey");
        deleteButton.addActionListener(e -> deleteJourney());

        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void loadJourneyData() {
        List<Journey> journeys = journeyController.getAllJourneys();
        tableModel.setRowCount(0); // Clear existing data
        for (Journey journey : journeys) {
            tableModel.addRow(new Object[]{journey.getId(), journey.getOrigin(), journey.getDestination(), journey.getSchedule(), journey.getPrice(), journey.getAvailableTickets()});
        }
    }

    private void addJourney() {
        // Create a panel for input
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField originField = new JTextField();
        JTextField destinationField = new JTextField();
        scheduleChooser = new JDateChooser();
        JTextField priceField = new JTextField();
        JTextField availableTicketsField = new JTextField();

        panel.add(new JLabel("Origin:"));
        panel.add(originField);
        panel.add(new JLabel("Destination:"));
        panel.add(destinationField);
        panel.add(new JLabel("Schedule:"));
        panel.add(scheduleChooser);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Available Tickets:"));
        panel.add(availableTicketsField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Journey", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String origin = originField.getText();
            String destination = destinationField.getText();
            Date scheduleDate = scheduleChooser.getDate();
            if (scheduleDate == null) {
                JOptionPane.showMessageDialog(this, "Please select a valid date.");
                return;
            }
            String schedule = new SimpleDateFormat("yyyy-MM-dd").format(scheduleDate);
            double price;
            int availableTickets;
            try {
                price = Double.parseDouble(priceField.getText());
                availableTickets = Integer.parseInt(availableTicketsField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid price and available tickets.");
                return;
            }

            Journey journey = new Journey();
            journey.setOrigin(origin);
            journey.setDestination(destination);
            journey.setSchedule(schedule);
            journey.setPrice(price);
            journey.setAvailableTickets(availableTickets);

            journeyController.addJourney(journey);
            loadJourneyData();
        }
    }

    private void editJourney() {
        int selectedRow = journeyTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) journeyTable.getValueAt(selectedRow, 0);
            String currentOrigin = (String) journeyTable.getValueAt(selectedRow, 1);
            String currentDestination = (String) journeyTable.getValueAt(selectedRow, 2);
            String currentSchedule = (String) journeyTable.getValueAt(selectedRow, 3);
            double currentPrice = (double) journeyTable.getValueAt(selectedRow, 4);
            int currentAvailableTickets = (int) journeyTable.getValueAt(selectedRow, 5);

            // Parse the current schedule to a Date object
            Date scheduleDate = null;
            try {
                scheduleDate = new SimpleDateFormat("yyyy-MM-dd").parse(currentSchedule);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Create a panel for input
            JPanel panel = new JPanel(new GridLayout(0, 1));
            JTextField originField = new JTextField(currentOrigin);
            JTextField destinationField = new JTextField(currentDestination);
            scheduleChooser = new JDateChooser(scheduleDate);
            JTextField priceField = new JTextField(String.valueOf(currentPrice));
            JTextField availableTicketsField = new JTextField(String.valueOf(currentAvailableTickets));

            panel.add(new JLabel("Origin:"));
            panel.add(originField);
            panel.add(new JLabel("Destination:"));
            panel.add(destinationField);
            panel.add(new JLabel("Schedule:"));
            panel.add(scheduleChooser);
            panel.add(new JLabel("Price:"));
            panel.add(priceField);
            panel.add(new JLabel("Available Tickets:"));
            panel.add(availableTicketsField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Edit Journey", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String origin = originField.getText();
                String destination = destinationField.getText();
                scheduleDate = scheduleChooser.getDate();
                String schedule = new SimpleDateFormat("yyyy-MM-dd").format(scheduleDate);
                double price;
                int availableTickets;
                try {
                    price = Double.parseDouble(priceField.getText());
                    availableTickets = Integer.parseInt(availableTicketsField.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter valid price and available tickets.");
                    return;
                }

                Journey journey = new Journey();
                journey.setId(id);
                journey.setOrigin(origin);
                journey.setDestination(destination);
                journey.setSchedule(schedule);
                journey.setPrice(price);
                journey.setAvailableTickets(availableTickets);

                journeyController.updateJourney(journey);
                loadJourneyData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a journey to edit.");
        }
    }

    private void deleteJourney() {
        int selectedRow = journeyTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) journeyTable.getValueAt(selectedRow, 0);
            journeyController.deleteJourney(id);
            loadJourneyData();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a journey to delete.");
        }
    }
}
