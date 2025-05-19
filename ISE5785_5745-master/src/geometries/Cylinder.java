package geometries;

import primitives.Ray;

/**
 * Represents a cylinder in 3D space, extending from the Tube class.
 * This class defines the properties of a cylinder, including its height,
 * axis, and radius.
 *
 * @author Eitan Lafair
 */
public class Cylinder extends Tube {

    /**
     * The height of the cylinder.
     */
    public double height;

    /**
     * Constructor for Cylinder.
     * Initializes the cylinder with the given axis, radius, and height.
     * The axis defines the central line of the cylinder, and the radius
     * defines the radius of the circular base. The height defines the
     * length of the cylinder along the axis.
     *
     * @param height The height of the cylinder.
     * @param axis   The axis along which the cylinder extends (defines the central line of the cylinder).
     * @param radius The radius of the circular base of the cylinder.
     */
    public Cylinder(double height, Ray axis, double radius) {
        super(axis, radius);  // Calls the constructor of the superclass (Tube)
        this.height = height;  // Sets the height of the cylinder
    }
}
