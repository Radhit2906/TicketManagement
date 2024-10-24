package views;

import controllers.TicketController;
import models.Ticket;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportPanel extends JPanel {
    private TicketController ticketController;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JDateChooser bookingStartDateChooser;
    private JDateChooser bookingEndDateChooser;
    private JDateChooser scheduleStartDateChooser;
    private JDateChooser scheduleEndDateChooser;

    public ReportPanel() {
        ticketController = new TicketController();
        setLayout(new BorderLayout());

        // Table for displaying reports
        reportTable = new JTable();
        tableModel = new DefaultTableModel(new String[]{"ID", "Journey", "Customer", "Contact", "Quantity", "Price per Ticket", "Total Price", "Schedule", "Booking Datetime"}, 0);
        reportTable.setModel(tableModel);
        loadReportData(null, null, null, null);

        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        // Add buttons and other UI elements for filtering and exporting reports
        JPanel controlPanel = new JPanel();
        bookingStartDateChooser = new JDateChooser();
        bookingEndDateChooser = new JDateChooser();
        scheduleStartDateChooser = new JDateChooser();
        scheduleEndDateChooser = new JDateChooser();

        JButton filterBookingButton = new JButton("Filter by Booking Datetime");
        filterBookingButton.addActionListener(e -> filterByBookingDatetime());

        JButton filterScheduleButton = new JButton("Filter by Schedule");
        filterScheduleButton.addActionListener(e -> filterBySchedule());

        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> exportToCSV());

        controlPanel.add(new JLabel("Booking Start Date:"));
        controlPanel.add(bookingStartDateChooser);
        controlPanel.add(new JLabel("Booking End Date:"));
        controlPanel.add(bookingEndDateChooser);
        controlPanel.add(filterBookingButton);

        controlPanel.add(new JLabel("Schedule Start Date:"));
        controlPanel.add(scheduleStartDateChooser);
        controlPanel.add(new JLabel("Schedule End Date:"));
        controlPanel.add(scheduleEndDateChooser);
        controlPanel.add(filterScheduleButton);

        controlPanel.add(exportButton);

        add(controlPanel, BorderLayout.SOUTH);
    }

    private void loadReportData(Timestamp bookingStartDate, Timestamp bookingEndDate, Timestamp scheduleStartDate, Timestamp scheduleEndDate) {
        List<Ticket> tickets;
        if (bookingStartDate != null && bookingEndDate != null) {
            tickets = ticketController.getTicketsByBookingDateRange(bookingStartDate, bookingEndDate);
        } else if (scheduleStartDate != null && scheduleEndDate != null) {
            tickets = ticketController.getTicketsByScheduleDateRange(scheduleStartDate, scheduleEndDate);
        } else {
            tickets = ticketController.getAllTickets();
        }

        tableModel.setRowCount(0); // Clear existing data
        for (Ticket ticket : tickets) {
            tableModel.addRow(new Object[]{
                ticket.getId(),
                ticket.getJourney().getOrigin() + " - " + ticket.getJourney().getDestination(),
                ticket.getCustomer().getName(),
                ticket.getCustomer().getContact(),
                ticket.getQuantity(),
                ticket.getJourney().getPrice(),
                ticket.getTotalPrice(),
                ticket.getJourney().getSchedule(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ticket.getBookingDatetime())
            });
        }
    }

    private void filterByBookingDatetime() {
        Date startDate = bookingStartDateChooser.getDate();
        Date endDate = bookingEndDateChooser.getDate();
        if (startDate == null || endDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both start date and end date for booking datetime filter.");
            return;
        }
        loadReportData(new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()), null, null);
    }

    private void filterBySchedule() {
        Date scheduleStartDate = scheduleStartDateChooser.getDate();
        Date scheduleEndDate = scheduleEndDateChooser.getDate();
        if (scheduleStartDate == null || scheduleEndDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both start date and end date for schedule filter.");
            return;
        }
        loadReportData(null, null, new Timestamp(scheduleStartDate.getTime()), new Timestamp(scheduleEndDate.getTime()));
    }

    private void exportToCSV() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = sdf.format(new Date());
        String defaultFileName = "Report_" + timestamp + ".csv";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File(defaultFileName));
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.write(tableModel.getColumnName(i) + ",");
                }
                writer.write("\n");

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        writer.write(tableModel.getValueAt(i, j).toString() + ",");
                    }
                    writer.write("\n");
                }

                JOptionPane.showMessageDialog(this, "Export successful.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting to CSV: " + e.getMessage());
            }
        }
    }
}
