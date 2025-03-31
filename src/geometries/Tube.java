package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

public class Tube extends RadialGeometry {

    public Ray axis;

    /**
     * Constructor for RadialGeometry.
     *
     * @param radius the radius of the geometry
     */
    public Tube(Ray axis, double radius) {
        super(radius);
        this.axis = axis;
    }

    @Override
    protected Vector getNormal(Point point) {
        return null;
    }
}
