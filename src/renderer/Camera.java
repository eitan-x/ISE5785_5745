package renderer;

import primitives.*;
import primitives.Vector;
import scene.Scene;

import java.util.*;
import java.util.stream.*;
import static primitives.Util.alignZero;

/**
 * Represents a Camera in 3D space with adjustable settings for view direction,
 * image resolution, and more.
 *
 * @author Eitan Lafair
 */
public class Camera implements Cloneable {
    private Vector vT0; // Direction vector from camera to view plane
    private Vector vUp; // Up direction vector
    private Vector vRight; // Right direction vector
    private Point p0; // Camera position

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

    // Additional fields for multithreading and progress
    private int threadsCount = 0;
    private boolean printProgress = false; // Whether to print progress
    private static long printInterval = 100L; // Interval for printing progress
    private PixelManager pixelManager; // Pixel manager for multithreading

    private boolean useAdaptiveSuperSampling = false;
    private int adaptiveMaxDepth = 3; //
    private double adaptiveThreshold = 0.15; //

    /**
     * Enables or disables Adaptive Super Sampling (ASS) feature.
     */
    public Camera enableAdaptiveSuperSampling(boolean enable) {
        this.useAdaptiveSuperSampling = enable; // Store the flag to enable or disable ASS
        return this; // Enable method chaining
    }

    /**
     * Sets the maximum recursion depth for Adaptive Super Sampling.
     */
    public Camera setAdaptiveMaxDepth(int depth) {
        this.adaptiveMaxDepth = depth; // Store the depth limit for recursive ASS sampling
        return this;
    }

    /**
     * Sets the threshold for color difference in Adaptive Super Sampling.
     */
    public Camera setAdaptiveThreshold(double threshold) {
        this.adaptiveThreshold = threshold; // Store the minimal color difference threshold
        return this;
    }

    /**
     * Enables or disables regular Anti-Aliasing.
     */
    public Camera enableAntiAliasing(boolean enable) {
        this.useAntiAliasing = enable; // Store the flag to enable or disable standard anti-aliasing
        return this;
    }

    /**
     * Sets the number of Anti-Aliasing samples per dimension.
     */
    public Camera setAntiAliasingSamples(int samplesPerDim) {
        this.samplesPerDim = samplesPerDim; // Set number of samples per row/column (e.g. 3 -> 3x3 = 9 samples)
        return this;
    }

    /**
     * Sets the sampling shape for Anti-Aliasing grid.
     */
    public Camera setAntiAliasingShape(BlackBoard.Shape shape) {
        this.aaShape = shape; // Store the desired sampling shape (e.g., square, random, circular)
        return this;
    }

    /**
     * Sets the number of threads to use for multithreaded rendering.
     * -2 = auto mode, -1 = single-threaded, any positive number = fixed thread count
     */
    public Camera setMultithreading(int threads) {
        if (threads < -3)
            throw new IllegalArgumentException("Multithreading parameter must be -2 or higher"); // Validate input

        if (threads == -2) {
            // Auto mode: use all available processors minus 2 (leave room for OS/system tasks)
            int cores = Runtime.getRuntime().availableProcessors() - 2;
            threadsCount = Math.max(cores, 1); // Ensure at least one thread is used
        } else {
            threadsCount = threads; // Use the specified thread count directly
        }

        return this;
    }

    /**
     * Sets the debug print interval (in seconds).
     */
    public Camera setDebugPrint(double interval) {
        if (interval < 0)
            throw new IllegalArgumentException("interval parameter must be non-negative"); // Validate input

        Camera.printInterval = (long) interval; // Convert to long and store
        return this;
    }

    /**
     * Computes the center point of a pixel in the view plane.
     */
    private Point getCenterOfPixel(int j, int i, Point pointCenter, double rX, double rY) {
        Point center = pointCenter; // Start from the center of the view plane

        // Calculate horizontal offset (x-axis) from center based on pixel column j
        double xJ = (j - (nX - 1) / 2d) * rX;

        // Calculate vertical offset (y-axis) from center based on pixel row i
        double yI = -(i - (nY - 1) / 2d) * rY;

        // Move horizontally if needed
        if (!Util.isZero(xJ))
            center = center.add(vRight.scale(xJ));

        // Move vertically if needed
        if (!Util.isZero(yI))
            center = center.add(vUp.scale(yI));

        return center; // Return the final position as pixel center
    }


    /**
     * Default constructor.
     */
    public Camera() {
    }

    /**
     * Constructor with position and orientation vectors.
     *
     * @param point   Camera position
     * @param vector  Forward direction
     * @param vector1 Up direction
     */
    public Camera(Point point, Vector vector, Vector vector1) {
        // Empty per your original code
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
        Point pointCenter = p0.add(vT0.scale(distance)); // Calculate center of view plane
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

    private Color castRayAA(int j, int i, Point pointCenter, double rX, double rY) {
        Point pixelCenter = pointCenter; // Start from the center of the view plane

        double xJ = (j - (nX - 1) / 2d) * rX; // Calculate horizontal offset for pixel
        double yI = -(i - (nY - 1) / 2d) * rY; // Calculate vertical offset for pixel

        if (!Util.isZero(xJ)) pixelCenter = pixelCenter.add(vRight.scale(xJ)); // Shift horizontally if needed
        if (!Util.isZero(yI)) pixelCenter = pixelCenter.add(vUp.scale(yI)); // Shift vertically if needed

        BlackBoard blackboard = new BlackBoard(
                pixelCenter, // Pixel center
                vRight,      // Horizontal axis
                vUp,         // Vertical axis
                rX,          // Pixel width
                rY,          // Pixel height
                samplesPerDim, // Samples per dimension
                aaShape      // Sampling shape
        );

        var samplePoints = blackboard.generateSamplePoints(); // Generate all sample points in the pixel
        Color finalColor = Color.BLACK; // Initialize color accumulator

        for (Point sample : samplePoints) {
            Vector dir = sample.subtract(p0); // Create direction vector from camera to sample
            if (dir.lengthSquared() == 0) continue; // Skip if vector is zero (invalid ray)

            Ray ray = new Ray(p0, dir); // Construct ray toward sample point
            finalColor = finalColor.add(rayTracer.traceRay(ray)); // Accumulate color from ray
        }

        pixelManager.pixelDone(); // Mark pixel as completed
        return finalColor.reduce(samplePoints.size()); // Return averaged color
    }

    /**
     * Casts a ray through a pixel and returns the resulting color.
     */
    private Color castRay(int j, int i, Point pointCenter, double rX, double rY) {
        Ray ray = constructRay(this.imageWriter.nX(), this.imageWriter.nY(), j, i); // Build ray for pixel
        pixelManager.pixelDone(); // Mark pixel as completed
        return this.rayTracer.traceRay(ray); // Trace ray and return color
    }

    /**
     * Render image using multi-threading by parallel streaming
     *
     * @return the camera object itself
     */
    private Camera renderImageStream() {
        final double rX = width / nX; // Pixel width
        final double rY = height / nY; // Pixel height
        final Point pointCenter = p0.add(vT0.scale(distance)); // Calculate center of view plane

        IntStream.range(0, nY * nX).parallel().forEach(idx -> { // Parallel loop over all pixels
            int i = idx / nX; // Row index
            int j = idx % nX; // Column index

            Point center = getCenterOfPixel(j, i, pointCenter, rX, rY); // Compute pixel center

            Color color = useAdaptiveSuperSampling
                    ? castRayASS(center, vRight, vUp, rX, rY, adaptiveMaxDepth) // Use adaptive super sampling
                    : (useAntiAliasing
                    ? castRayAA(j, i, pointCenter, rX, rY) // Use anti-aliasing
                    : castRay(j, i, pointCenter, rX, rY)); // Use regular ray casting

            this.imageWriter.writePixel(j, i, color); // Write pixel color to image
        });
        return this;
    }

    private Color castRayASS(Point center, Vector vRight, Vector vUp, double width, double height, int depth) {
        if (depth == 0) {
            return rayTracer.traceRay(new Ray(p0, center.subtract(p0))); // Base case: shoot a ray to center
        }

        double halfW = width / 2; // Half width for subdivision
        double halfH = height / 2; // Half height for subdivision

        // Compute corner points of the pixel
        Point p1 = center.add(vRight.scale(-halfW)).add(vUp.scale(halfH));   // top-left
        Point p2 = center.add(vRight.scale(halfW)).add(vUp.scale(halfH));    // top-right
        Point p3 = center.add(vRight.scale(halfW)).add(vUp.scale(-halfH));   // bottom-right
        Point p4 = center.add(vRight.scale(-halfW)).add(vUp.scale(-halfH));  // bottom-left

        // Cast rays to the 4 corners and center
        Color c1 = rayTracer.traceRay(new Ray(p0, p1.subtract(p0)));
        Color c2 = rayTracer.traceRay(new Ray(p0, p2.subtract(p0)));
        Color c3 = rayTracer.traceRay(new Ray(p0, p3.subtract(p0)));
        Color c4 = rayTracer.traceRay(new Ray(p0, p4.subtract(p0)));
        Color centerColor = rayTracer.traceRay(new Ray(p0, center.subtract(p0)));

        // Calculate average color
        Color avg = c1.add(c2).add(c3).add(c4).add(centerColor).reduce(5);

        // Check if color variance is small enough
        if (ColorChecker.isBelowThreshold(List.of(c1, c2, c3, c4, centerColor), adaptiveThreshold)) {
            return avg; // Stop recursion and return average
        }

        // Subdivide and recurse into 4 quadrants
        Color cTL = castRayASS(p1.add(center.subtract(p1).scale(0.5)), vRight, vUp, halfW, halfH, depth - 1); // Top-left
        Color cTR = castRayASS(p2.add(center.subtract(p2).scale(0.5)), vRight, vUp, halfW, halfH, depth - 1); // Top-right
        Color cBR = castRayASS(p3.add(center.subtract(p3).scale(0.5)), vRight, vUp, halfW, halfH, depth - 1); // Bottom-right
        Color cBL = castRayASS(p4.add(center.subtract(p4).scale(0.5)), vRight, vUp, halfW, halfH, depth - 1); // Bottom-left

        return cTL.add(cTR).add(cBR).add(cBL).reduce(4); // Return average of 4 quadrant colors
    }


    /**
     * Render image without multi-threading
     *
     * @return the camera object itself
     */
    private Camera renderImageNoThreads() {
        final double rX = width / nX; // Width of each pixel
        final double rY = height / nY; // Height of each pixel
        final Point pointCenter = p0.add(vT0.scale(distance)); // Center of view plane

        // Loop over all rows and columns (pixels)
        for (int i = 0; i < this.imageWriter.nY(); i++) {
            for (int j = 0; j < this.imageWriter.nX(); j++) {
                Point center = getCenterOfPixel(j, i, pointCenter, rX, rY); // Compute pixel center
                Color color = useAdaptiveSuperSampling
                        ? castRayASS(center, vRight, vUp, rX, rY, adaptiveMaxDepth) // Use ASS
                        : (useAntiAliasing
                        ? castRayAA(j, i, pointCenter, rX, rY) // Use AA
                        : castRay(j, i, pointCenter, rX, rY)); // Regular casting
                this.imageWriter.writePixel(j, i, color); // Write pixel color
            }
        }
        return this;
    }

    /**
     * Render image using multi-threading by creating and running raw threads
     *
     * @return the camera object itself
     */
    private Camera renderImageRawThreads() {
        final double rX = width / nX; // Pixel width
        final double rY = height / nY; // Pixel height
        final Point pointCenter = p0.add(vT0.scale(distance)); // Center of view plane

        var threads = new LinkedList<Thread>(); // Thread list

        // Create a thread for each worker
        for (int t = 0; t < threadsCount; t++) {
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = pixelManager.nextPixel()) != null) { // Get next pixel
                    int j = pixel.col();
                    int i = pixel.row();
                    Point center = getCenterOfPixel(j, i, pointCenter, rX, rY); // Pixel center
                    Color color = useAdaptiveSuperSampling
                            ? castRayASS(center, vRight, vUp, rX, rY, adaptiveMaxDepth)
                            : (useAntiAliasing ? castRayAA(j, i, pointCenter, rX, rY) : castRay(j, i, pointCenter, rX, rY));
                    imageWriter.writePixel(j, i, color); // Write pixel color
                }
            }));
        }

        // Start all threads
        for (var thread : threads) thread.start();

        // Wait for all threads to finish
        try {
            for (var thread : threads) thread.join();
        } catch (InterruptedException ignored) {
        }

        return this;
    }

    /**
     * This function renders image's pixel color map from the scene
     * included in the ray tracer object
     *
     * @return the camera object itself
     */
    public Camera renderImage() {
        pixelManager = new PixelManager(nY, nX, printInterval); // Initialize pixel manager
        return switch (threadsCount) {
            case 0 -> renderImageNoThreads(); // Single-threaded rendering
            case -1 -> renderImageStream(); // Parallel stream
            default -> renderImageRawThreads(); // Raw threads
        };
    }

    /**
     * Draws a grid on the image.
     */
    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                if (i % interval == 0 || j % interval == 0) {
                    imageWriter.writePixel(j, i, color); // Draw grid line pixel
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
        private final Camera camera = new Camera(); // Internal camera object

        public Builder setLocation(Point p) {
            if (p == null) throw new IllegalArgumentException("Camera location cannot be null");
            camera.p0 = p; // Set camera position
            return this;
        }

        public Builder setDirection(Vector vTo, Vector vUp) {
            if (vTo == null || vUp == null)
                throw new IllegalArgumentException("Direction vectors cannot be null");
            if (alignZero(vTo.dotProduct(vUp)) != 0)
                throw new IllegalArgumentException("vTo and vUp must be orthogonal");

            camera.vT0 = vTo.normalize(); // Normalize view direction
            camera.vUp = vUp.normalize(); // Normalize up direction
            camera.vRight = camera.vT0.crossProduct(camera.vUp); // Compute right vector
            return this;
        }

        public Builder setDirection(Point target, Vector approxUp) {
            if (target == null || approxUp == null)
                throw new IllegalArgumentException("Target and approxUp cannot be null");
            if (camera.p0 == null)
                throw new IllegalArgumentException("Camera location must be set before direction");

            Vector vTo = target.subtract(camera.p0).normalize(); // Compute vTo
            Vector vRight = vTo.crossProduct(approxUp).normalize(); // Compute vRight
            Vector vUp = vRight.crossProduct(vTo).normalize(); // Compute vUp

            camera.vT0 = vTo;
            camera.vRight = vRight;
            camera.vUp = vUp;
            return this;
        }

        public Builder setDirection(Point target) {
            return setDirection(target, new Vector(0, 1, 0)); // Use default up vector
        }

        public Builder setVpSize(double height, double width) {
            if (alignZero(width) <= 0 || alignZero(height) <= 0)
                throw new IllegalArgumentException("View plane size must be positive");
            camera.height = height; // Set view plane height
            camera.width = width;   // Set view plane width
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
            camera.nX = nX; // Set horizontal resolution
            camera.nY = nY; // Set vertical resolution
            return this;
        }

        public Builder setRayTracer(Scene scene, RayTracerType type) {
            if (type == RayTracerType.SIMPLE) {
                camera.rayTracer = new SimpleRayTracer(scene); // Set simple ray tracer
            } else {
                camera.rayTracer = null;
            }
            return this;
        }

        public Camera build() {
            if (camera.p0 == null)
                throw new MissingResourceException(MISSING_DATA_MSG, CAMERA_CLASS_NAME, "position");

            if (camera.vT0 == null || camera.vUp == null)
                throw new MissingResourceException(MISSING_DATA_MSG, CAMERA_CLASS_NAME, "vTO or vUP");

            if (camera.vRight == null) {
                camera.vT0 = camera.vT0.normalize();
                camera.vUp = camera.vUp.normalize();
                camera.vRight = camera.vT0.crossProduct(camera.vUp).normalize(); // Calculate missing right vector
            }

            if (alignZero(camera.width) <= 0 || alignZero(camera.height) <= 0)
                throw new IllegalStateException("View plane size must be set");
            if (alignZero(camera.distance) <= 0)
                throw new IllegalStateException("View plane distance must be set");
            if (camera.nX <= 0 || camera.nY <= 0)
                throw new IllegalStateException("Resolution must be positive");

            camera.imageWriter = new ImageWriter(camera.nX, camera.nY); // Init image writer

            if (camera.rayTracer == null) {
                camera.rayTracer = new SimpleRayTracer(null); // Fallback to empty scene
            }

            // Normalize vectors to ensure consistency
            camera.vT0 = camera.vT0.normalize();
            camera.vUp = camera.vUp.normalize();
            camera.vRight = camera.vRight.normalize();

            try {
                return (Camera) camera.clone(); // Return copy for safety
            } catch (CloneNotSupportedException e) {
                return camera; // Fallback if cloning fails
            }
        }
    }
}