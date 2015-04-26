function activateComponents(){
  // These sections are initially invisible
  $("#operations").hide();
  $("#rightPanel").hide();
  $("#feedback").hide();
  $("#zoomButtons").hide();

  /*** Gives functionality to the buttons ***/

  $("#upload").click(function () {
    var selection = $("#graphToUpload").val();

    if(!validateUpload(selection))
      return;

    var formData = new FormData($('form')[0]);

    $.ajax({
        type: "POST",
        url: BASE_URI,
        data: formData,
        // Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false,
        success: function (data, textStatus, response) {
            showFeedback("success", "Your graph was loaded successfully. If you want to use it in future executions, put the following hash in the input field: " + data);
            requestGraph(data);
        },
        error: function (response, textStatus, errorThrown) {
            showFeedback("danger", "There was a problem while parsing your graph.");
        }
    });
  });

  // Load a server-stored graph
  $("#loadGraphH").on("click", function(){
    var selection = $("#hash").val();

    // Stops if the selection is null
    if(!selection || selection.length === 0) return;
    
    // Stops if the input data (without the extension) hasn't the length of the hash
    if(selection.split("\.")[0].length != HASH_LENGTH){
      showFeedback("danger", "Your input doesn't correspond to a hash.")
      return;
    }

    requestGraph(selection);
  });

  $("#loadGraphS").on("click", function(){
    var selection = $("#selectedGraph").val();

    // Stops if the selection is null
    if(!selection || selection.length === 0) return;

    requestGraph(selection);
  });

  // Change layout
  $("#changeLayout").on("click", function(){
    var test = jQuery.extend(true, [], nodes);

    test.forEach(function(d){
      delete d.px;
      delete d.py;
      delete d.fixed;
      delete d.weight;
      delete d.index;
      d.info = "example";
    });

    //showFeedback("info", JSON.stringify(test));
    $.ajax({
        type: "POST",
        //url: "http://layout.jointjs.com/layout/circular/circular",
        //data: "{\"graph\":{\"cells\":" + JSON.stringify(nodes) + "}}",
        url: "api/layout/" + $("#layout").val(),
        data: JSON.stringify(test),
        contentType: "application/json",
        success: function (data, textStatus, response) {
            showFeedback("info", data);
        },
        error: function (response, textStatus, errorThrown) {
            showFeedback("danger", "Error: " + errorThrown);
        }
    });
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

    var message;

    // Different if the algorithm is done step by step or not
    if($("#oneStep").is(":checked") || forceOS)
      message = buildMessage(F_PATH, "'" + $("#algorithm").val() + " " + initialNode + " " + goalNode + "'");
    else
      message = buildMessage(P_PATH, "'" + $("#algorithm").val() + " " + initialNode + " " + goalNode + "'");
    forceOS = false;
    ws.send(message);
  });

  $("#showWeights").change("click", function(){
    showWeights = $(this).is(":checked");
    tick();
  });

  $("#initialNodeC").change("click", function(){
    if($(this).is(":checked") && selected != null){
        // Checks that the other node (goal) isn't the same as the selected one
        var goalNode = $("#goalNode").text();
        if(selected.localeCompare(goalNode)==0){
            showFeedback("danger", SAME_NODE_FEEDBACK);
            $(this)[0].checked = false;
        } else
            $("#initialNode").text(selected);
    } else
        $("#initialNode").text(null);
  });

  $("#goalNodeC").change("click", function(){
    if($(this).is(":checked") && selected != null){
        // Checks that the other node (initial) isn't the same as the selected one
        var initialNode = $("#initialNode").text();
        if(selected.localeCompare(initialNode)==0){
            showFeedback("danger", SAME_NODE_FEEDBACK);
            $(this)[0].checked = false;
        } else
            $("#goalNode").text(selected);
    } else
        $("#goalNode").text(null);
  });

  // Builds the algorithm menu dynamically
  var select = $("#algorithm")[0];
  for(i=0;i<ALGORITHMS.length;i++)
    select.options[i] = new Option(ALGORITHMS[i].toLowerCase().replace("_"," "), ALGORITHMS[i], false);

  // Does the same with the layout menu
  var layout = $("#layout")[0];
  for(i=0;i<LAYOUTS.length;i++)
    layout.options[i] = new Option(LAYOUTS[i], LAYOUTS[i], false);

  // Set of checkboxes' default values
  $("#showWeights")[0].checked = true;
  $("#oneStep")[0].checked = true;
  $("#initialNodeC")[0].checked = false;
  $("#goalNodeC")[0].checked = false;
}

// This is used when submitting the form to upload a graph
function validateUpload(selection){

    // It does nothing when the selection is empty
    if(!selection || selection.length === 0) return false;

    // Alerts and stops when the file doesn't have the right extension
    var extension = getExtension(selection);
    if(!checkExtension(extension)){
      showFeedback("danger", "Sorry, this application only supports the following file formats: " + EXT_SUPPORTED);
      return false;
    }

    return true;
}

function checkExtension(extension){
    return EXT_SUPPORTED.indexOf(extension) > -1;
}

function getExtension(filename){
    return filename.split("\.")[filename.split("\.").length-1].toLowerCase();
}

function requestGraph(filename){
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
            ajaxRequest(graphUrl);
            break;
        case "gexf":
            var newGEXF = GexfParser.fetch(graphUrl);
            var gD3 = gexfD3().graph(newGEXF).size([1000,1000]).nodeScale([5,20]);
            links = gD3.links();
            nodes = gD3.nodes();
            buildGraph();
            break;
        default:
            links = null;
     }
}

function ajaxRequest(url){
    $.ajax({
        type: "GET",
        url: url,
        contentType: "text/plain",
        success: function (data, textStatus, response) {
            links = data;
            nodes = null;
            buildGraph();

            // Sends a message so the server builds its internal model to work with the graph
            var filename = url.split("/")[url.split("/").length-1];
            var message = buildMessage(BEGIN, "'"+filename+"'");
            ws.send(message);
        },
        statusCode: {
            404: function(response, textStatus, errorThrown) {
                showFeedback("danger", "Error: graph not found");
            }
        }
    });
}

function buildGraph(){
     // Uses the received data to build the graph
     startForce();

     // Makes operations and zoom sections visible
     $("#operations").show();
     $("#zoomButtons").show();
}
