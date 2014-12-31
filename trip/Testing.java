package trip;

/* You MAY add public @Test methods to this class.  You may also add
 * additional public classes containing "Testing" in their name. These
 * may not be part of your trip package per se (that is, it must be
 * possible to remove them and still have your package work). */

import org.junit.Test;
import ucb.junit.textui;
import static org.junit.Assert.*;

/** Unit tests for the trip package. */
public class Testing {

    /** Run all JUnit tests in the graph package. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(trip.Testing.class));
    }

    /** Only Location and Distance are tested here because Main has only one
     *  public method, which is tested through black-box testing. */
    @Test
    public void testLocation() {
        Location loc = new Location("place", 1, 2);
        assertEquals("place error", "place", loc.getPlace());
        assertTrue("x coordinate error", 1 == loc.getX());
        assertTrue("y coordinate error", 2 == loc.getY());
        loc.setWeight(10);
        assertTrue("weight error", 10 == loc.weight());
    }

    @Test
    public void testDistance() {
        Distance dist = new Distance("place", "road", 10.0, "NS",
                "otherPlace");
        assertEquals("place error", "place", dist.getPlace());
        assertEquals("other place error", "otherPlace", dist.getOtherPlace());
        assertEquals("road error", "road", dist.getRoad());
        assertEquals("direction error", "NS", dist.getDirection());
        assertTrue("distance error", 10.0 == dist.getDist());
        assertTrue("weight error", 10.0 == dist.weight());
    }
}
