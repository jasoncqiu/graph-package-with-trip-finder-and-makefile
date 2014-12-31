package graph;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;

/** Implements a generalized traversal of a graph.  At any given time,
 *  there is a particular set of untraversed vertices---the "fringe."
 *  Traversal consists of repeatedly removing an untraversed vertex
 *  from the fringe, visting it, and then adding its untraversed
 *  successors to the fringe.  The client can dictate an ordering on
 *  the fringe, determining which item is next removed, by which kind
 *  of traversal is requested.
 *     + A depth-first traversal treats the fringe as a list, and adds
 *       and removes vertices at one end.  It also revisits the node
 *       itself after traversing all successors by calling the
 *       postVisit method on it.
 *     + A breadth-first traversal treats the fringe as a list, and adds
 *       and removes vertices at different ends.  It also revisits the node
 *       itself after traversing all successors as for depth-first
 *       traversals.
 *     + A general traversal treats the fringe as an ordered set, as
 *       determined by a Comparator argument.  There is no postVisit
 *       for this type of traversal.
 *  As vertices are added to the fringe, the traversal calls a
 *  preVisit method on the vertex.
 *
 *  Generally, the client will extend Traversal, overriding the visit,
 *  preVisit, and postVisit methods, as desired (by default, they do nothing).
 *  Any of these methods may throw StopException to halt the traversal
 *  (temporarily, if desired).  The preVisit method may throw a
 *  RejectException to prevent a vertex from being added to the
 *  fringe, and the visit method may throw a RejectException to
 *  prevent its successors from being added to the fringe.
 *  @author Jason Qiu
 */
public class Traversal<VLabel, ELabel> {

    /** Perform a traversal of G over all vertices reachable from V.
     *  ORDER determines the ordering in which the fringe of
     *  untraversed vertices is visited.  The effect of specifying an
     *  ORDER whose results change as a result of modifications made during the
     *  traversal is undefined. */
    public void traverse(Graph<VLabel, ELabel> G,
            Graph<VLabel, ELabel>.Vertex v,
            final Comparator<VLabel> order) {
        _graph = G;
        _lastTraversal = "general";
        _lastComp = order;
        if (!cont) {
            marked = new HashSet<Graph<VLabel, ELabel>.Vertex>();
        }
        Comparator<Graph<VLabel, ELabel>.Vertex> comp =
            new Comparator<Graph<VLabel, ELabel>.Vertex>() {
                @Override
                public int compare(Graph<VLabel, ELabel>.Vertex v1,
                        Graph<VLabel, ELabel>.Vertex v2) {
                    return order.compare(v1.getLabel(), v2.getLabel());
                }
            };
        PriorityQueue<Graph<VLabel, ELabel>.Vertex> fringe =
                new PriorityQueue<Graph<VLabel, ELabel>.Vertex>(1, comp);
        if (!marked.contains(v)) {
            fringe.add(v);
        }
        try {
            while (!fringe.isEmpty()) {
                Graph<VLabel, ELabel>.Vertex vert = fringe.poll();
                _finalVertex = vert;
                if (!marked.contains(vert)) {
                    try {
                        marked.add(vert);
                        visit(vert);
                    } catch (RejectException e) {
                        continue;
                    }
                    for (Graph<VLabel, ELabel>.Edge edge : G.outEdges(vert)) {
                        _finalEdge = edge;
                        try {
                            if (!marked.contains(edge.getV(vert))) {
                                preVisit(edge, vert);
                                fringe.add(edge.getV(vert));
                            }
                        } catch (RejectException e) {
                            continue;
                        }
                    }
                }
            }
        } catch (StopException e) {
            return;
        }
    }

    /** Performs a depth-first traversal of G over all vertices
     *  reachable from V.  That is, the fringe is a sequence and
     *  vertices are added to it or removed from it at one end in
     *  an undefined order.  After the traversal of all successors of
     *  a node is complete, the node itself is revisited by calling
     *  the postVisit method on it. */
    public void depthFirstTraverse(Graph<VLabel, ELabel> G,
            Graph<VLabel, ELabel>.Vertex v) {
        _graph = G;
        _lastTraversal = "depth";
        if (!cont) {
            marked = new HashSet<Graph<VLabel, ELabel>.Vertex>();
        }
        Stack<Graph<VLabel, ELabel>.Vertex> fringe =
                new Stack<Graph<VLabel, ELabel>.Vertex>();
        if (!marked.contains(v)) {
            fringe.push(v);
        }
        HashSet<Graph<VLabel, ELabel>.Vertex> postVisited =
                new HashSet<Graph<VLabel, ELabel>.Vertex>();
        try {
            while (!fringe.isEmpty()) {
                Graph<VLabel, ELabel>.Vertex vert = fringe.pop();
                _finalVertex = vert;
                if (!marked.contains(vert)) {
                    try {
                        marked.add(vert);
                        visit(vert);
                    } catch (RejectException e) {
                        continue;
                    }
                    fringe.push(vert);
                    for (Graph<VLabel, ELabel>.Edge edge : G.outEdges(vert)) {
                        _finalEdge = edge;
                        try {
                            if (!marked.contains(edge.getV(vert))) {
                                preVisit(edge, vert);
                                fringe.push(edge.getV(vert));
                            }
                        } catch (RejectException e) {
                            continue;
                        }
                    }
                } else {
                    if (!postVisited.contains(vert)) {
                        postVisit(vert);
                        postVisited.add(vert);
                    }
                }
            }
        } catch (StopException e) {
            return;
        }
    }

    /** Performs a breadth-first traversal of G over all vertices
     *  reachable from V.  That is, the fringe is a sequence and
     *  vertices are added to it at one end and removed from it at the
     *  other in an undefined order.  After the traversal of all successors of
     *  a node is complete, the node itself is revisited by calling
     *  the postVisit method on it. */
    public void breadthFirstTraverse(Graph<VLabel, ELabel> G,
            Graph<VLabel, ELabel>.Vertex v) {
        _graph = G;
        _lastTraversal = "breadth";
        if (!cont) {
            marked = new HashSet<Graph<VLabel, ELabel>.Vertex>();
        }
        LinkedList<Graph<VLabel, ELabel>.Vertex> fringe =
                new LinkedList<Graph<VLabel, ELabel>.Vertex>();
        if (!marked.contains(v)) {
            fringe.add(v);
        }
        try {
            while (!fringe.isEmpty()) {
                Graph<VLabel, ELabel>.Vertex vert = fringe.pop();
                _finalVertex = vert;
                if (!marked.contains(vert)) {
                    try {
                        marked.add(vert);
                        visit(vert);
                    } catch (RejectException e) {
                        continue;
                    }
                    for (Graph<VLabel, ELabel>.Edge edge : G.outEdges(vert)) {
                        _finalEdge = edge;
                        try {
                            if (!marked.contains(edge.getV(vert))) {
                                preVisit(edge, vert);
                                if (!fringe.contains(edge.getV(vert))) {
                                    fringe.add(edge.getV(vert));
                                }
                            }
                        } catch (RejectException e) {
                            continue;
                        }
                    }
                    fringe.add(vert);
                } else {
                    postVisit(vert);
                }
            }
        } catch (StopException e) {
            return;
        }
    }

    /** Continue the previous traversal starting from V.
     *  Continuing a traversal means that we do not traverse
     *  vertices that have been traversed previously. */
    public void continueTraversing(Graph<VLabel, ELabel>.Vertex v) {
        cont = true;
        if (_lastTraversal.equals("general")) {
            traverse(_graph, v, _lastComp);
        } else if (_lastTraversal.equals("depth")) {
            depthFirstTraverse(_graph, v);
        } else if (_lastTraversal.equals("breadth")) {
            breadthFirstTraverse(_graph, v);
        }
        cont = false;
    }

    /** If the traversal ends prematurely, returns the Vertex argument to
     *  preVisit, visit, or postVisit that caused a Visit routine to
     *  return false.  Otherwise, returns null. */
    public Graph<VLabel, ELabel>.Vertex finalVertex() {
        return _finalVertex;
    }

    /** If the traversal ends prematurely, returns the Edge argument to
     *  preVisit that caused a Visit routine to return false. If it was not
     *  an edge that caused termination, returns null. */
    public Graph<VLabel, ELabel>.Edge finalEdge() {
        return _finalEdge;
    }

    /** Returns the last graph argument to a traverse routine, or null if none
     *  of these methods have been called. */
    protected Graph<VLabel, ELabel> theGraph() {
        return _graph;
    }

    /** Method to be called when adding the node at the other end of E from V0
     *  to the fringe. If this routine throws a StopException,
     *  the traversal ends.  If it throws a RejectException, the edge
     *  E is not traversed. The default does nothing.
     */
    protected void preVisit(Graph<VLabel, ELabel>.Edge e,
            Graph<VLabel, ELabel>.Vertex v0) {
    }

    /** Method to be called when visiting vertex V.  If this routine throws
     *  a StopException, the traversal ends.  If it throws a RejectException,
     *  successors of V do not get visited from V. The default does nothing. */
    protected void visit(Graph<VLabel, ELabel>.Vertex v) {
    }

    /** Method to be called immediately after finishing the traversal
     *  of successors of vertex V in pre- and post-order traversals.
     *  If this routine throws a StopException, the traversal ends.
     *  Throwing a RejectException has no effect. The default does nothing.
     */
    protected void postVisit(Graph<VLabel, ELabel>.Vertex v) {
    }

    /** The Vertex (if any) that terminated the last traversal. */
    protected Graph<VLabel, ELabel>.Vertex _finalVertex;
    /** The Edge (if any) that terminated the last traversal. */
    protected Graph<VLabel, ELabel>.Edge _finalEdge;
    /** The last graph traversed. */
    protected Graph<VLabel, ELabel> _graph;
    /** Set of visited vertices. */
    private HashSet<Graph<VLabel, ELabel>.Vertex> marked;
    /** Last type of traversal called. */
    private String _lastTraversal;
    /** Last comparator for general traversal. */
    private Comparator<VLabel> _lastComp;
    /** Whether to continue a traversal or start a new one. */
    private boolean cont = false;
}
