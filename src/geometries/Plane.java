package geometries;

import primitives.Point;
import primitives.Vector;

public class Plane {
    final Point _q0;
    final Vector _normal;

    /**
     * Constructor that takes a point and a normal vector
     *
     * @param q0     a point
     * @param vector that gets normalize
     */
    public Plane(Point q0, Vector vector) {
        _q0 = q0;
        _normal = vector.normalize();
    }

    /**
     * Constructor that takes three points
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        _q0 = p1;
        _normal = null;
    }

    /**
     * Gets the normal vector of the plane.
     *
     * @param point
     * @return The normal vector of the plane.
     */

    public Vector getNormal(Point point) {
        return _normal;
    }

}
