package geometries;

import primitives.Point;
import primitives.Vector;

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
    protected Vector getNormal(Point point) {
        Vector vector = center.subtract(point).normalize();

        return vector ;
    }
}
