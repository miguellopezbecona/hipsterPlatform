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
    .size([WIDTH, HEIGHT])
    .linkDistance(LINK_DISTANCE)
    .charge(-200)
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
            .attr('fill', '#000000')
            .attr('stroke','#000000');
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
        .attr({'dx':LINK_DISTANCE*0.5,
               'font-size':10,
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
    .attr("nodeId", function(d) { return d.id; })
    .on("mouseout", mouseout)
    .on("click", click)
    .call(drag);

    // Customs right-click behaviour
    $('.node').contextMenu('node-context-menu', {
        'Mark/unmark this node as the <b>initial</b> one': {
            click: function(e) {
              var nodeId = e[0].__data__.id.toString();
              setInitialGoal(nodeId, "#initialNode");
            },
        },
        'Mark/unmark this node as the <b>goal</b> one': {
            click: function(e) {
              var nodeId = e[0].__data__.id.toString();
              setInitialGoal(nodeId, "#goalNode");
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

        node.append("text")
          .attr("x", function(d) {return 1.5*sizeScale(d.size);})
          .attr("dy", ".35em")
          .style("fill", defaultTextColor)
          .text(function(d) { return d.id; });
    } else {
        node.append("circle")
          .attr("r", defaultNodeSize)
          .attr("size", defaultNodeSize)
          .attr("color", nodeColors["default"])
          .style("fill", nodeColors["default"]);

        node.append("text")
          .attr("x", 1.5*defaultNodeSize)
          .attr("dy", ".35em")
          .style("fill", defaultTextColor)
          .text(function(d) { return d.id; });
    }

    node.each(function(d,i){
      d.fixed = true;
    });


    // Applies the default layout when the nodes haven't a position
    if(!hasNodeInfo)
      applyLayout(prepareServiceGraph(), DEFAULT_LAYOUT);

    graph.start();
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
    selected = d3.select(this).attr("nodeId");
    changeNode(selected, null, 2.0);

    var message = buildMessage(NODE, selected);

    // Requests information to the server
    ws.send(message);
}

function resetColorsAndSizes(){
    d3.selectAll(".node").select("circle").transition().attr("r", function(d){
      return d3.select(this).attr("size");}).style("fill", function(d){
      return d3.select(this).attr("color");});
    d3.selectAll(".node").select("text").transition().attr("x", function(d){
      return 1.5*d3.select("[nodeId='" + d.id + "']").select("circle").attr("size");}).style("font", defaultTextSize);

    d3.selectAll("line").style("stroke", pathColors["default"]);
    d3.selectAll("line").style("stroke-width", defaultLinkWidth);
}

function changeNode(id, color, sizeFactor){
    var nod =  d3.select("[nodeId='" + id + "']");
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

function highlightLink(source, target){
    var l = d3.selectAll("[source='" + source + "']").filter("[target='" + target + "']");
    l.style("stroke", pathColors["finalPath"]);
    l.style("stroke-width", 2*defaultLinkWidth);
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
            // Restores previous initial/goal node (if exists) to its original color
            if($(type).text() != null && $(type).text().length > 0)
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
