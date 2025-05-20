package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TubeTest {

    @Test
    void testGetNormal() {
        Tube tube = new Tube(new Ray(new Point(0, 0, 0), new Vector(1, 0, 0)), 1);

        // ============ Equivalence Partitions Tests ==============

        assertEquals(new Vector(0, 0, 1),
                tube.getNormal(new Point(1, 0, 1)),
                "ERROR: The calculation of normal to the tube is not calculated correctly");

        // =============== Boundary Values Tests ==================
        // Test at a boundary point: point lies exactly radius away from axis origin
        assertEquals(new Vector(0, 1, 0),
                tube.getNormal(new Point(0, 1, 0)),
                "ERROR: Normal at boundary point not calculated correctly");
    }

}