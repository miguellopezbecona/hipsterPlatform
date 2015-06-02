package hipster;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import javax.servlet.MultipartConfigElement;

/**
 * 
 * @author Miguel López
 */
public class Server implements Constants{
    private static int port = 5000;

    public static int getPort(){
        return port;
    }

    @SuppressWarnings("serial")
    public static class EventServlet extends WebSocketServlet {

        @Override
        public void configure(WebSocketServletFactory factory) {
            factory.getPolicy().setIdleTimeout(1000*60*30); // 30 minutes
            factory.register(WebSocketServer.class);
        }
    }

    public static void main(String[] args) {
        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server();
        ServerConnector connector = new ServerConnector(server);

        // Imports Heroku's port if it exists
        String portStr = System.getenv("PORT");
        if(portStr != null && !portStr.isEmpty())
            port = Integer.valueOf(portStr);

        connector.setPort(port);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        
        // Create the ResourceHandler. It is the object that will actually handle the request for a given file. It is
        // a Jetty Handler object so it is suitable for chaining with other handlers as you will see in other examples.
        ResourceHandler rh = new ResourceHandler();
        // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
        // In this example it is the current directory but it can be configured to anything that the jvm has access to.
        rh.setDirectoriesListed(false);
        rh.setWelcomeFiles(new String[]{ "index.html" });
        rh.setResourceBase(".");
 
        // Add the ResourceHandler to the server.
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{ rh, context });
        server.setHandler(handlers);

        // Add the websocket to a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", EventServlet.class);
        context.addServlet(holderEvents, "/webSocket/*");

        // Servlet to handle graph upload requests
        ServletHolder uploadHolder = new ServletHolder(new UploadServlet());
        uploadHolder.getRegistration().setMultipartConfig(new MultipartConfigElement(GRAPH_BASE_PATH));
        context.addServlet(uploadHolder, API_BASE + "/graph/*");

        // Servlet to handle websocket port requests
        ServletHolder portHolder = new ServletHolder(new PortServlet());
        context.addServlet(portHolder, API_BASE + "/port/*");

        // REST layout service
        ServletHolder serviceHolder = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        serviceHolder.setInitParameter("jersey.config.server.provider.classnames", LayoutServices.class.getCanonicalName());

        try {
            server.start();
            server.dump(System.err);
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }
}
