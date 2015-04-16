package hipster;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import com.google.gson.reflect.TypeToken;
import es.usc.citius.hipster.util.graph.HashBasedHipsterDirectedGraph;

/**
 *
 * @author Miguel López
 */
public class WebSocketServer extends WebSocketAdapter implements Constants{
    private List<Link> links;
    private List<Node> nodes;
    private static Map<Integer, List<Integer>> linkMap;
    private HashBasedHipsterDirectedGraph g;
    private List<String> auxiliarPath;

    public WebSocketServer(){
        auxiliarPath = null;
    }

    private void initializeGraph(String filename, String content){
        // If there is content, it means that the graph was sent by the client and must be converted.
        // If there isn't, the graph must be loaded with a DAO and it's already converted during the load.
        if(content.equals(" "))
            links = DAO.loadGraph(filename);
        else
            links = Constants.gson.fromJson(content, new TypeToken<List<Link>>(){}.getType());

	// Obtains the equivalent object to be used with Hipster
	g = Utils.initializeGraph(links);

        // TODO: this will be deleted when the tree layout is implemented manually
        // Converts "links" to a more manipulable data structure, where eachs nodeId holds all its children
        linkMap = new HashMap<>();
        int source, target;
        for(Link l : links) {
            source = Integer.parseInt(l.getSource());
            target = Integer.parseInt(l.getTarget());

            // Creates the nodes if they don't exist
            if(!linkMap.containsKey(source))
                linkMap.put(source, new ArrayList<Integer>());

            // The target has to be added if we want to include the leaf nodes
            if(!linkMap.containsKey(target))
                linkMap.put(target, new ArrayList<Integer>());

            // Adds the target to the list
            linkMap.get(source).add(target);
        }

        int numNodes = linkMap.size();
        nodes = new ArrayList<>();

        for(int i=0;i<numNodes;i++) {
          Node n = new Node();
          n.setNodeId(i);
          n.setInfo("Has " + random.nextInt() + " as info.");
          nodes.add(n);
        }
    }

    public static Map<Integer, List<Integer>> getLinkMap(){
        return linkMap;
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

    private String buildMessage(String type, String content){
        return "{\"type\": \"" + type + "\", \"content\": " + content + "}";
    }

    private void handleMessage(String message) {
        Message m = gson.fromJson(message, Message.class);
        String type = m.getType();
        String content = m.getContent();
        String response = null;

        switch(type){
	    case BEGIN:
		// Parses the content: filename and its content (if it was loaded by the client)
		String[] fieldsB = content.split("_");
		String filename = fieldsB[0];
		String body = fieldsB[1];

                initializeGraph(filename, body);
                handleLayouts(DEFAULT_LAYOUT);
                break;
	    case LAYOUT:
                handleLayouts(content);
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
                auxiliarPath = null;
		List<String> path = handleAlgorithm(content);

                response = buildMessage(F_PATH, gson.toJson(path));
                try {
                    getSession().getRemote().sendString(response);
		} catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case P_PATH:
		int toSend = 2;

                // Builds path if it is not already generated
		if(auxiliarPath==null || auxiliarPath.isEmpty())
                    auxiliarPath = handleAlgorithm(content);

                // It doesn't send the next node to be expanded if there is only left the end of the path
                else if(auxiliarPath.size()==1)
                    toSend = 1;

                // Returns the first two positions and removes the first from the original list
                List<String> sublist = new ArrayList(auxiliarPath.subList(0,toSend));
                auxiliarPath.remove(0);

                response = buildMessage(P_PATH, gson.toJson(sublist));
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

    private void onStart() {    
    }

    private List<String> handleAlgorithm(String content){
        List<String> path = null;

        // Parses the content: algorithm, initial node and goal node
        String[] fieldsP = content.split(" ");
        String algorithm = fieldsP[0];
        String origin = fieldsP[1];
        String goal = fieldsP[2];

        // Works different for each algorithm
        switch(algorithm){
            case DIJKSTRA: path = HipsterFachade.dijkstra(g, origin, goal); break;
            case DEPTH: path = HipsterFachade.depth(g, origin, goal); break;
            case BREADTH: path = HipsterFachade.breadth(g, origin, goal); break;
            case BELLMAN_FORD: path = HipsterFachade.bellmanFord(g, origin, goal); break;
            default: path = new ArrayList<>(); break;
        }
        return path;
    }

    // TODO: this should be implemented with REST services
    private void handleLayouts(String layout){
        String response = "";
        switch(layout){
            case FORCE:
                response = buildMessage(BUILD_GRAPH, Constants.gson.toJson(links));
                break;
            case TREE:
                response = buildMessage(BUILD_GRAPH, "["+Layout.buildTree(0)+"]");
                break;
        }

        try {
            getSession().getRemote().sendString(response);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
