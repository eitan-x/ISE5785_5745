package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the {@link Geometries} class, focusing on the
 * {@link Geometries#findIntersections(Ray)} method.
 * <p>
 * These tests verify the behavior of intersection detection with a collection of geometrical
 * shapes, including planes, spheres, and triangles. The suite includes equivalence partition
 * tests and boundary-value analysis.
 *
 * @author Eitan Lafair
 */
public class GeometriesTest {
    /**
     * Test for the {@link Geometries#findIntersections(Ray)} method.
     *
     * This test method verifies the behavior of finding intersections between a given ray and 
     * a collection of geometrical objects (Plane, Sphere, and Triangle). The following test 
     * cases are included:
     * <ul>
     *     <li><b>Equivalence Partitions:</b>
     *         <ul>
     *             <li>TC01: Ray intersects with more than one object but not all objects.</li>
     *         </ul>
     *     </li>
     *     <li><b>Boundary Values:</b>
     *         <ul>
     *             <li>TC10: An empty list of geometries (no intersections).</li>
     *             <li>TC11: Ray does not intersect with any geometry.</li>
     *             <li>TC12: Ray intersects with exactly one object.</li>
     *             <li>TC13: Ray intersects with all objects.</li>
     *         </ul>
     *     </li>
     * </ul>
     */
    @Test
    void testFindIntersections() {
        Plane plane = new Plane(new Point(1, 0, 0), new Point(2, 0, 0), new Point(1.5, 0, 1));
        Sphere sphere = new Sphere(new Point(1, 0, 1), 1);
        Triangle triangle = new Triangle(new Point(0, 2, 0), new Point(2, 2, 0), new Point(1.5, 2, 2));
        Geometries geometries = new Geometries(plane, sphere, triangle);


        // ============ Equivalence Partitions Tests ==============
        //TC01: More then one object intersect (but not all the objects)
        Ray rayManyObjectIntersect = new Ray(new Point(1, 1.5, 1), new Vector(0, -1, 0));
        assertEquals(3, geometries.findIntersections(rayManyObjectIntersect).size(),
                "More then one object intersect (but not all the objects)");

        // =============== Boundary Values Tests ==================
        //TC10: Empty list
        Geometries geometriesEmptyList = new Geometries();
        Ray rayEmptyList = new Ray(new Point(1, 1, 1), new Vector(0, -1, 0));

        assertNull(geometriesEmptyList.findIntersections(rayEmptyList), "The List empty");

        // TC11: No intersection with the objects
        Ray rayNoIntersections = new Ray(new Point(1, -1, 1), new Vector(0, -1, 0));

        assertNull(geometries.findIntersections(rayNoIntersections), "The ray suppose not intersect the objects");

        //TC12: One object intersect
        Ray rayOneObjectIntersect = new Ray(new Point(1.5, 1.5, 0.5), new Vector(0, 1, 0));
        assertEquals(1, geometries.findIntersections(rayOneObjectIntersect).size(),
                "Suppose to be one intersection point (one object intersect)");

        //TC13: All the objects intersect
        Ray rayAllObjectIntersect = new Ray(new Point(1, 2.5, 1), new Vector(0, -1, 0));
        assertEquals(4, geometries.findIntersections(rayAllObjectIntersect).size(),
                "Suppose to be 4 intersection points");

    }
}
