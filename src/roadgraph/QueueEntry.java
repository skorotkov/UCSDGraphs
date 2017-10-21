package roadgraph;

class QueueEntry implements Comparable<QueueEntry>{
    private Intersection intersection;
    private double cost;

    public QueueEntry(Intersection intersection, double cost) {
        this.intersection = intersection;
        this.cost = cost;
    }

    @Override
    public int compareTo(QueueEntry o) {
        return Double.compare(this.cost, o.getCost());
    }

    public Intersection getIntersection() {
        return intersection;
    }

    public double getCost() {
        return cost;
    }

    public static QueueEntry of(Intersection intersection, double cost) {
        return new QueueEntry(intersection, cost);
    }
}
