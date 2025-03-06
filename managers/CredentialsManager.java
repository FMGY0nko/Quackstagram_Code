package managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import models.User;

public class CredentialsManager {
private static final String CREDENTIALS_FILE = "data/credentials.txt";

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

    // Method to save user credentials (called from SignUpUI)
    public static void saveUserCredentials(String username, String password, String bio) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS_FILE, true))) {
            writer.write(username + ":" + password + ":" + bio);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to validate user login credentials (called from SignInUI)
    public static User verifyCredentials(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(":");
                if (credentials[0].equals(username) && credentials[1].equals(password)) {
                    String bio = credentials.length > 2 ? credentials[2] : "";
                    return new User(username, bio, password); // Return the authenticated User object
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null if authentication fails
    }

}
