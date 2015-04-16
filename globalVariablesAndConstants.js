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
const highlightPathColor = "red";
const highlightNodeColor = "red";
const nextNodeColor = "yellow";
const linkDistance = 70;

// Util constants
const debug = 0;
const reader = new FileReader();

// Global variables
var svg;
var zoom;
var container;
var showWeights = true;
var initialNode = null;
var goalNode = null;
var previousNode = null; // To be used in resolving search problems step by step
