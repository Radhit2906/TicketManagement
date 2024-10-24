package views;

import controllers.UserController;
import models.User;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private MainFrame mainFrame;
    private UserController userController;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        userController = new UserController();
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);
        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Back to Login");

        registerButton.addActionListener(e -> register());
        loginButton.addActionListener(e -> mainFrame.showLoginPanel());

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(registerButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        add(loginButton, gbc);
    }

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("customer");

        boolean success = userController.register(user);
        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful");
            mainFrame.showLoginPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed");
        }
    }
}
