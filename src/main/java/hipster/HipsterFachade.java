package hipster;

import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.model.problem.SearchProblem;
import es.usc.citius.hipster.util.graph.GraphSearchProblem;
import es.usc.citius.hipster.util.graph.HashBasedHipsterDirectedGraph;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HipsterFachade {
    private static Iterator it = null;

    /*** One step executions ***/
    public static List<String> dijkstraOS(HashBasedHipsterDirectedGraph g, String first, String goal){
        it = null;
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        Algorithm.SearchResult o = Hipster.createDijkstra(p).search(goal);
        return (List<String>) o.getOptimalPaths().get(0);
    }

    public static List<String> breadthOS(HashBasedHipsterDirectedGraph g, String first, String goal){
        it = null;
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        Algorithm.SearchResult o = Hipster.createBreadthFirstSearch(p).search(goal);
        return (List<String>) o.getOptimalPaths().get(0);
    }

    public static List<String> depthOS(HashBasedHipsterDirectedGraph g, String first, String goal){
        it = null;
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        Algorithm.SearchResult o = Hipster.createDepthFirstSearch(p).search(goal);
        return (List<String>) o.getOptimalPaths().get(0);
    }

    public static List<String> bellmanFordOS(HashBasedHipsterDirectedGraph g, String first, String goal){
        it = null;
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        Algorithm.SearchResult o = Hipster.createBellmanFord(p).search(goal);
        return (List<String>) o.getOptimalPaths().get(0);
    }

    /*** Step by step executions ***/
    public static List<String> dijkstraSbS(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        if(it==null) it = Hipster.createDijkstra(p).iterator();
        return Utils.handleIterator(it, goal);
    }

    public static List<String> breadthSbS(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        if(it==null) it = Hipster.createBreadthFirstSearch(p).iterator();
        return Utils.handleIterator(it, goal);
    }

    public static List<String> depthSbS(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        if(it==null) it = Hipster.createDepthFirstSearch(p).iterator();
        return Utils.handleIterator(it, goal);
    }

    public static List<String> bellmanFordSbS(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        if(it==null) it = Hipster.createBellmanFord(p).iterator();
        return Utils.handleIterator(it, goal);
    }
}
