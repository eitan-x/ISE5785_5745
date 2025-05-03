package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;
import geometries.Plane;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing Planes
 *
 * @author eitan lafair
 */
class PlaneTests {

    private static final double DELTA = 1e-6;

    /** Test method for {@link geometries.Plane#Plane(Point, Point, Point)}. */
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

    /** Test method for {@link geometries.Plane#getNormal(Point)}. */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        Point p1 = new Point(1, 0, 0);
        Point p2 = new Point(0, 1, 0);
        Point p3 = new Point(0, 0, 1);
        Plane plane = new Plane(p1, p2, p3);

        // generate expected normal using cross product
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        Vector expectedNormal = v1.crossProduct(v2).normalize();

        // TC01: Check if getNormal returns a unit vector orthogonal to the plane
        Vector result = plane.getNormal(new Point(0, 0, 0));

        assertEquals(1, result.length(), DELTA, "Plane normal is not a unit vector");

        // verify orthogonality
        assertEquals(0, result.dotProduct(v1), DELTA,
                "Plane normal is not orthogonal to vector p2 - p1");
        assertEquals(0, result.dotProduct(v2), DELTA,
                "Plane normal is not orthogonal to vector p3 - p1");

        // check same direction (or opposite)
        assertTrue(result.equals(expectedNormal) || result.equals(expectedNormal.scale(-1)),
                "Plane normal has incorrect direction");
    }
}
