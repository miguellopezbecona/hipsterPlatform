function activateComponents(){
  // These sections are initially invisible
  $("#operations").hide();
  $("#nodePanel").hide();
  $("#legendPanel").hide();
  $("#searchPanel").hide();
  $("#feedback").hide();
  $("#zoomButtons").hide();

  calculateView();

  // Builds color legend dynamically
  var colorList = "";
  for(var field in nodeColors) 
      colorList += "<p><span style=\"background-color: " + nodeColors[field] + "\">&nbsp;&nbsp;&nbsp;</span>: " + colorDescriptions[field] + ".</p>";
  $("#colorList").html(colorList);

  /*** Gives functionality to the buttons ***/
  $("#hideNodePanel").click(function () {
    $("#nodePanel").hide();
  });

  $("#showLegendPanel").click(function () {
    $("#legendPanel").show();
  });

  $("#hideLegendPanel").click(function () {
    $("#legendPanel").hide();
  });

  $("#hideSearchPanel").click(function () {
    $("#searchPanel").hide();
  });

  // Uploads a graph to the server
  $("#upload").click(function () {
    var selection = $("#graphToUpload").val();

    if(!validateUpload(selection))
      return;

    var formData = new FormData($('form')[0]);

    publishGraph(formData);
  });

  // Load a server-stored graph by inputting a hash
  $("#loadGraphH").on("click", function(){
    var selection = $("#hash").val();

    // Stops if the selection is null
    if(!selection || selection.length === 0) return;
    
    // Stops if the input data (without the extension) hasn't the length of the hash
    if(selection.split("\.")[0].length != HASH_LENGTH){
      showFeedback("danger", "Your input doesn't correspond to a hash.")
      return;
    }

    handleGraphRequest(selection);
  });

  // Load a server-stored graph by selecting an example
  $("#loadGraphS").on("click", function(){
    var selection = $("#selectedGraph").val();

    // Stops if the selection is null
    if(!selection || selection.length === 0) return;

    handleGraphRequest(selection);
  });

  // Change layout
  $("#changeLayout").on("click", function(){
    applyLayout(prepareServiceGraph(), $("#layout").val() );
  });

  // Resolves a search problem
  $("#showPath").on("click", function(){
    var initialNode = $("#initialNode").text();
    var goalNode = $("#goalNode").text();

    // You can't ask a path without specifing the initial and goal nodes
    if(initialNode==null || initialNode.length==0 || goalNode==null || goalNode.length==0){
      showFeedback("danger", NULL_NODE_FEEDBACK);
      return;
    }
    // This check could be redudant as it's also done when you select both nodes, but it adds security
    else if (initialNode.localeCompare(goalNode)==0){
      showFeedback("danger", SAME_NODE_FEEDBACK);
      return;
    }

    // Different keyword if the algorithm is done step by step or not
    var keyword;
    if(!$("#stepByStep").is(":checked") || forceOS)
      keyword = F_PATH;
    else
      keyword = P_PATH;

    // Base content
    var algorithm = $("#algorithm").val();
    var content = algorithm + "_" + initialNode + "_" + goalNode;

    // Adds graph info if the select algorithm is heuristic
    var heuristic = $("#heuristic").val();
    if(heuristic != null && isHeuristicAlgorithm(algorithm))
      content += "_" + heuristic + "_" + JSON.stringify(prepareServiceGraph().nodes);

    var message = buildMessage(keyword, "'" + content + "'");
    ws.send(message);
  });

  $("#showWeights").change("click", function(){
    var isVisible = $(this).is(":checked");
    var labelStyle;
    if(isVisible)
      labelStyle = "visible";
    else
      labelStyle = "hidden";
    d3.selectAll("textPath").style("visibility", labelStyle);
    tick();
  });

   d3.select(window).on('resize', function () {
     calculateView();
     centerGraph();
   });

  // Builds the algorithm menu dynamically
  var select = $("#algorithm")[0];
  for(i=0;i<ALGORITHMS.length;i++)
    select.options[i] = new Option(ALGORITHMS[i], ALGORITHMS[i], false);

  // Does the same with the layout menu
  var layout = $("#layout")[0];
  for(i=0;i<LAYOUTS.length;i++)
    layout.options[i] = new Option(LAYOUTS[i], LAYOUTS[i].toLowerCase(), false);

  // ... and with the heuristic one
  var heuristic = $("#heuristic")[0];
  for(i=0;i<HEURISTICS.length;i++)
    heuristic.options[i] = new Option(HEURISTICS[i], HEURISTICS[i].split(" ")[0], false);
}

function calculateView(){
     // Adapts width and height from main elements to new screen size
     WIDTH = window.innerWidth;
     HEIGHT = window.innerHeight - $("#showLegendPanel").height() - $("footer").height() - 10;
     $("#canvas").css("width", WIDTH);
     $("#canvas").css("height", HEIGHT);
     $("rect").attr("width", WIDTH);
     $("rect").attr("height", HEIGHT);
     $("svg").attr("width", WIDTH);
     $("svg").attr("height", HEIGHT);
}

function checkExtension(extension){
    return EXT_SUPPORTED.indexOf(extension) > -1;
}

function getExtension(filename){
    return filename.split("\.")[filename.split("\.").length-1].toLowerCase();
}

function isHeuristicAlgorithm(algorithm){
    return HEURISTIC_ALGORITHMS.indexOf(algorithm) > -1;
}


// Prepares a graph object, with a determined structure, to send to the service
function prepareServiceGraph(){
    var g = {};

    var nodeList = jQuery.extend(true, [], nodes);
    var validFields = ["id", "x", "y"];

    // Deletes unused fields from the copy to send the correct (and less) information
    nodeList.forEach(function(d){
      for(var field in d) {
          if(validFields.indexOf(field) == -1)
              delete d[field];
      }
    });
    g.nodes = nodeList;

    g.links = []; // "= links" can't be done because of "links" structure
    link.each(function(d){
      var aux = {};
      aux.source = d.source.id;
      aux.target = d.target.id;
      aux.weight = d.weight;
      g.links.push(aux);
    });
    g.width = WIDTH;
    g.height = HEIGHT;

    return g;
}

// This is used when submitting the form to upload a graph
function validateUpload(selection){

    // It does nothing when the selection is empty
    if(!selection || selection.length === 0) return false;

    // Alerts and stops when the file doesn't have the right extension
    var extension = getExtension(selection);
    if(!checkExtension(extension)){
      showFeedback("danger", EXT_SUPPORT_FEEDBACK);
      return false;
    }

    return true;
}

// Different graph fetching depending on graph's extension and type (example or not)
function handleGraphRequest(filename){
    var extension = getExtension(filename);
    var isHash = (filename.length - extension.length - 1) == HASH_LENGTH;

    // The url request will be different if the graph is hashed or not (an example)
    var graphUrl = GRAPH_BASE_PATH;
    if(!isHash)
        graphUrl += GRAPH_EXAMPLES_FOLDER;
    graphUrl += filename;

    // Different fetching depending on file's extension
    switch(extension){
        case "json":
            gD3 = null;
            requestGraph(graphUrl);
            break;
        case "gexf":
            var newGEXF = GexfParser.fetch(graphUrl);
            var isDirected = (newGEXF.defaultEdgeType != null && newGEXF.defaultEdgeType.localeCompare("undirected") != 0);
            gD3 = gexfD3().graph(newGEXF).size([WIDTH,HEIGHT]).nodeScale([5,20]);
            links = gD3.links();
            nodes = gD3.nodes();
            initialize(filename, isDirected);
            break;
        default:
            showFeedback("danger", EXT_SUPPORT_FEEDBACK);
     }
}


function initialize(filename, directed){
    currentNode = null;

    // Shows filename in some field
    $("#filename").text(filename);

    // Hides node and seach panel because of possible previous work
    $("#nodePanel").hide();
    $("#searchPanel").hide();

    // Sends a message so the server builds its internal model to work with the graph
    var message = buildMessage(BEGIN, "'"+filename+"'");
    ws.send(message);

    // Removes initial and goal nodes' values from previous graphs
    $("#initialNode").text(null);
    $("#goalNode").text(null);

    // Resets "show weights" checkbox to true to avoid problems
    $("#showWeights")[0].checked = true;

    // Activates the algorithm parameters for the case they were disabled due to a step-by-step execution
    disableParameters(false);

    // Uses the received data to build the graph
    buildGraph(directed);

    // Makes operations and zoom sections visible
    $("#operations").show();
    $("#zoomButtons").show();
}

function disableParameters(bol){
    $("#algorithm").prop("disabled", bol);
    $("#heuristic").prop("disabled", bol);
}

function showFeedback(type, content){
    $("#feedback").hide();
    $("#feedback").text(content);
    $("#feedback").attr("class", "alert alert-" + type);
    $("#feedback").show();
    if(!debug)
      $("#feedback").delay(10000).hide(1000);
}
