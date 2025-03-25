package primitives;

public class Point {
    /**

     */
    protected Double3 xyz;

    public Point(Double3 add) {
    }


    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        return (obj instanceof Point other)
        && this.xyz.equals(other.xyz);
    }

    @Override
    public final String toString() {return "Point ";}

    /**
     *
     * @return
     */

    public final double lengthSquared() {return ;}


    /**
     *
     * @return
     */
    public final double lengthSquared() {return ;}

    /**
     *
     * @param v
     * @return
     */
    public final Point add(Vector v ) {return new Point(this.xyz.add(v.xyz)) ;}

    /**
     *
     * @return
     */
    public final Vector subtract(Point P){return ;}

    /**
     *
     * @return
     */

    public final double distance(Point ) {return ;}

    /**
     *
     * @return
     */

    public final double distanceSquared(Point) {return ;}


}
