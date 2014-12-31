package trip;

import graph.Weighted;

/** A distance object.
 * @author Jason Qiu */
public class Distance implements Weighted {
    /** One of my locations. */
    private String place;
    /** Road name. */
    private String road;
    /** Distance of my road. */
    private double dist;
    /** My direction. */
    private String direction;
    /** My other location. */
    private String otherPlace;

    /** Construct a new Distance using P, R,  D, DIR, and P2. */
    public Distance(String p, String r, double d, String dir, String p2) {
        place = p;
        road = r;
        dist = d;
        direction = dir;
        otherPlace = p2;
    }

    /** Returns my place. */
    public String getPlace() {
        return place;
    }

    /** Returns my other place. */
    public String getOtherPlace() {
        return otherPlace;
    }

    /** Returns my road. */
    public String getRoad() {
        return road;
    }

    /** Returns my distance. */
    public double getDist() {
        return dist;
    }

    /** Returns my direction. */
    public String getDirection() {
        return direction;
    }

    @Override
    public double weight() {
        return dist;
    }
}
