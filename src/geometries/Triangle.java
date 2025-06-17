package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * Create a class for representation a Triangle
 *
 * @author Eitan Lafair
 */
public class Triangle extends Polygon {
    public Triangle(Point p1, Point p2, Point p3) {
        super(p1, p2, p3);
    }

    @Override
    protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
        var intersections = plane.findIntersections(ray);
        // Check if the ray intersects the plane of the triangle
        if (intersections == null)
            return null;

        // Retrieve the vertices of the triangle
        Point p0 = vertices.getFirst();
        Point p1 = vertices.get(1);
        Point p2 = vertices.getLast();

        // Retrieve the direction vector and head point of the ray
        Vector rayDirection = ray.direction;
        Point rayPoint = ray.head;

        if (p0.equals(rayPoint) || p1.equals(rayPoint) || p2.equals(rayPoint))
            return null; // The ray's head is one of the triangle's vertices

        // Calculate vectors representing edges of the triangle
        Vector v1 = p0.subtract(rayPoint);
        Vector v2 = p1.subtract(rayPoint);
        // Calculate normal vectors to the triangle's edges
        Vector n1 = v1.crossProduct(v2).normalize();
        // Calculate dot products between the normal vectors and the ray direction
        double d1 = alignZero(n1.dotProduct(rayDirection));
        // Check if the ray does not intersect the triangle.
        if (d1 == 0)
            return null;

        Vector v3 = p2.subtract(rayPoint);
        Vector n2 = v2.crossProduct(v3).normalize();
        double d2 = alignZero(n2.dotProduct(rayDirection));
        // Check if the ray does not intersect the triangle
        if (d1 * d2 <= 0)
            return null;

        Vector n3 = v3.crossProduct(v1).normalize();
        double d3 = alignZero(n3.dotProduct(rayDirection));
        // Check if the ray does not intersect the triangle
        if (d1 * d3 <= 0)
            return null;

        return List.of(new Intersection(this, intersections.getFirst(), this.getMaterial()));
    }

}
