package hipster;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ParamServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        // Returns connection params
        String toSend = Server.getHost();

        if(toSend.equals("localhost"))
            toSend += ":" + Integer.toString(Server.getPort());

        response.getWriter().write(toSend);
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
