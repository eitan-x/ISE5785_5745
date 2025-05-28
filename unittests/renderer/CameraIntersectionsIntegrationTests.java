    package renderer;

    import geometries.Intersectable;
    import geometries.Plane;
    import geometries.Triangle;
    import org.junit.jupiter.api.Test;
    import primitives.*;
    import geometries.Sphere;

    import java.util.List;

    import static org.junit.jupiter.api.Assertions.*;

    /**
     * Testing constructRay(int, int, int, int)
     * and findIntersections(Ray) of Sphere, Plane, and Triangle.
     * @author Eitan lafair
     */
    public class CameraIntersectionsIntegrationTests {
        public static final Point ZERO_POINT = new Point(0,0,0);
        private int countIntersectionsCameraGeometry(Camera camera, int Nx, int Ny, Intersectable geometry){
            int count = 0;
            List<Point> intersections;

            for (int i = 0; i < Nx; i++) {
                for (int j = 0; j < Ny; j++) {
                    intersections = geometry.findIntersections(camera.constructRay(Nx, Ny, j, i));
                    count += intersections == null ? 0 : intersections.size();
                }
            }
            return count; //Return the number of points of intersection between the geometries and a ray from the camera.
        }

        @Test
        public void testRayIntersectsSphere() {
            Camera camera1 = Camera.getBuilder().setLocation(Point.ZERO)
                    .setDirection(new Vector(0,0,-1), new Vector(0,-1,0))
                    .setVpSize(3d,3d)
                    .setVpDistance(1d)
                    .build();

            Camera camera2 = Camera.getBuilder()
                    .setLocation(new Point(0, 0, 0.5))
                    .setDirection(new Vector(0, 0, -1), new Vector(0, -1, 0))
                    .setVpSize(3d, 3d)
                    .setVpDistance(1d)
                    .build();


            Sphere sphere1 = new Sphere(new Point(0,0,0), 1);
            assertEquals(1, countIntersectionsCameraGeometry(camera1, 1, 1, sphere1));


            //TC02: Sphere r=2.5 (18 intersections)
            assertEquals(18, countIntersectionsCameraGeometry(camera2, 3, 3,
                    new Sphere(new Point(0,0,-2.5),2.5)),"Bad number of intersections");

            //TC03: Sphere r=2 (10 intersections)
            assertEquals(10, countIntersectionsCameraGeometry(camera2, 3, 3,
                    new Sphere(new Point(0,0,-2),2d)),"Bad number of intersections");

            //TC04: Sphere r=4 (9 intersections)
            assertEquals(9, countIntersectionsCameraGeometry(camera2,3,3,
                    new Sphere(new Point(0, 0, 1),4d)), "Bad number of intersections");

            //TC05: Sphere r=0.5 (0 intersections)
            assertEquals(0, countIntersectionsCameraGeometry(camera1,3,3,
                    new Sphere(new Point(0, 0, 1),0.5)), "Bad number of intersections");

        }

        @Test
        public void testRayIntersectsPlane() {
            Camera cmaera3 = Camera.getBuilder()
                    .setLocation(new Point(0, 0, 0.5))
                    .setDirection(new Vector(0, 0, 1), new Vector(0, -1, 0))
                    .setVpSize(3d, 3d)
                    .setVpDistance(1d)
                    .build();


            //TC01: The plane parallel to the View Plane (9 intersections)
            assertEquals(countIntersectionsCameraGeometry(cmaera3,3,3 ,new Plane(new Point(0, 0, 5),
                    new Vector(0, 0, 1))), 9,"Bad number of intersections");


            //TC02: Diagonal plane to the View Plane (9 intersections)
            assertEquals(countIntersectionsCameraGeometry(cmaera3,3,3, new Plane(new Point(0, 0, 5),
                    new Vector(0, -1, 2))), 9,"Bad number of intersections");

            //TC03: Diagonal plane with an obtuse angle to the View Plane (6 intersections)
            assertEquals(6, countIntersectionsCameraGeometry(cmaera3, 3,3, new Plane(new Point(0,0,2),
                    new Vector(1,1,1))), "Bad number of intersections");

            // TC04:The plane behind the view plane (0 intersections)
            assertEquals(0, countIntersectionsCameraGeometry(cmaera3, 3, 3, new Plane(new Point(0, 0, -4),
                    new Vector(0,0,1))), "Bad number of intersections");



        }


        @Test
        public void testRayIntersectsTriangle() {
            Camera cam = Camera.getBuilder()
                    .setLocation(ZERO_POINT)
                    .setDirection(new Vector(0, 0, -1), new Vector(0, -1, 0))
                    .setVpSize(3d, 3d)
                    .setVpDistance(1d)
                    .build();



            //TC01: Small triangle (1 intersection)
            assertEquals(1, countIntersectionsCameraGeometry(cam,3,3,new Triangle(new Point(1, -1, -2),
                    new Point(-1, -1, -2), new Point(0, 1, -2))),"Bad number of intersections");

            //TC02: Large triangle (2 intersection)
            assertEquals(2, countIntersectionsCameraGeometry(cam,3,3,new Triangle(new Point(1, -1, -2),
                    new Point(-1, -1, -2), new Point(0, 20, -2))),"Bad number of intersections");

        }




    }