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

    /*** ALGORITHMS ***/
    public final String DIJKSTRA = "Dijkstra";
    public final String BREADTH = "Breadth";
    public final String DEPTH = "Depth";
    public final String BELLMAN_FORD = "Bellman-Ford";
    public final String A_STAR = "A*";

    /*** HEURISTICS ***/
    public final String EUCLIDEAN = "Euclidean";
    public final String MANHATTAN = "Manhattan";

    /*** LAYOUTS ***/
    public final String RANDOM = "random";
    public final String CIRCLE = "circle";
    public final String FR = "fruchterman-reingold";
    public final String GRID = "grid";
    public final String ISOM = "isom";
    public final String KK = "kamada-kawai";
    public final String SPRING = "spring";

    /*** OTHERS CONSTANTS AND UTILS ***/
    public final int GRID_DISTANCE = 100; // Distance to consecutive nodes in Grid layout
    public final double INVERSE_GRID_DISTANCE = Math.pow(GRID_DISTANCE, -1); // Precalculated value
    public final Random random = new Random(System.currentTimeMillis());
    public final Gson gson = new Gson();
    public final String API_BASE = "/api";
    public final String GRAPH_BASE_PATH = "graphs/";
    public final String GRAPH_EXAMPLES_FOLDER = "examples/";
    public final String HASH_ALGORITHM = "SHA-1";
    public final short HASH_LENGTH = 40;
    public final short CHUNK_SIZE = 1024;
    public final short DEFAULT_PORT = 8080;
    public final String DEFAULT_HOSTNAME = "localhost";
    public final String HEROKU_APP = "hipster-platform.herokuapp.com";
}
