package ui;

import javax.swing.*;

import managers.UserRelationshipManager;
import models.User;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.awt.*;
import java.nio.file.*;
import java.util.stream.Stream;

public class InstagramProfileUI extends displayUI {
    private static final int PROFILE_IMAGE_SIZE = 80; // Adjusted size for the profile image to match UI
    private static final int GRID_IMAGE_SIZE = WIDTH / 3; // Static size for grid images
    private JPanel contentPanel; // Panel to display the image grid or the clicked image
    private JButton followButton; 
    private JPanel headerPanel; // Panel for the header
    private JPanel navigationPanel; // Panel for the navigation
    private User currentUser; // User object to store the current user's information

    public InstagramProfileUI(User user) {
        super("DACS Profile");
        this.currentUser = user;
        initializeUserData();
        System.out.println(currentUser.getPostsCount());
        contentPanel = new JPanel();
        headerPanel = createHeaderPanel(); // Initialize header panel
        navigationPanel = createNavigationPanel(); // Initialize navigation panel
        
        initializeUI();
    }

    private void initializeUserData() {
        UserRelationshipManager.loadUserData(currentUser); //Now the file reading logic is in UserRelationshipManager
    }

    private void initializeUI() {
        getContentPane().removeAll(); // Clear existing components

        // Re-add the header and navigation panels
        add(headerPanel, BorderLayout.NORTH);
        add(navigationPanel, BorderLayout.SOUTH);
        add(createContentPanel(createImageGridPanel()), BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel createHeaderPanel() {
        String loggedInUsername = readUsername();
        boolean isCurrentUser = loggedInUsername != null && loggedInUsername.equals(currentUser.getUsername());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.GRAY);

        // Top Part of the Header (Profile Image, Stats, Follow Button)
        JPanel topHeaderPanel = new JPanel(new BorderLayout(10, 0));
        topHeaderPanel.setBackground(new Color(249, 249, 249));

        // Profile image
        ImageIcon profileIcon = new ImageIcon(new ImageIcon("img/storage/profile/" + currentUser.getUsername() + ".png")
                .getImage().getScaledInstance(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, Image.SCALE_SMOOTH));
        JLabel profileImage = new JLabel(profileIcon);
        profileImage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topHeaderPanel.add(profileImage, BorderLayout.WEST);

        // Stats Panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        statsPanel.setBackground(new Color(249, 249, 249));
        System.out.println("Number of posts for this user" + currentUser.getPostsCount());
        statsPanel.add(createStatLabel(Integer.toString(currentUser.getPostsCount()), "Posts"));
        statsPanel.add(createStatLabel(Integer.toString(currentUser.getFollowersCount()), "Followers"));
        statsPanel.add(createStatLabel(Integer.toString(currentUser.getFollowingCount()), "Following"));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0)); // Add some vertical padding

        // Follow Button
        // Follow or Edit Profile Button
        // followButton.addActionListener(e ->
        // handleFollowAction(currentUser.getUsername()));
        followButton = new JButton();
        if (isCurrentUser) {
            followButton.setText("Edit Profile");
        } else {
            followButton.setText("Follow");
            if (UserRelationshipManager.isAlreadyFollowing(loggedInUsername, currentUser.getUsername())) {
                followButton.setText("Following");
            }
            followButton.addActionListener(e -> {
                handleFollowAction(currentUser.getUsername());
                followButton.setText("Following");
            });
        }

        followButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        followButton.setFont(new Font("Arial", Font.BOLD, 12));
        followButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, followButton.getMinimumSize().height)); // Make the
                                                                                                             // button
                                                                                                             // fill the
                                                                                                             // horizontal
                                                                                                             // space
        followButton.setBackground(new Color(225, 228, 232)); // A soft, appealing color that complements the UI
        followButton.setForeground(Color.BLACK);
        followButton.setOpaque(true);
        followButton.setBorderPainted(false);
        followButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add some vertical padding

        // Add Stats and Follow Button to a combined Panel
        JPanel statsFollowPanel = new JPanel();
        statsFollowPanel.setLayout(new BoxLayout(statsFollowPanel, BoxLayout.Y_AXIS));
        statsFollowPanel.add(statsPanel);
        statsFollowPanel.add(followButton);
        topHeaderPanel.add(statsFollowPanel, BorderLayout.CENTER);

        headerPanel.add(topHeaderPanel);

        // Profile Name and Bio Panel
        JPanel profileNameAndBioPanel = new JPanel();
        profileNameAndBioPanel.setLayout(new BorderLayout());
        profileNameAndBioPanel.setBackground(new Color(249, 249, 249));

        JLabel profileNameLabel = new JLabel(currentUser.getUsername());
        profileNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        profileNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // Padding on the sides

        JTextArea profileBio = new JTextArea(currentUser.getBio());
        System.out.println("This is the bio " + currentUser.getUsername());
        profileBio.setEditable(false);
        profileBio.setFont(new Font("Arial", Font.PLAIN, 12));
        profileBio.setBackground(new Color(249, 249, 249));
        profileBio.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Padding on the sides

        profileNameAndBioPanel.add(profileNameLabel, BorderLayout.NORTH);
        profileNameAndBioPanel.add(profileBio, BorderLayout.CENTER);

        headerPanel.add(profileNameAndBioPanel);

        return headerPanel;

    }

    private void handleFollowAction(String usernameToFollow) {
    String currentUserUsername = UserRelationshipManager.getLoggedInUsername();
    if (currentUserUsername != null) {
        boolean success = UserRelationshipManager.followUser(currentUserUsername, usernameToFollow);
        if (success) {
            System.out.println(currentUserUsername + " is now following " + usernameToFollow);
            initializeUI(); // Refresh the UI to update follow button
        } else {
            System.out.println("Failed to follow " + usernameToFollow);
        }
    } else {
        System.out.println("No logged-in user found.");
    }
    }

    private JPanel createImageGridPanel() {
        JPanel imageGridPanel = new JPanel(new GridLayout(0, 3, 5, 5));
    
        Path imageDir = Paths.get("img", "uploaded");
        try (Stream<Path> paths = Files.list(imageDir)) {
            paths.filter(path -> path.getFileName().toString().startsWith(currentUser.getUsername() + "_"))
                    .forEach(path -> {
                        ImageIcon imageIcon = new ImageIcon(new ImageIcon(path.toString()).getImage()
                                .getScaledInstance(GRID_IMAGE_SIZE, GRID_IMAGE_SIZE, Image.SCALE_SMOOTH));
                        JLabel imageLabel = new JLabel(imageIcon);
    
                        imageLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                displayImage(path.toString());
                            }
                        });
    
                        imageGridPanel.add(imageLabel);
                    });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    
        return imageGridPanel;
    }
    

    private void displayImage(String imagePath) {
        // Remove old content and reset layout
        getContentPane().removeAll();
        setLayout(new BorderLayout());
    
        JLabel fullSizeImageLabel = new JLabel();
        fullSizeImageLabel.setHorizontalAlignment(JLabel.CENTER);
    
        try {
            ImageIcon imageIcon = new ImageIcon(imagePath);
            fullSizeImageLabel.setIcon(imageIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        // Create a back button to return to the profile grid
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            initializeUI(); // Reload the profile grid
        });
    
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(fullSizeImageLabel, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    
        revalidate();
        repaint();
    }
    
    private JLabel createStatLabel(String number, String text) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" + number + "<br/>" + text + "</div></html>",
                SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(Color.BLACK);
        return label;
    }
}
