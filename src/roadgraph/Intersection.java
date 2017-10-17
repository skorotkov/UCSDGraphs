package roadgraph;

import geography.GeographicPoint;
import geography.RoadSegment;

import java.util.HashMap;
import java.util.Set;

/**
 * A class which represents the intersection.
 *
 * It contains a geographic point of the intersection itself and hash table which maps neighbor intersections
 * to roads leading to this particular neighbor.
 */
public class Intersection {
    private GeographicPoint point;
    private HashMap<GeographicPoint, RoadSegment> neighbors;

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
    public Set<GeographicPoint> getNeighbors() {
        return neighbors.keySet();
    }

    /**
     * Checks if the passed point corresponds to intersection which is a neighbor of this one
     * @param point to check
     * @return true if the passed point is neighbor of this one
     */
    public boolean hasNeighbor(GeographicPoint point) {
        return neighbors.containsKey(point);
    }

    /**
     * Adds the passed point to set of neighbor of this intersection
     * @param neighbor point of the neighbor intersection
     * @param readToNeighbor road leading to the neighbor intersection
     */
    public void addNeighbor(GeographicPoint neighbor, RoadSegment readToNeighbor) {
        neighbors.put(neighbor, readToNeighbor);
    }
}
