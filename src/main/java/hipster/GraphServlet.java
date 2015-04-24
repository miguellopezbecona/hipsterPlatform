package hipster;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.servlet.annotation.MultipartConfig;

@MultipartConfig
public class GraphServlet extends HttpServlet implements Constants{

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        // Obtains data from the form
        Part filePart = request.getPart("graphToUpload");
        String extension = getExtension(filePart);

        String hash = null;
        InputStream fileContent = null;
        try {
            // Obtains file's content and is passed to the DAO in order to save the graph and receive the associated hash
            fileContent = filePart.getInputStream();
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        // Obtains the whole name
        String hash = request.getParameter("hash");
        String extension = getExtension(hash);
        String resp = DAO.loadGraph(hash);

        response.setContentType("text/html");   

        // Sends one state or another depending of save's result (defined by the returned hash)
        if(resp == null)
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "the specified file doesn't exist.");
        else {
            response.setStatus(HttpServletResponse.SC_OK);        
            response.getWriter().write(resp);
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
}
