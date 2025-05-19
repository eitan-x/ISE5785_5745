package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static primitives.Util.alignZero;

/**
 * Testing Sphere
 *
 * @author Eitan lafair
 */
class SphereTest {

    /**
     * Test for the method {@link Sphere#getNormal(Point)}
     */
    @Test
    void testGetNormal() {
        Sphere sphere = new Sphere(new Point(1, 0, 0), 3);


        // ============ Equivalence Partitions Tests ==============//
        // TC01: tests for calculation of normal to the plane//
        assertEquals(new Vector(0, 0, -1), sphere.getNormal(new Point(1, 0, 3)), "The calculetion in getnormal() failed");

    }


    @Test
    void testFindIntersections() {
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1);

        // TC01: Ray's line is outside the sphere (0 points)
        Ray outsideRay = new Ray(new Point(-2, 2, 0), new Vector(1, 1, 0));
        assertNull(sphere.findIntersections(outsideRay), "Ray's line is outside the sphere");

        // TC02: Ray starts before and crosses the sphere (2 points)
        Ray intersectingRay = new Ray(new Point(-2, 0, 0), new Vector(3, 1, 0));
        List<Point> result1 = sphere.findIntersections(intersectingRay);

        List<Point> expectedPoints1 = List.of(
                new Point(-0.934846922834953, 0.355051025721682, 0),
                new Point(0.534846922834953, 0.844948974278318, 0)
        );

        assertNotNull(result1, "TC02: Expected 2 intersection points but got null");
        assertEquals(2, result1.size(), "TC02: Wrong number of intersection points");
        assertTrue(result1.containsAll(expectedPoints1) && expectedPoints1.containsAll(result1),
                "TC02: Wrong intersection points");

        // TC03: Ray starts inside the sphere (1 point)
        Ray insideRay = new Ray(new Point(0, 0, 0.01), new Vector(1, 0, 0));
        List<Point> result2 = sphere.findIntersections(insideRay);

        Point expected1 = insideRay.head.add(insideRay.direction.scale(
                Math.sqrt(1 - insideRay.head.subtract(sphere.center).lengthSquared())
        ));
        List<Point> expectedPoints2 = List.of(expected1);

        assertNotNull(result2, "TC03: Expected intersection but got null");
        assertEquals(1, result2.size(), "TC03: Expected exactly one intersection point");
        assertEquals(expectedPoints2, result2, "TC03: Incorrect intersection point");

        // TC04: Ray starts after the sphere (0 points)
        Ray afterRay = new Ray(new Point(2, 0, 0), new Vector(1, 1, 0));
        assertNull(sphere.findIntersections(afterRay), "Ray's line is outside the sphere");

        // ============================ Boundary Values Tests ========================//

        // **** Group 1: Ray's line crosses the sphere (but not the center)
        // TC11: Ray starts at sphere and goes inside (1 point)
         Ray ray1 =new Ray  (new Point(1, 0, 0), new Vector(-1, 0, 0));
        Point expected2 = new Point(-1, 0, 0);
        assertEquals(List.of(expected2) ,
                sphere.findIntersections(ray1),
                "Ray starts at sphere and goes inside"); ;



        // TC12: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(2, 0, 0), new Vector(3,1,0))),
                "Ray starts at sphere and goes inside");

        // **** Group 2: Ray's line goes through the center
        // TC21: Ray starts before the sphere (2 points)
        Ray ray2 =new Ray  (new Point(-1, 0, 0), new Vector(1, 0, 0));
        Point expected3 = new Point(1, 0, 0);
        assertEquals(List.of(expected3),
              sphere.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(1, 0, 0))),
               "Ray starts before the sphere and need to be 2 intersections");

        // TC22: Ray starts at sphere and goes inside (1 point)

        Ray ray3 = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        Point expected4 = new Point(1, 0, 0);
        List<Point> result3 = sphere.findIntersections(ray3);
        //System.out.println("result3: " + result3);

        assertNotNull(result3, "TC22: Expected intersection but got null");
        assertEquals(1, result3.size(), "TC22: Expected exactly one intersection point");
        assertEquals(List.of(expected4), result3, "TC22: Incorrect intersection point");

        // TC23: Ray starts inside (1 point)
        assertEquals(List.of(new Point(1, 0, 0)),
                sphere.findIntersections(new Ray(new Point(0.5, 0, 0), new Vector(1, 0, 0))),
               "Ray starts inside ");

        // TC24: Ray starts at the center (1 point)
        Ray ray4 = new Ray (new Point(0.3, 0.4, 0.1), new Vector(1, 1, 1));
        assertEquals(List.of(new Point(0.5970511508429254,0.6970511508429253,0.3970511508429254)),
               sphere.findIntersections(ray4),
                "Ray starts at the center ");

        // TC25: Ray starts at sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(2, 0, 0), new Vector(1, 0, 0))),
                "Ray starts at sphere and goes outside");

        // TC26: Ray starts after sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(new Point(3, 0, 0), new Vector(1, 0, 0))),
                "Ray starts after sphere");

        // **** Group 3: Ray's line is tangent to the sphere (all tests 0 points)
        // TC31: Ray starts before the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(2, -1, -1), new Vector(0, 1, 1))),
                "Ray starts before the tangent point");
        // TC32: Ray starts at the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(2, 0, 0), new Vector(0, 1, 1))),
                "Ray starts at the tangent point");
        // TC33: Ray starts after the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(2, 2, 2), new Vector(0, 1, 1))),
                "Ray starts after the tangent point");

        // **** Group 4: Special cases
        // TC41: Ray's line is outside, ray is orthogonal to ray start to sphere's center line
        assertNull(sphere.findIntersections(new Ray(new Point(3, 0, 0), new Vector(0, 0, 1))),
                "Ray starts after the tangent point");


        // TC42: Ray starts inside, ray is orthogonal to ray-start-to-sphere-center line (1 point)
        Ray ray5 = new Ray(new Point(0, 0.5, 0), new Vector(1, 0, 0));
        List<Point> result5 = sphere.findIntersections(ray5);

        assertNotNull(result5, "TC42: Expected intersection but got null");
        assertEquals(1, result5.size(), "TC42: Expected exactly one intersection point");

        Point expected5 = new Point(0.8660254037844386,0.5,0.0);

        assertEquals(expected5, result5.get(0), "TC42: Wrong intersection point");


    }

}











