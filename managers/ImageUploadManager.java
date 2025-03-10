package managers;

import utils.FileManager;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.ImageIcon;
import java.awt.*;


public class ImageUploadManager {
    public static String uploadImage(File selectedFile, String caption, String tags, int targetWidth, int targetHeight) {
        try {
            String username = readLoggedInUsername(); // Automatically get the username
            if (username.equals("unknown_user")) {
                System.out.println("Error: Could not determine logged-in user.");
                return null;
            }

            int imageId = getNextImageId(username);
            String fileExtension = getFileExtension(selectedFile);
            String newFileName = username + "_" + imageId + "." + fileExtension;

            Path destPath = Paths.get("img", "uploaded", newFileName);
            Files.createDirectories(destPath.getParent());
            Files.copy(selectedFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

            saveImageInfo(username + "_" + imageId, username, caption, tags);

            return scaleImage(destPath, targetWidth, targetHeight);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while uploading image: " + e.getMessage());
            return null;
        }
    }

    private static int getNextImageId(String username) {
        File imageDir = new File("img/uploaded");
        if (!imageDir.exists()) imageDir.mkdirs();
        String[] files = imageDir.list();
        return (files != null) ? files.length + 1 : 1; // Avoid NullPointerException
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1 || lastIndexOf == name.length() - 1) {
            return ""; // Return empty string for missing/invalid extensions
        }
        return name.substring(lastIndexOf + 1);
    }


    private static void saveImageInfo(String imageId, String username, String caption, String taggedUsers) {
    FileManager fileManager = FileManager.getInstance();
    Path infoFilePath = Paths.get("img", "image_details.txt");

    // Ensure directory exists
    try {
        Files.createDirectories(infoFilePath.getParent());
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Error: Could not create directories for " + infoFilePath);
        return;
    }

    List<String> lines = fileManager.readFile(infoFilePath.toString());

    // Generate timestamp
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    // Append new image details
    String details = String.format("ImageID: %s, Username: %s, Caption: %s, Tagged-Users: %s, Timestamp: %s, Likes: 0",
            imageId, username, caption, taggedUsers, timestamp);

    lines.add(details);
    fileManager.writeFile(infoFilePath.toString(), lines);
    }


    private static String readLoggedInUsername() {
        FileManager fileManager = FileManager.getInstance();
        List<String> lines = fileManager.readFile("data/users.txt");
            if (!lines.isEmpty()) {
                return lines.get(0).split(":")[0].trim(); // Extract the first username from the file
            }
        return "unknown_user"; // Default if no username is found
    }

    private static String scaleImage(Path imagePath, int targetWidth, int targetHeight) {
        ImageIcon imageIcon = new ImageIcon(imagePath.toString());
        Image img = imageIcon.getImage();
        int imgWidth = img.getWidth(null);
        int imgHeight = img.getHeight(null);

        if (imgWidth > 0 && imgHeight > 0) {
            double widthRatio = (double) targetWidth / imgWidth;
            double heightRatio = (double) targetHeight / imgHeight;
            double scale = Math.min(widthRatio, heightRatio);
            int scaledWidth = (int) (scale * imgWidth);
            int scaledHeight = (int) (scale * imgHeight);
            Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImg);
            return imagePath.toString();
        }
        return imagePath.toString();
    }

    public static void sendTagNotification(String taggedUser) {
        FileManager fileManager = FileManager.getInstance();
        String currentuser = readLoggedInUsername();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String notification = String.format("%s; %s; %s; %s; %s\n", "tag", currentuser, taggedUser, getNextImageId(currentuser), timestamp);

        List<String> notifications = fileManager.readFile("data/notifications.txt");
            notifications.add(notification);
        fileManager.writeFile("data/notifications.txt", notifications);
    }
}