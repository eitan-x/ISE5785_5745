package renderer;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.alignZero;

/**
 * Represents a Camera in 3D space with adjustable settings for view direction,
 * position, and viewing plane properties.
 * Author: Eitan Lafair
 */
public class Camera implements Cloneable {
    private Vector vT0;      // Direction vector from camera to view plane
    private Vector vUp;      // Up direction vector
    private Vector vRight;   // Right direction vector
    private Point p0;        // Camera position

    private Point pointCenter; // Center point of the view plane
    private double height = 0.0;   // View plane height
    private double width = 0.0;    // View plane width
    private double distance = 0.0; // Distance from camera to view plane

    /**
     * Default constructor for the Camera class.
     */
    public Camera() {
    }

    public Camera(Point point, Vector vector, Vector vector1) {
    }

    /**
     * Returns a new instance of the Camera builder.
     *
     * @return a Builder instance for creating a Camera object
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray through a pixel (i, j) on the camera's view plane.
     *
     * @param nX the number of horizontal pixels in the view plane
     * @param nY the number of vertical pixels in the view plane
     * @param j  the column index of the pixel
     * @param i  the row index of the pixel
     * @return a Ray through the specified pixel
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        // Calculate the center of the view plane
        pointCenter = p0.add(vT0.scale(distance));

        // Calculate the width and height of each pixel
        double rX = width / nX;
        double rY = height / nY;

        // Calculate the pixel's center offsets from the view plane center
        double xJ = (j - (nX - 1) / 2d) * rX;
        double yI = -(i - (nY - 1) / 2d) * rY;

        // Start from the center of the view plane
        Point pIJ = pointCenter;

        // Move right by xJ
        if (xJ != 0) {
            pIJ = pIJ.add(vRight.scale(xJ));
        }

        // Move up by yI
        if (yI != 0) {
            pIJ = pIJ.add(vUp.scale(yI));
        }

        // Create the direction vector from camera to pixel
        Vector vIJ = pIJ.subtract(p0);

        // Normalize direction (optional)
        Vector vIJ1 = vIJ.normalize();

        return new Ray(p0, vIJ); // Return the ray from camera position to pixel
    }

    /**
     * A builder class for creating instances of the Camera with configurable properties.
     * Author: itan lafair
     */
    public static class Builder {
        private final Camera camera = new Camera();

        /**
         * Sets the location (position) of the camera.
         *
         * @param p the point representing the camera's location
         * @return this builder instance for method chaining
         */
        public Builder setLocation(Point p) {
            if (p == null) {
                throw new IllegalArgumentException("Camera location point cannot be null");
            }
            camera.p0 = p; // Set the camera position
            return this;
        }

        /**
         * Sets the camera's viewing direction using two orthogonal vectors.
         *
         * @param vTo the direction vector toward the target
         * @param vUp the up direction vector
         * @return this builder instance for method chaining
         */
        public Builder setDirection(Vector vTo, Vector vUp) {
            if (vTo == null || vUp == null) {
                throw new IllegalArgumentException("Direction vectors cannot be null");
            }

            // Check orthogonality
            if (!(alignZero(vTo.dotProduct(vUp)) == 0)) {
                throw new IllegalArgumentException("vTo and vUp must be orthogonal");
            }

            // Normalize and set the vectors
            camera.vT0 = vTo.normalize();
            camera.vUp = vUp.normalize();
            camera.vRight = camera.vT0.crossProduct(camera.vUp);

            return this;
        }

        /**
         * Sets the camera's viewing direction using a target point and an approximate
         * upward direction vector.
         *
         * @param target   the point the camera is directed toward
         * @param approxUp the approximate upward vector
         * @return this builder instance
         */
        public Builder setDirection(Point target, Vector approxUp) {
            if (target == null || approxUp == null) {
                throw new IllegalArgumentException("Target point and approxUp vector cannot be null");
            }

            if (camera.p0 == null) {
                throw new IllegalArgumentException("Camera location must be set before setting direction");
            }

            // Compute the forward direction (vTo)
            Vector vTo = target.subtract(camera.p0).normalize();

            // Compute right vector as cross product of vTo and approxUp
            Vector vRight = vTo.crossProduct(approxUp).normalize();

            // Recalculate up vector for orthogonality
            Vector vUp = vRight.crossProduct(vTo).normalize();

            camera.vT0 = vTo;
            camera.vRight = vRight;
            camera.vUp = vUp;

            return this;
        }

        /**
         * Sets the camera's viewing direction using only a target point. Assumes
         * the global Y-axis as the approximate up direction.
         *
         * @param target the point the camera is directed toward
         * @return this builder instance
         */
        public Builder setDirection(Point target) {
            return setDirection(target, new Vector(0, 1, 0));
        }

        /**
         * Sets the size of the view plane.
         *
         * @param height the height of the view plane
         * @param width  the width of the view plane
         * @return this builder instance for method chaining
         */
        public Builder setVpSize(double height, double width) {
            if (alignZero(width) <= 0 || alignZero(height) <= 0)
                throw new IllegalArgumentException("Width and height must be greater than 0");

            camera.height = height;
            camera.width = width;
            return this;
        }

        /**
         * Sets the distance of the view plane from the camera.
         *
         * @param distance the distance of the view plane
         * @return this builder instance for method chaining
         */
        public Builder setVpDistance(double distance) {
            if (alignZero(distance) <= 0)
                throw new IllegalArgumentException("Distance must be greater than 0");
            camera.distance = distance;
            return this;
        }

        /**
         * Sets the resolution of the view plane. Method reserved for future implementation.
         *
         * @param nX the number of horizontal pixels
         * @param nY the number of vertical pixels
         * @return this builder instance for method chaining
         */
        public Builder setResolution(int nX, int nY) {
            // Currently not implemented
            return this;
        }

        /**
         * Builds and returns the Camera object with the configured settings.
         *
         * @return the constructed Camera object
         */
        public Camera build() {
            // Validation before building
            if (camera.p0 == null)
                throw new IllegalStateException("Camera location (p0) must be set");

            if (camera.vT0 == null || camera.vUp == null || camera.vRight == null)
                throw new IllegalStateException("Camera direction vectors (vTo, vUp, vRight) must be set");

            if (alignZero(camera.width) <= 0 || alignZero(camera.height) <= 0)
                throw new IllegalStateException("View plane size (width, height) must be set and > 0");

            if (alignZero(camera.distance) <= 0)
                throw new IllegalStateException("View plane distance must be set and > 0");

            return camera;
        }
    }
}
