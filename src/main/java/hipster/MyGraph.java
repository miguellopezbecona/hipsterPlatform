package hipster;

import java.util.List;

/**
 *
 * @author Miguel López
 */
public class MyGraph {
    private List<Node> nodes;
    private List<Link> links;

    public MyGraph() {
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
    
    
}
