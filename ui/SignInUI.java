package ui;
import javax.swing.*;
import models.User;
import utils.FileManager;
import managers.CredentialsManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

public class SignInUI extends displayUI {
    private JTextField txtUsername;
    private JTextField txtPassword; 
    
    public SignInUI() {
        super("Quackstagram - Register");
        initializeUI();
    }

    private void initializeUI() {
        // Adding components to the frame
        add(createHeaderPanel("Quackstagram 🐥"), BorderLayout.NORTH);
        add(createFieldsPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFieldsPanel() {
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        txtUsername = new JTextField("Username");
        txtPassword = new JTextField("Password");
        txtUsername.setForeground(Color.GRAY);
        txtPassword.setForeground(Color.GRAY);

        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(SignUpUI.createPhotoPanel()); 
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(txtUsername);
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(txtPassword);
        fieldsPanel.add(Box.createVerticalStrut(10));

        return fieldsPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10)); // Grid layout with 1 row, 2 columns
        buttonPanel.setBackground(Color.white);

        buttonPanel.add(createSignInBtn());
        buttonPanel.add(createRegisterBtn());

        return buttonPanel;
    }

    private JButton createSignInBtn() {
        JButton btnSignIn = new JButton("Sign-In");
        btnSignIn.addActionListener(this::onSignInClicked);
        btnSignIn.setBackground(new Color(244, 90, 255));
        btnSignIn.setForeground(Color.BLACK); // Set the text color to black
        btnSignIn.setFocusPainted(false);
        btnSignIn.setBorderPainted(false);
        btnSignIn.setFont(new Font("Arial", Font.BOLD, 14));

        return btnSignIn;
    }

    private JButton createRegisterBtn() {
        JButton btnRegisterNow = new JButton("No Account? Register Now");
        btnRegisterNow.addActionListener(this::onRegisterNowClicked);
        btnRegisterNow.setBackground(Color.WHITE); // Set a different color for distinction
        btnRegisterNow.setForeground(Color.BLACK);
        btnRegisterNow.setFocusPainted(false);
        btnRegisterNow.setBorderPainted(false);

        return btnRegisterNow;
    }

    private void onSignInClicked(ActionEvent event) {
        String enteredUsername = txtUsername.getText();
        String enteredPassword = txtPassword.getText();
        System.out.println(enteredUsername + " <-> " + enteredPassword);
        User authenticatedUser = CredentialsManager.verifyCredentials(enteredUsername, enteredPassword);
        if (authenticatedUser != null) {
            FileManager fileManager = FileManager.getInstance(); // Use Singleton FileManager
            List<String> userData = new ArrayList<>();
            userData.add(authenticatedUser.getUsername() + ":" + enteredPassword);

            fileManager.writeFile("data/users.txt", userData); 

            // Close the SignUpUI frame
            dispose();

            // Open the SignInUI frame
            SwingUtilities.invokeLater(() -> {
                InstagramProfileUI profileUI = new InstagramProfileUI(authenticatedUser);
                profileUI.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRegisterNowClicked(ActionEvent event) {
        // Close the SignInUI frame
        dispose();

        // Open the SignUpUI frame
        SwingUtilities.invokeLater(() -> {
            SignUpUI signUpFrame = new SignUpUI();
            signUpFrame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SignInUI frame = new SignInUI();
            frame.setVisible(true);
        });
    }
}
