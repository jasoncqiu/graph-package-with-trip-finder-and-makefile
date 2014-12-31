package make;

import java.util.ArrayList;
import java.util.HashSet;

/** An object containing a rule for makefile.
 * @author Jason Qiu */
public class Rule {
    /** My target. */
    private String target;
    /** My commands. */
    private ArrayList<String> commandSet;
    /** My prerequisites. */
    private HashSet<String> prereqs;

    /** Construct a new Rule using S, COMMANDS, and PREREQ. */
    public Rule(String s, ArrayList<String> commands, HashSet<String> prereq) {
        target = s;
        commandSet = commands;
        prereqs = prereq;
    }

    /** Returns target. */
    public String getTarget() {
        return target;
    }

    /** Returns commandSet. */
    public ArrayList<String> getCommandSet() {
        return commandSet;
    }

    /** Returns prereqs. */
    public HashSet<String> getPrereqs() {
        return prereqs;
    }

    /** Add a command CMD. */
    public void addCommand(String cmd) {
        commandSet.add(cmd);
    }

    /** Add a prereq PREREQ. */
    public void addPrereq(String prereq) {
        prereqs.add(prereq);
    }
}
