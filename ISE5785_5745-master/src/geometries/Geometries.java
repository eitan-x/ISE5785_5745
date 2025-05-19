package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A class representing a collection of geometries shapes.
 *
 * @author Eitan Lafair
 */
public class Geometries implements Intersectable {

    private final List<Intersectable> intersectables = new LinkedList<>();

    /**
     * Default constructor for the Geometries class.
     *
     */
    public Geometries() {
    }

    /**
     * Constructor for the Geometries class with initial geometries.
     *
     *
     * @param geometries One or more geometries to initialize the collection.
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds one or more geometries to the collection.
     *
     * @param geometries An array of geometries to be added.
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(intersectables, geometries);
    }

    /**
     * Finds all intersection points between a given ray and the geometries in the collection.
     *
     * @param ray The ray to intersect with the geometries.
     * @return A list of intersection points between the ray and the geometries.
     *
     */
    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> intersections = null;

        for (Intersectable geometry : intersectables) {
            List<Point> tempList = geometry.findIntersections(ray);
            if (tempList != null) {
                if (intersections == null) {
                    intersections = new LinkedList<>();
                }
                intersections.addAll(tempList);
            }
        }

        return intersections;
    }
}
