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
          n.setInfo(Integer.toString(Constants.random.nextInt()));
          nodes.add(n);
        }
    }
}
