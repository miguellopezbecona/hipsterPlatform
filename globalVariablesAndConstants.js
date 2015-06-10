var ws;
var wsUrl = "ws://$/webSocket";

// Keywords
const AVAILABLE_GRAPHS = "AVAILABLE_GRAPHS";
const BEGIN = "BEGIN";
const NODE = "NODE";
const F_PATH = "F_PATH";
const P_PATH = "P_PATH";
const ORIGINAL = "ORIGINAL";

// Layouts and algorithms
const ALGORITHMS = ["Dijkstra", "Depth", "Breadth", "Bellman-Ford", "A*"];
const HEURISTICS = ["Euclidean distance", "Manhattan distance"];
const HEURISTIC_ALGORITHMS = ["A*"];
const LAYOUTS = ["Circle", "Fruchterman-Reingold", "Grid", "ISOM", "Kamada-Kawai", "Random", "Spring"];
const DEFAULT_LAYOUT = "grid";

// Graphic-related constants and variables
var WIDTH;
var HEIGHT;

var nodeColors = {};
nodeColors["default"] = "rgb(49, 130, 189)"; // "#3182bd"
nodeColors["finalPath"] = "rgb(0, 128, 0)"; // "green"
nodeColors["current"] = "rgb(0, 255, 255)"; // "aqua"
nodeColors["processed"] = "rgb(255, 0, 0)"; // "red"
nodeColors["expanded"] = "rgb(255, 255, 0)"; // "yellow"
nodeColors["initialGoal"] = "rgb(128, 0, 128)"; // "purple"

var colorDescriptions = {};
colorDescriptions["default"] = "the default one";
colorDescriptions["finalPath"] = "the node is part of the final shortest path (one-step execution or when the step-by-step one finishes)";
colorDescriptions["current"] = "the node is the one processed in the last step made (step-by-step executions only)";
colorDescriptions["processed"] = "the node has been processed (step-by-step executions only)";
colorDescriptions["expanded"] = "the node can be expanded in the next iteration (step-by-step executions only)";
colorDescriptions["initialGoal"] = "the node is marked as the initial or goal one. The previous described colors can overwrite this one";

var pathColors = {};
pathColors["default"] = "black";
pathColors["finalPath"] = "green";

const defaultNodeSize = 8;
const defaultLinkWidth = 1;
const defaultTextColor = nodeColors["default"];
const costTextColor = "green";
const defaultTextSize = 12;

// Other util constants
const DEFAULT_GRAPH = "grid.json";
const SECS_BY_PING = 50;
const HASH_LENGTH = 40;
const EXT_SUPPORTED = ["gexf", "json"];
const SAME_NODE_FEEDBACK = "The initial node can't be the same as the goal one. Set them by right-clicking the desired node.";
const NULL_NODE_FEEDBACK = "You need to specify both initial and goal nodes first by right-clicking on them.";
const EXT_SUPPORT_FEEDBACK = "Sorry, this application only supports the following file formats: " + EXT_SUPPORTED;
const BASE_URI = "api/graph/"
const GRAPH_BASE_PATH = "graphs/";
const GRAPH_EXAMPLES_FOLDER = "examples/";
const debug = false;

// Global variables
var svg;
var zoom;
var container;
var forceOS = false;
var links;
var nodes;
var gD3;
var currentNode;
