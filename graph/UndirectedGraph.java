package graph;

import java.util.ArrayList;

/* Do not add or remove public or protected members, or modify the signatures of
 * any public methods.  You may add bodies to abstract methods, modify
 * existing bodies, or override inherited methods.  */

/** An undirected graph with vertices labeled with VLABEL and edges
 *  labeled with ELABEL.
 *  @author Jason Qiu
 */
public class UndirectedGraph<VLabel, ELabel> extends Graph<VLabel, ELabel> {

    /** An empty graph. */
    public UndirectedGraph() {
    }

    @Override
    public boolean isDirected() {
        return false;
    }

    /** Returns the number of outgoing edges incident to V. Assumes V is one of
     *  my vertices.  */
    @Override
    public int outDegree(Vertex v) {
        int result = 0;
        for (Edge e : getEdges()) {
            if (e.getV0() == v || e.getV1() == v) {
                result += 1;
            }
        }
        return result;
    }

    /** Returns true iff there is an edge (U, V) in me with any label. */
    @Override
    public boolean contains(Vertex u, Vertex v) {
        for (Edge e : getEdges()) {
            if ((e.getV0() == u && e.getV1() == v)
                    || (e.getV0() == v && e.getV1() == u)) {
                return true;
            }
        }
        return false;
    }

    /** Returns true iff there is an edge (U, V) in me with label LABEL. */
    @Override
    public boolean contains(Vertex u, Vertex v, ELabel label) {
        for (Edge e : getEdges()) {
            if (((e.getV0() == u && e.getV1() == v)
                    || (e.getV0() == v && e.getV1() == u))
                    && e.getLabel() == label) {
                return true;
            }
        }
        return false;
    }

    /** Remove all edges from V1 to V2 from me, if present.  The result is
     *  undefined if V1 and V2 are not among my vertices.  */
    @Override
    public void remove(Vertex v1, Vertex v2) {
        ArrayList<Edge> removal = new ArrayList<Edge>();
        for (Edge e : getEdges()) {
            if ((e.getV0() == v1 && e.getV1() == v2)
                    || (e.getV0() == v2 && e.getV1() == v1)) {
                removal.add(e);
            }
        }
        for (Edge e : removal) {
            remove(e);
        }
    }

    /** Returns an iterator over all successors of V. */
    @Override
    public Iteration<Vertex> successors(Vertex v) {
        ArrayList<Vertex> result = new ArrayList<Vertex>();
        for (Edge e : getEdges()) {
            if (e.getV0() == v) {
                result.add(e.getV1());
            } else if (e.getV1() == v) {
                result.add(e.getV0());
            }
        }
        return Iteration.iteration(result);
    }

    /** Returns iterator over all outgoing edges from V. */
    public Iteration<Edge> outEdges(Vertex v) {
        ArrayList<Edge> result = new ArrayList<Edge>();
        for (Edge e : getEdges()) {
            if (e.getV0() == v || e.getV1() == v) {
                result.add(e);
            }
        }
        return Iteration.iteration(result);
    }
}
