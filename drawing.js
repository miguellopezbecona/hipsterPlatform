var graph;
var link;
var node;
var edgelabels;
var edgepaths;
var selected;
var clicked;

function buildGraph(directed){
    // Cleans the canvas if anything was drawn before
    d3.select("svg").remove();

    var nodeCollection = {};
    var makeDefaultPositions = true;

    // This is crucial when setting colors, sizes and positions to the nodes
    var hasNodeInfo = (nodes != null);

    if(!hasNodeInfo){
      // Obtain the nodes with info from the links
      links.forEach(function(link) {
        link.source = nodeCollection[link.source] || (nodeCollection[link.source] = {id: link.source});
        link.target = nodeCollection[link.target] || (nodeCollection[link.target] = {id: link.target});
      });

      nodes = d3.values(nodeCollection);
    }

    // Defines the graph
    graph = d3.layout.force()
    .nodes(nodes)
    .links(links)
    .on("tick", tick);

    // Defines the zoom behavious
    zoom = d3.behavior.zoom().scaleExtent([0.1, 10]).on("zoom", zoomed);

    // Defines the drag behaviour
    var drag = d3.behavior.drag()
    .origin(function(d) { return d; })
    .on("dragstart", dragstarted)
    .on("drag", dragged)
    .on("dragend", dragended);

    svg = d3.select("#canvas").append("svg")
    .attr("width", WIDTH)
    .attr("height", HEIGHT)
    .call(zoom);

    // Allows to move the whole graphic
    svg.append("rect")
    .attr("width", WIDTH)
    .attr("height", HEIGHT)
    .style("fill", "none")
    .style("pointer-events", "all");

    container = svg.append("g");

    link = container.selectAll(".link")
    .data(graph.links())
    .enter().append("line")
    .attr("source", function(d) { return d.source.id; })
    .attr("target", function(d) { return d.target.id; })
    .attr('marker-end','url(#arrowhead)')
    .attr("stroke", pathColors["default"]);


    // Defines the arrow that shows each link's direction
    if(directed){
      container.append('defs').append('marker')
        .attr({'id':'arrowhead',
               'viewBox':'-0 -5 10 10',
               'refX':25,
               'refY':0,
               'orient':'auto',
               'markerWidth':5,
               'markerHeight':5,
               'xoverflow':'visible'})
        .append('svg:path')
            .attr('d', 'M 0,-5 L 10 ,0 L 0,5')
            .attr('fill', '#000000');
    }

    // Necessary to show links' weight
    edgepaths = container.selectAll(".edgepath")
        .data(links)
        .enter()
        .append('path')
        .attr('id',function(d,i) {return 'e'+i})
        .style("pointer-events", "none");

    edgelabels = container.selectAll(".edgelabel")
        .data(links)
        .enter()
        .append('text')
        .style("pointer-events", "none")
        .attr({'font-size':10,
               'fill':'#000000'});

    edgelabels.append('textPath')
        .attr('xlink:href',function(d,i) {return '#e'+i})
        .style("pointer-events", "none")
        .text(function(d,i){return d.weight});


    node = container.append("g")
    .attr("class", "nodes")
    .selectAll(".node")
    .data(graph.nodes())
    .enter().append("g")
    .attr("class", "node")
    .attr("nodeid", function(d) { return d.id; })
    .on("mouseout", mouseout)
    .on('contextmenu', function(d,i) {
        // Changes "Select" to "Unselect" when necessary
        // For initial node
        if($("#initialNode").text() != null && $("#initialNode").text().localeCompare(d.id)==0)
            $("#initialText").text("Unselect");
        else
            $("#initialText").text("Select");

        // For goal node
        if($("#goalNode").text() != null && $("#goalNode").text().localeCompare(d.id)==0)
            $("#goalText").text("Unselect");
        else
            $("#goalText").text("Select");
    })
    .on("click", click)
    .call(drag);

    // Customizes right-click behaviour
    $('.node').contextMenu('node-context-menu', {
        '<span id="initialText">Select</span> as <b>initial</b>': {
            click: function(e) {
              var nodeId = e[0].__data__.id.toString();

              // Prevents modifing this value in the middle of a step-by-step execution
              if(!doingSbS)
                  setInitialGoal(nodeId, "#initialNode");
              else
                  showFeedback("danger", "You can't change this value in the middle of a step-by-step execution! Please, first finish your search.");
            },
        },
        '<span id="goalText">Select</span> as <b>goal</b>': {
            click: function(e) {
              var nodeId = e[0].__data__.id.toString();

              // Prevents modifing this value in the middle of a step-by-step execution
              if(!doingSbS)
                  setInitialGoal(nodeId, "#goalNode");
              else
                  showFeedback("danger", "You can't change this value in the middle of a step-by-step execution! Please, finish first your search.");
            },
        }
    });

    // Uses imported data or generates it
    if(hasNodeInfo){
        var sizeScale = gD3.nodeScale();
        node.append("circle")
          // Radius and color are duplicated in order to ease transformations
          .attr("r", function(d) {return sizeScale(d.size);})
          .attr("size", function(d) {return sizeScale(d.size);})
          .attr("color", function(d) {return d.rgbColor;})
          .style("fill", function(d) {return d.rgbColor;});

        // Node's id label
        node.append("text")
          .attr("x", function(d) {return 1.5*sizeScale(d.size);})
          .attr("dy", ".35em")
          .style("fill", defaultTextColor)
          .text(function(d) { return d.id; });

        // Node's cost (used in step-by-step executions)
        node.append("text")
          .attr("x",  function(d) {return -0.75*sizeScale(d.size);})
          .attr("y",  function(d) {return -2.5*sizeScale(d.size);})
          .attr("class", "nodecost")
          .style("fill", costTextColor)
          .text("");
    } else {
        node.append("circle")
          .attr("r", defaultNodeSize)
          .attr("size", defaultNodeSize)
          .attr("color", nodeColors["default"])
          .style("fill", nodeColors["default"]);

        // Node's id label
        node.append("text")
          .attr("x", 1.5*defaultNodeSize)
          .attr("dy", ".35em")
          .style("fill", defaultTextColor)
          .text(function(d) { return d.id; });

        // Node's cost (used in step-by-step executions)
        node.append("text")
          .attr("x", -0.75*defaultNodeSize)
          .attr("y", -2.5*defaultNodeSize)
          .attr("class", "nodecost")
          .style("fill", costTextColor)
          .text("");
    }

    node.each(function(d,i){
      d.fixed = true;
    });

    // Applies the default layout when the nodes haven't a position
    if(!hasNodeInfo)
      applyLayout(prepareServiceGraph(), DEFAULT_LAYOUT);

    // Draws the graph for the first time
    tick();
}

function dragstarted(d) {
    d3.event.sourceEvent.stopPropagation();
    d.fixed = false;
}

function dragged(d) {
    d.x = d3.event.x;
    d.y = d3.event.y;
    tick();
}

function dragended(d) {
    d.fixed = true;
    tick();
}

function tick() {
    link
    .attr("x1", function(d) { return d.source.x; })
    .attr("y1", function(d) { return d.source.y; })
    .attr("x2", function(d) { return d.target.x; })
    .attr("y2", function(d) { return d.target.y; });

    node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

    // Updates weights' positions (center of the line)
    edgelabels.attr('dx',function(d,i) { return getLinkHalfLength(d.source.id, d.target.id); })

    edgepaths.attr('d', function(d) { var path='M '+d.source.x+' '+d.source.y+' L '+ d.target.x +' '+d.target.y; return path});

    // Prevents weight labels being inverted
    edgelabels.attr('transform',function(d,i){
        if (d.target.x<d.source.x){
            bbox = this.getBBox();
            rx = bbox.x + bbox.width*0.5;
            ry = bbox.y + bbox.height*0.5;
            return 'rotate(180 '+rx+' '+ry+')';
        }
        else
            return 'rotate(0)';
    });
}

function getLinkHalfLength(source, target){
    var l = d3.selectAll("[source='" + source + "']").filter("[target='" + target + "']");
    var xPart = Math.pow(l.attr("x2") - l.attr("x1"),2);
    var yPart = Math.pow(l.attr("y2") - l.attr("y1"),2);
    return 0.5*Math.sqrt(xPart + yPart);
}

// Restores the selected node to its initial state
function mouseout() {
    if(clicked){
      changeNode(selected, null, 1.0);
      clicked = false;
    }
}

// When a node is clicked, it becomes bigger and it sends a request to the server (by using websockets)
function click() {
    clicked = true;
    selected = d3.select(this).attr("nodeid");
    changeNode(selected, null, 2.0);

    var message = buildMessage(NODE, selected);

    // Requests information to the server
    ws.send(message);
}

function resetColorsAndSizes(){
    // Resets nodes' colors and sizes to their original except the initial/goal color
    d3.selectAll(".node").select("circle").transition()
    .attr("r", function(d){
      return d3.select(this).attr("size");})
    .style("fill", function(d){
      var goalNode = $("#goalNode").text();
      var nodeId = d3.select(this.parentNode).attr("nodeid");

      // Maintains goal node' color
      if(goalNode.localeCompare(nodeId) == 0)
        return nodeColors["initialGoal"];
      else
        return d3.select(this).attr("color");
    });
    
    // Cleans node's costs from step-by-step executions
    d3.selectAll(".nodecost").text("");

    // Places label to the correct place
    d3.selectAll(".node").select("text").transition().attr("x", function(d){
      return 1.5*d3.select("[nodeid='" + d.id + "']").select("circle").attr("size");}).style("font", defaultTextSize);

    // Resets links' colors and sizes to their original
    d3.selectAll("line").style("stroke", pathColors["default"]);
    d3.selectAll("line").style("stroke-width", defaultLinkWidth);
}

function changeNode(id, color, sizeFactor){
    if(id == null) return;

    var nod = d3.select("[nodeid='" + id + "']");
    var c =  nod.select("circle");
    var s = c.attr("size");
    nod.select("text").transition().attr("x", 1.5*sizeFactor*s).style("font", sizeFactor*defaultTextSize);
    c.transition().attr("r", sizeFactor*s);

    // Doesn't change color if there isn't need to do it
    if(color == null)
        return;

    if(color.localeCompare(ORIGINAL)==0)
        color = c.attr("color");
    c.style("fill", color);
}

function changeNodeCost(id, cost){
    if(id == null) return;

    d3.select("[nodeid='" + id + "']").select(".nodecost").text( parseFloat(cost).toFixed(1) );
}

function highlightLink(source, target){
    var l = d3.selectAll("[source='" + source + "']").filter("[target='" + target + "']");
    l.style("stroke", pathColors["finalPath"]);
    l.style("stroke-width", 4*defaultLinkWidth);
}

/**
 * nodeId: node's id to be marked/unmarked as initial/goal node
 * type: type of node to be marked/unmarked, "#initialNode" or "#goalNode"
 */
function setInitialGoal(nodeId, type){
    var other = null;
    if(type.localeCompare("#initialNode")==0)
        other = $("#goalNode").text();
    else if(type.localeCompare("#goalNode")==0)
        other = $("#initialNode").text();

    // Is the node already marked as initial/goal node?
    var isMarked = nodeId.localeCompare($(type).text()) == 0;

    if(!isMarked){
        // Checks that the other position node (initial or goal) isn't the same as the selected one
        if(nodeId.localeCompare(other)==0)
            showFeedback("danger", SAME_NODE_FEEDBACK);
        else {
            // Restores previous initial/goal node (if exists) to its original color and size if its current color is the initial/goal one
            if($(type).text() != null && $(type).text().length > 0 && d3.select("[nodeid='" + $(type).text() + "']").select("circle").style("fill").localeCompare(nodeColors["initialGoal"]) == 0)
                changeNode($(type).text(), ORIGINAL, 1.0);

            // Updates the value and highlights the node
            $(type).text(nodeId);
            changeNode(nodeId, nodeColors["initialGoal"], 1.0);
        }
    } else {
        // The node is already marked, so it restores the previous selected node to its original color and the value is removed
        if($(type).text() != null && !$(type).text().length == 0)
            changeNode($(type).text(), "ORIGINAL", 1.0);
        $(type).text(null);
    }
}
