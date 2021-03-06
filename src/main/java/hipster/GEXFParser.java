package hipster;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class GEXFParser {
    private static final List<String> necessaryTags = Arrays.asList("gexf","edges","nodes");

    public static MyGraph getGraph(String filePath) {
        MyGraph g = new MyGraph();
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList graphInfo = doc.getElementsByTagName("graph");
            Element el = (Element) graphInfo.item(0);
            String directedInfo = el.getAttribute("defaultedgetype");

            // If the field doesn't exist, it will be assumed that the graph is directed
            boolean isDirected = (directedInfo == null || !directedInfo.equals("undirected"));
            g.setDirected(isDirected);
 
            // Obtains link data
            NodeList linksX = doc.getElementsByTagName("edge");
            List<Link> linksL = new ArrayList<>();
            for (int i=0; i<linksX.getLength(); i++) {
                Node xmlNode = linksX.item(i);
                if(xmlNode.getNodeType() != Node.ELEMENT_NODE)
                    continue;
     
                Link link = new Link();
                Element e = (Element) xmlNode;
                String source = e.getAttribute("source");
                String target = e.getAttribute("target");
                String weightS = e.getAttribute("weight");
                double weight = 1;
                
                // Fills with weight value if possible
                if(!weightS.isEmpty())
                    weight = Double.valueOf(weightS);
                link.setSource(Integer.valueOf(source));
                link.setTarget(Integer.valueOf(target));
                link.setWeight(weight);
                linksL.add(link);
         
                if(g.isDirected())
                    continue;

                // Creates duplicated inverse link if graph is undirected
                linksL.add(new Link(link, true));
            }
            g.setLinks(linksL);

            // Obtains node data
            NodeList nodesX = doc.getElementsByTagName("node");
            List<MyNode> nodesL = new ArrayList<>();
            for (int i=0; i<nodesX.getLength(); i++) {
                Node xmlNode = nodesX.item(i);
                if(xmlNode.getNodeType() != Node.ELEMENT_NODE)
                    continue;
     
                MyNode node = new MyNode();
                Element e = (Element) xmlNode;
                int id = Integer.valueOf(e.getAttribute("id"));
                String label = e.getAttribute("label");
                
                node.setId(id);
                node.setInfo(label);
                nodesL.add(node);
            }
            g.setNodes(nodesL);
            return g;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isValid(InputStream is){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
 
        // If an exception is raised, then the file is bad-formed
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream copy = Utils.copyInputStream(is);
            Document doc = builder.parse(copy);

            // All GEXF valid graphs must have one "gexf", "edges" and "nodes" tag
            for(String tag : necessaryTags){
                NodeList someNode = doc.getElementsByTagName(tag);
                Element el = (Element) someNode.item(0);
                if(el == null)
                    return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
