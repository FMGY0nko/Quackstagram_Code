package managers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import models.User;

public class UserRelationshipManager {
    private static final String FOLLOWING_FILE_PATH = "data/following.txt";
    private static final String IMAGE_DETAILS_FILE_PATH = "img/image_details.txt";
    private static final String CREDENTIALS_FILE_PATH = "data/credentials.txt";

    public static boolean followUser(String currentUser, String usernameToFollow) {
        if (currentUser == null || usernameToFollow == null || currentUser.isEmpty() || usernameToFollow.isEmpty()) {
            return false; // Invalid input
        }

        boolean found = false;
        StringBuilder newContent = new StringBuilder();

        try {
            // Read and process following.txt
            if (Files.exists(Paths.get(FOLLOWING_FILE_PATH))) {
                try (BufferedReader reader = Files.newBufferedReader(Paths.get(FOLLOWING_FILE_PATH))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(":");
                        if (parts[0].trim().equals(currentUser)) {
                            found = true;
                            if (!line.contains(usernameToFollow)) {
                                line = line.concat(line.endsWith(":") ? "" : "; ").concat(usernameToFollow);
                            }
                        }
                        newContent.append(line).append("\n");
                    }
                }
            }

            // If the user was not found in following.txt, add them
            if (!found) {
                newContent.append(currentUser).append(": ").append(usernameToFollow).append("\n");
            }

            // Write the updated content back to following.txt
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FOLLOWING_FILE_PATH))) {
                writer.write(newContent.toString());
            }
            return true; // Successfully followed the user

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Follow action failed
    }

    
    // Method to check if a user is already following another user
    public static boolean isAlreadyFollowing(String currentUser, String usernameToCheck) {
        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(FOLLOWING_FILE_PATH).toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].trim().equals(currentUser)) {
                    String[] followedUsers = parts[1].split(";");
                    for (String followedUser : followedUsers) {
                        if (followedUser.trim().equals(usernameToCheck)) {
                            return true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to get the list of followers for a user
    public static int getFollowers(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FOLLOWING_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].trim().equals(username)) {
                    return parts[1].split(";").length;
                }
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Method to get the list of users a user is following
    public static int getFollowing(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FOLLOWING_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].trim().equals(username)) {
                    return parts[1].split(";").length;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Method to count the number of posts by a user
    public static int getUserPosts(String username) {
        int postCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(IMAGE_DETAILS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Username: " + username)) {
                    postCount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return postCount;
    }

    // Method to get the bio of a user
    public static String getUserBio(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username) && parts.length >= 3) {
                    return parts[2]; // Bio is stored in the third column
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No bio available.";
    }

    public static void loadUserData(User user) {
        user.setPostCount(getUserPosts(user.getUsername()));
        user.setFollowersCount(getFollowers(user.getUsername()));
        user.setFollowingCount(getFollowing(user.getUsername()));
        user.setBio(getUserBio(user.getUsername()));
    } 
}