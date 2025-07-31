package renderer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import geometries.*;
import lighting.*;
import primitives.*;
import scene.Scene;

public class AdaptiveSuperSamplingTest {

    private static final int IMAGE_SIZE = 800;  // Image resolution (800x800), high enough for noticeable render time

    /**
     * Creates a complex scene with many geometries and multiple light sources.
     */
    private Scene createComplexScene() {
        Scene scene = new Scene("MP2_Complex_Scene");

        // Set background color and ambient light
        scene.setBackground(new Color(20, 20, 20));
        scene.setAmbientLight(new AmbientLight(new Color(15, 15, 15)));


        // Add ground plane for all objects to stand on
        scene.geometries.add(
                new Polygon(
                        new Point(-500, -10, -300),
                        new Point(500, -10, -300),
                        new Point(500, -10, 100),
                        new Point(-500, -10, 100))
                        .setEmission(new Color(60, 60, 60)) // Neutral gray surface
                        .setMaterial(new Material()
                                .setKd(0.8)       // Strong diffuse reflection
                                .setKs(0.2)       // Low specular highlight
                                .setShininess(20)) // Soft lighting
        );

        // Add 30 colored spheres to the scene
        for (int i = 0; i < 10; i++) {
            scene.geometries.add(
                    new Sphere(new Point(i * 15 - 70, 0, -150), 10)
                            .setEmission(new Color(20 + i * 15, 40, 40 + i * 10))  // Gradient coloring
                            .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(80))
            );
        }

        // Add 30 triangles to form a pattern
        for (int i = 0; i < 10; i++) {
            scene.geometries.add(
                    new Triangle(
                            new Point(-50 + i * 10, 10, -120),
                            new Point(-40 + i * 10, 30, -120),
                            new Point(-30 + i * 10, 10, -120))
                            .setEmission(new Color(30 + i * 20, 60, 100))
                            .setMaterial(new Material().setKd(0.6).setKs(0.3).setShininess(40))
            );
        }

        // Add 30 quadrilateral polygons
        for (int i = 0; i < 10; i++) {
            scene.geometries.add(
                    new Polygon(
                            new Point(-70 + i * 10, 50, -130),
                            new Point(-60 + i * 10, 60, -130),
                            new Point(-50 + i * 10, 60, -130),
                            new Point(-50 + i * 10, 50, -130))
                            .setEmission(new Color(50, 50 + i * 10, 70))
                            .setMaterial(new Material().setKd(0.7).setKs(0.3).setShininess(25))
            );
        }



        // Add 5 different light sources
        scene.lights.add(new PointLight(new Color(255, 220, 200), new Point(100, 100, 100)).setKl(0.01).setKQ(0.001));
        scene.lights.add(new SpotLight(new Color(200, 200, 255), new Point(-100, 100, 50), new Vector(1, -1, -1)).setKl(0.01).setKQ(0.002));
        scene.lights.add(new DirectionalLight(new Color(100, 100, 100), new Vector(-1, -1, -1)));
        scene.lights.add(new PointLight(new Color(255, 255, 255), new Point(0, -50, 100)).setKl(0.01).setKQ(0.001));
        scene.lights.add(new SpotLight(new Color(255, 180, 180), new Point(50, 50, 150), new Vector(0, -1, -2)).setKl(0.01).setKQ(0.002));

        return scene;
    }

    /**
     * Creates a camera builder with default position, direction and settings.
     */
    private Camera.Builder baseCameraBuilder(Scene scene) {
        return Camera.getBuilder()
                .setLocation(new Point(0, 0, 200))                                // Camera position
                .setDirection(new Point(0, 0, -100), Vector.AXIS_Y)              // Camera direction (look forward)
                .setVpDistance(200)                                              // View plane distance
                .setVpSize(200, 200)                                             // View plane size
                .setResolution(IMAGE_SIZE, IMAGE_SIZE)                           // Image resolution
                .setRayTracer(scene, RayTracerType.SIMPLE);                      // Use simple ray tracer
    }

    // === TEST CASES ===

    /**
     * Test 1: ASS disabled, multithreading disabled (single-threaded rendering)
     */
    @Test
    public void test_ASS_Off_MT_Off() {
        Scene scene = createComplexScene();
        Camera camera = baseCameraBuilder(scene).build()
                .enableAdaptiveSuperSampling(false)
                .enableAntiAliasing(true)
                .setMultithreading(0);  // single-threaded mode

        long start = System.currentTimeMillis();
        camera.renderImage();  // Render image
        long end = System.currentTimeMillis();

        camera.writeToImage("ASS_Off_MT_Off.png");  // Save image
        System.out.println("Render time ASS OFF MT OFF: " + (end - start) + " ms");

        assertNotNull(camera);  // Ensure camera is initialized
    }

    /**
     * Test 2: ASS disabled, multithreading enabled (auto-thread count)
     */
    @Test
    public void test_ASS_Off_MT_On() {
        Scene scene = createComplexScene();
        Camera camera = baseCameraBuilder(scene).build()
                .enableAdaptiveSuperSampling(false)
                .enableAntiAliasing(true)
                .setMultithreading(-2);  // auto-thread mode

        long start = System.currentTimeMillis();
        camera.renderImage();
        long end = System.currentTimeMillis();

        camera.writeToImage("ASS_Off_MT_On.png");
        System.out.println("Render time ASS OFF MT ON: " + (end - start) + " ms");

        assertNotNull(camera);
    }

    /**
     * Test 3: ASS enabled, multithreading disabled
     */
    @Test
    public void test_ASS_On_MT_Off() {
        Scene scene = createComplexScene();
        Camera camera = baseCameraBuilder(scene).build()
                .enableAdaptiveSuperSampling(true)      // Enable adaptive sampling
                .setAdaptiveMaxDepth(3)                 // Max recursion depth
                .setAdaptiveThreshold(0.15)             // Color difference threshold
                .enableAntiAliasing(true)
                .setMultithreading(0);                  // Single-threaded

        long start = System.currentTimeMillis();
        camera.renderImage();
        long end = System.currentTimeMillis();

        camera.writeToImage("ASS_On_MT_Off.png");
        System.out.println("Render time ASS ON MT OFF: " + (end - start) + " ms");

        assertNotNull(camera);
    }

    /**
     * Test 4: ASS enabled, multithreading enabled
     */
    @Test
    public void test_ASS_On_MT_On() {
        Scene scene = createComplexScene();
        Camera camera = baseCameraBuilder(scene).build()
                .enableAdaptiveSuperSampling(true)
                .setAdaptiveMaxDepth(3)
                .setAdaptiveThreshold(0.15)
                .enableAntiAliasing(true)
                .setMultithreading(-2);  // auto-thread mode

        long start = System.currentTimeMillis();
        camera.renderImage();
        long end = System.currentTimeMillis();

        camera.writeToImage("ASS_On_MT_On.png");
        System.out.println("Render time ASS ON MT ON: " + (end - start) + " ms");

        assertNotNull(camera);
    }
}
