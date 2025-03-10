package managers;

import java.util.ArrayList;
import java.util.List;

import models.User;
import utils.FileManager;


public class UserRelationshipManager {
    private static final String FOLLOWING_FILE_PATH = "data/following.txt";
    private static final String IMAGE_DETAILS_FILE_PATH = "img/image_details.txt";
    private static final String CREDENTIALS_FILE_PATH = "data/credentials.txt";

    public static boolean followUser(String currentUser, String usernameToFollow) {
        if (currentUser == null || usernameToFollow == null || currentUser.isEmpty() || usernameToFollow.isEmpty()) {
            return false; // Invalid input
        }

        FileManager fileManager = FileManager.getInstance();
        List<String> lines = fileManager.readFile(FOLLOWING_FILE_PATH);
        boolean found = false;
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts[0].trim().equals(currentUser)) {
                found = true;
                if (!line.contains(usernameToFollow)) {
                    line = line.concat(line.endsWith(":") ? "" : "; ").concat(usernameToFollow);
                }
            }
            updatedLines.add(line);
        }

        if (!found) {
            updatedLines.add(currentUser + ": " + usernameToFollow);
        }

        fileManager.writeFile(FOLLOWING_FILE_PATH, updatedLines);
        return true;
    }

    
    // Method to check if a user is already following another user
    public static boolean isAlreadyFollowing(String currentUser, String usernameToCheck) {
        FileManager fileManager = FileManager.getInstance();
        List<String> lines = fileManager.readFile(FOLLOWING_FILE_PATH);
    
        for (String line : lines) {
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
        return false;
    }
    

    // Method to get the list of followers for a user
    public static int getFollowers(String username) {
        FileManager fileManager = FileManager.getInstance();
        List<String> lines = fileManager.readFile(FOLLOWING_FILE_PATH);
        int count = 0;
    
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length > 1 && parts[1].contains(username)) {
                count++;
            }
        }
        return count;
    }
    
    public static int getFollowing(String username) {
        FileManager fileManager = FileManager.getInstance();
        List<String> lines = fileManager.readFile(FOLLOWING_FILE_PATH);
    
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts[0].trim().equals(username)) {
                return parts[1].split(";").length;
            }
        }
        return 0;
    }
    
    
    // Method to count the number of posts by a user
    public static int getUserPosts(String username) {
        FileManager fileManager = FileManager.getInstance();
        List<String> lines = fileManager.readFile(IMAGE_DETAILS_FILE_PATH);
        int postCount = 0;
    
        for (String line : lines) {
            if (line.contains("Username: " + username)) {
                postCount++;
            }
        }
        return postCount;
    }
    

    // Method to get the bio of a user
    public static String getUserBio(String username) {
        FileManager fileManager = FileManager.getInstance();
        List<String> lines = fileManager.readFile(CREDENTIALS_FILE_PATH);
    
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts[0].equals(username) && parts.length >= 3) {
                return parts[2].trim(); // Bio is stored in the third column
            }
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