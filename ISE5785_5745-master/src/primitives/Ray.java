package primitives;

import static primitives.Util.isZero;

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


}
