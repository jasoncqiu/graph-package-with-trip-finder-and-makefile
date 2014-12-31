package make;

/* You MAY add public @Test methods to this class.  You may also add
 * additional public classes containing "Testing" in their name. These
 * may not be part of your make package per se (that is, it must be
 * possible to remove them and still have your package work). */

import org.junit.Test;

import ucb.junit.textui;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashSet;

/** Unit tests for the make package. */
public class Testing {

    /** Run all JUnit tests in the make package. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(make.Testing.class));
    }

    /** Only rule is tested here because Main has only one public method,
     * which is tested through black-box testing. */
    @Test
    public void testRule() {
        Rule rule = new Rule("target", new ArrayList<String>(),
                new HashSet<String>());
        rule.addCommand("command1");
        rule.addCommand("command2");
        rule.addPrereq("prereq1");
        rule.addPrereq("prereq2");
        assertEquals("target error", "target", rule.getTarget());
        assertEquals("command set error", "command1",
                rule.getCommandSet().get(0));
        assertEquals("command set error", "command2",
                rule.getCommandSet().get(1));
        assertTrue("prerequisite error",
                rule.getPrereqs().contains("prereq1"));
        assertTrue("prerequisite error",
                rule.getPrereqs().contains("prereq2"));
    }
}
