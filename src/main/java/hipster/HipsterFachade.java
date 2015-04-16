package hipster;

import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.model.problem.SearchProblem;
import es.usc.citius.hipster.util.graph.GraphSearchProblem;
import es.usc.citius.hipster.util.graph.HashBasedHipsterDirectedGraph;
import java.util.ArrayList;
import java.util.List;

public class HipsterFachade {
    public static List<String> dijkstra(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        Algorithm.SearchResult o = Hipster.createDijkstra(p).search(goal);
        return (List<String>) o.getOptimalPaths().get(0);
    }

    public static List<String> breadth(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        Algorithm.SearchResult o = Hipster.createBreadthFirstSearch(p).search(goal);
        return (List<String>) o.getOptimalPaths().get(0);
    }

    public static List<String> depth(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        Algorithm.SearchResult o = Hipster.createDepthFirstSearch(p).search(goal);
        return (List<String>) o.getOptimalPaths().get(0);
    }

    public static List<String> bellmanFord(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        Algorithm.SearchResult o = Hipster.createBellmanFord(p).search(goal);
        return (List<String>) o.getOptimalPaths().get(0);
    }
}
