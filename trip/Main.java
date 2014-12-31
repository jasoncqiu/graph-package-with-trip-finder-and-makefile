package trip;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import graph.Distancer;
import graph.Graph;
import graph.Graphs;
import graph.UndirectedGraph;

/** Initial class for the 'trip' program.
 *  @author Jason Qiu
 */
public final class Main {

    /** Entry point for the CS61B trip program.  ARGS may contain options
     *  and targets:
     *      [ -m MAP ] [ -o OUT ] [ REQUEST ]
     *  where MAP (default Map) contains the map data, OUT (default standard
     *  output) takes the result, and REQUEST (default standard input) contains
     *  the locations along the requested trip.
     */
    public static void main(String... args) {
        String mapFileName;
        String outFileName;
        String requestFileName;
        mapFileName = "Map";
        outFileName = requestFileName = null;
        int a;
        for (a = 0; a < args.length; a += 1) {
            if (args[a].equals("-m")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    mapFileName = args[a];
                }
            } else if (args[a].equals("-o")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    outFileName = args[a];
                }
            } else if (args[a].startsWith("-")) {
                usage();
            } else {
                break;
            }
        }
        if (a == args.length - 1) {
            requestFileName = args[a];
        } else if (a > args.length) {
            usage();
        }
        if (requestFileName != null) {
            try {
                System.setIn(new FileInputStream(requestFileName));
            } catch  (FileNotFoundException e) {
                System.err.printf("Could not open %s.%n", requestFileName);
                System.exit(1);
            }
        }
        if (outFileName != null) {
            try {
                System.setOut(new PrintStream(new FileOutputStream(outFileName),
                        true));
            } catch  (FileNotFoundException e) {
                System.err.printf("Could not open %s for writing.%n",
                        outFileName);
                System.exit(1);
            }
        }
        parseMapfile(mapFileName);
        parseRequests();
        trip(mapFileName);
    }

    /** Print a trip for the request on the standard input to the stsndard
     *  output, using the map data in MAPFILENAME.
     */
    private static void trip(String mapFileName) {
        Graph<Location, Distance> g =
                new UndirectedGraph<Location, Distance>();
        for (Location loc : locations) {
            g.add(loc);
        }
        Graph<Location, Distance>.Vertex v1 = null;
        Graph<Location, Distance>.Vertex v2 = null;
        for (Distance dist : distances) {
            v1 = findVertex(g, dist.getPlace());
            v2 = findVertex(g, dist.getOtherPlace());
            g.add(v1, v2, dist);
        }
        for (String[] request : requests) {
            System.out.println("From " + request[0] + ":\n");
            ArrayList<String> result = new ArrayList<String>();
            for (int i = 0; i < request.length - 1; i += 1) {
                v1 = findVertex(g, request[i]);
                v2 = findVertex(g, request[i + 1]);
                Graph<Location, Distance>.Vertex prevV = v1;
                Distance d;
                for (Graph<Location, Distance>.Edge edge
                        : Graphs.shortestPath(g, v1, v2, distancer)) {
                    d = edge.getLabel();
                    String direction;
                    if (edge.getV0() == prevV) {
                        direction =
                                convertDir(d.getDirection().substring(1, 2));
                    } else {
                        direction =
                                convertDir(d.getDirection().substring(0, 1));
                    }
                    prevV = edge.getV(prevV);
                    if (prevV == v2) {
                        result.add("Take " + d.getRoad() + " " + direction
                                + " for " + d.getDist() + " miles to "
                                + prevV.getLabel().getPlace() + ".");
                    } else {
                        result.add("Take " + d.getRoad() + " " + direction
                                + " for " + d.getDist() + " miles.");
                    }
                }
            }
            processResult(result);
        }
    }

    /** Processes the map file defined by MAPFILENAME. */
    private static void parseMapfile(String mapFileName) {
        try {
            Scanner inp = new Scanner(new FileInputStream(mapFileName));
            String s;
            while (inp.hasNext())  {
                s = inp.next();
                if (s.equals("L")) {
                    Location loc = new Location(
                            inp.next(), inp.nextDouble(), inp.nextDouble());
                    locations.add(loc);
                } else {
                    Distance dist = new Distance(inp.next(), inp.next()
                            , inp.nextDouble(), inp.next(), inp.next());
                    distances.add(dist);
                }
            }
            inp.close();
        } catch (IOException e) {
            System.err.println("mapfile error");
            System.exit(1);
        }
    }

    /** Processes requests from the system input. */
    private static void parseRequests() {
        Scanner inp = new Scanner(System.in);
        String s;
        while (inp.hasNextLine())  {
            s = inp.nextLine().trim();
            if (!s.isEmpty()) {
                requests.add(s.split("\\s*,\\s*"));
            }
        }
        inp.close();
    }

    /** Returns the vertex with place S in G. */
    private static Graph<Location, Distance>.Vertex findVertex(
            Graph<Location, Distance> g, String s) {
        for (Graph<Location, Distance>.Vertex vert : g.vertices()) {
            if (vert.getLabel().getPlace().equals(s)) {
                return vert;
            }
        }
        System.err.println("vertex not in graph");
        System.exit(1);
        return null;
    }

    /** Processes the resulting directions in RESULT, rounding distances
     * and combining adjacent similar directions. */
    private static void processResult(ArrayList<String> result) {
        int j = 1;
        while (j < result.size()) {
            String[] line1 = result.get(j - 1).split("\\s");
            String[] line2 = result.get(j).split("\\s");
            if (line1.length > 6) {
                j += 1;
                continue;
            }
            String road1 = line1[1];
            String road2 = line2[1];
            String dir1 = line1[2];
            String dir2 = line2[2];
            double dist1 = Double.parseDouble(line1[4]);
            double dist2 = Double.parseDouble(line2[4]);
            if (road1.equals(road2) && dir1.equals(dir2)) {
                double sum = dist1 + dist2;
                line2[4] = sum + "";
                String combined = "";
                for (String s : line2) {
                    combined += s + " ";
                }
                result.remove(j);
                result.set(j - 1, combined.trim());
            } else {
                j += 1;
            }
        }
        int count = 1;
        for (String s : result) {
            String[] temp = s.split("\\s");
            temp[4] = round(Double.parseDouble(temp[4]));
            String combined = "";
            for (String str : temp) {
                combined += str + " ";
            }
            System.out.println(count + ". " + combined.trim());
            count += 1;
        }
    }

    /** Returns a rounded double D in the form of a string. */
    private static String round(double d) {
        DecimalFormat f = new DecimalFormat("#.#");
        return Double.valueOf(f.format(d)) + "";
    }

    /** Returns the direction S converted to its word counterpart. */
    private static String convertDir(String s) {
        if (s.equals("N")) {
            return "north";
        } else if (s.equals("S")) {
            return "south";
        } else if (s.equals("E")) {
            return "east";
        } else if (s.equals("W")) {
            return "west";
        }
        System.err.println("direction cannot be parsed");
        System.exit(1);
        return null;
    }

    /** Print a brief usage message and exit program abnormally. */
    private static void usage() {
        System.out.println("Usage: java [-m MAP_FILE] [-o OUTPUT_FILE]"
                + " [REQUESTS]");
        System.exit(1);
    }

    /** My distancer, used as the heuristic function. */
    private static Distancer<Location> distancer = new Distancer<Location>() {
            @Override
            public double dist(Location loc1, Location loc2) {
                Double a = loc1.getX() - loc2.getX();
                Double b = loc1.getY() - loc2.getY();
                return Math.sqrt(a * a + b * b);
            }
        };
    /** My locations. */
    private static ArrayList<Location> locations = new ArrayList<Location>();
    /** My distances. */
    private static ArrayList<Distance> distances = new ArrayList<Distance>();
    /** My requests. */
    private static ArrayList<String[]> requests = new ArrayList<String[]>();
}
