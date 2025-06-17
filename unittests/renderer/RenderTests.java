package renderer;

import static java.awt.Color.*;

import lighting.Material;
import org.junit.jupiter.api.Test;

import geometries.Sphere;
import geometries.Triangle;
import lighting.AmbientLight;
import primitives.*;
import scene.Scene;

/**
 * Test rendering a basic image
 * @author Dan
 */
class RenderTests {
   /** Default constructor to satisfy JavaDoc generator */
   RenderTests() { /* to satisfy JavaDoc generator */ }

   /** Camera builder of the tests */
   private final Camera.Builder camera = Camera.getBuilder() //
      .setLocation(Point.ZERO).setDirection(new Point(0, 0, -1), Vector.AXIS_Y) //
      .setVpDistance(100) //
      .setVpSize(500, 500);

   /**
    * Produce a scene with basic 3D model and render it into a png image with a
    * grid
    */
   @Test
   void renderTwoColorTest() {
      Scene scene = new Scene("Two color").setBackground(new Color(75, 127, 90))
         .setAmbientLight(new AmbientLight(new Color(255, 191, 191)));
      scene.geometries //
         .add(// center
              new Sphere(new Point(0, 0, -100), 50d),
              // up left
              new Triangle(new Point(-100, 0, -100), new Point(0, 100, -100), new Point(-100, 100, -100)),
              // down left
              new Triangle(new Point(-100, 0, -100), new Point(0, -100, -100), new Point(-100, -100, -100)),
              // down right
              new Triangle(new Point(100, 0, -100), new Point(0, -100, -100), new Point(100, -100, -100)));

      camera //
         .setRayTracer(scene, RayTracerType.SIMPLE) //
         .setResolution(1000, 1000) //
         .build() //
         .renderImage() //
         .printGrid(100, new Color(YELLOW)) //
         .writeToImage("Two color render test");
   }

   // For stage 6 - please disregard in stage 5
   /**
    * Produce a scene with basic 3D model - including individual lights of the
    * bodies and render it into a png image with a grid
    */
   @Test
   void renderMultiColorTest() {
      Scene scene = new Scene("Multi color").setAmbientLight(new AmbientLight(new Color(51, 51, 51)));
      scene.geometries //
         .add(// center
              new Sphere(new Point(0, 0, -100), 50),
              // up left
              new Triangle(new Point(-100, 0, -100), new Point(0, 100, -100), new Point(-100, 100, -100)) //
                 .setEmission(new Color(GREEN)),
              // down left
              new Triangle(new Point(-100, 0, -100), new Point(0, -100, -100), new Point(-100, -100, -100)) //
                 .setEmission(new Color(RED)),
              // down right
              new Triangle(new Point(100, 0, -100), new Point(0, -100, -100), new Point(100, -100, -100)) //
                 .setEmission(new Color(BLUE)));

      camera //
         .setRayTracer(scene, RayTracerType.SIMPLE) //
         .setResolution(1000, 1000) //
         .build() //
         .renderImage() //
         .printGrid(100, new Color(WHITE)) //
         .writeToImage("color render test");
   }


   @Test
   void renderAmbientMaterialTest() {
      // Create a new scene with a white ambient light and green background
      Scene scene = new Scene("Ambient material test")
              .setBackground(new Color(75, 127, 90)) // Optional background color
              .setAmbientLight(new AmbientLight(new Color(WHITE))); // Set ambient light to white

      // Add geometries with material containing ambient reflection coefficients (KA)
      scene.geometries
              .add(
                      // Center sphere with KA = (0.4, 0.4, 0.4)
                      new Sphere(new Point(0, 0, -100), 50d)
                              .setMaterial(new Material().setkA(new Double3(0.4))),

                      // Top-left triangle (formerly green) with KA = (0, 0.8, 0)
                      new Triangle(new Point(-100, 0, -100), new Point(0, 100, -100), new Point(-100, 100, -100))
                              .setMaterial(new Material().setkA(new Double3(0, 0.8, 0))),

                      // Bottom-left triangle (formerly red) with KA = (0.8, 0, 0)
                      new Triangle(new Point(-100, 0, -100), new Point(0, -100, -100), new Point(-100, -100, -100))
                              .setMaterial(new Material().setkA(new Double3(0.8, 0, 0))),

                      // Bottom-right triangle (formerly blue) with KA = (0, 0, 0.8)
                      new Triangle(new Point(100, 0, -100), new Point(0, -100, -100), new Point(100, -100, -100))
                              .setMaterial(new Material().setkA(new Double3(0, 0, 0.8)))
              );

      // Build the camera and render the image with a white grid
      camera
              .setRayTracer(scene, RayTracerType.SIMPLE)
              .setResolution(1000, 1000)
              .build()
              .renderImage()
              .printGrid(100, new Color(WHITE))
              .writeToImage("ambient material test"); // New output file name
   }





}
