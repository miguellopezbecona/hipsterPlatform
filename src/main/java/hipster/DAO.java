package hipster;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 *
 * @author Miguel
 */
public class DAO implements Constants{
    public synchronized static List<Link> loadGraph(String filename, boolean isExample){
        // Adding a bit of security: it doesn't allow files that contain "#", ";", "/" or ".."
        if(filename.matches(".*([#;/]|\\.\\.).*"))
            return null;

        String path = GRAPH_BASE_PATH;
        if(isExample)
            path += GRAPH_EXAMPLES_FOLDER + filename;
        else
            path += filename;

        String extension = filename.split("\\.")[filename.split("\\.").length-1].toLowerCase();

        try {
            // TODO: Different parsing depending on file's extension
            switch(extension){
                case "json":
                    return gson.fromJson(new FileReader(path), new TypeToken<List<Link>>(){}.getType());
                default:
                    return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public synchronized static String saveGraph(String extension, String content){
        // Adding a bit of security: it doesn't allow files that contain "#", ";", "/"
        if(extension.matches(".*[#;/].*"))
            return null;

        String hash = Utils.generateHash(Long.toString(System.currentTimeMillis()));

        // The used filename will consist in a hash of the system's current time
        String filename = hash + "." + extension;

        String path = GRAPH_BASE_PATH + filename;
        PrintWriter writer;
        try {
            writer = new PrintWriter(path, "UTF-8");
            writer.println(content);
            writer.close();
            return hash;
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            return null;
        }
    }    
}
