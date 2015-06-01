package hipster;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Miguel López
 */
public class MyGraph {
    private List<MyNode> nodes;
    private List<Link> links;
    private int width;
    private int height;
    private boolean directed;

    public MyGraph() {
    }

    public List<MyNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<MyNode> nodes) {
        this.nodes = nodes;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public void initializeRandomNodes(){
        if(nodes != null && !nodes.isEmpty())
            return;

        int numMyNodes = links.size() + 1;
        nodes = new ArrayList<>();

        for(int i=0;i<numMyNodes;i++) {
          MyNode n = new MyNode();
          n.setId(i);
          n.setInfo("nothing");
          nodes.add(n);
        }
    }

    public void duplicateLinks(){
        List<Link> list = new ArrayList();
        for(Link l : links)
            list.add(new Link(l, true));
        links.addAll(list);
    }

    /**
     * Returns the node whose id is state. It's implemented because array index couldn't be equal to node id
     * @param state - The state (id) of the node you want to get
     * @returns The node whose id is "state", null in any other cases
     */ 
    public MyNode getNode(String state){
        if(nodes == null || state == null || state.isEmpty())
            return null;

        for(MyNode n : nodes){
            String idStr = Integer.toString(n.getId());
            if(idStr.equals(state))
                return n;
        }

        return null;
    }
}
