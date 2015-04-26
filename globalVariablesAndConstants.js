var ws;
const wsUrl = "ws://localhost:8080/webSocket";

// Keywords
const AVAILABLE_GRAPHS = "AVAILABLE_GRAPHS";
const BEGIN = "BEGIN";
const BUILD_GRAPH = "BUILD_GRAPH";
const NODE = "NODE";
const F_PATH = "F_PATH";
const P_PATH = "P_PATH";

// Layouts and algorithms
const ALGORITHMS = ["DIJKSTRA", "DEPTH", "BREADTH", "BELLMAN_FORD"];
const LAYOUTS = ["random"];
const DEFAULT_LAYOUT = LAYOUTS[0];

// Graphic-related constants
const WIDTH = screen.width;
const HEIGHT = screen.height;
const BASE_RADIUS = 8;
const BASE_W = WIDTH*0.5 - 100;
const BASE_H = HEIGHT*0.5 - 100;
const LINK_DISTANCE = 70;
// These probably won't be constants by the final version
const defaultNodeColor = "#3182bd";
const defaultPathColor = "black";
const highlightPathColor = "green";
const highlightNodeColor = "green";
const possibleNodeColor = "red";
const nextNodeColor = "yellow";

// Other util constants
const HASH_LENGTH = 40;
const SAME_NODE_FEEDBACK = "The initial node can't be the same as the goal one. Set them by clicking the desired node.";
const NULL_NODE_FEEDBACK = "You need to specify both initial and goal nodes first by clicking on them.";
const EXT_SUPPORTED = ["gexf", "json"];
const BASE_URI = "api/graph/"
const GRAPH_BASE_PATH = "graphs/";
const GRAPH_EXAMPLES_FOLDER = "examples/";
const debug = 0;

// Global variables
var svg;
var zoom;
var container;
var showWeights = true;
var forceOS = false;
var links;
var nodes;
