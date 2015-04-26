package hipster;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.gson.reflect.TypeToken;

import java.util.List;

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
    public Response applyLayout (@PathParam("layout") String layout, String literal){
        // Bad request if the list is empty
        if(literal == null || literal.isEmpty())
            return Response.status(Response.Status.BAD_REQUEST).build();

        List<Node> list = gson.fromJson(literal, new TypeToken<List<Node>>(){}.getType());

        switch(layout){
            case RANDOM:
                for(Node n : list){
                    n.setX( Math.abs(random.nextInt() % 900) + 300);
                    n.setY( Math.abs(random.nextInt() % 300) + 100);
                }
                break;
        }

        return Response.ok(gson.toJson(list),MediaType.APPLICATION_JSON).build(); 
    }
}
