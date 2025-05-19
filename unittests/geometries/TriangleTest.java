package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

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






    }





}