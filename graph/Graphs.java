package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

/** Assorted graph algorithms.
 *  @author Jason Qiu
 */
public final class Graphs {

    /* A* Search Algorithms */

    /** Returns a path from V0 to V1 in G of minimum weight, according
     *  to the edge weighter EWEIGHTER.  VLABEL and ELABEL are the types of
     *  vertex and edge labels.  Assumes that H is a distance measure
     *  between vertices satisfying the two properties:
     *     a. H.dist(v, V1) <= shortest path from v to V1 for any v, and
     *     b. H.dist(v, w) <= H.dist(w, V1) + weight of edge (v, w), where
     *        v and w are any vertices in G.
     *
     *  As a side effect, uses VWEIGHTER to set the weight of vertex v
     *  to the weight of a minimal path from V0 to v, for each v in
     *  the returned path and for each v such that
     *       minimum path length from V0 to v + H.dist(v, V1)
     *              < minimum path length from V0 to V1.
     *  The final weights of other vertices are not defined.  If V1 is
     *  unreachable from V0, returns null and sets the minimum path weights of
     *  all reachable nodes.  The distance to a node unreachable from V0 is
     *  Double.POSITIVE_INFINITY. */
    public static <VLabel, ELabel> List<Graph<VLabel, ELabel>.Edge>
    shortestPath(Graph<VLabel, ELabel> G,
                 Graph<VLabel, ELabel>.Vertex V0,
                 final Graph<VLabel, ELabel>.Vertex V1,
                 final Distancer<? super VLabel> h,
                 final Weighter<? super VLabel> vweighter,
                 Weighting<? super ELabel> eweighter) {
        Comparator<Graph<VLabel, ELabel>.Vertex> comp =
            new Comparator<Graph<VLabel, ELabel>.Vertex>() {
                @Override
                public int compare(Graph<VLabel, ELabel>.Vertex v1,
                        Graph<VLabel, ELabel>.Vertex v2) {
                    Double dist1 = vweighter.weight(v1.getLabel())
                            + h.dist(v1.getLabel(), V1.getLabel());
                    Double dist2 = vweighter.weight(v2.getLabel())
                            + h.dist(v2.getLabel(), V1.getLabel());
                    return dist1.compareTo(dist2);
                }
            };
        HashMap<Graph<VLabel, ELabel>.Vertex, Graph<VLabel, ELabel>.Vertex>
        parent = new HashMap<Graph<VLabel, ELabel>.Vertex,
        Graph<VLabel, ELabel>.Vertex>();
        for (Graph<VLabel, ELabel>.Vertex v: G.vertices()) {
            vweighter.setWeight(v.getLabel(), Double.POSITIVE_INFINITY);
            parent.put(v, null);
        }
        vweighter.setWeight(V0.getLabel(), 0.0);
        PriorityQueue<Graph<VLabel, ELabel>.Vertex> fringe =
                new PriorityQueue<Graph<VLabel, ELabel>.Vertex>(1, comp);
        for (Graph<VLabel, ELabel>.Vertex v: G.vertices()) {
            fringe.add(v);
        }
        while (!fringe.isEmpty()) {
            Graph<VLabel, ELabel>.Vertex vert = fringe.poll();
            if (vert == V1) {
                break;
            }
            for (Graph<VLabel, ELabel>.Edge edge : G.outEdges(vert)) {
                double newDist = vweighter.weight(vert.getLabel())
                        + eweighter.weight(edge.getLabel());
                if (newDist < vweighter.weight(edge.getV(vert).getLabel())) {
                    vweighter.setWeight(edge.getV(vert).getLabel(), newDist);
                    fringe.remove(edge.getV(vert));
                    fringe.add(edge.getV(vert));
                    parent.remove(edge.getV(vert));
                    parent.put(edge.getV(vert), vert);
                }
            }
        }
        List<Graph<VLabel, ELabel>.Edge> result =
                new ArrayList<Graph<VLabel, ELabel>.Edge>();
        Graph<VLabel, ELabel>.Vertex parentV = parent.get(V1);
        Graph<VLabel, ELabel>.Vertex childV = V1;
        while (childV != V0) {
            for (Graph<VLabel, ELabel>.Edge e : G.outEdges(parentV)) {
                if (e.getV(parentV) == childV) {
                    result.add(e);
                    break;
                }
            }
            childV = parentV;
            parentV = parent.get(parentV);
        }
        Collections.reverse(result);
        return result;
    }

    /** Returns a path from V0 to V1 in G of minimum weight, according
     *  to the weights of its edge labels.  VLABEL and ELABEL are the types of
     *  vertex and edge labels.  Assumes that H is a distance measure
     *  between vertices satisfying the two properties:
     *     a. H.dist(v, V1) <= shortest path from v to V1 for any v, and
     *     b. H.dist(v, w) <= H.dist(w, V1) + weight of edge (v, w), where
     *        v and w are any vertices in G.
     *
     *  As a side effect, sets the weight of vertex v to the weight of
     *  a minimal path from V0 to v, for each v in the returned path
     *  and for each v such that
     *       minimum path length from V0 to v + H.dist(v, V1)
     *           < minimum path length from V0 to V1.
     *  The final weights of other vertices are not defined.
     *
     *  This function has the same effect as the 6-argument version of
     *  shortestPath, but uses the .weight and .setWeight methods of
     *  the edges and vertices themselves to determine and set
     *  weights. If V1 is unreachable from V0, returns null and sets
     *  the minimum path weights of all reachable nodes.  The distance
     *  to a node unreachable from V0 is Double.POSITIVE_INFINITY. */
    public static
    <VLabel extends Weightable, ELabel extends Weighted>
    List<Graph<VLabel, ELabel>.Edge>
    shortestPath(Graph<VLabel, ELabel> G,
                 Graph<VLabel, ELabel>.Vertex V0,
                 final Graph<VLabel, ELabel>.Vertex V1,
                 final Distancer<? super VLabel> h) {
        Comparator<Graph<VLabel, ELabel>.Vertex> comp =
            new Comparator<Graph<VLabel, ELabel>.Vertex>() {
                @Override
                public int compare(Graph<VLabel, ELabel>.Vertex v1,
                        Graph<VLabel, ELabel>.Vertex v2) {
                    Double dist1 = v1.getLabel().weight()
                            + h.dist(v1.getLabel(), V1.getLabel());
                    Double dist2 = v2.getLabel().weight()
                            + h.dist(v2.getLabel(), V1.getLabel());
                    return dist1.compareTo(dist2);
                }
            };
        HashMap<Graph<VLabel, ELabel>.Vertex, Graph<VLabel, ELabel>.Vertex>
        parent = new HashMap<Graph<VLabel, ELabel>.Vertex,
        Graph<VLabel, ELabel>.Vertex>();
        for (Graph<VLabel, ELabel>.Vertex v: G.vertices()) {
            v.getLabel().setWeight(Double.POSITIVE_INFINITY);
            parent.put(v, null);
        }
        V0.getLabel().setWeight(0.0);
        PriorityQueue<Graph<VLabel, ELabel>.Vertex> fringe =
                new PriorityQueue<Graph<VLabel, ELabel>.Vertex>(1, comp);
        for (Graph<VLabel, ELabel>.Vertex v: G.vertices()) {
            fringe.add(v);
        }
        while (!fringe.isEmpty()) {
            Graph<VLabel, ELabel>.Vertex vert = fringe.poll();
            if (vert == V1) {
                break;
            }
            for (Graph<VLabel, ELabel>.Edge edge : G.outEdges(vert)) {
                double newDist = vert.getLabel().weight()
                        + edge.getLabel().weight();
                if (newDist < edge.getV(vert).getLabel().weight()) {
                    edge.getV(vert).getLabel().setWeight(newDist);
                    fringe.remove(edge.getV(vert));
                    fringe.add(edge.getV(vert));
                    parent.remove(edge.getV(vert));
                    parent.put(edge.getV(vert), vert);
                }
            }
        }
        List<Graph<VLabel, ELabel>.Edge> result =
                new ArrayList<Graph<VLabel, ELabel>.Edge>();
        Graph<VLabel, ELabel>.Vertex parentV = parent.get(V1);
        Graph<VLabel, ELabel>.Vertex childV = V1;
        while (childV != V0) {
            for (Graph<VLabel, ELabel>.Edge e : G.outEdges(parentV)) {
                if (e.getV(parentV) == childV) {
                    result.add(e);
                    break;
                }
            }
            childV = parentV;
            parentV = parent.get(parentV);
        }
        Collections.reverse(result);
        return result;
    }

    /** Returns a distancer whose dist method always returns 0. */
    public static final Distancer<Object> ZERO_DISTANCER =
        new Distancer<Object>() {
            @Override
            public double dist(Object v0, Object v1) {
                return 0.0;
            }
        };
}
