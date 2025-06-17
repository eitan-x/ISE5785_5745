package renderer;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import primitives.Color;
import scene.Scene;

import java.util.MissingResourceException;

import static primitives.Util.alignZero;

/**
 * Represents a Camera in 3D space with adjustable settings for view direction,
 * position, and viewing plane properties.
 * Handles ray construction, image rendering, and grid drawing on images.
 * Uses a builder pattern for convenient setup.
 *
 * @author Eitan Lafair
 */
public class Camera implements Cloneable {
    private Vector vT0; // Direction vector from camera to view plane
    private Vector vUp; // Up direction vector
    private Vector vRight; // Right direction vector
    private Point p0; // Camera position

    private Point pointCenter; // Center point of the view plane
    private double height = 0.0; // View plane height
    private double width = 0.0; // View plane width
    private double distance = 0.0; // Distance from camera to view plane

    private ImageWriter imageWriter; // Responsible for writing image files
    private RayTracerBase rayTracer; // Ray tracer for rendering
    private int nX = 1; // Image resolution X
    private int nY = 1; // Image resolution Y


    private static final String CAMERA_CLASS_NAME = "Camera";
    private static final String MISSING_DATA_MSG = "Missing rendering data";



    /**
     * Default constructor.
     */
    public Camera() {
    }

    /**
     * Constructor with position and orientation vectors.
     * @param point Camera position
     * @param vector Forward direction
     * @param vector1 Up direction
     */
    public Camera(Point point, Vector vector, Vector vector1) {
    }

    /**
     * Returns a new instance of the Camera builder.
     * @return a Builder instance for creating a Camera object
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray through a pixel (i, j) on the camera's view plane.
     * @param nX number of horizontal pixels
     * @param nY number of vertical pixels
     * @param j column index of the pixel
     * @param i row index of the pixel
     * @return the ray passing through the pixel
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        pointCenter = p0.add(vT0.scale(distance));
        double rX = width / nX;
        double rY = height / nY;

        double xJ = (j - (nX - 1) / 2d) * rX;
        double yI = -(i - (nY - 1) / 2d) * rY;

        Point pIJ = pointCenter;
        if (xJ != 0) pIJ = pIJ.add(vRight.scale(xJ));
        if (yI != 0) pIJ = pIJ.add(vUp.scale(yI));

        Vector vIJ = pIJ.subtract(p0);
        return new Ray(p0, vIJ);
    }

    /**
     * Casts a ray through a pixel (j, i) and gets its color by tracing it.
     * @param j column index of the pixel
     * @param i row index of the pixel
     * @return color obtained by tracing the ray
     */
    private Color castRay(int j, int i) {
        Ray ray = constructRay(
                this.imageWriter.nX(),
                this.imageWriter.nY(),
                j,
                i);
        return this.rayTracer.traceRay(ray);
    }

    /**
     * Renders the image by tracing rays through all pixels.
     * @return this Camera object
     */
    public Camera renderImage() {
        if (this.imageWriter == null)
            throw new UnsupportedOperationException("Missing imageWriter");
        if (this.rayTracer == null)
            throw new UnsupportedOperationException("Missing rayTracerBase");

        for (int i = 0; i < this.imageWriter.nX(); i++) {
            for (int j = 0; j < this.imageWriter.nY(); j++) {
                Color color = castRay(j, i);
                this.imageWriter.writePixel(j, i, color);
            }
        }
        return this;
    }

    /**
     * Prints a grid on the image using the given color and interval.
     * @param interval grid spacing
     * @param color grid color
     * @return this Camera object
     */
    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                if (i % interval == 0 || j % interval == 0) {
                    imageWriter.writePixel(j, i, color);
                }
            }
        }
        return this;
    }

    /**
     * Writes the image to a file.
     * @param filename the name of the image file (without extension)
     * @return this Camera object
     */
    public Camera writeToImage(String filename) {
        imageWriter.writeToImage(filename);
        return this;
    }

    /**
     * A builder class for creating Camera instances with fluent API.
     */
    public static class Builder {
        private final Camera camera = new Camera();

        /**
         * Sets the location of the camera.
         * @param p the camera position
         * @return this builder instance
         */
        public Builder setLocation(Point p) {
            if (p == null) throw new IllegalArgumentException("Camera location cannot be null");
            camera.p0 = p;
            return this;
        }

        /**
         * Sets the camera orientation using vTo and vUp vectors.
         * @param vTo direction to look at
         * @param vUp up direction
         * @return this builder instance
         */
        public Builder setDirection(Vector vTo, Vector vUp) {
            if (vTo == null || vUp == null)
                throw new IllegalArgumentException("Direction vectors cannot be null");
            if (alignZero(vTo.dotProduct(vUp)) != 0)
                throw new IllegalArgumentException("vTo and vUp must be orthogonal");

            camera.vT0 = vTo.normalize();
            camera.vUp = vUp.normalize();
            camera.vRight = camera.vT0.crossProduct(camera.vUp);
            return this;
        }

        /**
         * Sets camera direction to face a target point using an approximate up vector.
         * @param target target point to look at
         * @param approxUp approximate up direction
         * @return this builder instance
         */
        public Builder setDirection(Point target, Vector approxUp) {
            if (target == null || approxUp == null)
                throw new IllegalArgumentException("Target and approxUp cannot be null");
            if (camera.p0 == null)
                throw new IllegalArgumentException("Camera location must be set before direction");

            Vector vTo = target.subtract(camera.p0).normalize();
            Vector vRight = vTo.crossProduct(approxUp).normalize();
            Vector vUp = vRight.crossProduct(vTo).normalize();

            camera.vT0 = vTo;
            camera.vRight = vRight;
            camera.vUp = vUp;
            return this;
        }

        /**
         * Sets camera direction to look at a target with default up vector (0,1,0).
         * @param target target point to look at
         * @return this builder instance
         */
        public Builder setDirection(Point target) {
            return setDirection(target, new Vector(0, 1, 0));
        }

        /**
         * Sets the size of the view plane.
         * @param height height of the view plane
         * @param width width of the view plane
         * @return this builder instance
         */
        public Builder setVpSize(double height, double width) {
            if (alignZero(width) <= 0 || alignZero(height) <= 0)
                throw new IllegalArgumentException("View plane size must be positive");
            camera.height = height;
            camera.width = width;
            return this;
        }

        /**
         * Sets the distance between the camera and the view plane.
         * @param distance distance to view plane
         * @return this builder instance
         */
        public Builder setVpDistance(double distance) {
            if (alignZero(distance) <= 0)
                throw new IllegalArgumentException("View plane distance must be positive");
            camera.distance = distance;
            return this;
        }

        /**
         * Sets the resolution of the view plane.
         * @param nX number of horizontal pixels
         * @param nY number of vertical pixels
         * @return this builder instance
         */
        public Builder setResolution(int nX, int nY) {
            if (nX <= 0 || nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");
            camera.nX = nX;
            camera.nY = nY;
            return this;
        }

        /**
         * Sets the ray tracer using scene and type.
         * @param scene the scene to use
         * @param type the ray tracer type (currently only SIMPLE supported)
         * @return this builder instance
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                camera.rayTracer = new SimpleRayTracer(scene);
            } else {
                camera.rayTracer = null;
            }
            return this;
        }

        /**
         * Builds the final camera object.
         * @return the constructed Camera
         */
        public Camera build() {
            if (camera.p0 == null)
                throw new MissingResourceException(MISSING_DATA_MSG,CAMERA_CLASS_NAME,"position");

            if (camera.vT0 == null || camera.vUp == null)
                throw new MissingResourceException(MISSING_DATA_MSG,CAMERA_CLASS_NAME,"vTO or vUP");

            if (camera.vRight == null) {
                // ננרמל את vTo ו־vUp לפני החישוב
                camera.vT0 = camera.vT0.normalize();
                camera.vUp = camera.vUp.normalize();

                camera.vRight = camera.vT0.crossProduct(camera.vUp).normalize();
            }

            if (alignZero(camera.width) <= 0 || alignZero(camera.height) <= 0)
                throw new IllegalStateException("View plane size must be set");
            if (alignZero(camera.distance) <= 0)
                throw new IllegalStateException("View plane distance must be set");
            if (camera.nX <= 0 || camera.nY <= 0)
                throw new IllegalStateException("Resolution must be positive");

            camera.imageWriter = new ImageWriter(camera.nX, camera.nY);
            if (camera.rayTracer == null) {
                camera.rayTracer = new SimpleRayTracer(null); // Empty scene
            }
            camera.vT0 = camera.vT0.normalize();
            camera.vUp = camera.vUp.normalize();
            camera.vRight = camera.vRight.normalize();

            try {
                return (Camera)camera.clone();
            } catch (CloneNotSupportedException e) {
                Camera temp = new Camera();
                temp =camera;
                return temp;
            }
        }

    }
}
