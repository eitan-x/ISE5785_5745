package geometries;

import primitives.Point;
import primitives.Vector;

public class Sphere extends RadialGeometry {


    public Point center;

    /**
     * Constructor for RadialGeometry.
     *
     * @param radius the radius of the geometry
     */
    Sphere(Point center, double radius) {
        super(radius);
        this.center = center;
    }

    @Override
    protected Vector getNormal(Point point) {
        return null;
    }
}
