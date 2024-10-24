package views;

import models.User;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private User user;

    public MainFrame() {
        setTitle("Travel Ticket Management");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        showLoginPanel();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void showLoginPanel() {
        getContentPane().removeAll();
        add(new LoginPanel(this), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showRegisterPanel() {
        getContentPane().removeAll();
        add(new RegisterPanel(this), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void showMainPanel() {
        getContentPane().removeAll();
        JTabbedPane tabbedPane = new JTabbedPane();

        if (user.getRole().equals("admin")) {
            tabbedPane.addTab("Manage Journeys", new JourneyManagementPanel());
            tabbedPane.addTab("Reports", new ReportPanel());
        } else if (user.getRole().equals("customer")) {
            tabbedPane.addTab("Book Tickets", new TicketBookingPanel(this));
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void logout() {
        setUser(null);
        showLoginPanel();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
