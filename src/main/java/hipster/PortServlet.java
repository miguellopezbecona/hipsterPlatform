package hipster;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PortServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        // Returns server port
        response.getWriter().write(Integer.toString(Server.getPort()));
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
