package hipster;

import java.util.Random;
import com.google.gson.Gson;

/**
 *
 * @author Miguel López
 */
public interface Constants {
    /*** KEYWORDS ***/
    public final String AVAILABLE_GRAPHS = "AVAILABLE_GRAPHS";
    public final String BEGIN = "BEGIN";
    public final String NODE = "NODE";
    public final String F_PATH = "F_PATH";
    public final String P_PATH = "P_PATH";
    public final String BUILD_GRAPH = "BUILD_GRAPH";
    public final String LAYOUT = "LAYOUT";

    /*** ALGORITHMS ***/
    public final String DIJKSTRA = "DIJKSTRA";
    public final String BREADTH = "BREADTH";
    public final String DEPTH = "DEPTH";
    public final String BELLMAN_FORD = "BELLMAN_FORD";

    /*** LAYOUTS ***/
    public final String FORCE = "FORCE";
    public final String TREE = "TREE";
    public final String DEFAULT_LAYOUT = FORCE;

    /*** OTHERS AND UTILS ***/
    public final Random random = new Random(System.currentTimeMillis());
    public final Gson gson = new Gson();
    public final String GRAPH_BASE_PATH = "graphs/";
}
