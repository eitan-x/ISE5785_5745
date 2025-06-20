package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static primitives.Util.alignZero;

/**
 * SpotLight represents a light source that has direction and a beam angle.
 */
public class SpotLight extends PointLight {

    private final Vector direction;
    private double narrowBeam = 1.0; // Beam concentration factor

    /**
     * Constructs a spot light with intensity, position and direction.
     * @param intensity the color intensity of the light
     * @param position the position of the light source
     * @param direction the direction of the spotlight
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }

    public SpotLight setNarrowBeam(double narrowBeam) {
        this.narrowBeam = narrowBeam;
        return this;
    }

    @Override
    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    @Override
    public SpotLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    public SpotLight setKq(double kQ) {
        super.setKQ(kQ);
        return this;
    }

    /**
     * Returns the attenuated and direction-weighted intensity at point p
     */
    @Override
    public Color getIntensity(Point p) {
        Vector l = getL(p);
        double projection = alignZero(direction.dotProduct(l));

        if (projection <= 0) {
            return Color.BLACK; // Point is outside of the spotlight cone
        }

        // scale by beam narrowing factor
        double factor = Math.pow(projection, narrowBeam);

        return super.getIntensity(p).scale(factor);
    }
}
