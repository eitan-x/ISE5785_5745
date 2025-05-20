package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * A class for representing a Plane
 *
 * @author Eitan Lafair
 */
public class Plane implements Intersectable{
    final Point _q0;
    final Vector _normal;

    /**
     * Constructor that takes a point and a normal vector.
     * The normal vector is normalized.
     *
     * @param q0 A point on the plane.
     * @param vector A vector normal to the plane (will be normalized).
     */
    public Plane(Point q0, Vector vector) {
        _q0 = q0;
        _normal = vector.normalize();
    }

    /**
     * Constructor that takes three points and creates from them a Plane.
     *
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     */
    public Plane(Point p1, Point p2, Point p3) {
        // Check if any of the points are the same
        if (p1.equals(p2) || p1.equals(p3) || p2.equals(p3)) {
            throw new IllegalArgumentException("Points must be distinct");
        }

        // Calculate the vectors between the points
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);

        // Check if the points are collinear (do not form a plane)
        if (v1.crossProduct(v2).length() == 0) {
            throw new IllegalArgumentException("Points must not be collinear");
        }

        // Create the plane with the points
        _q0 = p1;
        _normal = v1.crossProduct(v2).normalize(); // Calculate the normal vector
    }

    /**
     * Returns the normal vector of the plane.
     *
     * @param point A point on the plane.
     * @return The normalized normal vector of the plane.
     */
    public Vector getNormal(Point point) {
        return _normal;
    }


    @Override
    public List<Point> findIntersections(Ray ray){
        Point rayStart = ray.head; // The starting point of the ray.
        Vector rayDirection = ray.direction; // The normalized direction of the ray.

        if (_q0.equals(rayStart)) { // If the ray originates from the plane, it does not intersect the plane.
            return null; // Return null as no intersection exists.
        }

        Vector planeNormal = _normal; // The plane's normal vector.

        double denominator = planeNormal.dotProduct(rayDirection); // Denominator of parameter 't' in the intersection formula.

        // Check if the ray is parallel to the plane (denominator is zero).
        if (isZero(denominator)) { // A zero denominator implies no intersection or the ray lies within the plane.
            return null; // Return null as no intersection occurs.
        }

        Vector difference = _q0.subtract(rayStart); // Vector from ray start point to reference point on the plane.
        double numerator = alignZero(planeNormal.dotProduct(difference)); // Compute numerator for parameter 't'.

        // t should be bigger than 0
        // If the numerator is zero, the ray starts within the plane.
        if (isZero(numerator)) {
            return null;
        }

        double t = alignZero(numerator / denominator); // Compute the value of parameter 't'.

        // Validate that 't' is positive, meaning the intersection occurs in front of the ray's starting point.
        if (t <= 0) {
            return null; // Return null as the intersection occurs behind the ray's start point.
        }

        return List.of(ray.getPoint(t)); // Return the intersection point as a single-element list.
    }


}
