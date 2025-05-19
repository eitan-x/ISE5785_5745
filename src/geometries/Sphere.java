package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import primitives.Util.*;
import java.util.List;

import static java.lang.Math.sqrt;
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
    Sphere(Point center, double radius) {
        super(radius);
        this.center = center;
    }

    @Override
    public Vector getNormal(Point point) {
        Vector vector = center.subtract(point).normalize();

        return vector ;
    }

    @Override
    public List<Point> findIntersections(Ray ray){
       Vector u = center.subtract(ray.head);
        double tm = alignZero(ray.direction.dotProduct(u));
        double d = alignZero(sqrt(u.lengthSquared()- tm*tm));

        if (d >= radius )
            return null;


        double th = sqrt(alignZero(radius*radius- d * d));
        double t1= tm+ th;
        double t2= tm- th;

        if(t1 > 0 & t2 >0)
            return List.of(
                    ray.head.add(ray.direction.scale(t1)),
                    ray.head.add(ray.direction.scale(t2))
            );

        if (t1 > 0) {
            Point p1 = ray.head.add(ray.direction.scale(t1));
            return List.of(p1);
        }

        if (t2 > 0) {
            Point p2 = ray.head.add(ray.direction.scale(t2));
            return List.of(p2);
        }
        return null;


    }
}
