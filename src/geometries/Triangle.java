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
    public List<Point> findIntersections(Ray ray){
        // we take three vectors from the same starting point and connect them to the triangle's vertices
        // we get a pyramid

        //Check if the ray intersect the plane.
        if (plane.findIntersections(ray) == null) {
            return null;
        }
        // the three vectors from the same starting point
        Vector v1 = vertices.get(0).subtract(ray.head);
        Vector v2 = vertices.get(1).subtract(ray.head);
        Vector v3 = vertices.get(2).subtract(ray.head);


        //we want to get a normal for each pyramid's face so we do the crossProduct
        Vector n1 = v1.crossProduct(v2).normalize();
        Vector n2 = v2.crossProduct(v3).normalize();
        Vector n3 = v3.crossProduct(v1).normalize();

        // the ray's vector  - it has the same starting point as the three vectors from above
        Vector v = ray.direction;

        // check if the vector's direction (from Subtraction between the ray's vector to each vector from above) are equal
        // if not - there is no intersection point between the ray and the triangle
        if ((alignZero(v.dotProduct(n1)) > 0 && alignZero(v.dotProduct(n2)) > 0 && alignZero(v.dotProduct(n3)) > 0) ||
                (alignZero(v.dotProduct(n1)) < 0 && alignZero(v.dotProduct(n2)) < 0 && alignZero(v.dotProduct(n3)) < 0)){

            return plane.findIntersections(ray);
        }
        return null;



    }
}
