package make;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import graph.Graph;
import graph.NoLabel;
import graph.DirectedGraph;
import graph.Traversal;

/** Initial class for the 'make' program.
 *  @author Jason Qiu
 */
public final class Main {

    /** Entry point for the CS61B make program.  ARGS may contain options
     *  and targets:
     *      [ -f MAKEFILE ] [ -D FILEINFO ] TARGET1 TARGET2 ...
     */
    public static void main(String... args) {
        String makefileName;
        String fileInfoName;
        if (args.length == 0) {
            usage();
        }
        makefileName = "Makefile";
        fileInfoName = "fileinfo";
        int a;
        for (a = 0; a < args.length; a += 1) {
            if (args[a].equals("-f")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    makefileName = args[a];
                }
            } else if (args[a].equals("-D")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    fileInfoName = args[a];
                }
            } else if (args[a].startsWith("-")) {
                usage();
            } else {
                break;
            }
        }
        ArrayList<String> targets = new ArrayList<String>();
        for (; a < args.length; a += 1) {
            targets.add(args[a]);
        }
        make(makefileName, fileInfoName, targets);
    }

    /** Carry out the make procedure using MAKEFILENAME as the makefile,
     *  taking information on the current file-system state from FILEINFONAME,
     *  and building TARGETS, or the first target in the makefile if TARGETS
     *  is empty.
     */
    private static void make(String makefileName, String fileInfoName,
            List<String> targets) {
        parseFileInfo(fileInfoName);
        parseMakefile(makefileName);
        DirectedGraph<Rule, NoLabel> g = new DirectedGraph<Rule, NoLabel>();
        for (Rule r : rules) {
            g.add(r);
        }
        for (Graph<Rule, NoLabel>.Vertex v : g.vertices()) {
            for (String s : v.getLabel().getPrereqs()) {
                Graph<Rule, NoLabel>.Vertex vert = findVertex(g, s);
                if (vert != null) {
                    if (g.contains(vert, v)) {
                        System.err.println("makefile contains a cycle");
                        System.exit(1);
                    }
                    g.add(v, vert);
                } else if (!nameDates.containsKey(s)) {
                    System.err.println("contains a target with no rule"
                            + " and does not currently exist");
                    System.exit(1);
                }
            }
        }
        MakeTraversal t = new MakeTraversal();
        for (String target : targets) {
            t.depthFirstTraverse(g, findVertex(g, target));
        }
    }

    /** Returns the vertex with target S and non-empty command set in G. */
    private static Graph<Rule, NoLabel>.Vertex findVertex(
            Graph<Rule, NoLabel> g, String s) {
        for (Graph<Rule, NoLabel>.Vertex vert : g.vertices()) {
            if (vert.getLabel().getTarget().equals(s)) {
                return vert;
            }
        }
        return null;
    }

    /** A traversal for the make program. */
    private static class MakeTraversal extends Traversal<Rule, NoLabel> {
        /** postVisits the vertex V, executing its command set if conditions
         *  are met. */
        @Override
        protected void postVisit(Graph<Rule, NoLabel>.Vertex v) {
            if (v == null || v.getLabel().getCommandSet().isEmpty()) {
                return;
            }
            if (!nameDates.containsKey(v.getLabel().getTarget())) {
                for (String s : v.getLabel().getCommandSet()) {
                    System.out.println(s);
                }
                nameDates.put(v.getLabel().getTarget(), currentTime);
                currentTime += 1;
            } else {
                int myAge = nameDates.get(v.getLabel().getTarget());
                for (String str : v.getLabel().getPrereqs()) {
                    if (nameDates.get(str) > myAge) {
                        for (String s : v.getLabel().getCommandSet()) {
                            System.out.println(s);
                        }
                        nameDates.remove(v.getLabel().getTarget());
                        nameDates.put(v.getLabel().getTarget(), currentTime);
                        currentTime += 1;
                        break;
                    }
                }
            }
        }
    }

    /** Print a brief usage message and exit program abnormally. */
    private static void usage() {
        System.out.println("Usage: java make.Main [-f MAKEFILE] [-D FILEINFO]"
                + " TARGET1 TARGET2 ...");
        System.exit(1);
    }

    /** Process the fileInfo file named FILEINFONAME. */
    private static void parseFileInfo(String fileInfoName) {
        try {
            Scanner inp = new Scanner(new FileInputStream(fileInfoName));
            String s = inp.nextLine();
            currentTime = Integer.parseInt(s.trim());
            while (inp.hasNextLine())  {
                s = inp.nextLine();
                String[] tokens = s.split("\\s");
                nameDates.put(tokens[0], Integer.parseInt(tokens[1]));
            }
            inp.close();
        } catch (IOException e) {
            System.err.println("fileinfo error");
            System.exit(1);
        }
    }

    /** Process the makefile named MAKEFILENAME. */
    private static void parseMakefile(String makefileName) {
        ArrayList<Rule> tempRules = new ArrayList<Rule>();
        try {
            Scanner inp = new Scanner(new FileInputStream(makefileName));
            Rule rule = null;
            while (inp.hasNextLine())  {
                String s = inp.nextLine();
                if (s.matches("\\s+\\S+.*")) {
                    if (rule == null) {
                        System.err.println("makefile error");
                        System.exit(1);
                    }
                    rule.addCommand(s);
                    continue;
                }
                if (s.trim().isEmpty()) {
                    continue;
                }
                String [] tokens = s.split("\\s");
                if (!tokens[0].startsWith("#")) {
                    String target =
                            tokens[0].substring(0, tokens[0].length() - 1);
                    rule = new Rule(target, new ArrayList<String>(),
                            new HashSet<String>());
                    for (int i = 1; i < tokens.length; i += 1) {
                        rule.addPrereq(tokens[i]);
                    }
                    tempRules.add(rule);
                }
            }
            inp.close();
        } catch (IOException e) {
            System.err.println("makefile error");
            System.exit(1);
        }
        HashSet<String> t = new HashSet<String>();
        for (Rule r : tempRules) {
            t.add(r.getTarget());
        }
        for (String tar : t) {
            HashSet<String> p = new HashSet<String>();
            ArrayList<String> cmd = new ArrayList<String>();
            for (Rule rule : tempRules) {
                if (rule.getTarget().equals(tar)) {
                    for (String s : rule.getPrereqs()) {
                        p.add(s);
                    }
                    if (!rule.getCommandSet().isEmpty()) {
                        if (!cmd.isEmpty()) {
                            System.err.println("multiple non-empty"
                                    + " command sets for a target");
                            System.exit(1);
                        }
                        cmd = rule.getCommandSet();
                    }
                }
            }
            rules.add(new Rule(tar, cmd, p));
        }
    }

    /** Current time. */
    private static int currentTime;
    /** Name-date pairs. */
    private static HashMap<String, Integer> nameDates =
            new HashMap<String, Integer>();
    /** List of rules. */
    private static ArrayList<Rule> rules = new ArrayList<Rule>();
}
