var startedSbS;

function buildMessage(type, content){
  return "{\"type\": \"" + type + "\", \"content\": " + content + "}";
}

// When the document is ready, the websocket is initialized
$(document).ready(function() {
  ws = new WebSocket(wsUrl);
  startedSbS = false;

  $.getScript('buttons.js', function(){
    activateComponents();
  });

  ws.onopen = function(){
    // Requests the available graphs
    var initialMessage = buildMessage(AVAILABLE_GRAPHS, "''");
    ws.send(initialMessage);
  };

  // What does the client do when it receives a message
  ws.onmessage = function (evt){

    // Converts the received string to a JSON object
    var message = JSON.parse(evt.data);

    switch(message.type){
      case NODE:
        showNodeInfo(message.content);
        break;
      case F_PATH:
        // Resets the stroke of the lines and the color of the nodes to discard changes from other painted paths
        d3.selectAll(".node").select("circle").style("fill", defaultNodeColor);
    	d3.selectAll("line").style("stroke", defaultPathColor);
        showFullPath(message.content);
        break;
      case P_PATH:
        // If it's the beginning of this phase, resets the stroke of the lines and the color of the nodes to discard changes from other painted paths
        if(!startedSbS){
          d3.selectAll(".node").select("circle").style("fill", defaultNodeColor);
    	  d3.selectAll("line").style("stroke", defaultPathColor);
          startedSbS = true;
        }

        showPartialPath(message.content);
        break;
      case AVAILABLE_GRAPHS:
        // Builds the graph menu with the received data
        var graphMenu = $("#selectedGraph")[0];
        for(i=0;i<message.content.length;i++)
          graphMenu.options[i] = new Option(message.content[i], message.content[i], false);
        break;
    }

    // Copies the literal string to the right panel when debugging
    if(debug){
    	$("#rightPanelContent").html("<p>"+evt.data+"</p>");
    	$("#rightPanel").show();
    	$("#rightPanel").delay(10000).hide(1000);
    }

  };

});

function showFeedback(type, content){
    $("#feedback").text(content);
    $("#feedback").attr("class", "alert alert-" + type);
    $("#feedback").show();
    $("#feedback").delay(10000).hide(1000);
}

function showNodeInfo(nodeInfo){
    var info = "<p>Node selected: "+nodeInfo.id+"</p>";
    info += "<p>Node info: "+nodeInfo.info+"</p>";

    $("#rightPanelText").html(info);
    $("#rightPanel").show();
}

function showFullPath(data){
    startedSbS = false;
    $("#algorithm").prop("disabled", false);

    /* To avoid duplicated data, instead of receiving every pair of source-target,
     * all the numbers are the target, except for the first one, that is the initial source
     */
    // Colors the links and the nodes
    d3.select("[nodeId='" + data[0] + "']").select("circle").style("fill", highlightNodeColor);
    var i;
    for(i=0;i<data.length-1;i++){
      d3.selectAll("[source='"+data[i]+"']").filter("[target='"+data[i+1]+"']").style("stroke", highlightPathColor);
      d3.select("[nodeId='" + data[i+1] + "']").select("circle").style("fill", highlightNodeColor);
    }
}

function showPartialPath(data){
    // Prevents non-sense change. If you are running an algorithm step by step, you should not change it during the proccess
    $("#algorithm").prop("disabled", true);

    // In every step, the server will send back the next node and the following ones to be expanded
    d3.select("[nodeId='" + data[0] + "']").select("circle").style("fill", possibleNodeColor);

    // If the next goal is the goal one, the search is completed, so it forces a full path call
    var goalNode = $("#goalNode").text();
    if(data[0].localeCompare(goalNode)==0){
      forceOS = true;
      startedSbS = false;
      return;
    }

    var i;
    for(i=1;i<data.length;i++)
      d3.select("[nodeId='" + data[i] + "']").select("circle").style("fill", nextNodeColor);      
}
