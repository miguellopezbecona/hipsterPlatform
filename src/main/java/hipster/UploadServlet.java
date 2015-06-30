package hipster;

import com.google.gson.JsonSyntaxException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.servlet.annotation.MultipartConfig;

@MultipartConfig
public class UploadServlet extends HttpServlet implements Constants{

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        Part filePart = null;

        // Obtains data from the form
        try {
            filePart = request.getPart("graphToUpload");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String extension = getExtension(filePart);

        String hash = null;
        InputStream fileContent = null;
        try {
            // Obtains file's content
            fileContent = filePart.getInputStream();

            // Tests file format. If it's okay, it's passed to the DAO in order to save the graph and receive the associated hash
            if(hasValidFormat(fileContent, extension))
                hash = DAO.saveGraph(fileContent, extension);
        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
        } finally {
            if (fileContent != null) fileContent.close();
        }

        // Sends one state or another depending of save's result (defined by the returned hash)
        if(hash == null)
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        else {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/html");          
            response.getWriter().write(hash + "." + extension);
        }
    }

    private String getExtension(Part part) {
        // Parses the header so it maintains only the filename
        String filename = part.getHeader("content-disposition").split("filename=\"")[1].replace("\"", "");

        return getExtension(filename);
    }

    private String getExtension(String filename) {
        return filename.split("\\.")[filename.split("\\.").length-1].toLowerCase();
    }

    private boolean hasValidFormat(InputStream is, String extension){
        switch(extension){
            case "json":
                MyGraph g = null;
                is.mark(Integer.MAX_VALUE);
                try {
                    g = gson.fromJson(new InputStreamReader(is), MyGraph.class);
                    is.reset();

                    // A valid graph must, at least, have link information
                    return ( (g != null) && (g.getLinks() != null) );
                } catch (IOException | JsonSyntaxException ex) {
                    return false;
                }
            case "gexf":
                return GEXFParser.isValid(is);
            default:
                return false;
        }
    }
}
