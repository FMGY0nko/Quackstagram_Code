package managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ExploreManager {
    public static File[] loadPostImages() {
        File imageDir = new File("img/uploaded");
        if (imageDir.exists() && imageDir.isDirectory()) {
            return imageDir.listFiles((dir, name) -> name.matches(".*\\.(png|jpg|jpeg)"));
        }
        return new File[]{}; // Return empty array if no images are found
    }

    public static String[] getImageDetails(String imageId) {
        Path detailsPath = Paths.get("img", "image_details.txt");
        try (Stream<String> lines = Files.lines(detailsPath)) {
            String details = lines
                    .filter(line -> line.contains("ImageID: " + imageId))
                    .findFirst()
                    .orElse("");
    
            if (!details.isEmpty()) {
                // Split the details into key-value pairs
                String[] parts = details.split(", ");
                String[] result = new String[6]; // Ensure the correct length: ImageID, Username, Bio, Tags, Timestamp, Likes
                result[0] = imageId; // ImageID is already known
                result[5] = "0"; // Default likes value
    
                for (String part : parts) {
                    if (part.startsWith("Username: ")) {
                        result[1] = part.substring("Username: ".length());
                    } else if (part.startsWith("Bio: ")) {
                        result[2] = part.substring("Bio: ".length());
                    } else if (part.startsWith("Tagged-Users: ") || part.startsWith("Tags: ")) {
                        result[3] = part.substring(part.indexOf(": ") + 2); // Handle both "Tagged-Users" and "Tags"
                    } else if (part.startsWith("Timestamp: ")) {
                        result[4] = part.substring("Timestamp: ".length());
                    } else if (part.startsWith("Likes: ")) {
                        result[5] = part.substring("Likes: ".length());
                    }
                }
    
                return result;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new String[]{"", "", "", "", "", "0"}; // Ensure the correct length
    }
}
