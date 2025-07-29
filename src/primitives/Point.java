package primitives;

/**
 * Creating a new class for point representation
 */
public class Point {

    protected final Double3 xyz;

    public static final Point ZERO = new Point(0, 0, 0);


    /**
     * Creating a constructor that takes a Double3 object and creates a new Point object.
     *
     * @param xyz a Double3 object representing the coordinates of the point in 3D space.
     */
    public Point(Double3 xyz) {
        this.xyz = xyz;
    }

    public Point(double x, double y, double z) {
        xyz = new Double3(x, y, z);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Point other)
                && this.xyz.equals(other.xyz);
    }

    @Override
    public String toString() {
        return "Point" + xyz.toString();
    }


    /**
     * /**
     * Add a vector to a point
     *
     * @param vector The vector to add to the point.
     * @return A new Point object.
     */

    public Point add(Vector vector) {
        return new Point(this.xyz.add(vector.xyz));
    }


    /**
     * Subtracts a given point from the current point and returns the resulting vector.
     *
     * @param point The point to subtract.
     * @return A new Vector representing the difference.
     * @throws IllegalArgumentException if the resulting vector is the zero vector.
     */
    public final Vector subtract(Point point) {
        Vector result = new Vector(this.xyz.subtract(point.xyz));
        return result;
    }

    /**
     * @return
     */

    public final double distance(Point point) {
        return Math.sqrt(this.distanceSquared(point));
    }

    /**
     * @return
     */

    public final double distanceSquared(Point point) {
        double dx = this.xyz.d1() - point.xyz.d1();
        double dy = this.xyz.d2() - point.xyz.d2();
        double dz = this.xyz.d3() - point.xyz.d3();
        return dx * dx + dy * dy + dz * dz;
    }




}
