package renderer;

import geometries.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * A basic ray tracer that supports local and global lighting effects.
 * @author: Eitan Lafair
 */
public class SimpleRayTracer extends RayTracerBase {

    /** Small value used to shift ray start point to avoid self-intersections */
    public static final double DELTA = 0.1;

    /** Max recursion depth for color calculation */
    private static final int MAX_CALC_COLOR_LEVEL = 10;

    /** Minimum coefficient to stop recursion */
    private static final double MIN_CALC_COLOR_K = 0.00001;

    /** Starting coefficient for full light */
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Constructs a ray tracer for the given scene
     */
    public SimpleRayTracer(Scene scene) {
        super(scene); // Calls the constructor of RayTracerBase
    }

    /**
     * Traces a ray into the scene and returns the resulting color
     */
    @Override
    public Color traceRay(Ray ray) {
        var intersections = scene.geometries.calculateIntersections(ray); // Finds intersections with scene geometries
        if (intersections == null) return scene.background; // If no intersection, return background color

        Intersection closestIntersection = ray.findClosestIntersection(intersections);
        if (closestIntersection == null) return scene.background;  //

        return calcColor(ray.findClosestIntersection(intersections), ray); // Calculate color at closest intersection
    }

    /**
     * Finds the closest intersection of the given ray
     */
    private Intersection findClosestIntersection(Ray ray) {
        try {
            List<Intersection> intersections = scene.geometries.calculateIntersections(ray); // Get intersections
            if (intersections == null) return null;
            return ray.findClosestIntersection(intersections); // Return closest
        } catch (IllegalArgumentException e) {
            return null; // If invalid, return null
        }
    }

    /**
     * Calculates the color at the intersection using local and global effects
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        if (!preprocessIntersection(intersection, ray.direction)) return Color.BLACK; // Ensure vectors are valid
        return calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K); // Start recursive color calculation
    }

    /**
     * Recursive method for calculating local and global lighting effects
     */
    private Color calcColor(Intersection intersection, int level, Double3 k) {
        if (alignZero(intersection.normalAtPoint.dotProduct(intersection.viewDirection)) == 0)
            return Color.BLACK; // Skip if view direction is perpendicular
        Color color = calcColorLocalEffects(intersection); // Local light (diffuse + specular)
        return level == 1 ? color : color.add(calcGlobalEffects(intersection, level, k)); // Add global effects if needed
    }

    /**
     * Calculates the local lighting effects (diffuse and specular)
     */
    private Color calcColorLocalEffects(Intersection intersection) {
        Color color = intersection.geometry.getEmission(); // Start with emission color
        for (LightSource lightSource : scene.lights) {
            if (!setLightSource(intersection, lightSource)) continue; // Prepare light vectors

            // Compute light transparency to current point
            Double3 ktr = transparency(intersection, lightSource,
                    intersection.lightDirection,
                    intersection.normalAtPoint,
                    intersection.vnDotProduct);

            if (ktr.product(INITIAL_K).lowerThan(MIN_CALC_COLOR_K)) continue; // Skip if too dim

            // Compute diffuse and specular components
            Double3 diffuse = calcDiffusive(intersection);
            Double3 specular = calcSpecular(intersection);
            Double3 totalEffect = diffuse.add(specular);

            // Add contribution from this light source
            Color iL = lightSource.getIntensity(intersection.point).scale(ktr);
            color = color.add(iL.scale(totalEffect));
        }
        return color;
    }

    /**
     * Initializes normal and view direction for the intersection
     */
    private boolean preprocessIntersection(Intersection intersection, Vector v) {
        intersection.viewDirection = v.normalize(); // Normalize view vector
        intersection.normalAtPoint = intersection.geometry.getNormal(intersection.point); // Get surface normal
        intersection.vnDotProduct = alignZero(v.dotProduct(intersection.normalAtPoint)); // Dot product for direction check

        // If normal is in wrong direction, flip it
        if (intersection.vnDotProduct > 0) {
            intersection.normalAtPoint = intersection.normalAtPoint.scale(-1);
            intersection.vnDotProduct = -intersection.vnDotProduct;
        }
        return intersection.normalAtPoint != null && intersection.viewDirection != null; // Ensure both are set
    }

    /**
     * Prepares light direction and dot product at intersection
     */
    private boolean setLightSource(Intersection intersection, LightSource light) {
        intersection.lightSource = light; // Assign current light
        intersection.lightDirection = light.getL(intersection.point).normalize(); // Compute light direction
        intersection.lnDotProduct = alignZero(
                intersection.lightDirection.dotProduct(intersection.normalAtPoint)); // Dot product with normal

        // Return true only if light and view are on same side
        return intersection.lnDotProduct * intersection.vnDotProduct > 0;
    }

    /**
     * Computes specular (mirror-like) reflection intensity
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector v = intersection.viewDirection.scale(-1.0); // Invert view direction
        double factor = alignZero(v.dotProduct(calcReflection(intersection))); // Angle with reflection vector
        factor = factor <= 0 ? 0 : Math.pow(factor, intersection.material.nShininess); // Apply shininess
        return intersection.material.kS.scale(factor); // Scale by material
    }

    /**
     * Computes diffuse (matte-like) light intensity
     */
    private Double3 calcDiffusive(Intersection intersection) {
        Double3 diffuse = intersection.material.kD.scale(intersection.lnDotProduct); // Dot product scaled
        // Ensure all values are positive
        return new Double3(
                Math.abs(diffuse.d1()),
                Math.abs(diffuse.d2()),
                Math.abs(diffuse.d3()));
    }

    /**
     * Computes how much light passes through transparent objects to the point
     */
    private Double3 transparency(Intersection gp, LightSource light, Vector l, Vector n, double nv) {
        Vector lightDirection = l.scale(-1.0); // Reverse light direction
        Vector delta = n.scale(nv < 0 ? DELTA : -DELTA); // Offset to avoid self-intersection
        Ray lightRay = new Ray(gp.point.add(delta), lightDirection); // Shadow ray
        List<Intersection> intersections = scene.geometries.calculateIntersections(lightRay);

        if (intersections == null) return Double3.ONE; // Fully transparent

        Double3 ktr = Double3.ONE;
        for (Intersection inter : intersections) {
            double lightDistance = light.getDistance(gp.point); // Distance to light
            double intersectionDistance = inter.point.distance(gp.point); // Distance to obstacle

            if (alignZero(intersectionDistance - lightDistance) < 0) {
                ktr = ktr.product(inter.material.kT); // Multiply transparency
                if (ktr.lowerThan(MIN_CALC_COLOR_K)) return Double3.ZERO; // Stop if too dim
            }
        }
        return ktr; // Final transparency value
    }

    /**
     * Calculates the reflection vector
     */
    private Vector calcReflection(Intersection intersection) {
        Vector normal = intersection.normalAtPoint;
        return intersection.lightDirection
                .add(normal.scale(-2.0 * intersection.lightDirection.dotProduct(normal))); // Reflect around normal
    }

    /**
     * Computes global lighting effects (reflection and refraction)
     */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        Color color = intersection.geometry.getEmission();
        if (level == 1 || k.lowerThan(MIN_CALC_COLOR_K)) return Color.BLACK; // Stop if too deep or dim

        // Add ambient contribution
        if (scene.ambientLight.getIntensity() != null) {
            color = color.add(scene.ambientLight.getIntensity().scale(intersection.material.kA));
        }

        Vector v = intersection.viewDirection;
        Vector n = intersection.normalAtPoint;
        Point point = intersection.point;
        double nv = alignZero(n.dotProduct(v));

        // Handle reflection
        Double3 kR = intersection.material.kR;
        if (!kR.equals(Double3.ZERO)) {
            Vector r = v.subtract(n.scale(nv * 2));
            Ray reflectedRay = new Ray(point, r, n);
            color = color.add(calcGlobalEffect(reflectedRay, level, k, kR));
        }

        // Handle refraction
        Double3 kT = intersection.material.kT;
        if (!kT.equals(Double3.ZERO)) {
            Ray refractedRay = new Ray(point, v, n);
            color = color.add(calcGlobalEffect(refractedRay, level, k, kT));
        }

        return color;
    }

    /**
     * Recursive helper for computing color from secondary rays (reflection/refraction)
     */
    private Color calcGlobalEffect(Ray secondaryRay, int level, Double3 k, Double3 kEffect) {
        Intersection intersection = findClosestIntersection(secondaryRay);
        if (intersection == null) return Color.BLACK;

        if (!preprocessIntersection(intersection, secondaryRay.direction)) return Color.BLACK;

        Color effectColor = calcColor(intersection, level - 1, k.product(kEffect));
        return effectColor.scale(kEffect);
    }
}
