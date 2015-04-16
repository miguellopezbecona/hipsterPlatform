package hipster;

import java.util.List;

/**
 *
 * @author Miguel López
 */
public class Layout {
    public static String buildTree(int node){
	String n = "";
        int numChildren;

        n += "{\"name\": " + node;
        List<Integer> children = WebSocketServer.getLinkMap().get(node);
        if( (numChildren = children.size() )!=0){
            n += ", \"children\": [";
            for(int i=0;i<numChildren;i++){
                n += buildTree(children.get(i));
                if(i!=numChildren-1)
                    n += ",";
            }
            n += "]";
        }
        n += "}";

        return n;
    }
}
