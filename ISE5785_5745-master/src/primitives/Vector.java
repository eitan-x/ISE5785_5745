package primitives;

/**
 * Create a class for representation Vector
 */

public class Vector extends Point {
    /**
     * Creating a constructor for the class Vector.
     *
     * @param xyz
     */

    public Vector(Double3 xyz) {
        super(xyz);

        //Check if the coordinates create ZERO vector.
        if (xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("ZERO vector not allowed");
        }
    }

    /**
     * Creating a constructor for the class Vector.
     */

    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("ZERO vector is not allowed");
        }
    }

    /**
     * Add the vector to this vector and return the result
     *
     * @param vector The vector to add to this vector.
     * @return A new Vector object.
     */

    public Vector add(Vector vector) {

        return new Vector(xyz.add(vector.xyz));
    }


    /**
     * Multiple vector with a scaler
     *
     * @param scalar The double that gets multiple
     * @return New vector
     */
    public Vector scale(double scalar) {
        return new Vector(xyz.scale(scalar));
    }

    /**
     * Computes the dot product of this vector and another vector.
     *
     * @param vector The vector to compute the dot product with.
     * @return The scalar dot product of the two vectors.
     */
    public double dotProduct(Vector vector) {

        //A ⋅ B = (x1 * x2) + (y1 * y2) + (z1 * z2)
        return this.xyz.d1() * vector.xyz.d1() +
                this.xyz.d2() * vector.xyz.d2() +
                this.xyz.d3() * vector.xyz.d3();
    }

    /**
     * Computes the cross product of this vector and another vector.
     *
     * @param vector The vector to compute the cross product with.
     * @return A new Vector representing the cross product of this vector and the given vector.
     */

    public Vector crossProduct(Vector vector) {

        //A × B = (y1*z2 - z1*y2, z1*x2 - x1*z2, x1*y2 - y1*x2)
        double x1 = this.xyz.d1(), y1 = this.xyz.d2(), z1 = this.xyz.d3();
        double x2 = vector.xyz.d1(), y2 = vector.xyz.d2(), z2 = vector.xyz.d3();

        double x = y1 * z2 - z1 * y2;
        double y = z1 * x2 - x1 * z2;
        double z = x1 * y2 - y1 * x2;

        return new Vector(x, y, z);

    }


    /**
     * Returns the squared length of the vector
     *
     * @return The squared length of the vector.
     */

    public double lengthSquared() {
        //|v|² = v ⋅ v = x² + y² + z²
        return (this.xyz.d1() * this.xyz.d1() +
                this.xyz.d2() * this.xyz.d2() +
                this.xyz.d3() * this.xyz.d3());
    }

    /**
     * Returns the  length of the vector
     *
     * @return The length of the vector
     */

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Creating a new vector that is the result of the normalization of the current vector.
     *
     * @return A normalized vector
     */

    public Vector normalize() {
        return new Vector(xyz.reduce(length()));
    }

    @Override
    public String toString() {
        return "Vector" + xyz;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Vector other)
                && this.xyz.equals(other.xyz);
    }

}
