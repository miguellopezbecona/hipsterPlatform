function activateComponents(){
  // These sections are initially invisible
  $("#operations").hide();
  $("#rightPanel").hide();
  $("#feedback").hide();
  $("#zoomButtons").hide();

  /*** Gives functionality to the buttons ***/

  // Load your graph with a file
  $("#loadGraphP").on("click", function(){
    var selection = $("#graphToLoadP").val();

    // It does nothing when the selection is empty
    if(!selection || selection.length === 0) return;

    // Alerts when it doesn't have the right extension
    var extension = getExtension(selection);
    if(!checkExtension(extension)){
      showFeedback("danger", "Sorry, this application only supports the following file formats: " + EXT_SUPPORTED);
      return;
    }

    // Loads the selected file
    var file = $("#graphToLoadP")[0].files[0];
    reader.onload = function(e) {
      // The content will be filename_content
      var message = buildMessage(BEGIN, "'"+selection+"_"+reader.result+"'");
      ws.send(message);
    }
    reader.readAsText(file);
  });

  // Load a server-stored graph
  $("#loadGraph").on("click", function(){
    var selection = $("#hash").val();

    // It the hash is empty, the selection will be the examples list
    if(!selection || selection.length === 0){
      selection = $("#graphToLoad").val();

      // Does the same check with the menu (there could be no examples)
      if(!selection || selection.length === 0) return;
    }
    // Stops if the input data (without the extension) hasn't the length of the hash
    else if(selection.split("\.")[0].length != HASH_LENGTH){
      showFeedback("danger", "Your input doesn't correspond to a hash.")
      return;
    }

    var message = buildMessage(BEGIN, "'"+selection+"_ '");

    // Sends a message so the server gives back the selected graph
    ws.send(message);
  });

  // Change layout
  $("#changeLayout").on("click", function(){
    d3.select("svg").remove(); // Resets the graphic
    var message = buildMessage(LAYOUT, $("#layout").val());
    ws.send(message);
  });

  // Resolves a search problem
  $("#showPath").on("click", function(){
    // Default nodes that should be removed later
    if(initialNode==null) initialNode = "0";
    if(goalNode==null) goalNode = "7";

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

function checkExtension(extension){
    return EXT_SUPPORTED.indexOf(extension) > -1;
}

function getExtension(filename){
    return filename.split("\.")[filename.split("\.").length-1];
}
