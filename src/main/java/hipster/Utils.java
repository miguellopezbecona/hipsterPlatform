package hipster;

import es.usc.citius.hipster.util.graph.HashBasedHipsterDirectedGraph;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;

public class Utils implements Constants{
    public static HashBasedHipsterDirectedGraph initializeGraph(List<Link> list){
        HashBasedHipsterDirectedGraph g = new HashBasedHipsterDirectedGraph<>();
        for(Link l : list)
            g.connect(l.getSource(), l.getTarget(), l.getWeight());
        
        return g;
    }

    public static List<String> obtainAvailableGraphs(){
        List<String> list = new ArrayList<>();

        File folder = new File(GRAPH_BASE_PATH);
        for (File file : folder.listFiles())
            list.add(file.getName());

        return list;
    }

}
