package hipster;

import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.model.problem.SearchProblem;
import es.usc.citius.hipster.util.graph.GraphSearchProblem;
import es.usc.citius.hipster.util.graph.HashBasedHipsterDirectedGraph;
import java.util.Iterator;
import java.util.List;

public class HipsterFacade { 

    /*** One step executions ***/
    public static List<String> dijkstraOS(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        Algorithm.SearchResult o = Hipster.createDijkstra(p).search(goal);
        return (List<String>) o.getOptimalPaths().get(0);
    }

    public static List<String> breadthOS(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        Algorithm.SearchResult o = Hipster.createBreadthFirstSearch(p).search(goal);
        return (List<String>) o.getOptimalPaths().get(0);
    }

    public static List<String> depthOS(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        Algorithm.SearchResult o = Hipster.createDepthFirstSearch(p).search(goal);
        return (List<String>) o.getOptimalPaths().get(0);
    }

    public static List<String> bellmanFordOS(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        Algorithm.SearchResult o = Hipster.createBellmanFord(p).search(goal);
        return (List<String>) o.getOptimalPaths().get(0);
    }

    /*** Step by step executions ***/
    public static Iterator dijkstraIt(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        return Hipster.createDijkstra(p).iterator();
    }

    public static Iterator breadthIt(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        return Hipster.createBreadthFirstSearch(p).iterator();
    }

    public static Iterator depthIt(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        return Hipster.createDepthFirstSearch(p).iterator();
    }

    public static Iterator bellmanFordIt(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        return Hipster.createBellmanFord(p).iterator();
    }
}
