package hipster;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.gson.reflect.TypeToken;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
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
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response applyLayout (@PathParam("layout") String layoutName, String literal){
        // Bad request if the list is empty
        if(literal == null || literal.isEmpty())
            return Response.status(Response.Status.BAD_REQUEST).build();

        // Some initialization work
        MyGraph graph = gson.fromJson(literal, new TypeToken<MyGraph>(){}.getType());
        
        Graph<Node, Link> g = new SparseMultigraph<>();
        for(Link l : graph.getLinks()) {
            g.addEdge(l,
                graph.getNodes().get(Integer.valueOf(l.getSource())),
                graph.getNodes().get(Integer.valueOf(l.getTarget()))
            );
        }

        Layout<Node, Point2D> layout = null;

        switch(layoutName){
            case RANDOM:
                for(Node n : graph.getNodes()){
                    n.setX( Math.abs(random.nextInt() % 900) + 300);
                    n.setY( Math.abs(random.nextInt() % 300) + 100);
                }
                break;
            case CIRCLE:
                layout = new CircleLayout(g);
                break;
            /*case DAG:
                layout = new DAGLayout(g); // Raises NullPointerException at layout.initializeLocation()
                break;*/
            case FR:
                layout = new FRLayout(g);
                break;
            case ISOM:
                layout = new ISOMLayout(g);
                break;
            case KK:
                layout = new KKLayout(g);
                break;
            case SPRING:
                layout = new SpringLayout(g);
                break;
            default: // Unknown layout: bad request
                return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        // Same work to every non-random layout
        if(!layoutName.equals(RANDOM)){
            layout.setSize(new Dimension(1300, 700));
            for (Node n : graph.getNodes()) {
                Point2D coord = layout.transform(n);
                n.setX(coord.getX());
                n.setY(coord.getY());
            }
        }

        return Response.ok(gson.toJson(graph.getNodes()),MediaType.APPLICATION_JSON).build(); 
    }
}
