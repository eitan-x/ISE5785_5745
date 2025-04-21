package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing Vectors
 * @author Eitan Lafair
 */
class VectorTest {
    //vectors for tests
    Vector v1 = new Vector(1,2,3);
    Vector v2 = new Vector(-2,9,4);
    Vector v3 = new Vector(0,4,-3);
    Vector v4 = new Vector(1, 2, -1);
    Vector v5 = new Vector(2, -1, 0);
    Vector v6 = new Vector(4,0,3);

    /**
     * Test method for {@link primitives.Vector#scale(double)}.
     */
    @Test
    void testScale() {
        assertEquals(new Vector(0,8,-6) ,v3.scale(2), "ERROR: scale()function wrong value");

    }


    /**
     * Test method for {@link primitives.Vector#dotProduct(Vector)}.
     */
    @Test
    void testDotProduct() {
        //=============== Boundary Values Tests ==================
        assertEquals(0 ,v4.dotProduct(v5) , "ERROR: dotProduct() for orthogonal vectors is not zero");

        //// ============ Equivalence Partitions Tests ==============
        assertEquals(28 , v1.dotProduct(v2), " ERROR: dotProduct() wrong value");
    }

    /**
     * Test for the method {@link Vector#crossProduct(Vector)}
     */
    @Test
    void testCrossProduct() {
        // ============ Equivalence Partitions Tests ==============
        Vector v2 = new Vector(0, 3, -2);
        Vector vr = v1.crossProduct(v2);

        // TC01: Test that length of cross-product is proper (orthogonal vectors taken
        // for simplicity)
        assertEquals(v1.length() * v2.length(), vr.length(), 0.00001, "crossProduct() wrong result length");


        // =============== Boundary Values Tests ==================
        // TC02: test zero vector from cross-productof co-lined vectors
        Vector v3 = new Vector(-2, -4, -6);
        assertThrows(IllegalArgumentException.class, () -> v1.crossProduct(v3),
                "crossProduct() for parallel vectors does not throw an exception");
    }



    /**
     * Test for the method {@link Vector#lengthSquared()}
     */
    @Test
    void testLengthSquared() {
        assertEquals(14,v1.lengthSquared(),"ERRO");
    }

    /**
     * test for the method {@link Vector#length()}
     */
    @Test
    void testLength() {
        assertEquals(Math.sqrt(14) ,v1.length(),"ERRO");
    }

    /**
     * Test for the method {@link Vector#normalize()}
     */
    @Test
    void testNormalize() {
        Vector v = new Vector(0, 3, 4);
        Vector n= v.normalize();

        // ============ Equivalence Partitions Tests ==============

        // TC01: Normalizing a  vector
        assertEquals(new Vector(0.8, 0, 0.6), v6.normalize(), "ERROR: normalize() gives wrong result for general vector");

        // TC02: Check that normalize() returns a new vector and doesn't modify the original
        assertFalse(v == n, "ERROR: normalize() should not change the original vector");

        // TC03: After normalization, the length squared should be 1
        assertEquals(1d, n.lengthSquared(), 0.00001, "ERROR: normalized vector length is not 1");


        // =============== Boundary Value Tests ==================

        // TC04: Trying to normalize the zero vector should throw an exception
        assertThrows(IllegalArgumentException.class,
                () -> new Vector(0, 0, 0).normalize(),
                "ERROR: normalize() should throw exception for zero vector");
    }

    /**
     * Test fot the method {@link Vector#add(Vector)}
     */
    @Test
    void testAdd() {
        assertEquals(new Vector(6,-1,3), v5.add(v6) ,"ERROR: add() function wrong value");

    }


    /**
     * Teat for the method {@link Vector#subtract(Point)}
     */
    @Test
    void testSubtract(){
        Vector v7 = v1.scale(-1);
        assertEquals(new Vector(3,-2,0) , v6.add(v7),"ERROR");
    }


}