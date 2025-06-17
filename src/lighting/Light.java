package lighting;

import primitives.Color;

/**
 * Abstract base class for all light sources.
 * Holds the light intensity color.
 *
 * @author Eitan
 */
public abstract class Light {

    /** The light intensity color */
    protected final Color intensity;

    /**
     * Constructs a light with the given intensity.
     *
     * @param intensity the color intensity of the light
     */
    protected Light(Color intensity) {
        this.intensity = intensity;
    }

    /**
     * Returns the intensity color of the light.
     *
     * @return the light intensity
     */
    public Color getIntensity() {
        return intensity;
    }
}
