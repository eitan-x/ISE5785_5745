package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class PointLight extends Light implements LightSource {


    protected final Point position;
    private double kC = 1d;
    private double kL = 0d;
    private double kQ = 0d;


    /**
     * Constructs a light with the given intensity.
     *
     * @param intensity the color intensity of the light
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    public PointLight setKc(double kC) {
        this.kC = kC;
        return this;
    }

    public PointLight setKl(double kL) {
        this.kL = kL;
        return this;
    }

    public PointLight setKQ(double kQ) {
        this.kQ = kQ;
        return this;
    }



    @Override
    public Color getIntensity(Point p) {
        double distanceSquared = p.distanceSquared(position);
        double distance = Math.sqrt(distanceSquared);
        double attenuation = kC + kL * distance + kQ * distanceSquared;
        return intensity.scale(1.0 / attenuation); // במקום reduce()
    }


    @Override
    public Vector getL(Point p) {
        return p.subtract(position).normalize();
    }


    private double narrowBeam = 1d;

    public SpotLight setNarrowBeam(double narrowBeam) {
        this.narrowBeam = narrowBeam;
        return (SpotLight) this;
    }
}
