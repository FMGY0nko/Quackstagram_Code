package managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuackstagramHomeManager {
    private static final Path FOLLOWING_FILE = Paths.get("data", "following.txt");
    private static final Path IMAGE_DETAILS_FILE = Paths.get("img", "image_details.txt");
    private static final Path NOTIFICATIONS_FILE = Paths.get("data", "notifications.txt");

    public static List<String> getFollowedUsers(String currentUser) {
        try (BufferedReader reader = Files.newBufferedReader(FOLLOWING_FILE)) {
            return reader.lines()
                    .filter(line -> line.startsWith(currentUser + ":"))
                    .flatMap(line -> Stream.of(line.split(":")[1].split(";")))
                    .map(String::trim)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<String[]> getFeedPosts(String currentUser) {
        List<String[]> posts = new ArrayList<>();
        List<String> followedUsers = getFollowedUsers(currentUser);
    
        try (BufferedReader reader = Files.newBufferedReader(IMAGE_DETAILS_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(", ");
                String imagePoster = getFieldValue(details, "Username");
    
                if (followedUsers.contains(imagePoster)) {
                    String imageId = getFieldValue(details, "ImageID");
                    String imagePath = "img/uploaded/" + imageId + ".png";
                    String description = getFieldValue(details, "Bio");
                    String tags = getFieldValue(details, "Tagged-Users"); // Handle both "Tagged-Users" and "Tags"
                    if (tags.isEmpty()) {
                        tags = getFieldValue(details, "Tags"); // Fallback to "Tags" if "Tagged-Users" is not found
                    }
                    String likes = "Likes: " + getFieldValue(details, "Likes");
                    String timestamp = getFieldValue(details, "Timestamp");
    
                    posts.add(new String[]{imagePoster, description, likes, imagePath, tags});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return posts;
    }
    
    // Helper method to get the value of a field by its label
    private static String getFieldValue(String[] details, String fieldName) {
        for (String part : details) {
            if (part.startsWith(fieldName + ": ")) {
                return part.substring(fieldName.length() + 2).trim();
            }
        }
        return ""; // Return empty string if the field is not found
    }

    public static void updateLikeCount(String imageId, String currentUser) {
        StringBuilder updatedContent = new StringBuilder();
        boolean updated = false;
        String imageOwner = "";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (BufferedReader reader = Files.newBufferedReader(IMAGE_DETAILS_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ImageID: " + imageId)) {
                    String[] parts = line.split(", ");
                    imageOwner = parts[1].split(": ")[1];
                    int likes = Integer.parseInt(parts[4].split(": ")[1]) + 1;
                    parts[4] = "Likes: " + likes;
                    line = String.join(", ", parts);
                    updated = true;
                }
                updatedContent.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (updated) {
            try (BufferedWriter writer = Files.newBufferedWriter(IMAGE_DETAILS_FILE)) {
                writer.write(updatedContent.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Save notification
            String notification = String.format("%s; %s; %s; %s\n", imageOwner, currentUser, imageId, timestamp);
            try (BufferedWriter notificationWriter = Files.newBufferedWriter(NOTIFICATIONS_FILE,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                notificationWriter.write(notification);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}