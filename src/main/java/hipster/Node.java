package hipster;

/**
 *
 * @author Miguel López
 */
public class Node {
    private int nodeId;
    private String info;

    public Node(){
    }

    public int getNodeId(){
        return nodeId;
    }

    public String getInfo(){
        return info;
    }

    public void setNodeId(int id){
        nodeId = id;
    }

    public void setInfo(String in){
        info = in;
    }
}
