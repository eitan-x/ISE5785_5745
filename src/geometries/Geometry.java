package geometries;

import lighting.Material;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Abstract class representing a geometric shape.
 * @author Eitan Lafair
 */
public abstract class Geometry extends Intersectable {


    /**
     * The emission color of the geometry (default is black).
     */
    protected Color emission = Color.BLACK;



    private Material material = new Material();

    public Material getMaterial() {
        return material;
    }


    public Geometry setMaterial(Material material) {
        this.material = material;
        return this;

    }



    /**
     * Returns the emission color of the geometry.
     *
     * @return the emission color
     */
    public Color getEmission() {
        return emission;
    }

    /**
     * Sets the emission color of the geometry.
     * This method supports method chaining (Builder pattern).
     *
     * @param emission the emission color to set
     * @return this geometry instance
     */
    public Geometry setEmission(Color emission) {
        this.emission = emission;
        return this;
    }

    /**
     * Returns the normal vector at a given point on the geometry.
     *
     * @param point the point on the geometric shape
     * @return the normal vector at the given point
     */
    public abstract Vector getNormal(Point point);




}

