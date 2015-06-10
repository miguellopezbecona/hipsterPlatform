function printGrid(){
  var side = parseInt(document.getElementById("side").value);
  document.getElementById("output").innerHTML = generateGrid(side);
}

function generateGrid(side){
  if(side < 2 || side > 10) return "";

  var numNodes = side*side;
  var graph = {};
  var links = [];

  for (var r = 0; r < side; r++) {
    for (var c = 0; c < side; c++) {
      var nodeId = r*side +c;

      // Adds link to the next horizontal node, except for the last column
      if(c != (side -1) )
        links.push({
          source: nodeId,
          target: nodeId+1,
          weight: 1
        });
 
      // Adds link to the next vertical node, except for the last row
      if(r != (side -1) )
        links.push({
          source: nodeId,
          target: nodeId + side,
          weight: 1
        });

      // Adds diagonal links to lower nodes if possible (not in final row)
      // Link to southeast node (not in final column)
      if(c != (side -1) && r != (side - 1) )
        links.push({
          source: nodeId,
          target: nodeId + side + 1,
          weight: parseFloat(Math.SQRT2).toFixed(2)
        });

      // Link to southwest node (not in first column)
      if(c != 0 && r != (side - 1))
        links.push({
          source: nodeId,
          target: nodeId + side - 1,
          weight: parseFloat(Math.SQRT2).toFixed(2)
        });

      }
  }

  graph.links = links;
  graph.directed = false;
  return JSON.stringify(graph);
}
