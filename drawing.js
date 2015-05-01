var graph;
var link;
var node;
var edgelabels;
var edgepaths;
var selected;
var auxiliarRadius = null;
var auxiliarTextDistance = null;

function startDrawing(){
    // Cleans the canvas if anything was drawn before
    d3.select("svg").remove();

    var nodeCollection = {};
    var makeDefaultPositions = true;

    // If "nodes" isn't null, it means that it comes from a gexf file, whose positions are already defined
    var isGexfGraph = (nodes != null);

    if(!isGexfGraph){
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
    .data(graph.links())
    .enter().append("line")
    .attr("source", function(d) { return d.source.id; })
    .attr("target", function(d) { return d.target.id; })
    .attr('marker-end','url(#arrowhead)')
    .attr("stroke", defaultPathColor);

    // Defines the arrow that shows each link's direction
    container.append('defs').append('marker')
        .attr({'id':'arrowhead',
               'viewBox':'-0 -5 10 10',
               'refX':17,
               'refY':0,
               'orient':'auto',
               'markerWidth':10,
               'markerHeight':10,
               'xoverflow':'visible'})
        .append('svg:path')
            .attr('d', 'M 0,-5 L 10 ,0 L 0,5')
            .attr('fill', '#000000')
            .attr('stroke','#000000');

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

    // GEXF graphs' nodes will have preset sizes and colors
    if(isGexfGraph){
        var sizeScale = gD3.nodeScale();
        node.append("circle")
          .attr("r", function(d) {return sizeScale(d.size);})
          .style("fill", function(d) {return d.rgbColor;});

        node.append("text")
          .attr("x", function(d) {return 1.5*sizeScale(d.size);})
          .attr("dy", ".35em")
          .style("fill", defaultNodeColor)
          .text(function(d) { return d.id; });
    } else {
        node.append("circle")
          .attr("r", BASE_RADIUS)
          .style("fill", defaultNodeColor);

        node.append("text")
          .attr("x", 1.5*BASE_RADIUS)
          .attr("dy", ".35em")
          .style("fill", defaultNodeColor)
          .text(function(d) { return d.id; });
    }



    // Adjusts a grid-like initial positioning
    var side = Math.ceil(Math.sqrt(nodes.length));
    node.each(function(d,i){
      d.fixed = true;
      if(!isGexfGraph){
        d.x = BASE_W + (i % side) * LINK_DISTANCE;
        d.y = BASE_H + Math.floor(i/side) * LINK_DISTANCE;
      }
    });

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
    if(showWeights)
      edgepaths.attr('d', function(d) { var path='M '+d.source.x+' '+d.source.y+' L '+ d.target.x +' '+d.target.y; return path});
    else
      edgepaths.attr('d', function(d) { var path='M -2000 -2000 L -2000 -2000'; return path});
}

// Restores the selected node to its initial state
function mouseout() {
    if(selected != null){
      if(auxiliarTextDistance != null){
        d3.select("[nodeId='" + selected + "']").select("text").transition().attr("x", auxiliarTextDistance).style("font", "12px serif");
        auxiliarTextDistance = null;
      }
      if(auxiliarRadius != null){
        d3.select(this).select("circle").transition().attr("r", auxiliarRadius);
        auxiliarRadius = null;
      }
    }
}

// When a node is clicked, it becomes bigger and it sends a request to the server (by using websockets)
function click() {
    auxiliarTextDistance = d3.select(this).select("text").attr("x");
    d3.select(this).select("text").transition().attr("x", 1.5*auxiliarTextDistance).style("font", "17.5px serif");

    auxiliarRadius = d3.select(this).select("circle").attr("r");
    d3.select(this).select("circle").transition().attr("r", 2*auxiliarRadius);

    selected = d3.select(this).select("text").text();
    var message = buildMessage(NODE, selected);

    // Requests information to the server
    ws.send(message);
}

