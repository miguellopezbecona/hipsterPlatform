function zoomed(){
  container.attr("transform", "translate(" + zoom.translate() + ")scale(" + zoom.scale() + ")");
}

function interpolateZoom (translate, scale) {
    return d3.transition().duration(350).tween("zoom", function () {
        var iTranslate = d3.interpolate(zoom.translate(), translate),
            iScale = d3.interpolate(zoom.scale(), scale);
        return function (t) {
            zoom
                .scale(iScale(t))
                .translate(iTranslate(t));
            zoomed();
        };
    });
}

function zoomClick() {
    var clicked = d3.event.target,
        direction = 1,
        factor = 0.2,
        target_zoom = 1,
        center = [WIDTH * 0.5, HEIGHT * 0.5],
        extent = zoom.scaleExtent(),
        translate = zoom.translate(),
        translate0 = [],
        l = [],
        view = {x: translate[0], y: translate[1], k: zoom.scale()};

    d3.event.preventDefault();
    if(this.id === 'zoomIn')
      direction = 1;
    else if(this.id === 'zoomOut')
      direction = -1;
    else
      return;
    target_zoom = zoom.scale() * (1 + factor * direction);

    if (target_zoom < extent[0] || target_zoom > extent[1]) { return false; }

    translate0 = [(center[0] - view.x) / view.k, (center[1] - view.y) / view.k];
    view.k = target_zoom;
    l = [translate0[0] * view.k + view.x, translate0[1] * view.k + view.y];

    view.x += center[0] - l[0];
    view.y += center[1] - l[1];

    interpolateZoom([view.x, view.y], view.k);
}

function centerGraph(){
    // Obtains graphic's bounds
    var minX, maxX, minY, maxY;
    minX = minY = Number.MAX_VALUE;
    maxX = maxY = Number.MIN_VALUE;
    node.each(function(d){
        if(d.x < minX) minX = d.x;
        else if(d.x > maxX) maxX = d.x;

        if(d.y < minY) minY = d.y;
        else if(d.y > maxY) maxY = d.y;
    });

    // Obtains "free" space where to place the graph
    var someMargin = 50;
    var usedWidth = $("#leftPanel").width() + $("#leftPanel").position().left + someMargin;
    var usedHeight = $("#showLegendPanel").height();
    var availableWidth = WIDTH - usedWidth;

    // Calculates graph proportion
    var factor = availableWidth / (maxX - minX);
    if(factor > 1) factor = 1; // No zoomIn if there is enough view

    var translateX = usedWidth - minX;
    var translateY = usedHeight - minY;

    // Centers the view to the right place with the appropiate scale factor
    container.attr("transform", "translate(" + translateX + "," + translateY + ")scale(" + factor + ")");

    // Updates zoom object to maintain the view
    zoom.translate([translateX,translateY]);
    zoom.scale(factor);
    zoomed();
    tick();
}

d3.selectAll('i').on('click', zoomClick);

d3.select('#centerGraph').on('click', centerGraph);

