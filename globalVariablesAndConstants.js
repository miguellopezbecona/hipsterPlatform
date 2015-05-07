var ws;
const wsUrl = "ws://localhost:8080/webSocket";

// Keywords
const AVAILABLE_GRAPHS = "AVAILABLE_GRAPHS";
const BEGIN = "BEGIN";
const NODE = "NODE";
const F_PATH = "F_PATH";
const P_PATH = "P_PATH";
const ORIGINAL = "ORIGINAL";

// Layouts and algorithms
const ALGORITHMS = ["DIJKSTRA", "DEPTH", "BREADTH", "BELLMAN_FORD"];
const LAYOUTS = ["Circle", "Fruchterman-Reingold", "Grid", "ISOM", "Kamada-Kawai", "Random", "Spring"];

// Graphic-related constants
const WIDTH = screen.width;
const HEIGHT = screen.height - 100;
const BASE_W = WIDTH*0.5 - 100;
const BASE_H = HEIGHT*0.5 - 100;
const LINK_DISTANCE = 70;
// These probably won't be constants by the final version
const defaultNodeColor = "#3182bd";
const defaultNodeSize = 8;
const defaultPathColor = "black";
const defaultLinkWidth = 1;
const defaultTextColor = defaultNodeColor;
const defaultTextSize = 12;
const highlightPathColor = "green";
const highlightNodeColor = "green";
const possibleNodeColor = "red";
const nextNodeColor = "yellow";
const initialGoalColor = "purple";

// Other util constants
const HASH_LENGTH = 40;
const EXT_SUPPORTED = ["gexf", "json"];
const SAME_NODE_FEEDBACK = "The initial node can't be the same as the goal one. Set them by clicking the desired node.";
const NULL_NODE_FEEDBACK = "You need to specify both initial and goal nodes first by clicking on them.";
const EXT_SUPPORT_FEEDBACK = "Sorry, this application only supports the following file formats: " + EXT_SUPPORTED;
const BASE_URI = "api/graph/"
const GRAPH_BASE_PATH = "graphs/";
const GRAPH_EXAMPLES_FOLDER = "examples/";
const debug = false;

// Global variables
var svg;
var zoom;
var container;
var showWeights = true;
var forceOS = false;
var links;
var nodes;
var gD3;
