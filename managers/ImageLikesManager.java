package managers;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import utils.FileManager;

public class ImageLikesManager {

    private final String likesFilePath = "data/likes.txt";

    // Method to like an image
    public void likeImage(String username, String imageID) throws IOException {
        Map<String, Set<String>> likesMap = readLikes();
        if (!likesMap.containsKey(imageID)) {
            likesMap.put(imageID, new HashSet<>());
        }
        Set<String> users = likesMap.get(imageID);
        if (users.add(username)) { // Only add and save if the user hasn't already liked the image
            saveLikes(likesMap);
        }
    }

    // Method to read likes from file
    private Map<String, Set<String>> readLikes() throws IOException {
        Map<String, Set<String>> likesMap = new HashMap<>();
        FileManager fileManager = FileManager.getInstance();
        List<String> lines = fileManager.readFile(likesFilePath);
        for (String line : lines) {
                String[] parts = line.split(":");
                String imageID = parts[0];
                Set<String> users = Arrays.stream(parts[1].split(",")).collect(Collectors.toSet());
                likesMap.put(imageID, users);
            }
        return likesMap;
    }

    // Method to save likes to file
    private void saveLikes(Map<String, Set<String>> likesMap) throws IOException {
        FileManager fileManager = FileManager.getInstance(); // Use Singleton
        List<String> lines = new ArrayList<>();
            for (Map.Entry<String, Set<String>> entry : likesMap.entrySet()) {
                String line = entry.getKey() + ":" + String.join(",", entry.getValue());
                lines.add(line);
            }
        fileManager.writeFile(likesFilePath, lines);  
    }

}
