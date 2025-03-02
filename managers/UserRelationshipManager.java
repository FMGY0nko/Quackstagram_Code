package managers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import models.User;

public class UserRelationshipManager {

    private final String followersFilePath = "data/followers.txt";
    private static final String FOLLOWING_FILE_PATH = "data/following.txt";
    //private static final String FOLLOWERS_FILE_PATH = "data/following.txt";
    private static final String IMAGE_DETAILS_FILE_PATH = "img/image_details.txt";
    private static final String CREDENTIALS_FILE_PATH = "data/credentials.txt";

    // Method to follow a user
    public void followUser(String follower, String followed) throws IOException {
        if (!isAlreadyFollowing(follower, followed)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(followersFilePath, true))) {
                writer.write(follower + ":" + followed);
                writer.newLine();
            }
        }
    }

    // Method to check if a user is already following another user
    private boolean isAlreadyFollowing(String follower, String followed) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(followersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(follower + ":" + followed)) {
                    return true;
                }
            }
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
