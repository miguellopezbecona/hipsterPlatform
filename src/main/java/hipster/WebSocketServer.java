package hipster;

import com.google.gson.reflect.TypeToken;
import es.usc.citius.hipster.model.function.HeuristicFunction;
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

    // Auxilial variables used for heuristic functions
    private MyGraph auxGraph;
    private String goalState;

    /*** Heuristic functions ***/
    // Euclidean distance: sqrt( (x2-x1)^2 + (y2-y1)^2 )
    private HeuristicFunction<String, Double> euclideanDistance = new HeuristicFunction<String, Double>() {
        @Override
        public Double estimate(String state) {
            MyNode processingNode = auxGraph.getNode(state);
            MyNode goalNode = auxGraph.getNode(goalState);
            double xPart = Math.pow( goalNode.getX() - processingNode.getX(), 2);
            double yPart = Math.pow( goalNode.getY() - processingNode.getY(), 2);

            return Math.sqrt(xPart + yPart);
        }
    };

    // Manhattan distance: abs(x2-x1) + abs(y2-y1)
    private HeuristicFunction<String, Double> manhattanDistance = new HeuristicFunction<String, Double>() {
        @Override
        public Double estimate(String state) {
            MyNode processingNode = auxGraph.getNode(state);
            MyNode goalNode = auxGraph.getNode(goalState);
            double xPart = Math.abs( goalNode.getX() - processingNode.getX() );
            double yPart = Math.abs( goalNode.getY() - processingNode.getY() );

            return xPart + yPart;
        }
    };

    public WebSocketServer(){
        auxGraph = new MyGraph();
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
        Message m;
        try {
            m = gson.fromJson(message, Message.class);
        } catch (Exception e) {
            if(message != null)
                System.out.println("Message received: " + message);
            return;
        }

        if(m == null)
            return;

        String type = m.getType();
        String content = m.getContent();
        String response = null;

        if(type == null || content == null)
            return;

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

        // The DAO will return null if the saved graph has a bad format. This should never happen.
        if(graph == null)
            return;

	// Obtains the equivalent object to be used with Hipster
	hipsterGraph = Utils.initializeGraph(graph.getLinks());

        // Builds node info if it doesn't exist
        graph.initializeRandomNodes();

        it = null;
    }

    private List<String> handleAlgorithm(String content, boolean oneStep){
        List<String> path = null;

        // Parses the content: algorithm, initial node, goal node and heuristic table
        String[] fields = content.split("_");
        String algorithm = fields[0];
        String origin = fields[1];
        String goal = fields[2];

        List<MyNode> nodeInfo = null;
        HeuristicFunction<String, Double> hf = null;

        // If there is extra info, it means that an heuristic algorithm was selected
        if(fields.length == 5){
            String heuristic = fields[3];
            nodeInfo = gson.fromJson(fields[4],new TypeToken<List<MyNode>>(){}.getType());
            auxGraph.setNodes(nodeInfo);
            goalState = goal;

            switch(heuristic){
                case EUCLIDEAN:
                    hf = euclideanDistance;
                    break;
                case MANHATTAN:
                    hf = manhattanDistance;
                    break;
            }
        }

        // Works different for each algorithm
        if(oneStep){
            it = null;
            switch(algorithm){
                case DIJKSTRA: path = HipsterFacade.dijkstraOS(hipsterGraph, origin, goal); break;
                case DEPTH: path = HipsterFacade.depthOS(hipsterGraph, origin, goal); break;
                case BREADTH: path = HipsterFacade.breadthOS(hipsterGraph, origin, goal); break;
                case BELLMAN_FORD: path = HipsterFacade.bellmanFordOS(hipsterGraph, origin, goal); break;
                case A_STAR: path = HipsterFacade.AStarOS(hipsterGraph, origin, goal, hf); break;
                default: path = new ArrayList<>(); break;
            }
        } else {
            if(it == null)
            switch(algorithm){
                case DIJKSTRA: it = HipsterFacade.dijkstraIt(hipsterGraph, origin, goal); break;
                case DEPTH: it = HipsterFacade.depthIt(hipsterGraph, origin, goal); break;
                case BREADTH: it = HipsterFacade.breadthIt(hipsterGraph, origin, goal); break;
                case BELLMAN_FORD: it = HipsterFacade.bellmanFordIt(hipsterGraph, origin, goal); break;
                case A_STAR: it = HipsterFacade.AStarIt(hipsterGraph, origin, goal, hf); break;
           }
           path = Utils.handleIterator(it, goal);
        }
        return path;
    }
}
