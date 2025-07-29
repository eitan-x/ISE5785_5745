package renderer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import scene.Scene;

/**
 * Test class for verifying the AntiAliasing feature in the renderer.
 *
 * @author Eitan Lafair
 */
public class AntiAliasingFeatureTest {

    /**
     * Main test method that builds a scene with at least 10 geometries and 3 light sources,
     * renders the scene twice (with and without AntiAliasing),
     *
     */
    @Test
    public void antiAliasingOnOffRenderTest() {
        // Create a scene for testing AntiAliasing effects
        Scene scene = createTestScene();

        // Build a camera with specific parameters:
        // location, view direction, viewport size, resolution, and ray tracer type
        Camera.Builder baseBuilder = Camera.getBuilder()
                .setLocation(new Point(0, 0, 150))               // Camera position in 3D space
                .setDirection(new Point(0, 0, -50), Vector.AXIS_Y) // Camera looks towards negative Z-axis, with Y as the up axis
                .setVpDistance(150)                              // Distance from camera to viewport (view plane)
                .setVpSize(150, 150)                            // Width and height of the viewport (view plane)
                .setResolution(670, 670)                        // Number of pixels horizontally and vertically
                .setRayTracer(scene, RayTracerType.SIMPLE);    // Use simple ray tracer with the created scene

        // Create a camera instance with AntiAliasing disabled
        Camera cameraNoAA = baseBuilder.build()
                .enableAntiAliasing(false);                     // Turn AntiAliasing off

        // Measure time before rendering without AA
        long startNoAA = System.currentTimeMillis();
        cameraNoAA.renderImage();                           // Render the image without AntiAliasing
        long endNoAA = System.currentTimeMillis();          // Measure time after rendering

        // Save the rendered image to a file named "AntiAliasing_OFF_1"
        cameraNoAA.writeToImage("AntiAliasing_OFF_1");

        // Print the duration of rendering without AntiAliasing
        System.out.println("Render time without AA: " + (endNoAA - startNoAA) + " ms");

        // Create a camera instance with AntiAliasing enabled
        Camera cameraWithAA = baseBuilder.build()
                .enableAntiAliasing(true)                        // Turn AntiAliasing on
                .setAntiAliasingSamples(9)                      // Set the number of AA samples per axis (9x9 = 81 total)
                .setAntiAliasingShape(BlackBoard.Shape.CIRCLE);// Use a circular sampling shape for AA

        // Measure time before rendering with AA
        long startAA = System.currentTimeMillis();
        cameraWithAA.renderImage();                          // Render the image with AntiAliasing
        long endAA = System.currentTimeMillis();             // Measure time after rendering

        // Save the rendered image to a file named "AntiAliasing_ON_1"
        cameraWithAA.writeToImage("AntiAliasing_ON_1");

        // Print the duration of rendering with AntiAliasing
        System.out.println("Render time with AA: " + (endAA - startAA) + " ms");

        // Basic assertions to ensure cameras were created successfully (not null)
        assertNotNull(cameraNoAA);
        assertNotNull(cameraWithAA);
    }

    /**
     * Helper method to create a complex test scene
     *
     * @return A Scene object configured with geometries and lighting for testing.
     */
    private Scene createTestScene() {
        Scene scene = new Scene("AntiAliasingTestScene");                 // Create a new scene with a name
        scene.setBackground(new Color(20, 20, 20));                      // Set a dark gray background color
        scene.setAmbientLight(new AmbientLight(new Color(30, 30, 30)));  // Set low-level ambient light for basic illumination

        // Add 5 spheres with different positions, sizes, colors, and materials
        for (int i = 0; i < 5; i++) {
            scene.geometries.add(
                    new Sphere(new Point(i * 20 - 40, 0, -100), 10)           // Position spheres spaced along X axis, all at Z = -100
                            .setEmission(new Color(50 + 40 * i, 20 + 30 * i, 20 + 25 * i)) // Gradually change color per sphere
                            .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(100)) // Set material properties: diffuse, specular, shininess
            );
        }

        // Add 3 triangles with sharp edges placed in different areas to highlight AA effect on edges
        scene.geometries.add(
                new Triangle(new Point(-30, -30, -90), new Point(-10, -10, -90), new Point(-30, 10, -90))
                        .setEmission(new Color(255, 0, 0))                      // Red colored triangle
                        .setMaterial(new Material().setKd(0.6).setKs(0.4).setShininess(50)) // Material with moderate diffuse and specular reflection
        );
        scene.geometries.add(
                new Triangle(new Point(10, -30, -90), new Point(30, -10, -90), new Point(30, 10, -90))
                        .setEmission(new Color(0, 255, 0))                      // Green colored triangle
                        .setMaterial(new Material().setKd(0.6).setKs(0.4).setShininess(50))
        );
        scene.geometries.add(
                new Triangle(new Point(-10, 10, -90), new Point(10, 10, -90), new Point(0, 30, -90))
                        .setEmission(new Color(0, 0, 255))                      // Blue colored triangle
                        .setMaterial(new Material().setKd(0.6).setKs(0.4).setShininess(50))
        );

        // Add 2 small polygons near the spheres, creating more sharp edges for AA testing
        scene.geometries.add(
                new Polygon(
                        new Point(-50, 30, -90),
                        new Point(-30, 30, -90),
                        new Point(-30, 50, -90),
                        new Point(-50, 50, -90)
                )
                        .setEmission(new Color(255, 255, 0))                     // Yellow colored polygon
                        .setMaterial(new Material().setKd(0.7).setKs(0.3).setShininess(10)) // Material with higher diffuse reflection, low shininess
        );

        scene.geometries.add(
                new Polygon(
                        new Point(30, 30, -90),
                        new Point(50, 30, -90),
                        new Point(50, 50, -90),
                        new Point(30, 50, -90)
                )
                        .setEmission(new Color(0, 255, 255))                     // Cyan colored polygon
                        .setMaterial(new Material().setKd(0.7).setKs(0.3).setShininess(10))
        );


        //  Point light with mild attenuation
        scene.lights.add(new PointLight(new Color(150, 150, 150), new Point(40, 50, 100))
                .setKl(0.01).setKQ(0.001));
        // Spot light pointing downward and forward
        scene.lights.add(new SpotLight(new Color(100, 100, 150), new Point(-40, 50, 100),
                new Vector(0, -1, -2))
                .setKl(0.01).setKq(0.001));
        //  Directional light simulating distant light source
        scene.lights.add(new DirectionalLight(new Color(120, 120, 120), new Vector(1, -1, -1)));

        return scene;    // Return the fully constructed scene
    }
}
