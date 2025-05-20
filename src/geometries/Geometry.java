package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * Abstract class representing a geometric shape.
 */
public abstract class Geometry implements Intersectable {

    /**
     * Returns the normal vector at a given point on the geometry.
     *
     * @param point the point on the geometric shape
     * @return the normal vector at the given point
     */
    public abstract Vector getNormal(Point point);


}

