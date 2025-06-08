package lighting;

import primitives.Color;

/**
 * Represents ambient light in a scene, which uniformly illuminates all objects.
 * Ambient light has no direction and affects all objects equally.
 */
public class AmbientLight {

    private final Color intensity;

    /**
     * A constant representing the absence of ambient light (black).
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Constructs an AmbientLight object with a given color intensity.
     *
     * @param intensity The color/intensity of the ambient light.
     */
    public AmbientLight(Color intensity) {
        this.intensity = intensity;
    }

    /**
     * Returns the color intensity of the ambient light.
     *
     * @return The color intensity.
     */
    public Color getIntensity() {
        return intensity;
    }
}
