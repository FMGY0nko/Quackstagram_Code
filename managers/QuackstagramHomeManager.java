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
                String imagePoster = details[1].split(": ")[1];

                if (followedUsers.contains(imagePoster)) {
                    String imagePath = "img/uploaded/" + details[0].split(": ")[1] + ".png";
                    String description = details[2].split(": ")[1];
                    String likes = "Likes: " + details[4].split(": ")[1];

                    posts.add(new String[]{imagePoster, description, likes, imagePath});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return posts;
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

