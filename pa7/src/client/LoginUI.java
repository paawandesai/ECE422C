package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI extends JPanel implements ActionListener {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final ClientBackend clientBackend;
    private final AuctionUI auctionUI;

    public LoginUI(ClientBackend clientBackend, AuctionUI auctionUI) {
        this.clientBackend = clientBackend;
        this.auctionUI = auctionUI;

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(usernameLabel, gbc);
        gbc.gridx++;
        add(usernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        add(passwordLabel, gbc);
        gbc.gridx++;
        add(passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(loginButton, gbc);
    }
    public void reset() {
        usernameField.setText("");
        passwordField.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (clientBackend.login(username, password)) {
            auctionUI.showAuctions();
        } else {
            JOptionPane.showMessageDialog(this, "Incorrect username or password");
        }
    }
}
