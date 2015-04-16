function activateComponents(){
  // This section is initially invisible
  $("#operations").hide();

  // Gives functionality to the buttons
  $("#loadGraphP").on("click", function(){
    var selection = $("#graphToLoadP").val();

    // It does nothing when the selection is empty
    if(!selection || selection.length === 0) return;

    // Loads the selected file
    var file = $("#graphToLoadP")[0].files[0];
    reader.onload = function(e) {
      // The content will be filename_content. The server will parse it in this way
      var message = buildMessage(BEGIN, "'"+selection+"_"+reader.result+"'");
      ws.send(message);
    }
    reader.readAsText(file);
  });

  $("#loadGraph").on("click", function(){
    var selection = $("#graphToLoad").val();

    // It does nothing when the selection is empty
    if(!selection || selection.length === 0) return;

    var message = buildMessage(BEGIN, "'"+selection+"_ '");

    // Sends a message so the server gives back the selected graph
    ws.send(message);
  });

  $("#changeLayout").on("click", function(){
    d3.select("svg").remove(); // Resets the graphic
    var message = buildMessage(LAYOUT, $("#layout").val());
    ws.send(message);
  });

  $("#showPath").on("click", function(){
    // Default nodes that should be removed later
    if(initialNode==null) initialNode = "0";
    if(goalNode==null) goalNode = "7";

    var message;

    // Different if the algorithm is done step by step or not
    if($("#oneStep").is(":checked"))
      message = buildMessage(F_PATH, "'" + $("#algorithm").val() + " " + initialNode + " " + goalNode + "'");
    else
      message = buildMessage(P_PATH, "'" + $("#algorithm").val() + " " + initialNode + " " + goalNode + "'");
    ws.send(message);
  });

  $("#showWeights").change("click", function(){
    showWeights = $(this).is(":checked");
    tick();
  });

  // Builds the algorithm menu dynamically
  var select = $("#algorithm")[0];
  for(i=0;i<ALGORITHMS.length;i++)
    select.options[i] = new Option(ALGORITHMS[i].toLowerCase().replace("_"," "), ALGORITHMS[i], false);

  // Does the same with the layout menu
  var layout = $("#layout")[0];
  for(i=0;i<LAYOUTS.length;i++)
    layout.options[i] = new Option(LAYOUTS[i].toLowerCase(), LAYOUTS[i], false);

  // These checkboxes are activated by default
  $("#showWeights")[0].checked = true;
  $("#oneStep")[0].checked = true;
}
