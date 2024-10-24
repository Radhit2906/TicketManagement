package views;

import controllers.TicketController;
import controllers.JourneyController;
import models.Ticket;
import models.Journey;
import models.Customer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TicketBookingPanel extends JPanel {
    private MainFrame mainFrame;
    private TicketController ticketController;
    private JourneyController journeyController;
    private JComboBox<String> originComboBox;
    private JComboBox<String> destinationComboBox;
    private JLabel journeyLabel;
    private JComboBox<String> scheduleComboBox;
    private JTextField customerNameField;
    private JTextField customerContactField;
    private JTextField quantityField;
    private JLabel availableTicketsLabel;
    private List<Journey> journeys;

    public TicketBookingPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        ticketController = new TicketController();
        journeyController = new JourneyController();
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Origin and Destination dropdowns
        originComboBox = new JComboBox<>();
        destinationComboBox = new JComboBox<>();
        loadOriginsAndDestinations();

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Origin:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(originComboBox, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        add(new JLabel("Destination:"), gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        add(destinationComboBox, gbc);

        // Search button
        JButton searchButton = new JButton("Search Journeys");
        searchButton.addActionListener(e -> searchJourneys());

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        add(searchButton, gbc);

        // Journey label
        journeyLabel = new JLabel();

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(new JLabel("Journey:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        add(journeyLabel, gbc);

        gbc.gridwidth = 1;

        // Schedule dropdown
        scheduleComboBox = new JComboBox<>();
        scheduleComboBox.addActionListener(e -> updateAvailableTickets());

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Schedule:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        add(scheduleComboBox, gbc);

        gbc.gridwidth = 1;

        // Available tickets label
        availableTicketsLabel = new JLabel();

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Available Tickets:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        add(availableTicketsLabel, gbc);

        gbc.gridwidth = 1;

        // Customer details
        customerNameField = new JTextField();
        customerContactField = new JTextField();

        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("Customer Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        add(customerNameField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 5;
        add(new JLabel("Customer Contact:"), gbc);

        gbc.gridx = 3;
        gbc.gridy = 5;
        add(customerContactField, gbc);

        // Ticket quantity
        quantityField = new JTextField();

        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        add(quantityField, gbc);

        // Book ticket button
        JButton bookButton = new JButton("Book Ticket");
        bookButton.addActionListener(e -> bookTicket());

        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(bookButton, gbc);

        gbc.gridwidth = 1;
    }

    private void loadOriginsAndDestinations() {
        List<Journey> journeys = journeyController.getAllJourneys();
        for (Journey journey : journeys) {
            if (!containsItem(originComboBox, journey.getOrigin())) {
                originComboBox.addItem(journey.getOrigin());
            }
            if (!containsItem(destinationComboBox, journey.getDestination())) {
                destinationComboBox.addItem(journey.getDestination());
            }
        }
    }

    private boolean containsItem(JComboBox<String> comboBox, String item) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(item)) {
                return true;
            }
        }
        return false;
    }

    private void searchJourneys() {
        String origin = (String) originComboBox.getSelectedItem();
        String destination = (String) destinationComboBox.getSelectedItem();
        journeys = journeyController.getJourneysByOriginAndDestination(origin, destination);
        journeyLabel.setText("");  // Clear previous journey
        scheduleComboBox.removeAllItems();  // Clear previous schedules
        if (!journeys.isEmpty()) {
            Journey journey = journeys.get(0);
            journeyLabel.setText(journey.getOrigin() + " - " + journey.getDestination());
            for (Journey j : journeys) {
                scheduleComboBox.addItem(j.getSchedule());
            }
        }
    }

    private void updateAvailableTickets() {
        String selectedSchedule = (String) scheduleComboBox.getSelectedItem();
        Journey selectedJourney = journeys.stream()
                                          .filter(j -> j.getSchedule().equals(selectedSchedule))
                                          .findFirst()
                                          .orElse(null);
        if (selectedJourney != null) {
            availableTicketsLabel.setText(String.valueOf(selectedJourney.getAvailableTickets()));
        }
    }

    private void bookTicket() {
        if (journeys == null || journeys.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No journeys available.");
            return;
        }

        String selectedSchedule = (String) scheduleComboBox.getSelectedItem();
        String customerName = customerNameField.getText();
        String customerContact = customerContactField.getText();
        String quantityText = quantityField.getText();

        // Validasi input
        if (customerName.isEmpty() || customerContact.isEmpty() || quantityText.isEmpty() || selectedSchedule == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity. Please enter a number.");
            return;
        }

        Journey selectedJourney = journeys.stream()
                                          .filter(j -> j.getSchedule().equals(selectedSchedule))
                                          .findFirst()
                                          .orElse(null);

        if (selectedJourney == null) {
            JOptionPane.showMessageDialog(this, "Selected schedule is not available.");
            return;
        }

        if (selectedJourney.getAvailableTickets() == 0) {
            JOptionPane.showMessageDialog(this, "Tickets are sold out.");
            return;
        }

        if (quantity > selectedJourney.getAvailableTickets()) {
            JOptionPane.showMessageDialog(this, "Not enough tickets available.");
            return;
        }

        Customer customer = new Customer();
        customer.setName(customerName);
        customer.setContact(customerContact);

        Ticket ticket = new Ticket();
        ticket.setJourney(selectedJourney);
        ticket.setCustomer(customer);
        ticket.setQuantity(quantity);
        ticket.setTotalPrice(selectedJourney.getPrice() * quantity);

        ticketController.bookTicket(ticket);

        // Update available tickets
        int newAvailableTickets = selectedJourney.getAvailableTickets() - quantity;
        journeyController.updateAvailableTickets(selectedJourney.getId(), newAvailableTickets);

        // Tampilkan pesan konfirmasi dan detail tiket
        showTicketDetails(ticket);

        // Reset form
        resetForm();
    }

    private void showTicketDetails(Ticket ticket) {
        String message = "Journey: " + ticket.getJourney().getOrigin() + " - " + ticket.getJourney().getDestination() + "\n" +
                         "Schedule: " + ticket.getJourney().getSchedule() + "\n" +
                         "Customer Name: " + ticket.getCustomer().getName() + "\n" +
                         "Customer Contact: " + ticket.getCustomer().getContact() + "\n" +
                         "Quantity: " + ticket.getQuantity() + "\n" +
                         "Price per Ticket: " + ticket.getJourney().getPrice() + "\n" +
                         "Total Price: " + ticket.getTotalPrice();
        JOptionPane.showMessageDialog(this, message, "Ticket Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetForm() {
        originComboBox.setSelectedIndex(0);
        destinationComboBox.setSelectedIndex(0);
        journeyLabel.setText("");
        scheduleComboBox.removeAllItems();
        availableTicketsLabel.setText("");
        customerNameField.setText("");
        customerContactField.setText("");
        quantityField.setText("");
        journeys = null;
    }
}
