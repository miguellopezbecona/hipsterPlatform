package hipster;

import es.usc.citius.hipster.util.graph.HashBasedHipsterDirectedGraph;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

/**
 *
 * @author Miguel López
 */
public class WebSocketServer extends WebSocketAdapter implements Constants{
    private MyGraph graph;
    private HashBasedHipsterDirectedGraph hipsterGraph;
    private Iterator it;

    public WebSocketServer(){
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        handleMessage(message);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
    }

    private void handleMessage(String message) {
        Message m = gson.fromJson(message, Message.class);
        String type = m.getType();
        String content = m.getContent();
        String response = null;

        switch(type){
	    case BEGIN:
		initializeGraph(content);
                break;
            case NODE:
                int id = Integer.valueOf(content);
                String nodeInfo = gson.toJson(graph.getNodes().get(id));
                response = buildMessage(NODE, nodeInfo);
                sendMessage(response);
                break;
            case F_PATH:
		List<String> path = handleAlgorithm(content, true);

                response = buildMessage(F_PATH, gson.toJson(path));
                sendMessage(response);
                break;
            case P_PATH:
		List<String> nextNodes = handleAlgorithm(content, false);

                response = buildMessage(P_PATH, gson.toJson(nextNodes));
                sendMessage(response);
                break;
            case AVAILABLE_GRAPHS:
                List<String> graphs = Utils.obtainAvailableGraphs();
                response = buildMessage(AVAILABLE_GRAPHS, gson.toJson(graphs));
                sendMessage(response);
                break;
            default:
		// Generic message
		System.out.println("Message type: " + type + ", content: " + content);
        }
    }

    private String buildMessage(String type, String content){
        return "{\"type\": \"" + type + "\", \"content\": " + content + "}";
    }

    private void sendMessage(String message){
        try {
            getSession().getRemote().sendString(message);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initializeGraph(String filename){
        String extension = filename.split("\\.")[filename.split("\\.").length-1].toLowerCase();

        // -1 is include because of the dot "."
        boolean isExample = ( filename.length() - extension.length() - 1) != HASH_LENGTH;
        graph = DAO.loadGraph(filename, isExample);

	// Obtains the equivalent object to be used with Hipster
	hipsterGraph = Utils.initializeGraph(graph.getLinks());

        // Builds node info if it doesn't exist
        graph.initializeRandomNodes();

        it = null;
    }

    private List<String> handleAlgorithm(String content, boolean oneStep){
        List<String> path = null;

        // Parses the content: algorithm, initial node and goal node
        String[] fieldsP = content.split(" ");
        String algorithm = fieldsP[0];
        String origin = fieldsP[1];
        String goal = fieldsP[2];

        // Works different for each algorithm
        if(oneStep){
            it = null;
            switch(algorithm){
                case DIJKSTRA: path = HipsterFacade.dijkstraOS(hipsterGraph, origin, goal); break;
                case DEPTH: path = HipsterFacade.depthOS(hipsterGraph, origin, goal); break;
                case BREADTH: path = HipsterFacade.breadthOS(hipsterGraph, origin, goal); break;
                case BELLMAN_FORD: path = HipsterFacade.bellmanFordOS(hipsterGraph, origin, goal); break;
                default: path = new ArrayList<>(); break;
            }
        } else {
            if(it == null)
            switch(algorithm){
                case DIJKSTRA: it = HipsterFacade.dijkstraIt(hipsterGraph, origin, goal); break;
                case DEPTH: it = HipsterFacade.depthIt(hipsterGraph, origin, goal); break;
                case BREADTH: it = HipsterFacade.breadthIt(hipsterGraph, origin, goal); break;
                case BELLMAN_FORD: it = HipsterFacade.bellmanFordIt(hipsterGraph, origin, goal); break;
           }
           path = Utils.handleIterator(it, goal);
        }
        return path;
    }
}
