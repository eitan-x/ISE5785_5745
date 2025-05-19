package geometries;


import primitives.Point;
import primitives.Ray;

import java.util.List;

/**
 * Represents an interface for geometric objects that can be intersected by a ray.
 * This interface defines a method to find intersection points between a ray and the geometric object.
 *
 * @author Eitan Lafair
 */
public interface Intersectable {

    /**
     * Finds intersection points between the given ray and the geometric object.
     *
     * @param ray The ray for which to find intersections with the geometric object.
     * @return A list of intersection points if there are any, or {@code null} if no intersections are found.
     */
    List<Point> findIntersections(Ray ray);
}
