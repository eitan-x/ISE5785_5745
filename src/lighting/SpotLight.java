package lighting;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * SpotLight class represents a light source with a specific position in the scene
 */
public class SpotLight extends PointLight{
    private final Vector direction;
    private double narrowBeam = 1d;

    public SpotLight setNarrowBeam(double narrowBeam) {
        this.narrowBeam = narrowBeam;
        return this;
    }



    /**
     * get intensity of the light at a specific point
     * @param color color of the light
     * @param direction direction of the light
     * @param position position of the light source
     */
    public SpotLight(Color color, Point position, Vector direction) {
        super(color, position);
        this.direction = direction.normalize();
    }

    /**
     * set attenuation factor
     * @param kC attenuation factor
     * @return
     */

    public SpotLight setKc(double kC) {
        super.setKc(kC);
        return this;
    }

    /**
     * set attenuation factor
     * @param kL attenuation factor
     * @return this SpotLight instance
     */

    public PointLight setKl(double kL) {
        super.setKl(kL);
        return this;
    }

    /**
     * set attenuation factor
     * @param kQ attenuation factor
     * @return this SpotLight instance
     */

    public SpotLight setKq(double kQ) {
        super.setKQ(kQ);
        return this;
    }

    /**
     * get direction of the light at a specific point
     * @param p point to which the intensity is calculated
     * @return direction of the light at a specific point
     */
    @Override
    public Color getIntensity(Point p) {
        Color oldColor = super.getIntensity(p);
        return oldColor.scale(Math.max(0d, direction.dotProduct(getL(p))));
    }
}