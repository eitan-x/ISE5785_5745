package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static java.lang.Math.sqrt;
import static org.junit.jupiter.api.Assertions.*;

class TriangleTest {
    private static final double DELTA = 0.000000001;

    @Test
    void testGetNormal() {

        // Given: three non-collinear points
        Point p1 = new Point(1, 2, 3);
        Point p2 = new Point(4, 0, 1);
        Point p3 = new Point(0, -1, 2);

        Triangle triangle = new Triangle(p1, p2, p3);

        // When: calling getNormal
        Vector normal = triangle.getNormal(p1);
        Vector expectedNormal = new Vector(4.0/sqrt(162), -5.0/sqrt(162), 11.0/sqrt(162));
        Vector expectedNormal2 = new Vector(-4.0/sqrt(162), 5.0/sqrt(162), -11.0/sqrt(162));


        assertTrue(expectedNormal.equals(normal) || expectedNormal2.equals(normal));


        // Then: check that the normal is unit length
        assertEquals(1, normal.length(), DELTA, "Normal is not a unit vector");

        // And: it's orthogonal to both edges
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p1);
        assertEquals(0, normal.dotProduct(v1), DELTA, "Normal not orthogonal to edge p1->p2");
        assertEquals(0, normal.dotProduct(v2), DELTA, "Normal not orthogonal to edge p1->p3");
    }

    @Test
    void testFindIntersections() {
        Triangle triangle = new Triangle(new Point(0, 1, 0), new Point(0, 5, 0), new Point(0, 3, 5));
        // ============ Equivalence Partitions Tests ==============
        // TC01: The intersection point is in the triangle
        assertEquals(List.of(new Point(0, 3, 1)),
                triangle.findIntersections(new Ray(new Point(1, 3, 0), new Vector(-1, 0, 1))),
                "The point supposed to be in the triangle");

        // TC02: The intersection point is outside the triangle, against edge
        assertNull(triangle.findIntersections(new Ray(new Point(1, 0, 0), new Vector(-1, 0, 1))),
                "The point supposed to be outside the triangle, against edge");

        // TC03: The intersection point is outside the triangle, against vertex
        assertNull(triangle.findIntersections(new Ray(new Point(1, 0, 0), new Vector(-1, 0.1, -0.1))),
                "The point supposed to be outside the triangle, against vertex");

        // =============== Boundary Values Tests ==================
        // TC10: The point is on edge
        assertNull(triangle.findIntersections(new Ray(new Point(1, 3, 0), new Vector(-1, 0, 0))),
                "The point supposed to be on edge");

        // TC11: The point is in vertex
        assertNull(triangle.findIntersections(new Ray(new Point(1, 1, 0), new Vector(-1, 0, 0))),
                "The point supposed to be in vertex");

        // TC12: The point is on edge's continuation
        assertNull(triangle.findIntersections(new Ray(new Point(1, 0, 0), new Vector(-1, 0.1, 0))),
                "The point supposed to be on edge's continuation");
    }








}