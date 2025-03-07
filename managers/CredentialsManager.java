package managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import models.User;

public class CredentialsManager {
private static final String CREDENTIALS_FILE = "data/credentials.txt";

    // Method to hash password using SHA-256
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to save user credentials (called from SignUpUI)
    public static void saveUserCredentials(String username, String password, String bio) {
        String hashedPassword = hashPassword(password);
        if (hashedPassword != null) {
            String userData = username + ":" + hashedPassword + ":" + bio + "\n";
            try {
                Files.write(Paths.get(CREDENTIALS_FILE), userData.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to validate user login credentials (called from SignInUI)
    public static User verifyCredentials(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 3 && parts[0].equals(username)) {
                    String storedHashedPassword = parts[1];
                    return new User(username, parts[2], storedHashedPassword); // Bio is at parts[2]
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null if authentication fails
    }

    // Method to check if a username already exists (called from SignUpUI)
    public static boolean userExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(username + ":")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
