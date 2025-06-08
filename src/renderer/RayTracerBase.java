package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * A base class for ray tracers.
 * @author  Eitan Lafair
 */
public abstract class RayTracerBase {

    /**
     * The 3D scene to render
     */
    protected final Scene scene;

    /**
     * Constructor that sets the scene.
     * @param scene the scene to use for tracing rays
     */
    public RayTracerBase(Scene scene) {
        this.scene = scene;
    }

    /**
     * Traces a ray and returns the color it "sees".
     * Each subclass will implement this differently.
     * @param ray the ray to trace
     * @return the color seen by the ray
     */
    public abstract Color traceRay(Ray ray);
}
