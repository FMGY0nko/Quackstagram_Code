package ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import managers.ImageUploadManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImageUploadUI extends displayUI {
    private JLabel imagePreviewLabel;
    private JTextArea bioTextArea;
    private JTextArea tagsTextArea;
    private JButton uploadButton;
    private JButton saveButton;

    public ImageUploadUI() {
        super("Upload Image");
        initializeUI();
    }

    private void initializeUI() {
        // Add panels to frame
        add(createHeaderPanel(" Upload Image 🐥"), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createNavigationPanel(), BorderLayout.SOUTH);
    }

    protected JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Image preview
        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imagePreviewLabel.setPreferredSize(new Dimension(WIDTH, HEIGHT / 3));
        panel.add(imagePreviewLabel);

        // Bio text area
        bioTextArea = new JTextArea("Enter a caption");
        bioTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        bioTextArea.setLineWrap(true);
        bioTextArea.setWrapStyleWord(true);
        JScrollPane bioScrollPane = new JScrollPane(bioTextArea);
        bioScrollPane.setPreferredSize(new Dimension(WIDTH - 50, HEIGHT / 6));
        panel.add(bioScrollPane);

        // Tags text area
        tagsTextArea = new JTextArea("Tag users (comma separated)");
        tagsTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        tagsTextArea.setLineWrap(true);
        tagsTextArea.setWrapStyleWord(true);
        JScrollPane tagsScrollPane = new JScrollPane(tagsTextArea);
        tagsScrollPane.setPreferredSize(new Dimension(WIDTH - 50, HEIGHT / 6));
        panel.add(tagsScrollPane);

        // Upload button
        uploadButton = new JButton("Upload Image");
        uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadButton.addActionListener(this::uploadAction);
        panel.add(uploadButton);

        // Save button (for bio)
        saveButton = new JButton("Save Caption");
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(this::saveBioAction);
        panel.add(saveButton);

        return createContentPanel(panel);
    }

    private void uploadAction(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an image file");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg"));

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            int previewWidth = imagePreviewLabel.getWidth() > 0 ? imagePreviewLabel.getWidth() : WIDTH;
            int previewHeight = imagePreviewLabel.getHeight() > 0 ? imagePreviewLabel.getHeight() : HEIGHT;

            String imagePath = ImageUploadManager.uploadImage(selectedFile, bioTextArea.getText(), previewWidth, 
            previewHeight);
            //System.out.println("Uploaded image path: " + imagePath);
            if (imagePath != null) {
                imagePreviewLabel.setIcon(new ImageIcon(imagePath));
                uploadButton.setText("Upload Another Image");
                JOptionPane.showMessageDialog(this, "Image uploaded and preview updated!");

                // Process tags
                String tags = tagsTextArea.getText();
                if (!tags.isEmpty()) {
                    String[] taggedUsers = tags.split(",");
                    for (String user : taggedUsers) {
                        user = user.trim();
                        if (!user.isEmpty()) {
                            ImageUploadManager.sendTagNotification(user);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Image upload failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveBioAction(ActionEvent event) {
        // Here you would handle saving the bio text
        String bioText = bioTextArea.getText();
        // For example, save the bio text to a file or database
        JOptionPane.showMessageDialog(this, "Caption saved: " + bioText);
    }
}