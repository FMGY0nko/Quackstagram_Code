package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

// FileManager is a Singleton class responsible for reading and writing files across the whole application
public class FileManager {
    private static FileManager instance;

    // Private constructor so no one else could create another instance of this class
    private FileManager(){ 
        //constructor is left empty because ...
    }

    // Retuns the single instance of the FileManager class
    public static FileManager getInstance(){
        if (instance == null){
            instance = new FileManager();
        }
        return instance;
    }

    // Reads all the lines from a file and returns then as a list of strings
    public List<String> readFile(String filename){
        List<String> lines = new ArrayList<>();
        try (BufferedReader bufferr = new BufferedReader(new FileReader(filename))){
            String line;
            while ((line = bufferr.readLine()) != null){
                lines.add(line); 
            }
        } catch (IOException e){
            e.printStackTrace(); // prints an error message when an the file is not found
        }    
        return lines;
    }

    // Writes all the lines from a list line by line to a file
    public void writeFile(String filename, List<String> lines){
        try (BufferedWriter bufferw = new BufferedWriter(new FileWriter(filename))){
            for (String line : lines){
                bufferw.write(line);
                bufferw.newLine();
            }
        } catch(IOException e){
            e.printStackTrace(); 
        }
    }
}
