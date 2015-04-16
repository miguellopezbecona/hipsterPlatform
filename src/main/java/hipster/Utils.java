package hipster;

import es.usc.citius.hipster.util.graph.HashBasedHipsterDirectedGraph;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Utils implements Constants{
    public static HashBasedHipsterDirectedGraph initializeGraph(List<Link> list){
        HashBasedHipsterDirectedGraph g = new HashBasedHipsterDirectedGraph<>();
        for(Link l : list)
            g.connect(l.getSource(), l.getTarget(), l.getWeight());
        
        return g;
    }

    public static List<String> obtainAvailableGraphs(){
        List<String> list = new ArrayList<>();

        String file = null;

        try {
            // Does a system call in order to know what files are in the graph folder
            Process p = Runtime.getRuntime().exec("ls " + GRAPH_BASE_PATH);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((file = stdInput.readLine()) != null)
                list.add(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

}
