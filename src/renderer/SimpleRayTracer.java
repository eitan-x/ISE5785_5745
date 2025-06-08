package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

import java.util.List;

/**
 * A simple ray tracer that finds the color at the closest point hit by a ray.
 * @author  Eitan Lafair
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Creates the ray tracer with a given scene.
     * @param scene the 3D scene to trace rays in
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Traces a ray and returns the color at the closest hit point.
     * If the ray doesn't hit anything, returns the background color.
     * @param ray the ray to trace
     * @return the color at the closest point or background color
     */
    @Override
    public Color traceRay(Ray ray) {
        // Look for intersections with objects in the scene
        List<Point> intersections = scene.geometries.findIntersections(ray);

        // If no intersections, return the background color
        if (intersections == null || intersections.isEmpty()) {
            return this.scene.background;
        }

        // Find the closest point to the ray
        Point closestPoint = ray.findClosestPoint(intersections);

        // Return the color at that point
        return calcColor(closestPoint);
    }

    /**
     * Calculates the color at a given point.
     * Right now it only returns the ambient light.
     * @param point the point to get the color for
     * @return the color at the point
     */
    private Color calcColor(Point point) {
        return this.scene.ambientLight.getIntensity();
    }
}
