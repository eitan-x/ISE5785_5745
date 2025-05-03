package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Testing Points
 * @author Eitan Lafair
 */
class PointTest {
    Point p1 = new Point(0,1,2);
    Point p2 = new Point(5,0,0);
    Point p3 = new Point(-2,1,-3);

    Vector v1 = new Vector(1,4,6);

    /**
     * Test for the method {@link Point#add(Vector)}
     */
    @Test
    void add() {
        assertEquals(new Point(-1, 5,3), p3.add(v1),"ERROR1");
    }
    

    /**
     * Test for the method {@link Point#subtract(Point)}
     */
    @Test
    void subtract() {
        assertEquals(new Vector(3,3,9),v1.subtract(p3),"ERROR2");

    }


    /**
     * Test for the method {@link Point#distance(Point)}
     */
    @Test
    void distance() {
        assertEquals(Math.sqrt(30),p1.distance(p2),"ERROR3");

    }

    /**
     * Test for the method {@link Point#distanceSquared(Point)}
     */
    @Test
    void distanceSquared() {
        assertEquals(30, p1.distanceSquared(p2),"ERROR4");

    }

}