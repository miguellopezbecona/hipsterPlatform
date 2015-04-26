package hipster;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

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
    public Response getGraph (@PathParam("layout") String layout, List<Node> list){
        // Bad request if the list is empty
        if(list == null || list.isEmpty())
            return Response.status(Response.Status.BAD_REQUEST).build();

        switch(layout){
            case RANDOM:
                for(Node n : list){
                    n.setX(random.nextInt() % 1200);
                    n.setY(random.nextInt() % 600);
                }
                break;
        }

        return Response.ok(list,MediaType.APPLICATION_JSON).build(); 
    }
}
