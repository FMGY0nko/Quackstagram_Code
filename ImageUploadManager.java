import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class ImageUploadManager {
    public static String uploadImage(File selectedFile, String caption) {
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

            saveImageInfo(username + "_" + imageId, username, caption);

            return destPath.toString();
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


    private static void saveImageInfo(String imageId, String username, String caption) {
        Path infoFilePath = Paths.get("img", "image_details.txt");

        try {
            // Ensure the directory exists before writing the file
            Files.createDirectories(infoFilePath.getParent());

            // Create the file if it does not exist
            File file = infoFilePath.toFile();
            if (!file.exists()) {
                file.createNewFile();
            }

            // Generate timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Write image details
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                String details = String.format("ImageID: %s, Username: %s, Caption: %s, Timestamp: %s, Likes: 0",
                        imageId, username, caption, timestamp);

                writer.write(details);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not save image details for " + imageId);
        }
    }

    private static String readLoggedInUsername() {
        Path usersFilePath = Paths.get("data", "users.txt");
        try (BufferedReader reader = Files.newBufferedReader(usersFilePath)) {
            String line = reader.readLine();
            if (line != null) {
                return line.split(":")[0].trim(); // Extract the first username from the file
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "unknown_user"; // Default if no username is found
    }


}