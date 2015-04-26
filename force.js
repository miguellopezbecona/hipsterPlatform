var force;
var link;
var node;
var edgelabels;
var edgepaths;
var selected;

function startForce(){
    // Cleans the canvas if anything was drawn before
    d3.select("svg").remove();

    var nodeCollection = {};
    var makeDefaultPositions = true;

    if(nodes == null){
      // Obtain the nodes with info from the links
      links.forEach(function(link) {
        link.source = nodeCollection[link.source] || (nodeCollection[link.source] = {id: link.source});
        link.target = nodeCollection[link.target] || (nodeCollection[link.target] = {id: link.target});
      });

      nodes = d3.values(nodeCollection);
    }
    // If nodes isn't null, it means that it comes from a gexf file, whose positions are already defined
      else
    makeDefaultPositions = false;

    // Defines the force layout
    force = d3.layout.force()
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

    svg = d3.select("body").append("svg")
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
    .data(force.links())
    .enter().append("line")
    .attr("source", function(d) { return d.source.id; })
    .attr("target", function(d) { return d.target.id; })
    .attr("stroke", defaultPathColor);

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
    .data(force.nodes())
    .enter().append("g")
    .attr("class", "node")
    .attr("nodeId", function(d) { return d.id; })
    .on("mouseout", mouseout)
    .on("click", click)
    .call(drag);

    node.append("circle")
    .attr("r", BASE_RADIUS)
    .style("fill", defaultNodeColor);

    node.append("text")
    .attr("x", 12)
    .attr("dy", ".35em")
    .style("fill", defaultNodeColor)
    .text(function(d) { return d.id; });

     // Adjusts a grid-like initial positioning
     var rows = Math.ceil(Math.sqrt(nodes.length));
     var columns = Math.ceil(nodes.length / rows);
     node.each(function(d,i){
       d.fixed = true;
       if(makeDefaultPositions){
         d.x = BASE_W + Math.floor(i/columns) * LINK_DISTANCE;
         d.y = BASE_H + (i % rows) * LINK_DISTANCE;
       }
     });

     force.start();
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
    if(showWeights)
      edgepaths.attr('d', function(d) { var path='M '+d.source.x+' '+d.source.y+' L '+ d.target.x +' '+d.target.y; return path});
    else
      edgepaths.attr('d', function(d) { var path='M -2000 -2000 L -2000 -2000'; return path});
}

// Restores the selected node to its initial state
function mouseout() {
    if(selected != null){
      d3.select("[nodeId='" + selected + "']").select("text").transition().attr("x", 12).style("font", "12px serif");
      d3.select("[nodeId='" + selected + "']").select("circle").transition().attr("r", BASE_RADIUS);
    }
}

// When a node is clicked, it becomes bigger and it sends a request to the server (by using websockets)
function click() {
    d3.select(this).select("text").transition().attr("x", 22).style("font", "17.5px serif");

    d3.select(this).select("circle").transition().attr("r", 2*BASE_RADIUS)

    selected = d3.select(this).select("text").text();
    var message = buildMessage(NODE, selected);

    // Requests information to the server
    ws.send(message);
}

