package lighting;

import primitives.Color;

/**
 * Ambient light source — uniform light with no direction.
 *
 * @author Eitan
 */
public class AmbientLight extends Light {

    /** Constant representing no ambient light (black) */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

    /**
     * Creates ambient light with given color intensity.
     * @param ia ambient intensity
     */
    public AmbientLight(Color ia) {
        super(ia);
    }

    /**
     * Returns ambient light intensity.
     * @return color intensity
     */

    public Color getIntensity() {
        return intensity;
    }
}
