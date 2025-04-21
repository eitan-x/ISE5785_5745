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
     * Test for the method {@link}
     */
    @Test
    void add() {
        assertEquals(new Point(-1, 5,3), p3.add(v1),"ERROR");

    }

    @Test
    void subtract() {

    }

    @Test
    void distance() {

    }

    @Test
    void distanceSquared() {

    }

}