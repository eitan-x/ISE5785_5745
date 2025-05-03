package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**x`
 * Testing Sphere
 *
 * @author Eitan lafair
 */
class SphereTest {

    /**
     * 
     * Test for the method {@link Sphere#getNormal(Point)}
     */
    @Test
    void testGetNormal(){
        Sphere sphere = new Sphere(new Point(0,0,0),3);


        // ============ Equivalence Partitions Tests ==============//
        // TC01: tests for calculation of normal to the plane//
        assertEquals(new Vector(0,0,-1), sphere.getNormal(new Point(0,0,3)));

    }
}