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
      case BUILD_GRAPH:
        links = message.content;

        // Uses the new object to initialize the graphic
        if(links[0].children==null)
          startForce();    
        else
          buildTree();

        // Makes begin section invisible, and does the inverse with the operations and zoom ones
        $("#begin").hide();
        $("#operations").show();
		$("#zoomButtons").show();
        break;
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
          previousNode = initialNode;
          startedSbS = true;
        }

        showPartialPath(message.content);
        break;
      case AVAILABLE_GRAPHS:
        // Builds the graph menu with the received data
        var graphMenu = $("#graphToLoad")[0];
        for(i=0;i<message.content.length;i++)
          graphMenu.options[i] = new Option(message.content[i], message.content[i], false);
        break;
    }

    // Copies the literal string to a div when debugging
    if(debug)
    	$("#text").html("<p>"+evt.data+"</p>");
  };

});

function showNodeInfo(nodeInfo){
    var info = "<p>Node selected: "+nodeInfo.nodeId+"</p>";
    info += "<p>Node info: "+nodeInfo.info+"</p>";

    $("#rightPanelContent").html(info);
	$("#rightPanel").show();
}

function showFullPath(data){
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
    /* In every step, the server will send back the next node (so, the target of the next link)
     * to be painted and the following one to be expanded
     */ 
    d3.select("[nodeId='" + data[0] + "']").select("circle").style("fill", highlightNodeColor);
    d3.selectAll("[source='"+previousNode+"']").filter("[target='"+data[0]+"']").style("stroke", highlightPathColor);

    /* The node to be expanded is only highlighted when it exists (the server sends two nodes)
     * If not, it's the end of the path
     */
    if(data.length > 1){
      d3.select("[nodeId='" + data[1] + "']").select("circle").style("fill", nextNodeColor);
      previousNode = data[0];
    } else {
      startedSbS = false;
      previousNode = null;
    }
}
