package hipster;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import com.google.gson.reflect.TypeToken;

/**
 *
 * @author Miguel
 */
public class DAO implements Constants{
    public static List<Link> loadGraph(String filename, boolean isExample){
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
            // Different parsing depending on file's extension 
            switch(extension){
                case "json":
                    return gson.fromJson(new FileReader(path), new TypeToken<List<Link>>(){}.getType());
                case "gexf":
                    return GEXFParser.getLinks(path);
                default:
                    return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String saveGraph(InputStream content, String extension){
        // Adding a bit of security: it doesn't allow files that contain "#", ";", "/"
        if(extension.matches(".*[#;/].*"))
            return null;

        String hash = Utils.generateHash(content);

        // The used filename will consist in a hash of the system's current time
        String filename = hash + "." + extension;

        String path = GRAPH_BASE_PATH + filename;
        OutputStream out = null;
        try {
            File f = new File(GRAPH_BASE_PATH + filename);

            // It won't write the graph if it already exists
            if(f.exists())
                return hash;

            out = new FileOutputStream(f);

            int read = 0;
            final byte[] bytes = new byte[CHUNK_SIZE];

            // Writes to the file in chunks
            while ((read = content.read(bytes)) != -1)
                out.write(bytes, 0, read);

            return hash;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (out != null) { try { out.close(); } catch (IOException e) { e.printStackTrace(); } } 
        }
    }
}
