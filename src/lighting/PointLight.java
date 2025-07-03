package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * A point light that emits light from a specific location in all directions.
 */
public class PointLight extends Light implements LightSource {

    /** The position of the light in space */
    protected final Point position;

    /** Constant attenuation factor */
    private double kC = 1d;

    /** Linear attenuation factor */
    private double kL = 0d;

    /** Quadratic attenuation factor */
    private double kQ = 0d;

    /**
     * Creates a new point light with a color and position.
     *
     * @param intensity the color/intensity of the light
     * @param position the location of the light in 3D space
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    /**
     * Sets the constant attenuation value.
     *
     * @param kC constant factor
     * @return this light for chaining
     */
    public PointLight setKc(double kC) {
        this.kC = kC;
        return this;
    }

    /**
     * Sets the linear attenuation value.
     *
     * @param kL linear factor
     * @return this light for chaining
     */
    public PointLight setKl(double kL) {
        this.kL = kL;
        return this;
    }

    /**
     * Sets the quadratic attenuation value.
     *
     * @param kQ quadratic factor
     * @return this light for chaining
     */
    public PointLight setKQ(double kQ) {
        this.kQ = kQ;
        return this;
    }

    /**
     * Calculates how strong the light is at a certain point.
     *
     * @param p the point to calculate intensity at
     * @return the color (intensity) at point p
     */
    @Override
    public Color getIntensity(Point p) {
        double distanceSquared = p.distanceSquared(position);
        double distance = Math.sqrt(distanceSquared);
        double attenuation = kC + kL * distance + kQ * distanceSquared;
        return intensity.scale(1.0 / attenuation); // instead of reduce()
    }

    /**
     * Gets the direction vector from the light to a given point.
     *
     * @param p the point to calculate direction to
     * @return the direction vector (normalized)
     */
    @Override
    public Vector getL(Point p) {
        return p.subtract(position).normalize();
    }

    /** Beam narrowing factor for spotlights */
    private double narrowBeam = 1d;

    /**
     * Sets how narrow the spotlight beam should be.
     *
     * @param narrowBeam the beam narrowing value
     * @return the spotlight with updated beam value
     */
    public SpotLight setNarrowBeam(double narrowBeam) {
        this.narrowBeam = narrowBeam;
        return (SpotLight) this;
    }
}

