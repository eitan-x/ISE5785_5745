package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Interface representing a generic light source in a 3D scene.
 * Provides methods to get the intensity, direction, and distance of the light from a point.
 *
 * @author Eitan Lafair
 */
public interface LightSource {

    /**
     * Returns the intensity (color) of the light at a specific point in space.
     *
     * @param p the point to calculate the light's intensity at
     * @return the color intensity of the light at the point
     */
    Color getIntensity(Point p);

    /**
     * Returns the normalized direction vector from the light source to the given point.
     *
     * @param p the point to which the light direction is calculated
     * @return the direction vector from the light to the point
     */
    Vector getL(Point p);

    /**
     * Returns the distance from the light source to the given point.
     * Useful for shadow and attenuation calculations.
     *
     * @param point the point to measure distance to
     * @return the distance from the light to the point
     */
    double getDistance(Point point);
}
