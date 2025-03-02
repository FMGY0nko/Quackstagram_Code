package ui;

import javax.swing.*;

import utils.TimeUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class NotificationsUI extends displayUI {
    public NotificationsUI() {
        super("Notifications");
        initializeUI();
    }

    private void initializeUI() {
        // Add panels to frame
        add(createHeaderPanel(" Notifications 🐥"), BorderLayout.NORTH);
        add(createScrollPane(), BorderLayout.CENTER);
        add(createNavigationPanel(), BorderLayout.SOUTH);
    }

    protected JPanel createScrollPane() {
        // Content Panel for notifications
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createNotificationPanel());
        return createContentPanel(panel);
    }

    private JPanel createNotificationPanel() {
        JPanel notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "notifications.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].trim().equals(readUsername())) {
                    // Format the notification message
                    String userWhoLiked = parts[1].trim();
                    String imageId = parts[2].trim();
                    String timestamp = parts[3].trim();
                    String notificationMessage = userWhoLiked + " liked your picture - " + TimeUtils.getElapsedTime(timestamp)
                            + " ago";

                    // Add the notification to the panel
                    JLabel notificationLabel = new JLabel(notificationMessage);
                    notificationPanel.add(notificationLabel, BorderLayout.CENTER);

                    // Add profile icon (if available) and timestamp
                    // ... (Additional UI components if needed)
                    
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return notificationPanel;
    }
}
