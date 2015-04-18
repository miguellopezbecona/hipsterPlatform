var ws;
const wsUrl = "ws://localhost:8080/web/hipster";

// Keywords
const AVAILABLE_GRAPHS = "AVAILABLE_GRAPHS";
const BEGIN = "BEGIN";
const BUILD_GRAPH = "BUILD_GRAPH";
const NODE = "NODE";
const F_PATH = "F_PATH";
const P_PATH = "P_PATH";
const LAYOUT = "LAYOUT";

// Layouts and algorithms
const ALGORITHMS = ["DIJKSTRA", "DEPTH", "BREADTH", "BELLMAN_FORD"];
const LAYOUTS = ["FORCE", "TREE"];
const DEFAULT_LAYOUT = LAYOUTS[0];

// Graphic-related constants
const width = 1200;
const height = 350;
const r = 8;
const defaultNodeColor = "#3182bd";
const defaultPathColor = "black";
const highlightPathColor = "green";
const highlightNodeColor = "green";
const possibleNodeColor = "red";
const nextNodeColor = "yellow";
const linkDistance = 70;

// Other util constants
const HASH_LENGTH = 40;
const SAME_NODE_FEEDBACK = "The initial node can't be the same as the goal one. Set them by clicking the desired node.";
const NULL_NODE_FEEDBACK = "You need to specify both initial and goal nodes first by clicking on them.";
const EXT_SUPPORTED = ["gexf", "json"];
const debug = 0;
const reader = new FileReader();

// Global variables
var svg;
var zoom;
var container;
var showWeights = true;
var forceOS = false;
