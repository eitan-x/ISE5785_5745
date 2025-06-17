package geometries;

import lighting.LightSource;
import lighting.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * Abstract base for all intersectable geometries.
 * @author Eitan Lafair
 */
public abstract class Intersectable {

    /**
     * Returns intersection points (without geometry info) of a ray with the geometry.
     * @param ray the ray to test
     * @return list of intersection points, or null if none
     */
    public List<Point> findIntersections(Ray ray) {
        var intersections = calculateIntersections(ray);
        return intersections == null ? null : intersections.stream().map(i -> i.point).toList();
    }

    /**
     * Calculates all intersections with geometry (with geometry info).
     * To be implemented by subclasses.
     * @param ray the ray to intersect
     * @return list of intersections, or null if none
     */
    protected abstract List<Intersection> calculateIntersectionsHelper(Ray ray);

    /**
     * Wrapper for internal intersection method. Part of NVI pattern.
     * @param ray the ray to intersect
     * @return list of intersections, or null if none
     */
    public final List<Intersection> calculateIntersections(Ray ray) {
        return calculateIntersectionsHelper(ray);
    }

    /**
     * Record of an intersection point and its geometry.
     */
    public static class Intersection {
        public Geometry geometry;
        public Point point;
        public final Material material;
        public Vector viewDirection;      // Vector from the point toward the camera (viewer)
        public Vector normalAtPoint;      // Surface normal at the intersection point
        public double vnDotProduct;       // Dot product of view direction and normal
        public LightSource lightSource;   // Current light source affecting the point
        public Vector lightDirection;     // Vector from the point toward the light source
        public double lnDotProduct;       // Dot product of light direction and normal




        /**
         * Constructs an intersection record.
         * @param geometry the intersected geometry
         * @param point the intersection point
         */
        public Intersection(Geometry geometry, Point point, Material material) {
            this.geometry = geometry;
            this.point = point;
            this.material = material;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            return (obj instanceof Intersection other)
                    && geometry == other.geometry
                    && point.equals(other.point);
        }

        @Override
        public String toString() {
            return geometry.toString() + " intersects at " + point.toString();
        }
    }
}
