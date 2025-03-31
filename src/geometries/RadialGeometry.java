package geometries;

/**
 * Abstract class representing geometric shapes with a radius.
 */
public abstract class RadialGeometry extends Geometry {

    protected final double radius;
    protected final double radiusSquared;


    /**
     * Constructor for RadialGeometry.
     *
     * @param radius the radius of the geometry
     */
    public RadialGeometry(double radius) {
        this.radius = radius;
        this.radiusSquared = radius * radius;

    }

    ;


}
