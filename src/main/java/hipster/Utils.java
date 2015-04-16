package hipster;

import es.usc.citius.hipster.util.graph.HashBasedHipsterDirectedGraph;
import es.usc.citius.hipster.algorithm.AStar;
import es.usc.citius.hipster.algorithm.BellmanFord;
import es.usc.citius.hipster.algorithm.BreadthFirstSearch;
import es.usc.citius.hipster.algorithm.DepthFirstSearch;
import es.usc.citius.hipster.util.graph.GraphSearchProblem;
import es.usc.citius.hipster.model.impl.WeightedNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
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

    /** Returns a list containing the next node and the ones to be expanded
     * depending on the type of iterator
     */
    public static List<String> handleIterator(Iterator it, String goal){
        List<String> ret = new ArrayList<>();
        String nextNode;
        
        if(it instanceof AStar.Iterator){
            AStar.Iterator aux = (AStar.Iterator) it;
            
            if(aux.hasNext()){
                nextNode = aux.next().state().toString();
                ret.add(nextNode);
                ret.addAll(aux.getOpen().keySet());
                return ret;
            }
            else
                return null;
        } else if(it instanceof BreadthFirstSearch.Iterator){
            BreadthFirstSearch.Iterator aux = (BreadthFirstSearch.Iterator) it;
            
            if(aux.hasNext()){
                nextNode = aux.next().state().toString();
                ret.add(nextNode);
                Queue<WeightedNode> q = aux.getQueue();
                for(WeightedNode s : q)
                    ret.add(s.state().toString());
                if(nextNode.equals(goal))      
                    it = null;
                return ret;
            }
            else
                return null;
        } else if(it instanceof DepthFirstSearch.Iterator){
            DepthFirstSearch.Iterator aux = (DepthFirstSearch.Iterator) it;
            
            if(aux.hasNext()){
                nextNode = aux.next().state().toString();
                ret.add(nextNode);
                ret.addAll(aux.getClosed());
                if(nextNode.equals(goal))      
                    it = null;
                return ret;
            }
            else
                return null;
        } else if(it instanceof BellmanFord.Iterator){
            BellmanFord.Iterator aux = (BellmanFord.Iterator) it;
            
            if(aux.hasNext()){
                nextNode = aux.next().state().toString();
                ret.add(nextNode);
                if(nextNode.equals(goal))      
                    it = null;
                return ret;
            }
            else
                return null;
        } else
            return null;
    }
    
}
