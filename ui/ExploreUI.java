package ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import managers.ExploreManager;
import models.User;
import utils.TimeUtils;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ExploreUI extends displayUI {

    private static final int IMAGE_SIZE = WIDTH / 3; // Size for each image in the grid

    public ExploreUI() {
        super("Explore");
        initializeUI();
    }

    private void initializeUI() {

        getContentPane().removeAll(); // Clear existing components
        setLayout(new BorderLayout()); // Reset the layout manager

        add(createHeaderPanel(" Explore 🐥"), BorderLayout.NORTH);
        add(createContentPanel(createMainContentPanel()), BorderLayout.CENTER); // ✅ Uses displayUI
        add(createNavigationPanel(), BorderLayout.SOUTH);

        revalidate();
        repaint();

    }

    private JPanel createMainContentPanel() {
        JPanel imageGridPanel = new JPanel(new GridLayout(0, 3, 2, 2));
        File[] imageFiles = ExploreManager.loadPostImages(); 

        for (File imageFile : imageFiles) {
            ImageIcon imageIcon = new ImageIcon(new ImageIcon(imageFile.getPath()).getImage()
                    .getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH));
            JLabel imageLabel = new JLabel(imageIcon);

            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    displayImage(imageFile.getPath());
                }
            });

            imageGridPanel.add(imageLabel);
        }

        return imageGridPanel;
    }

    private void displayImage(String imagePath) {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
    
        add(createHeaderPanel(" Explore 🐥"), BorderLayout.NORTH);
        add(createNavigationPanel(), BorderLayout.SOUTH);
    
        String imageId = new File(imagePath).getName().split("\\.")[0];
        String[] details = ExploreManager.getImageDetails(imageId);
        String username = details[1]; // Username
        System.out.println(username);
        String bio = details[2]; // Bio
        System.out.println(bio);
        String tags = details[3]; // Tags
        System.out.println(tags);
        String timestampString = details[4]; // Timestamp
        System.out.println(timestampString);
        int likes = Integer.parseInt(details[5]); // Likes
        System.out.println(likes);
    
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton usernameLabel = new JButton(username);
        usernameLabel.addActionListener(e -> openProfileUI(username));
        JLabel timeLabel = new JLabel(TimeUtils.getElapsedTime(timestampString) + " ago.");
        topPanel.add(usernameLabel, BorderLayout.WEST);
        topPanel.add(timeLabel, BorderLayout.EAST);
    
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            imageLabel.setIcon(new ImageIcon(originalImage));
        } catch (IOException ex) {
            imageLabel.setText("Image not found");
        }
    
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JTextArea bioTextArea = new JTextArea(bio + "\nTagged Users: " + tags);
        bioTextArea.setEditable(false);
        JLabel likesLabel = new JLabel("Likes: " + likes);
        bottomPanel.add(bioTextArea, BorderLayout.CENTER);
        bottomPanel.add(likesLabel, BorderLayout.SOUTH);
    
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
        imagePanel.add(imageLabel);
        JPanel wrappedPanel = createContentPanel(imagePanel); // Uses displayUI
    
        add(topPanel, BorderLayout.NORTH);
        add(wrappedPanel, BorderLayout.CENTER); // Uses displayUI for scrollable image
        add(bottomPanel, BorderLayout.SOUTH);
    
        revalidate();
        repaint();
    }

    // Open the clicked user's profile
    private void openProfileUI(String username) {
        System.out.println("Opening profile for user: " + username);
        this.dispose();
        User user = new User(username);
        InstagramProfileUI profileUI = new InstagramProfileUI(user);
        profileUI.setVisible(true);
    }

}
