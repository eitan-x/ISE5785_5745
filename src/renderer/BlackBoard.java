package renderer;

import primitives.Point;
import primitives.Vector;

import java.util.ArrayList;
import java.util.List;

import static primitives.Util.isZero;

/**
 * class represents a 2D sampling area in space,
 *
 * @author Eitan Lafair
 */
public class BlackBoard {

    /**
     * Enum representing the sampling shape: either full rectangle or inner circle (ellipse).
     */
    public enum Shape {
        RECTANGLE, CIRCLE
    }

    // Center point of the blackboard
    private final Point center;

    // Unit vectors defining the local axes of the blackboard (right = x-axis, up = y-axis)
    private final Vector right;
    private final Vector up;

    // Dimensions of the sampling area
    private final double width;
    private final double height;

    // Number of samples per axis (total samples = samplesPerDim^2)
    private final int samplesPerDim;

    // Shape of sampling area (RECTANGLE or CIRCLE)
    private final Shape shape;

    /**
     * Constructs a new {@code BlackBoard} sampling area.
     *
     * @param center        The center of the blackboard
     * @param right         The local horizontal (X-axis) direction vector
     * @param up            The local vertical (Y-axis) direction vector
     * @param width         The width of the sampling area
     * @param height        The height of the sampling area
     * @param samplesPerDim Number of samples along each axis (total = samples^2)
     * @param shape         The sampling shape: RECTANGLE or CIRCLE
     */
    public BlackBoard(Point center, Vector right, Vector up, double width, double height,
                      int samplesPerDim, Shape shape) {
        this.center = center;
        this.right = right.normalize();     // Normalize to ensure unit vector
        this.up = up.normalize();           // Normalize to ensure unit vector
        this.width = width;
        this.height = height;
        this.samplesPerDim = samplesPerDim;
        this.shape = shape;
    }

    /**
     * Generates a list of sample points within the blackboard area
     * according to the specified shape and sampling density.
     *
     * @return A list of sampled {@code Point} objects within the area
     */
    public List<Point> generateSamplePoints() {
        List<Point> samples = new ArrayList<>();

        // Calculate cell width/height for each sub-region in the grid
        double cellWidth = width / samplesPerDim;
        double cellHeight = height / samplesPerDim;

        // Half dimensions used for centered offset
        double halfWidth = width / 2.0;
        double halfHeight = height / 2.0;

        // Loop through the grid of sub-cells
        for (int i = 0; i < samplesPerDim; i++) {
            for (int j = 0; j < samplesPerDim; j++) {

                // Compute local offset u (horizontal) and v (vertical)
                // Center the sample within each cell
                double u = (i + 0.5) * cellWidth - halfWidth;
                double v = (j + 0.5) * cellHeight - halfHeight;

                // If shape is CIRCLE, discard sample points outside ellipse
                if (shape == Shape.CIRCLE &&
                        (u * u) / (halfWidth * halfWidth) + (v * v) / (halfHeight * halfHeight) > 1.0)
                    continue;

                // Scale direction vectors by u and v, skipping zeros for efficiency
                Vector rightScaled = isZero(u) ? null : right.scale(u);
                Vector upScaled = isZero(v) ? null : up.scale(v);

                // Start from center and apply scaled offsets
                Point sample = center;
                if (rightScaled != null) sample = sample.add(rightScaled);
                if (upScaled != null) sample = sample.add(upScaled);

                // Add the computed sample to the list
                samples.add(sample);
            }
        }

        return samples;
    }

    /**
     * Returns the total number of potential samples (including those skipped by shape filtering).
     *
     * @return Total number of samples (samplesPerDim squared)
     */
    public int getSampleCount() {
        return samplesPerDim * samplesPerDim;
    }
}
