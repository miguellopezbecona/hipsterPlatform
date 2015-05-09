package hipster;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import java.awt.Dimension;
import java.awt.geom.Point2D;

/**
 *
 * @author Miguel López
 */
@Path("/api/layout")
public class LayoutServices implements Constants {
    
    @POST
    @Path("/{layout}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response applyLayout (@PathParam("layout") String layoutName, MyGraph graph){
        int width = graph.getWidth();
        int height = graph.getHeight();

        // Bad request if the list is empty or the dimensions are wrong
        if(graph == null || width < 1 || height < 1)
            return Response.status(Response.Status.BAD_REQUEST).build();

        List<MyNode> nodes = graph.getNodes();
        
        // Some initialization work
        Graph<MyNode, Link> g = new DirectedSparseMultigraph<>();
        for(Link l : graph.getLinks()) {
            g.addEdge(l,
                nodes.get(Integer.valueOf(l.getSource())),
                nodes.get(Integer.valueOf(l.getTarget()))
            );
        }

        Layout<MyNode, Point2D> layout = null;

        switch(layoutName){
            case RANDOM:
                applyRandom(nodes, width, height);
                break;
            case CIRCLE:
                layout = new CircleLayout(g);
                applyLayout(layout, nodes, width, height);
                break;
            case FR:
                layout = new FRLayout(g);
                applyLayout(layout, nodes, width, height);
                break;
            case GRID:
                applyGrid(nodes, width, height);
                break;
            case ISOM:
                layout = new ISOMLayout(g);
                applyLayout(layout, nodes, width, height);
                break;
            case KK:
                layout = new KKLayout(g);
                applyLayout(layout, nodes, width, height);
                break;
            case SPRING:
                layout = new SpringLayout(g);
                applyLayout(layout, nodes, width, height);
                break;
            default: // Unknown layout: bad request
                return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok(gson.toJson(nodes),MediaType.APPLICATION_JSON).build(); 
    }

    // Same work to every non-random or grid layout
    private void applyLayout(Layout<MyNode, Point2D> layout, List<MyNode> nodes, int width, int height){
        layout.setSize(new Dimension(width, height));
        for (MyNode n : nodes) {
            Point2D coord = layout.transform(n);
            n.setX(coord.getX());
            n.setY(coord.getY());
        }
    }

    private void applyGrid(List<MyNode> nodes, int width, int height){
        // Tries to build a square-like grid
        int side = (int) Math.ceil(Math.sqrt(nodes.size()));

        int baseW = width/2 - 200;
        int baseH = height/2 - 100;
        int nodeDistance = (width - baseW) / side;

        for(int r=0;r<side;r++){
            for(int c=0;c<side;c++){
                int toSelect = r*side+c;

                // Stops when all nodes are processed
                if(toSelect == nodes.size())
                    break;
                MyNode node = nodes.get(toSelect);
                node.setX(baseW + c * nodeDistance);
                node.setY(baseH + r * nodeDistance);
            }
        }
    }

    private void applyRandom(List<MyNode> nodes, int width, int height){
        for(MyNode n : nodes){
            n.setX( Math.abs(random.nextInt() % width) );
            n.setY( Math.abs(random.nextInt() % height) );
        }
    }
}
