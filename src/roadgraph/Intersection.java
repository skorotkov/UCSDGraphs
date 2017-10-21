package roadgraph;

import geography.GeographicPoint;
import geography.RoadSegment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class which represents the intersection.
 *
 * It contains a geographic point of the intersection itself and hash table which maps neighbor intersections
 * to roads leading to this particular neighbor.
 */
public class Intersection {
    private GeographicPoint point;
    private double cost = Double.POSITIVE_INFINITY;
    private HashMap<Intersection, Road> neighbors;

    /**
     * Create new intersection
     * @param point geographic point of the intersection
     */
    public Intersection(GeographicPoint point) {
        this.point = point;
        neighbors = new HashMap<>();
    }

    /**
     * Returns all neighbors of this intersection
     * @return set of geographical points of all neighbors
     */
    public Map<Intersection, Double> getNeighbors() {
        return neighbors.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getLength()));
    }

    /**
     * Checks if the passed point corresponds to intersection which is a neighbor of this one
     * @param point to check
     * @return true if the passed point is neighbor of this one
     */
    public boolean hasNeighbor(Intersection point) {
        return neighbors.containsKey(point);
    }

    /**
     * Adds the passed point to set of neighbor of this intersection
     * @param neighbor point of the neighbor intersection
     * @param readToNeighbor road leading to the neighbor intersection
     */
    public void addNeighbor(Intersection neighbor, Road readToNeighbor) {
        neighbors.put(neighbor, readToNeighbor);
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void resetCost() {
        this.cost = Double.POSITIVE_INFINITY;
    }

    public GeographicPoint getPoint() {
        return point;
    }

}
