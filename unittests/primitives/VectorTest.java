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
     * Test method for {@link Vector#scale(double)}.
     */
    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests ==============
        // TC01: Test scaling a vector by a positive scalar
        assertEquals(new Vector(0, 8, -6), v3.scale(2), "ERROR: scale()function wrong value");

        // TC02: Test scaling a vector by a negative scalar
        assertEquals(new Vector(-1, -2, -3), v1.scale(-1), "ERROR: scale() wrong result with negative scalar");

        //=============== Boundary Values Tests ==================
        // TC10: Test scaling a vector by zero, which should throw an exception
        assertThrows(IllegalArgumentException.class, () -> v1.scale(0), "ERROR: scale(0) should throw exception");

    }


    /**
     * Test method for {@link Vector#dotProduct(Vector)}.
     */
    @Test
    void testDotProduct() {
        //=============== Boundary Values Tests ==================
        // TC01: Test the dot product of two orthogonal vectors (expected to be zero)
        assertEquals(0, v4.dotProduct(v5), "ERROR: dotProduct() for orthogonal vectors is not zero");

        // ============ Equivalence Partitions Tests ==============
        // TC02: Test the dot product of two non-orthogonal vectors
        assertEquals(28, v1.dotProduct(v2), " ERROR: dotProduct() wrong value");
    }

    /**
     * Test for the method {@link Vector#crossProduct(Vector)}
     */
    @Test
    void testCrossProduct() {
        // ============ Equivalence Partitions Tests ==============
        Vector v2 = new Vector(0, 3, -2);
        Vector vr = v1.crossProduct(v2);

        // TC01: Test that the length of the cross-product is the product of the lengths of the vectors
        // (orthogonal vectors taken for simplicity)
        assertEquals(v1.length() * v2.length(), vr.length(), 0.00001, "crossProduct() wrong result length");

        // TC02: Validate that the cross-product result is orthogonal to the first operand
        assertEquals(0, vr.dotProduct(v1), 0.00001, "crossProduct() result is not orthogonal to first operand");
        // TC03: Validate that the cross-product result is orthogonal to the second operand
        assertEquals(0, vr.dotProduct(v2), 0.00001, "crossProduct() result is not orthogonal to second operand");


        // =============== Boundary Values Tests ==================
        // TC04: Test attempting a cross-product of parallel vectors, which should throw an exception
        Vector v3 = new Vector(-2, -4, -6);
        assertThrows(IllegalArgumentException.class, () -> v1.crossProduct(v3),
                "crossProduct() for parallel vectors does not throw an exception");
    }



    /**
     * Test for the method {@link Vector#lengthSquared()}
     */
    @Test
    void testLengthSquared() {
        // TC01: Test the squared length calculation of a vector
        assertEquals(14, v1.lengthSquared(), "ERRO");
    }

    /**
     * test for the method {@link Vector#length()}
     */
    @Test
    void testLength() {
        // TC01: Test the length calculation of a vector
        assertEquals(Math.sqrt(14), v1.length(), "ERRO");
    }

    /**
     * Test for the method {@link Vector#normalize()}
     */
    @Test
    void testNormalize() {
        Vector v = new Vector(0, 3, 4);
        Vector n= v.normalize();

        // ============ Equivalence Partitions Tests ==============

        // TC01: Normalizing a vector and checking the result
        assertEquals(new Vector(0.8, 0, 0.6), v6.normalize(), "ERROR: normalize() gives wrong result for general vector");

        // TC02: Check that normalize() creates a new vector and does not modify the original
        assertFalse(v == n, "ERROR: normalize() should not change the original vector");

        // TC03: Test that the squared length of a normalized vector is equal to 1
        assertEquals(1d, n.lengthSquared(), 0.00001, "ERROR: normalized vector length is not 1");


        // =============== Boundary Value Tests ==================

        // TC04: Trying to normalize the zero vector (should throw an exception)
        assertThrows(IllegalArgumentException.class,
                () -> new Vector(0, 0, 0).normalize(),
                "ERROR: normalize() should throw exception for zero vector");
    }

    /**
     * Test fot the method {@link Vector#add(Vector)}
     */
    @Test
    void testAdd() {
        // TC01: Test adding two general vectors
        assertEquals(new Vector(6, -1, 3), v5.add(v6), "ERROR: add() function wrong value");

        // ============ Equivalence Partitions Tests ==============
        // TC02: Test adding two opposite vectors (should throw an exception)
        Vector v7 = new Vector(-2, 1, 0); // v7 = -v5
        assertThrows(IllegalArgumentException.class, () -> v5.add(v7), "ERROR: add() with negative vector should throw exception");

    }


    /**
     * Teat for the method {@link Vector#subtract(Point)}
     */
    @Test
    void testSubtract(){
        // TC01: Test subtracting a scaled vector from another vector
        Vector v7 = v1.scale(-1);
        assertEquals(new Vector(3, -2, 0), v6.add(v7), "ERROR");

        // ============ Equivalence Partitions Tests ==============
        // TC02: Test subtracting a vector from itself (should throw an exception)
        assertThrows(IllegalArgumentException.class, () -> v1.subtract(v1), "ERROR: subtract() of vector from itself should throw exception");
    }


}