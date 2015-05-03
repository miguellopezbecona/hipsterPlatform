package hipster;

import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
 
public class GEXFParser {
    public static List<Link> getLinks(String filePath) {
        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
 
            NodeList links = doc.getElementsByTagName("edge");
            List<Link> list = new ArrayList<>();

            for (int i=0; i<links.getLength(); i++) {
                Node xmlNode = links.item(i);
                if(xmlNode.getNodeType() != Node.ELEMENT_NODE)
                    continue;
     
                Link link = new Link();
                Element e = (Element) xmlNode;
                String source = e.getAttribute("source");
                String target = e.getAttribute("target");
                String weightS = e.getAttribute("weight");
                double weight = 0;
                
                // Fills with weight value if possible
                if(!weightS.isEmpty())
                    weight = Double.valueOf(weightS);
                link.setSource(source);
                link.setTarget(target);
                link.setWeight(weight);
                list.add(link);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
