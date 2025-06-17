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
    protected List<Intersection> calculateIntersectionsHelper(Ray ray)  {
        if (ray.head.equals(center)) {
            // If the ray's head is at the center of the sphere, return one intersection point
            return List.of(new Intersection(this, ray.getPoint(radius) , this.getMaterial()));
        }
        // Create a vector from the ray's head to the sphere's center
        Vector u = center.subtract(ray.head);
        // Calculate the projection of u onto the ray's direction
        double tm = ray.direction.dotProduct(u);
        // Calculate the distance from the sphere's center to the projection
        double d = Math.sqrt(u.lengthSquared() - tm * tm);

        // If d is greater than the sphere's radius, there are no intersections
        if (d >= radius) return null;

        // Calculate th and t0, t1
        double th = Math.sqrt(radius * radius - d * d);

        double t0 = alignZero(tm - th);
        double t1 = alignZero(tm + th);

        // If t0 and t1 are both positive, return both intersection points
        if (t0 > 0 && t1 > 0) {
            return List.of(
                    new Intersection(this, ray.getPoint(t0),this.getMaterial()),
                    new Intersection(this, ray.getPoint(t1),this.getMaterial()));
        }

        // If only one of them is positive, return that intersection point
        if (t0 > 0) {
            return List.of(new Intersection(this, ray.getPoint(t0),this.getMaterial()));
        }

        if (t1 > 0) {
            return List.of(new Intersection(this, ray.getPoint(t1),this.getMaterial()));
        }

        // If both are negative, return null
        return null;
    }
}
