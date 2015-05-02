package hipster;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import es.usc.citius.hipster.util.graph.HashBasedHipsterDirectedGraph;

/**
 *
 * @author Miguel López
 */
public class WebSocketServer extends WebSocketAdapter implements Constants{
    private List<Link> links;
    private List<Node> nodes;
    private HashBasedHipsterDirectedGraph g;

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
		// In this case, the content is just the filename
		// If the filename refers to a hash, its length (without the extension) will be always 40 (for SHA-1 algorithm)
                if(content.split("\\.")[0].length()==HASH_LENGTH)
                    links = DAO.loadGraph(content, false);

                // If not, it is an example graph (no example will have a filename length of 40)
                else
                    links = DAO.loadGraph(content, true);

                initializeGraph();
                break;
            case NODE:
                int id = Integer.valueOf(content);
                String nodeInfo = gson.toJson(nodes.get(id));
                response = buildMessage(NODE, nodeInfo);
                try {
                    getSession().getRemote().sendString(response);
		} catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case F_PATH:
		List<String> path = handleAlgorithm(content, true);

                response = buildMessage(F_PATH, gson.toJson(path));
                try {
                    getSession().getRemote().sendString(response);
		} catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case P_PATH:
		List<String> nextNodes = handleAlgorithm(content, false);

                response = buildMessage(P_PATH, gson.toJson(nextNodes));
                try {
                    getSession().getRemote().sendString(response);
		} catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case AVAILABLE_GRAPHS:
                List<String> graphs = Utils.obtainAvailableGraphs();
                response = buildMessage(AVAILABLE_GRAPHS, gson.toJson(graphs));
                try {
                    getSession().getRemote().sendString(response);
		} catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            default:
		// Generic message
		System.out.println("Message type: " + type + ", content: " + content);
        }
    }

    private String buildMessage(String type, String content){
        return "{\"type\": \"" + type + "\", \"content\": " + content + "}";
    }

    private void initializeGraph(){
	// Obtains the equivalent object to be used with Hipster
	g = Utils.initializeGraph(links);

        // Builds node info
        int numNodes = links.size() + 1;
        nodes = new ArrayList<>();

        for(int i=0;i<numNodes;i++) {
          Node n = new Node();
          n.setId(i);
          n.setInfo("Has " + random.nextInt() + " as info.");
          nodes.add(n);
        }

        HipsterFacade.resetIt();
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
            switch(algorithm){
                case DIJKSTRA: path = HipsterFacade.dijkstraOS(g, origin, goal); break;
                case DEPTH: path = HipsterFacade.depthOS(g, origin, goal); break;
                case BREADTH: path = HipsterFacade.breadthOS(g, origin, goal); break;
                case BELLMAN_FORD: path = HipsterFacade.bellmanFordOS(g, origin, goal); break;
                default: path = new ArrayList<>(); break;
            }
        } else {
            switch(algorithm){
                case DIJKSTRA: path = HipsterFacade.dijkstraSbS(g, origin, goal); break;
                case DEPTH: path = HipsterFacade.depthSbS(g, origin, goal); break;
                case BREADTH: path = HipsterFacade.breadthSbS(g, origin, goal); break;
                case BELLMAN_FORD: path = HipsterFacade.bellmanFordSbS(g, origin, goal); break;
                default: path = new ArrayList<>(); break;
           }
        }
        return path;
    }
}
