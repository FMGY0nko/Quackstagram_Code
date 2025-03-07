package ui;
import javax.swing.*;
import utils.TimeUtils;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
                // Checks whether the notification is a tag or not 
                if (parts[0].trim().equals("tag")) {
                    if (parts[2].trim().equals(readUsername())) {
                        String tagger = parts[1].trim();
                        String timestamp = parts[4].trim();

                        String notificationMessage = tagger + " tagged you in a photo - " + TimeUtils.getElapsedTime(timestamp)
                            + " ago";

                        // Add the notification to the panel
                        JLabel notificationLabel = new JLabel(notificationMessage);
                        notificationPanel.add(notificationLabel, BorderLayout.CENTER);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return notificationPanel;
    }
}
