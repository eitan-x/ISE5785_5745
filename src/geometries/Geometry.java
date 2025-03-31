package geometries;

import primitives.Vector;
import primitives.Point;

/**
 * Abstract class representing a geometric shape.
 */
public abstract class Geometry {

    /**
     * Returns the normal vector at a given point on the geometry.
     *
     * @param point the point on the geometric shape
     * @return the normal vector at the given point
     */
    protected abstract Vector getNormal(Point point);


}

