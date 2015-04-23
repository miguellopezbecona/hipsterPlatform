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
        url: "uploadGraph",
        data: formData,
        // Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false,
        success: function (data, textStatus, response) {
            showFeedback("success", "Your graph was loaded successfully. If you want to use it in future executions, put the following hash in the input field: " + data);
             var message = buildMessage(BEGIN, "'"+data+"'");

             // Sends a message so the server gives back the uploaded graph
             ws.send(message);
        },
        error: function (response, textStatus, errorThrown) {
            showFeedback("danger", "There was a problem while parsing your graph." + textStatus + " " + errorThrown);
        }
    });
  });

  // Load a server-stored graph
  $("#loadGraphH").on("click", function(){
    var selection = $("#hash").val();

    // It the hash is empty, the selection will be the examples list
    if(!selection || selection.length === 0) return;
    
    // Stops if the input data (without the extension) hasn't the length of the hash
    if(selection.split("\.")[0].length != HASH_LENGTH){
      showFeedback("danger", "Your input doesn't correspond to a hash.")
      return;
    }

    var message = buildMessage(BEGIN, "'"+selection+"'");

    // Sends a message so the server gives back the selected graph
    ws.send(message);
  });

  $("#loadGraphS").on("click", function(){
    var selection = $("#selectedGraph").val();

    var message = buildMessage(BEGIN, "'"+selection+"'");

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
    layout.options[i] = new Option(LAYOUTS[i].toLowerCase(), LAYOUTS[i], false);

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
    return filename.split("\.")[filename.split("\.").length-1];
}
