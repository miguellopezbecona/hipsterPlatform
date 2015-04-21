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
public class UploadServlet extends HttpServlet implements Constants{

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        // Obtains data from the form
        Part filePart = request.getPart("graphToUpload");
        String extension = getExtension(filePart);

        WebSocketServer wss = new WebSocketServer(); // Doesn't obtain right server instance...
        String hash = null;
        InputStream fileContent = null;
        try {
            // Obtains file's content and is passed to the DAO in order to save the graph and receive the associated hash
            fileContent = filePart.getInputStream();
            hash = DAO.saveGraph(fileContent, extension);

            // Resets the stream (the DAO will use it until its end)
            fileContent = filePart.getInputStream();

            List<Link> links = DAO.obtainLinks(fileContent, extension);
            wss.setLinks(links);
            wss.initializeGraph();
            //wss.sendHash(hash, extension); // Will crash
        } catch (FileNotFoundException fne) {
            fne.printStackTrace();
        } finally {
            if (fileContent != null) fileContent.close();
        }

        // Redirects to the view
        response.sendRedirect("index.html");
        return;
    }

    private String getExtension(Part part) {
        // Parses the header so it maintains only the filename
        String filename = part.getHeader("content-disposition").split("filename=\"")[1].replace("\"", "");

        return filename.split("\\.")[filename.split("\\.").length-1].toLowerCase();
    }
}
