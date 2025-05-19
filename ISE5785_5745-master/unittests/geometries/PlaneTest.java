package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing Planes
 *
 * @author eitan lafair
 */
class PlaneTests {

    private static final double DELTA = 0.000001;
    private static final double ONE = 1.0;

    /** Test method for {@link Geometries.Plane#Plane(Point, Point, Point)}. */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Correct plane with three non-collinear points
        assertDoesNotThrow(() ->
                        new Plane(new Point(0, 0, 1),
                                new Point(1, 0, 0),
                                new Point(0, 1, 0)),
                "Failed constructing a correct plane");

        // =============== Boundary Values Tests ==================

        // TC02: p1 equals p2
        assertThrows(IllegalArgumentException.class, //
                () -> new Plane(new Point(1, 2, 3),
                        new Point(1, 2, 3),
                        new Point(2, 3, 4)),
                "Constructed a plane with two identical points (p1 = p2)");

        // TC03: p1 equals p3
        assertThrows(IllegalArgumentException.class, //
                () -> new Plane(new Point(1, 2, 3),
                        new Point(2, 3, 4),
                        new Point(1, 2, 3)),
                "Constructed a plane with two identical points (p1 = p3)");

        // TC04: p2 equals p3
        assertThrows(IllegalArgumentException.class, //
                () -> new Plane(new Point(1, 2, 3),
                        new Point(2, 3, 4),
                        new Point(2, 3, 4)),
                "Constructed a plane with two identical points (p2 = p3)");

        // TC05: All points are collinear
        assertThrows(IllegalArgumentException.class, //
                () -> new Plane(new Point(0, 0, 0),
                        new Point(1, 1, 1),
                        new Point(2, 2, 2)),
                "Constructed a plane with collinear points");
    }

    /** Test method for {@link Geometries.Plane#getNormal(Point)}. */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(4, 0, 1);
        Point p3 = new Point(0, -1, 2);
        Plane plane = new Plane(p1, p2, p3);

        // Expected normal vector manually calculated
        double sqrt66 = Math.sqrt(66);
        Vector expectedNormal = new Vector(-4 / sqrt66, -1 / sqrt66, -7 / sqrt66);

        // Actual result from the getNormal method
        Vector result = plane.getNormal(p1);

        // TC01: Check if getNormal returns a unit vector orthogonal to the plane
        assertEquals(1, result.length(), DELTA, "Plane normal is not a unit vector");

        // Compute the original vectors for orthogonality check
        Vector v1 = p2.subtract(p1);  // (3, -2, -2)
        Vector v2 = p3.subtract(p1);  // (-1, -3, -1)

        // Check orthogonality of the result with both original vectors
        assertEquals(0, result.dotProduct(v1), DELTA,
                "Plane normal is not orthogonal to vector p2 - p1");
        assertEquals(0, result.dotProduct(v2), DELTA,
                "Plane normal is not orthogonal to vector p3 - p1");


    }

    @Test
    void testFindIntersections() {
        Plane plane = new Plane(new Point(1, 0, 1), new Point(0, 1, 1), new Point(1, 1, 1));

        // ================ EP: The Ray must be neither orthogonal nor parallel to the plane ==================
        //TC01: Ray intersects the plane
        assertEquals(List.of(new Point(1, 0.5, 1)),
                plane.findIntersections(new Ray(new Point(0, 0.5, 0), new Vector(1, 0, 1))),
                "Ray intersects the plane");

        //TC02: Ray does not intersect the plane
        assertNull(plane.findIntersections(new Ray(new Point(1, 0.5, 2), new Vector(1, 2, 5))),
                "Ray does not intersect the plane");


        // ====================== Boundary Values Tests =======================//
        // **** Group: Ray is parallel to the plane
        //TC10: The ray included in the plane
        assertNull(plane.findIntersections(new Ray(new Point(1, 2, 1), new Vector(1, 0, 0))),
                "Ray is parallel to the plane, the ray included in the plane");

        //TC11: The ray not included in the plane
        assertNull(plane.findIntersections(new Ray(new Point(1, 2, 2), new Vector(1, 0, 0))),
                "Ray is parallel to the plane, the ray not included in the plane");

        // **** Group: Ray is orthogonal to the plane
        //TC12: according to 𝑃0, before the plane
        assertEquals(List.of(new Point(1, 1, 1)),
                plane.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, 0, 1))),
                "Ray is orthogonal to the plane, according to p0, before the plane");

        //TC13: according to 𝑃0, in the plane
        assertNull(plane.findIntersections(new Ray(new Point(1, 2, 1), new Vector(0, 0, 1))),
                "Ray is orthogonal to the plane, according to p0, in the plane");

        //TC14: according to 𝑃0, after the plane
        assertNull(plane.findIntersections(new Ray(new Point(1, 2, 2), new Vector(0, 0, 1))),
                "Ray is orthogonal to the plane, according to p0, after the plane");

        // **** Group: Ray is neither orthogonal nor parallel to
        //TC15: Ray begins at the plane
        assertNull(plane.findIntersections(new Ray(new Point(2, 4, 1), new Vector(2, 3, 5))),
                "Ray is neither orthogonal nor parallel to ray and begin at the plane");

        //TC16: Ray begins in the same point which appears as reference point in the plane
        assertNull(plane.findIntersections(new Ray(new Point(1, 0, 1), new Vector(2, 3, 5))),
                "Ray is neither orthogonal nor parallel to ray and begins in the same point " +
                        "which appears as reference point in the plane");


    }
}
