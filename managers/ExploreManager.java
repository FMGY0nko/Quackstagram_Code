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
                return details.split(", ");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new String[]{"", "", "", "", "0"}; // Ensure the correct length
    }
}
