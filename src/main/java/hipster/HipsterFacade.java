package hipster;

import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.model.function.HeuristicFunction;
import es.usc.citius.hipster.model.impl.WeightedNode;
import es.usc.citius.hipster.model.problem.SearchProblem;
import es.usc.citius.hipster.util.graph.GraphSearchProblem;
import es.usc.citius.hipster.util.graph.HashBasedHipsterDirectedGraph;
import java.util.Iterator;
import java.util.List;

public class HipsterFacade implements Constants {
    /*** One step executions ***/
    private static List<String> handleOSReturn(Algorithm.SearchResult o){ 
        List<String> data = (List<String>) o.getOptimalPaths().get(0); // Gets the path
        data.add(Integer.toString(o.getIterations())); // Appends the cost of the search in iterations
        WeightedNode n = (WeightedNode) o.getGoalNode();
        data.add(n.getCost().toString()); // Appends the cost of the path as well
        return data;
    }

    public static List<String> dijkstraOS(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        return handleOSReturn( Hipster.createDijkstra(p).search(goal) );
    }

    public static List<String> breadthOS(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        return handleOSReturn( Hipster.createBreadthFirstSearch(p).search(goal) );
    }

    public static List<String> depthOS(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        return handleOSReturn( Hipster.createDepthFirstSearch(p).search(goal) );
    }

    public static List<String> bellmanFordOS(HashBasedHipsterDirectedGraph g, String first, String goal){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().build();
        return handleOSReturn( Hipster.createBellmanFord(p).search(goal) );
    }

    public static List<String> AStarOS(HashBasedHipsterDirectedGraph g, String first, String goal, HeuristicFunction<String, Double> hf){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().useHeuristicFunction(hf).build();
        return handleOSReturn( Hipster.createAStar(p).search(goal) );
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

    public static Iterator AStarIt(HashBasedHipsterDirectedGraph g, String first, String goal, HeuristicFunction<String, Double> hf){
        SearchProblem p = GraphSearchProblem.startingFrom(first).in(g).takeCostsFromEdges().useHeuristicFunction(hf).build();
        return Hipster.createAStar(p).iterator();
    }
}
