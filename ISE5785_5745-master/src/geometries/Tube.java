package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.isZero;

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
    public Vector getNormal(Point point) {
        Vector tubeCenter = axis.direction;
        Point tubePoint = axis.head;

        double projection = tubeCenter.dotProduct(point.subtract(tubePoint));
        if (isZero(projection)) return point.subtract(tubePoint).normalize();
        Point tubeCenterPoint= tubePoint.add(tubeCenter.scale(projection));
        Vector normal = point.subtract(tubeCenterPoint).normalize();

        return normal;
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        return List.of();
    }
}
