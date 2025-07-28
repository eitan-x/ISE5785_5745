package renderer;

import geometries.Intersectable.Intersection;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * Ray tracer that calculates lighting, reflection, and transparency for a scene.
 * @author Eitan Lafair
 */
public class SimpleRayTracer extends RayTracerBase {

    public static final double DELTA = 0.1;
    private static final int MAX_CALC_COLOR_LEVEL = 10;
    private static final double MIN_CALC_COLOR_K = 0.00001;
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Constructor for the ray tracer.
     * @param scene the scene to trace
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Main method to trace a ray and return its final color.
     * @param ray the ray to trace
     * @return color at the closest intersection or background
     */
    @Override
    public Color traceRay(Ray ray) {
        var intersections = scene.geometries.calculateIntersections(ray);
        if (intersections == null) {
            return scene.background;
        }
        return calcColor(ray.findClosestIntersection(intersections), ray);
    }

    /**
     * Finds the closest intersection point of a given ray.
     * @param ray the ray to check
     * @return the closest intersection or null
     */
    private Intersection findClosestIntersection(Ray ray) {
        try {
            List<Intersection> intersections = scene.geometries.calculateIntersections(ray);
            if (intersections == null) {
                return null;
            }
            return ray.findClosestIntersection(intersections);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Calculates the color at a given intersection using lighting and recursion.
     * @param intersection intersection to process
     * @param ray the view ray
     * @return the resulting color
     */
    private Color calcColor(Intersection intersection, Ray ray) {
        if (!preprocessIntersection(intersection, ray.direction)) {
            return Color.BLACK;
        }
        return calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K);
    }

    /**
     * Recursive color calculation for global and local effects.
     * @param intersection the point to shade
     * @param level current recursion level
     * @param k accumulated transparency/reflection coefficient
     * @return final color
     */
    private Color calcColor(Intersection intersection, int level, Double3 k) {
        // if view direction is orthogonal to surface, return black
        if (alignZero(intersection.normalAtPoint.dotProduct(intersection.viewDirection)) == 0)
            return Color.BLACK;

        Color color = calcColorLocalEffects(intersection);
        return level == 1 ? color : color.add(calcGlobalEffects(intersection, level, k));
    }

    /**
     * Calculates local lighting effects like diffuse and specular.
     * @param intersection the point to shade
     * @return resulting color from lights
     */
    private Color calcColorLocalEffects(Intersection intersection) {
        Color color = intersection.geometry.getEmission(); // base color of object
        for (LightSource lightSource : scene.lights) {
            if (!setLightSource(intersection, lightSource)) continue;

            // how much light passes through objects between light and point
            Double3 ktr = transparency(intersection, lightSource, intersection.lightDirection, intersection.normalAtPoint, intersection.vnDotProduct);

            // skip if too little light gets through
            if (ktr.product(INITIAL_K).lowerThan(MIN_CALC_COLOR_K)) continue;

            // calculate diffuse and specular lighting
            Double3 diffuse = calcDiffusive(intersection);
            Double3 specular = calcSpecular(intersection);
            Double3 totalEffect = diffuse.add(specular);
            Color iL = lightSource.getIntensity(intersection.point).scale(ktr);

            color = color.add(iL.scale(totalEffect));
        }
        return color;
    }

    /**
     * Prepares the intersection by calculating view direction and normal.
     * @param intersection the point
     * @param v the incoming view direction
     * @return true if preparation succeeded
     */
    private boolean preprocessIntersection(Intersection intersection, Vector v) {
        intersection.viewDirection = v.normalize(); // normalize view direction
        intersection.normalAtPoint = intersection.geometry.getNormal(intersection.point); // get surface normal
        intersection.vnDotProduct = alignZero(v.dotProduct(intersection.normalAtPoint)); // dot product for angle calc

        // flip normal if it's facing the wrong way
        if (intersection.vnDotProduct > 0) {
            intersection.normalAtPoint = intersection.normalAtPoint.scale(-1);
            intersection.vnDotProduct = -intersection.vnDotProduct;
        }

        return intersection.normalAtPoint != null && intersection.viewDirection != null;
    }

    /**
     * Sets light direction and calculates light-surface angle.
     * @param intersection the point to light
     * @param light the light source
     * @return true if light contributes to the point
     */
    private boolean setLightSource(Intersection intersection, LightSource light) {
        intersection.lightSource = light;
        intersection.lightDirection = light.getL(intersection.point).normalize();
        intersection.lnDotProduct = alignZero(intersection.lightDirection.dotProduct(intersection.normalAtPoint));
        return intersection.lnDotProduct * intersection.vnDotProduct > 0;
    }

    /**
     * Calculates the specular (shiny) effect from the light.
     * @param intersection the point to shade
     * @return specular contribution
     */
    private Double3 calcSpecular(Intersection intersection) {
        Vector v = intersection.viewDirection.scale(-1.0);
        double factor = alignZero(v.dotProduct(calcReflection(intersection)));
        factor = factor <= 0 ? 0 : Math.pow(factor, intersection.material.nShininess);
        return intersection.material.kS.scale(factor);
    }

    /**
     * Calculates the diffuse (matte) effect from the light.
     * @param intersection the point to shade
     * @return diffuse contribution
     */
    private Double3 calcDiffusive(Intersection intersection) {
        Double3 diffuse = intersection.material.kD.scale(intersection.lnDotProduct);
        // make sure all components are positive
        double d1 = Math.abs(diffuse.d1());
        double d2 = Math.abs(diffuse.d2());
        double d3 = Math.abs(diffuse.d3());
        return new Double3(d1, d2, d3);
    }

    /**
     * Checks how transparent the path is between a point and a light source.
     * @param gp the point on geometry
     * @param light the light source
     * @param l light direction
     * @param n normal at point
     * @param nv dot product of view and normal
     * @return transparency coefficient
     */
    private Double3 transparency(Intersection gp, LightSource light, Vector l, Vector n, double nv) {
        Vector lightDirection = l.scale(-1.0);
        Vector delta = n.scale(nv < 0 ? DELTA : -DELTA);
        Ray lightRay = new Ray(gp.point.add(delta), lightDirection);
        List<Intersection> intersections = scene.geometries.calculateIntersections(lightRay);

        if (intersections == null) return Double3.ONE;

        Double3 ktr = Double3.ONE;
        for (Intersection inter : intersections) {
            double lightDistance = light.getDistance(gp.point);
            double intersectionDistance = inter.point.distance(gp.point);

            // if the intersection is between the light and the point
            if (alignZero(intersectionDistance - lightDistance) < 0) {
                ktr = ktr.product(inter.material.kT); // multiply transparency
                if (ktr.lowerThan(MIN_CALC_COLOR_K)) return Double3.ZERO;
            }
        }
        return ktr;
    }

    /**
     * Calculates the reflection vector.
     * @param intersection the point to reflect
     * @return reflection vector
     */
    private Vector calcReflection(Intersection intersection) {
        Vector normal = intersection.normalAtPoint;
        return intersection.lightDirection
                .add(normal.scale(-2.0 * intersection.lightDirection.dotProduct(normal)));
    }

    /**
     * Calculates global effects like reflection and refraction.
     * @param intersection the point to process
     * @param level recursion level
     * @param k current accumulated coefficient
     * @return global contribution color
     */
    private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
        Color color = intersection.geometry.getEmission();
        if (level == 1 || k.lowerThan(MIN_CALC_COLOR_K)) {
            return Color.BLACK;
        }

        // add ambient light
        if (scene.ambientLight.getIntensity() != null) {
            color = color.add(scene.ambientLight.getIntensity().scale(intersection.material.kA));
        }

        Vector v = intersection.viewDirection;
        Vector n = intersection.normalAtPoint;
        Point point = intersection.point;
        double nv = alignZero(n.dotProduct(v));

        // handle reflection
        Double3 kR = intersection.material.kR;
        if (!kR.equals(Double3.ZERO)) {
            Vector r = v.subtract(n.scale(nv * 2));
            Ray reflectedRay = new Ray(point, r, n);
            color = color.add(calcGlobalEffect(reflectedRay, level, k, kR));
        }

        // handle refraction
        Double3 kT = intersection.material.kT;
        if (!kT.equals(Double3.ZERO)) {
            double na = n.dotProduct(v);
            Vector delta = n.scale(na > 0 ? DELTA : -DELTA);
            Ray refractedRay = new Ray(point, v, n);

            color = color.add(calcGlobalEffect(refractedRay, level, k, kT));
        }

        return color;
    }

    /**
     * Helper for recursive global lighting effects.
     * @param secondaryRay the ray to trace
     * @param level current recursion depth
     * @param k current coefficient
     * @param kEffect effect multiplier (reflection/refraction)
     * @return calculated color
     */
    private Color calcGlobalEffect(Ray secondaryRay, int level, Double3 k, Double3 kEffect) {
        Intersection intersection = findClosestIntersection(secondaryRay);
        if (intersection == null) {
            return Color.BLACK;
        }

        if (!preprocessIntersection(intersection, secondaryRay.direction)) {
            return Color.BLACK;
        }

        Color effectColor = calcColor(intersection, level - 1, k.product(kEffect));
        return effectColor.scale(kEffect);
    }
}
