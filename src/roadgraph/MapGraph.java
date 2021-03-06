/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which reprsents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
package roadgraph;


import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import geography.GeographicPoint;
import util.GraphLoader;

/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which represents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
public class MapGraph {

    private HashMap<GeographicPoint, Intersection> intersections = new HashMap<>();

    /**
     * Create a new empty MapGraph
     */
    public MapGraph()
    {
    }

    /**
     * Get the number of vertices (road intersections) in the graph
     * @return The number of vertices in the graph.
     */
    public int getNumVertices()
    {
        return intersections.size();
    }

    /**
     * Return the intersections, which are the vertices in this graph.
     * @return The vertices in this graph as GeographicPoints
     */
    public Set<GeographicPoint> getVertices()
    {
        return intersections.keySet();
    }

    /**
     * Get the number of road segments in the graph
     * @return The number of edges in the graph.
     */
    public int getNumEdges()
    {
        return intersections.values().stream().map(i -> i.getNeighbors().size()).mapToInt(i -> i).sum();
    }


    /** Add a node corresponding to an intersection at a Geographic Point
     * If the location is already in the graph or null, this method does
     * not change the graph.
     * @param location  The location of the intersection
     * @return true if a node was added, false if it was not (the node
     * was already in the graph, or the parameter is null).
     */
    public boolean addVertex(GeographicPoint location) {
        return location != null && intersections.putIfAbsent(location, new Intersection(location)) == null;
    }

    /**
     * Adds a directed edge to the graph from pt1 to pt2.
     * Precondition: Both GeographicPoints have already been added to the graph
     * @param from The starting point of the edge
     * @param to The ending point of the edge
     * @param roadName The name of the road
     * @param roadType The type of the road
     * @param length The length of the road, in km
     * @throws IllegalArgumentException If the points have not already been
     *   added as nodes to the graph, if any of the arguments is null,
     *   or if the length is less than 0.
     */
    public void addEdge(GeographicPoint from, GeographicPoint to, String roadName,
            String roadType, double length) throws IllegalArgumentException {

        if (from == null || to == null || roadName == null || roadType == null || length < 0 ||
            !intersections.containsKey(from) || !intersections.containsKey(to) ||
            intersections.get(from).hasNeighbor(intersections.get(to)))
            //throw new IllegalArgumentException();
            return;

        intersections.get(from).addNeighbor(
                intersections.get(to), new Road(from, to, new ArrayList<>(), roadName, roadType, length));
    }


    /** Find the path from start to goal using breadth first search
     *
     * @param start The starting location
     * @param goal The goal location
     * @return The list of intersections that form the shortest (unweighted)
     *   path from start to goal (including both start and goal).
     */
    public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
        // Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return bfs(start, goal, temp);
    }

    /** Find the path from start to goal using breadth first search
     *
     * @param start The starting location
     * @param goal The goal location
     * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
     * @return The list of intersections that form the shortest (unweighted)
     *   path from start to goal (including both start and goal).
     */
    public List<GeographicPoint> bfs(GeographicPoint start,
                                      GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
    {
        HashMap<GeographicPoint, GeographicPoint> path = new HashMap<>();
        if (bfsSearch(start, goal, nodeSearched, path))
            return createPath(goal, path);
        else
            return null;
    }

    /** Find the path from start to goal using Dijkstra's algorithm
     *
     * @param start The starting location
     * @param goal The goal location
     * @return The list of intersections that form the shortest path from
     *   start to goal (including both start and goal).
     */
    public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
        // Dummy variable for calling the search algorithms
        // You do not need to change this method.
        Consumer<GeographicPoint> temp = (x) -> System.out.println(x.toString());
        return dijkstra(start, goal, temp);
    }

    /** Find the path from start to goal using Dijkstra's algorithm
     *
     * @param start The starting location
     * @param goal The goal location
     * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
     * @return The list of intersections that form the shortest path from
     *   start to goal (including both start and goal).
     */
    public List<GeographicPoint> dijkstra(GeographicPoint start,
                                          GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
    {
        HashMap<GeographicPoint, GeographicPoint> path = new HashMap<>();
        if (weightedSearch(start, goal, nodeSearched, path, QueueEntry::compareTo))
            return createPath(goal, path);
        else
            return null;
    }

    /** Find the path from start to goal using A-Star search
     *
     * @param start The starting location
     * @param goal The goal location
     * @return The list of intersections that form the shortest path from
     *   start to goal (including both start and goal).
     */
    public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
        // Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> System.out.println(x.toString());
        return aStarSearch(start, goal, temp);
    }

    /** Find the path from start to goal using A-Star search
     *
     * @param start The starting location
     * @param goal The goal location
     * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
     * @return The list of intersections that form the shortest path from
     *   start to goal (including both start and goal).
     */
    public List<GeographicPoint> aStarSearch(GeographicPoint start,
                                             GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
    {
        HashMap<GeographicPoint, GeographicPoint> path = new HashMap<>();
        if (weightedSearch(start, goal, nodeSearched, path,
                Comparator.comparingDouble(a -> a.getCost() + a.getIntersection().getPoint().distance(goal))))
            return createPath(goal, path);
        else
            return null;
    }


    /**
     * Reconstruct the path from the traversal history
     * @param goal Goal point
     * @param traversalHistory History of graph traversal, maps nodes to previously visited ones.
     *
     * @return list of points forming a path
     */
    private List<GeographicPoint> createPath(GeographicPoint goal, HashMap<GeographicPoint, GeographicPoint> traversalHistory) {
        LinkedList<GeographicPoint> list = new LinkedList<>();

        GeographicPoint curr = goal;
        while (traversalHistory.containsKey(curr)) {
            list.addFirst(curr);
            curr = traversalHistory.get(curr);
        }
        list.addFirst(curr);

        return list;
    }

    /**
     * Search the path from the start point to the goal point in the graph using the breadth first search
     * @param start Start point
     * @param goal Goal point
     * @param nodeSearched A hook for visualization.
     * @param traversalHistory History of graph traversal, maps nodes to previously visited ones. If path is found it may
     *             be used to reconstruct the path.
     * @return true is path is found, false otherwise
     */
    private boolean bfsSearch(GeographicPoint start, GeographicPoint goal, Consumer<GeographicPoint> nodeSearched,
                              HashMap<GeographicPoint, GeographicPoint> traversalHistory) {
        HashSet<GeographicPoint> visited = new HashSet<>();
        Queue<GeographicPoint> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while(!queue.isEmpty()) {
            GeographicPoint curr = queue.remove();
            nodeSearched.accept(curr);

            if (curr.equals(goal))
                return true;

            getNeighbors(curr)
                .stream()
                .filter(n -> !visited.contains(n))
                .forEach(n -> {
                    queue.add(n);
                    visited.add(n);
                    traversalHistory.put(n, curr);
                });
        }

        return false;
    }

    private boolean weightedSearch(GeographicPoint start, GeographicPoint goal, Consumer<GeographicPoint> nodeSearched,
                                   HashMap<GeographicPoint, GeographicPoint> traversalHistory, Comparator<QueueEntry> comparator) {
        HashSet<Intersection> visited = new HashSet<>();
        PriorityQueue<QueueEntry> queue = new PriorityQueue<>(comparator);

        intersections.values().forEach(Intersection::resetCost);

        Intersection startNode = intersections.get(start);
        startNode.setCost(0);
        queue.add(QueueEntry.of(startNode, startNode.getCost()));

        int counter = 0;

        while(!queue.isEmpty()) {
            Intersection curr = queue.remove().getIntersection();
            counter++;
            if (visited.contains(curr))
                continue;
            nodeSearched.accept(curr.getPoint());

            visited.add(curr);
            if (curr.getPoint().equals(goal)) {
                System.out.println("counter=" + counter);
                return true;
            }

            curr.getNeighbors()
                .entrySet()
                .stream()
                .filter(e -> !visited.contains(e.getKey()))
                .filter(e -> curr.getCost() + e.getValue() < e.getKey().getCost())
                .forEach(e -> {
                    e.getKey().setCost(curr.getCost() + e.getValue());
                    queue.add(QueueEntry.of(e.getKey(), e.getKey().getCost()));
                    traversalHistory.put(e.getKey().getPoint(), curr.getPoint());
                });
        }

        return false;
    }

    /**
     * Get neighbors of intersection represented by this point
     * @param point
     * @return set of neighbors as list of geographical points
     */
    private Set<GeographicPoint> getNeighbors(GeographicPoint point) {
        return intersections.get(point).getNeighbors().entrySet().stream().map(e -> e.getKey().getPoint()).collect(Collectors.toSet());
    }


    public static void main(String[] args)
    {
        System.out.print("Making a new map...");
        MapGraph firstMap = new MapGraph();
        System.out.print("DONE. \nLoading the map...");
        GraphLoader.loadRoadMap("data/testdata/simpletest.map", firstMap);
        System.out.println("DONE.");

        // You can use this method for testing.


        /* Here are some test cases you should try before you attempt
         * the Week 3 End of Week Quiz, EVEN IF you score 100% on the
         * programming assignment.
         */
/*
        MapGraph simpleTestMap = new MapGraph();
        GraphLoader.loadRoadMap("data/testdata/simpletest.map", simpleTestMap);

        GeographicPoint testStart = new GeographicPoint(1.0, 1.0);
        GeographicPoint testEnd = new GeographicPoint(8.0, -1.0);

        System.out.println("Test 1 using simpletest: Dijkstra should be 9 and AStar should be 5");
        System.out.println("Test 1 Dijkstra");
        List<GeographicPoint> testroute = simpleTestMap.dijkstra(testStart,testEnd);
        System.out.println("Test 1 AStar");
        List<GeographicPoint> testroute2 = simpleTestMap.aStarSearch(testStart,testEnd);


        MapGraph testMap = new MapGraph();
        GraphLoader.loadRoadMap("data/maps/utc.map", testMap);

        // A very simple test using real data
        testStart = new GeographicPoint(32.869423, -117.220917);
        testEnd = new GeographicPoint(32.869255, -117.216927);
        System.out.println("Test 2 using utc: Dijkstra should be 13 and AStar should be 5");
        System.out.println("Test 2 Dijkstra");
        testroute = testMap.dijkstra(testStart,testEnd);
        System.out.println("Test 2 AStar");
        testroute2 = testMap.aStarSearch(testStart,testEnd);


        // A slightly more complex test using real data
        testStart = new GeographicPoint(32.8674388, -117.2190213);
        testEnd = new GeographicPoint(32.8697828, -117.2244506);
        System.out.println("Test 3 using utc: Dijkstra should be 37 and AStar should be 10");
        System.out.println("Test 3 Dijkstra");
        testroute = testMap.dijkstra(testStart,testEnd);
        System.out.println("Test 3 AStar");
        testroute2 = testMap.aStarSearch(testStart,testEnd);

*/

        /* Use this code in Week 3 End of Week Quiz */
        MapGraph theMap = new MapGraph();
        System.out.print("DONE. \nLoading the map...");
        GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
        System.out.println("DONE.");

        GeographicPoint start = new GeographicPoint(32.8648772, -117.2254046);
        GeographicPoint end = new GeographicPoint(32.8660691, -117.217393);


        List<GeographicPoint> route = theMap.dijkstra(start,end);
        List<GeographicPoint> route2 = theMap.aStarSearch(start,end);


    }
}
