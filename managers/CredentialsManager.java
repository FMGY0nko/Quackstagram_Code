package managers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import models.User;
import utils.FileManager;

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
        FileManager fileManager = FileManager.getInstance(); // Use Singleton FileManager
        List<String> lines = fileManager.readFile(CREDENTIALS_FILE);
    
        String hashedPassword = hashPassword(password); // Hash the password
    
        if (hashedPassword != null) {
            String userData = username + ":" + hashedPassword + ":" + bio;
            lines.add(userData); 
    
            fileManager.writeFile(CREDENTIALS_FILE, lines); 
        }
    }    

    // Method to validate user login credentials (called from SignInUI)
    public static User verifyCredentials(String username, String password) {
        FileManager fileManager = FileManager.getInstance(); // use the singleton instance
        List<String> lines = fileManager.readFile(CREDENTIALS_FILE);
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length >= 3 && parts[0].equals(username)) {
                    String storedHashedPassword = parts[1];
                    return new User(username, parts[2], storedHashedPassword); // Bio is at parts[2]
                }
            }
        return null; // Return null if authentication fails
    }

    // Method to check if a username already exists (called from SignUpUI)
    public static boolean userExists(String username) {
        FileManager fileManager = FileManager.getInstance(); // use the singleton instance
        List<String> lines = fileManager.readFile(CREDENTIALS_FILE);
            for (String line : lines) {
                if (line.startsWith(username + ":")) {
                    return true;
                }
            }
        return false;
    }
}
