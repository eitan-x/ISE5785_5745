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
 * @author Eitan lafair
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructs the ray tracer with a given scene.
     * @param scene the 3D scene to trace rays in
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Traces a ray and returns the color at the closest intersection point.
     * @param ray the ray to trace
     * @return the color at the closest point, or the background color if no intersection found
     */
    @Override
    public Color traceRay(Ray ray) {
        List<Intersection> intersections = scene.geometries.calculateIntersections(ray);

        if (intersections == null) {
            return scene.background;
        }

        Intersection closestIntersection = ray.findClosestIntersection(intersections);

        if (closestIntersection == null) {
            return scene.background;
        }

        return calcColor(closestIntersection,ray);
    }

    public boolean preprocessIntersection(Intersection intersection, Vector rayDirection) {
        intersection.viewDirection = rayDirection;

        // Compute normal at the intersection point
        intersection.normalAtPoint = intersection.geometry.getNormal(intersection.point);

        // Compute the dot product of the view direction and the normal
        intersection.vnDotProduct = alignZero(intersection.viewDirection.dotProduct(intersection.normalAtPoint));

        // Return false if the dot product is zero, otherwise true
        return !isZero(intersection.vnDotProduct);
    }

    public boolean setLightSource(Intersection intersection, LightSource lightSource){
        // Set the light source
        intersection.lightSource = lightSource;

        // Compute direction from the point to the light source
        intersection.lightDirection = lightSource.getL(intersection.point);

        // Compute the dot product between light direction and normal
        intersection.lnDotProduct = alignZero(intersection.lightDirection.dotProduct(intersection.normalAtPoint));

        // ✅ Return false if the light and view are on opposite sides of the surface
        return intersection.vnDotProduct * intersection.lnDotProduct > 0;
    }





    private Color calcColorLocalEffects(Intersection intersection) {
        // Start with the geometry’s emission color
        Color result = intersection.geometry.getEmission();

        // If the view‐normal dot is zero, no local lighting contribution
        if (!preprocessIntersection(intersection, intersection.viewDirection)) {
            return result;
        }

        // For each light in the scene
        for (LightSource light : scene.lights) {
            // Initialize light‐related fields; skip if dot is zero
            if (!setLightSource(intersection, light)) {
                continue;
            }

            double nl = intersection.lnDotProduct;           // N·L
            Vector l   = intersection.lightDirection;        // direction to light
            Color  iL  = light.getIntensity(intersection.point);

            // Compute diffuse and specular terms
            Double3 diff = calcDiffusive(intersection, nl);
            Double3 spec = calcSpecular(intersection, l, nl);

            // Accumulate contribution: I_L * (diffuse + specular)
            result = result.add(iL.scale(diff).add(iL.scale(spec)));
        }

        return result;
    }


    private Double3 calcSpecular(Intersection inter, Vector l, double nl) {
        // normalize normal and view‐direction
        Vector n = inter.normalAtPoint.normalize();
        Vector v = inter.viewDirection.normalize().scale(-1); // from point back to camera

        // reflection vector R = 2*(N·L)*N – L
        Vector r = n.scale(2 * nl).subtract(l).normalize();

        // R·V
        double rv = alignZero(r.dotProduct(v));
        if (rv <= 0) {
            // if angle > 90°, no specular contribution
            return Double3.ZERO;
        }

        // specular = kS * (R·V)^shininess
        return inter.material.kS.scale(Math.pow(rv, inter.material.nShininess));
    }


    private Double3 calcDiffusive(Intersection inter, double nl) {
        // diffuse = kD * |N·L|
        double factor = nl < 0 ? -nl : nl;
        return inter.material.kD.scale(factor);
    }





    /**
     * Calculates the color at a given intersection point.
     * Currently, returns the sum of the ambient light and the emission color of the geometry.
     * @param intersection the intersection object containing geometry and point
     * @return the calculated color at the intersection
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        // initialize intersection data; return black if view direction is perpendicular to surface
        if (!preprocessIntersection(intersection, ray.direction)) {
            return Color.BLACK;
        }

        // apply ambient lighting: ambient intensity multiplied by material's ambient coefficient
        Color color = scene.ambientLight.getIntensity()
                .scale(intersection.geometry.getMaterial().kA);

        // add the diffuse and specular contributions from all light sources
        Color localEffects = calcColorLocalEffects(intersection);
        color = color.add(localEffects);

        return color;
    }

}
