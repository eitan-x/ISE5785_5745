package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * Create a class for representation a Sphere
 *
 * @author Eitan lafair
 */
public class Sphere extends RadialGeometry {


    public Point center;

    /**
     * Constructs a Sphere with a specified center point and radius.
     *
     * @param center The center point of the sphere.
     * @param radius The radius of the sphere.
     */
    public Sphere(Point center, double radius) {
        super(radius);
        this.center = center;
    }

    @Override
    public Vector getNormal(Point point) {
        Vector vector = center.subtract(point).normalize();

        return vector ;
    }


    @Override
    public List<Point> findIntersections(Ray ray) {

        // The starting point of the ray
        Point p0 = ray.head;

        // Sphere's center point
        Point O = center;

        // Direction vector of the ray
        Vector V = ray.direction;

        // Check if the ray starts at the sphere's center
        if (O.equals(p0)) {

            // Calculate the intersection point by scaling the direction vector to the sphere's radius
            Point newPoint = p0.add(ray.direction.scale(radius));
            return List.of(newPoint);
        }

        // Vector from the ray's starting point to the sphere's center
        Vector U = O.subtract(p0);

        // Projection of U on the direction vector (ray's parameter value at closest approach)
        double tm = V.dotProduct(U);

        // Distance from the sphere's center to the closest point on the ray
        double d = alignZero(Math.sqrt(U.lengthSquared() - tm * tm));

        // If the distance is greater than or equal to the sphere's radius, there's no intersection
        if (d >= radius) {
            return null;
        }

        // Calculate the half-chord distance along the ray within the sphere
        double th = alignZero(Math.sqrt(radius * radius - d * d));

        // Calculate the two intersection points' parameters
        double t1 = tm - th;
        double t2 = tm + th;

        // If both intersection points are in front of the ray's start, return both points
        if (t1 > 0 && t2 > 0) {

            return List.of(ray.getPoint(t1), ray.getPoint(t2));
        }

        // If only the first intersection point is valid, return that point
        if (t1 > 0) {

            return List.of(ray.getPoint(t1));
        }

        // If only the second intersection point is valid, return that point
        if (t2 > 0) {

            return List.of(ray.getPoint(t2));
        }

        // If neither intersection point is valid, return null
        return null;

    }
}
