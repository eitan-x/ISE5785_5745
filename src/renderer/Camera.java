package renderer;

import primitives.*;
import scene.Scene;

import java.util.MissingResourceException;

import static primitives.Util.alignZero;

/**
 * Represents a Camera in 3D space with adjustable settings for view direction,
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

    private static final String CAMERA_CLASS_NAME = "Camera"; // Class name for error messages
    private static final String MISSING_DATA_MSG = "Missing rendering data"; // Error message

    private boolean useAntiAliasing = false; // Whether anti-aliasing is enabled
    private int samplesPerDim = 5; // Number of samples per pixel dimension for anti-aliasing
    private BlackBoard.Shape aaShape = BlackBoard.Shape.RECTANGLE; // Shape of anti-aliasing sampling area

    public Camera enableAntiAliasing(boolean enable) {
        this.useAntiAliasing = enable;
        return this;
    }

    public Camera setAntiAliasingSamples(int samplesPerDim) {
        this.samplesPerDim = samplesPerDim;
        return this;
    }

    public Camera setAntiAliasingShape(BlackBoard.Shape shape) {
        this.aaShape = shape;
        return this;
    }

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
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray through a pixel (i, j) on the camera's view plane.
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        pointCenter = p0.add(vT0.scale(distance)); // Calculate center of view plane
        double rX = width / nX; // Pixel width
        double rY = height / nY; // Pixel height

        double xJ = (j - (nX - 1) / 2d) * rX; // X offset from center
        double yI = -(i - (nY - 1) / 2d) * rY; // Y offset from center (negative because image Y is downward)

        Point pIJ = pointCenter;
        if (xJ != 0) pIJ = pIJ.add(vRight.scale(xJ)); // Add horizontal offset
        if (yI != 0) pIJ = pIJ.add(vUp.scale(yI)); // Add vertical offset

        Vector vIJ = pIJ.subtract(p0); // Vector from camera to pixel
        return new Ray(p0, vIJ); // Construct ray through the pixel
    }

    private Color castRayAA(int j, int i) {
        pointCenter = p0.add(vT0.scale(distance)); // Center of view plane
        double rX = width / nX;
        double rY = height / nY;

        double xJ = (j - (nX - 1) / 2d) * rX;
        double yI = -(i - (nY - 1) / 2d) * rY;

        Point pixelCenter = pointCenter;
        if (!Util.isZero(xJ)) pixelCenter = pixelCenter.add(vRight.scale(xJ));
        if (!Util.isZero(yI)) pixelCenter = pixelCenter.add(vUp.scale(yI));

        BlackBoard blackboard = new BlackBoard(
                pixelCenter,
                vRight,
                vUp,
                rX,
                rY,
                samplesPerDim,
                aaShape
        );

        var samplePoints = blackboard.generateSamplePoints();
        Color finalColor = Color.BLACK;

        for (Point sample : samplePoints) {
            Vector dir = sample.subtract(p0); // Vector to sample
            if (dir.lengthSquared() == 0) continue; // Skip zero vector

            Ray ray = new Ray(p0, dir); // Create ray
            finalColor = finalColor.add(rayTracer.traceRay(ray)); // Accumulate color
        }

        return finalColor.reduce(samplePoints.size()); // Average color
    }

    /**
     * Casts a ray through a pixel and returns the resulting color.
     */
    private Color castRay(int j, int i) {
        Ray ray = constructRay(this.imageWriter.nX(), this.imageWriter.nY(), j, i);
        return this.rayTracer.traceRay(ray);
    }

    /**
     * Renders the image by tracing rays through all pixels.
     */
    public Camera renderImage() {
        if (this.imageWriter == null)
            throw new UnsupportedOperationException("Missing imageWriter");
        if (this.rayTracer == null)
            throw new UnsupportedOperationException("Missing rayTracerBase");

        for (int i = 0; i < this.imageWriter.nY(); i++) {
            for (int j = 0; j < this.imageWriter.nX(); j++) {
                Color color = useAntiAliasing ? castRayAA(j, i) : castRay(j, i);
                this.imageWriter.writePixel(j, i, color); // Write pixel color to image
            }
        }
        return this;
    }

    /**
     * Draws a grid on the image.
     */
    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                if (i % interval == 0 || j % interval == 0) {
                    imageWriter.writePixel(j, i, color); // Draw grid line
                }
            }
        }
        return this;
    }

    /**
     * Writes the rendered image to file.
     */
    public Camera writeToImage(String filename) {
        imageWriter.writeToImage(filename); // Save image to file
        return this;
    }

    /**
     * Builder class for constructing a Camera using fluent API.
     */
    public static class Builder {
        private final Camera camera = new Camera();

        public Builder setLocation(Point p) {
            if (p == null) throw new IllegalArgumentException("Camera location cannot be null");
            camera.p0 = p;
            return this;
        }

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

        public Builder setDirection(Point target) {
            return setDirection(target, new Vector(0, 1, 0)); // Default up vector
        }

        public Builder setVpSize(double height, double width) {
            if (alignZero(width) <= 0 || alignZero(height) <= 0)
                throw new IllegalArgumentException("View plane size must be positive");
            camera.height = height;
            camera.width = width;
            return this;
        }

        public Builder setVpDistance(double distance) {
            if (alignZero(distance) <= 0)
                throw new IllegalArgumentException("View plane distance must be positive");
            camera.distance = distance;
            return this;
        }

        public Builder setResolution(int nX, int nY) {
            if (nX <= 0 || nY <= 0)
                throw new IllegalArgumentException("Resolution must be positive");
            camera.nX = nX;
            camera.nY = nY;
            return this;
        }

        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                camera.rayTracer = new SimpleRayTracer(scene);
            } else {
                camera.rayTracer = null;
            }
            return this;
        }

        public Camera build() {
            if (camera.p0 == null)
                throw new MissingResourceException(MISSING_DATA_MSG,CAMERA_CLASS_NAME,"position");

            if (camera.vT0 == null || camera.vUp == null)
                throw new MissingResourceException(MISSING_DATA_MSG,CAMERA_CLASS_NAME,"vTO or vUP");

            if (camera.vRight == null) {
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
                camera.rayTracer = new SimpleRayTracer(null); // Default to empty scene
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
