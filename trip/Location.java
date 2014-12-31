package trip;

import graph.Weightable;

/** A location object.
 * @author Jason Qiu */
public class Location implements Weightable {
    /** My place. */
    private String place;
    /** My x coordinate. */
    private double _x;
    /** My y coordinate. */
    private double _y;
    /** My weight. */
    private double _w;

    /** Construct a new location using P, X, and Y. */
    public Location(String p, double x, double y) {
        place = p;
        _x = x;
        _y = y;
    }

    /** Returns my place. */
    public String getPlace() {
        return place;
    }

    /** Returns my x coordinate. */
    public double getX() {
        return _x;
    }

    /** Returns my y coordinate. */
    public double getY() {
        return _y;
    }

    @Override
    public double weight() {
        return _w;
    }

    @Override
    public void setWeight(double w) {
        _w = w;
    }
}
