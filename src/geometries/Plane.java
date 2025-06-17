
package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.List;

/**
 * Abstract class which represents an infinite plane in the 3D space
 * Represented by the normal vector to the plane and a point
 * the vector will always be normalized
 */
public class Plane extends Geometry {
    private final Point q;
    private final Vector normal;

    /**
     * constructor which defined a plane with 3 points
     * @param a
     * @param b
     * @param c
     */
    public Plane(Point a, Point b, Point c) {
        q = a;
        normal = (a.subtract(b).crossProduct(a.subtract(c))).normalize(); //normalize((a-b)x(a-c)) = normalize(ba x ca)
    }
    /**
     * constructor which defined a plane with a normal vector and a point
     * @param q
     * @param normal
     * does not have to be normalized
     * */
    public Plane(Point q, Vector normal) {
        this.q = q;
        this.normal = normal.normalize();
    }
    /**
     returns the normal vector to the plane.
     of course, normalize
     The same in every point
     @return normal
      * */
    public Vector getNormal() {
        return normal;
    }
    @Override
    public Vector getNormal(Point point) {
        return normal;
    }
    @Override
    public List<Point> findIntersections(Ray ray) {
        double denominator = Util.alignZero(normal.dotProduct(ray.direction));
        if (Util.isZero(denominator)) // the ray is parallel to the plane
            return null;

        double t;

        try {
            t = Util.alignZero(normal.dotProduct(q.subtract(ray.head)) / denominator);
        }
        catch (IllegalArgumentException e) { //q-head == 0 => head == q => ray starts on plane
            return null; }

        if (t > 0)
            return List.of(ray.getPoint(t));
        return null;

    }
    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        List<Point> list = findIntersections(ray);
        if (list == null || list.isEmpty())
            return null;
        return List.of(new Intersection(this, list.getFirst() , this.getMaterial()));
    }

}