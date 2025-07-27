package renderer;

import lighting.LightSource;
import lighting.Material;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;
import geometries.Intersectable.Intersection;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;
/**
 * A simple ray tracer that finds the color at the closest point hit by a ray.
 * Shoots rays into the scene and returns the color of what they hit.
 */
public class SimpleRayTracer extends RayTracerBase {

    private static final double DELTA = 0.1; // Small offset to avoid self-shadowing

    /**
     * Constructs the ray tracer with a given scene.
     * @param scene the 3D scene to trace rays in
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Traces a ray and returns the color at the closest intersection point.
     * Basically: Shoots a ray and sees what color it hits.
     */
    @Override
    public Color traceRay(Ray ray) {
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);
        if (intersections == null) return scene.background;

        Intersection closestIntersection = ray.findClosestIntersection(intersections);
        if (closestIntersection == null) return scene.background;

        return calcColor(closestIntersection, ray);
    }

    /**
     * Checks if a point is lit by a light source (i.e., not in shadow).
     * Basically: Sends a ray toward the light and checks if anything blocks it.
     */
    private boolean unshaded(Intersection intersection) {
        Vector l = intersection.lightDirection.scale(-1); // Direction from point to light
        Vector n = intersection.normalAtPoint;
        Vector delta = n.scale(DELTA); // Slight offset to avoid the ray starting inside the surface
        Ray shadowRay = new Ray(intersection.point.add(delta), l); // Shadow ray toward light

        List<Intersection> shadowIntersections = scene.geometries.calculateIntersections(shadowRay);
        if (shadowIntersections == null) return true;

        double lightDistance = intersection.lightSource.getDistance(intersection.point);

        for (Intersection inter : shadowIntersections) {
            if (inter.geometry != intersection.geometry) {
                double t = inter.point.distance(shadowRay.head); // Distance from shadow ray start to intersection
                if (alignZero(t - lightDistance) < 0) {
                    return false; // Something blocks the light
                }
            }
        }

        return true; // No object blocks the light
    }

    /**
     * Prepares the intersection for shading calculations by computing the normal and view direction.
     * Basically: Sets up some values we need to do lighting math.
     */
    public boolean preprocessIntersection(Intersection intersection, Vector rayDirection) {
        intersection.viewDirection = rayDirection; // Direction from point to viewer
        intersection.normalAtPoint = intersection.geometry.getNormal(intersection.point); // Surface normal
        intersection.vnDotProduct = alignZero(intersection.viewDirection.dotProduct(intersection.normalAtPoint)); // Viewer-normal angle
        return !isZero(intersection.vnDotProduct); // If viewer looks straight across the surface, skip lighting
    }

    /**
     * Sets light-related fields for the intersection.
     * Basically: Tells the intersection which light is shining on it and from what direction.
     */
    public boolean setLightSource(Intersection intersection, LightSource lightSource) {
        intersection.lightSource = lightSource;
        intersection.lightDirection = lightSource.getL(intersection.point); // Direction from point to light
        intersection.lnDotProduct = alignZero(intersection.lightDirection.dotProduct(intersection.normalAtPoint)); // Light-normal angle
        return intersection.vnDotProduct * intersection.lnDotProduct > 0; // Light and viewer must be on same side
    }

    /**
     * Calculates the local lighting at a point (diffuse + specular).
     * Basically: Adds the light effects from all light sources if the point is not in shadow.
     */
    private Color calcColorLocalEffects(Intersection intersection) {
        Color result = intersection.geometry.getEmission(); // Start with the object’s self-glow color

        if (!preprocessIntersection(intersection, intersection.viewDirection)) {
            return result; // No lighting if surface is sideways to viewer
        }

        for (LightSource light : scene.lights) {
            if (!setLightSource(intersection, light) || !unshaded(intersection)) {
                continue; // Skip lights that are blocked or from the wrong direction
            }

            double nl = intersection.lnDotProduct;
            Vector l = intersection.lightDirection;
            Color iL = light.getIntensity(intersection.point); // Light intensity at this point

            Double3 diff = calcDiffusive(intersection, nl); // Matte lighting
            Double3 spec = calcSpecular(intersection, l, nl); // Shiny highlight

            result = result.add(iL.scale(diff).add(iL.scale(spec))); // Add total light effect
        }

        return result;
    }

    /**
     * Calculates the specular reflection (the shiny highlight).
     * Basically: Adds a white sparkle if the surface reflects light toward the viewer.
     */
    private Double3 calcSpecular(Intersection inter, Vector l, double nl) {
        Vector n = inter.normalAtPoint.normalize();
        Vector v = inter.viewDirection.normalize();
        Vector r = n.scale(2 * nl).subtract(l).normalize(); // Reflection vector
        double rv = alignZero(r.dotProduct(v)); // Angle between reflection and viewer

        if (rv <= 0) return Double3.ZERO; // No shine if facing wrong way

        return inter.material.kS.scale(Math.pow(rv, inter.material.nShininess)); // Shiny effect
    }

    /**
     * Calculates the diffuse reflection (basic lighting).
     * Basically: Adds brightness based on how directly the light hits the surface.
     */
    private Double3 calcDiffusive(Intersection inter, double nl) {
        double factor = nl < 0 ? -nl : nl; // Use the absolute value of N·L
        return inter.material.kD.scale(factor); // Matte lighting = kD * |N·L|
    }

    /**
     * Calculates the total color at an intersection (ambient + local lighting).
     * Basically: Figures out the final color at the point, including shadows and light.
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        if (!preprocessIntersection(intersection, ray.direction)) {
            return Color.BLACK; // Skip if camera looks flat across the surface
        }

        Color color = scene.ambientLight.getIntensity()
                .scale(intersection.geometry.getMaterial().kA); // Ambient light = base light everywhere

        color = color.add(calcColorLocalEffects(intersection)); // Add all other lights
        return color;
    }
}

