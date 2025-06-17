package primitives;

import java.util.List;

import static primitives.Util.isZero;
import geometries.Intersectable.Intersection;

/**
 * Opening a Class for representation Ray
 *
 * @author Eitan lafair
 */
public class Ray {

    public final Point head;
    public final Vector direction;

    /**
     * Constructs a Ray from a starting point and a direction vector.
     * The direction vector is normalized upon creation.
     *
     * @param head The starting point of the ray.
     * @param direction The direction vector of the ray (will be normalized).
     */
    public Ray(Point head, Vector direction) {
        this.head = head;
        this.direction = direction.normalize();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Ray other)
                && this.head.equals(other.head)
                && this.direction.equals(other.direction);
    }

    @Override
    public String toString() {
        return "Ray [start=" + head.toString() + ", direction=" + direction.toString() + "]";
    }


    /**
     * Returns a point on the ray at a given distance from the head of the ray.
     *
     * @param t The distance from the head of the ray. Must not be zero.
     * @return The point at the given distance along the ray.
     *
     */
    public Point getPoint(double t) {
        if (isZero(t)) {
            throw new IllegalArgumentException("t is equal to 0 produce an illegal ZERO vector");
        }
        return head.add(direction.scale(t));
    }


    public Intersection findClosestIntersection(List<Intersection> intersections) {
        if (intersections == null || intersections.isEmpty()) {
            return null;
        }
        Intersection closest = null;
        double minDistance = Double.POSITIVE_INFINITY;

        for (Intersection inter : intersections) {
            double distance = head.distance(inter.point);
            if (distance < minDistance) {
                minDistance = distance;
                closest = inter;
            }
        }
        return closest;
    }

    /**
     * Finds the closest point to the ray's origin from a list of points.
     *
     * @return The closest point to the ray's head, or null if the list is empty.
     */

    public Point findClosestPoint(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return null;
        }
        Intersection closest = findClosestIntersection(
                points.stream().map(p -> new Intersection(null, p,null)).toList()
        );
        return closest == null ? null : closest.point;
    }








}
