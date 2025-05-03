package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Create a class for representation a tube
 *
 * @author Eitan lafair
 */
public class Tube extends RadialGeometry {

    public Ray axis;

    /**
     * Constructs a Tube with a given axis and radius.
     *
     * @param axis The central axis of the tube, represented as a Ray.
     * @param radius The radius of the tube.
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
