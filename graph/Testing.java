package graph;

import java.util.Comparator;
import org.junit.Test;
import ucb.junit.textui;
import static org.junit.Assert.*;

/* You MAY add public @Test methods to this class.  You may also add
 * additional public classes containing "Testing" in their name. These
 * may not be part of your graph package per se (that is, it must be
 * possible to remove them and still have your package work). */

/** Unit tests for the graph package.
 *  @author Jason Qiu
 */
public class Testing {

    /** Run all JUnit tests in the graph package. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(graph.Testing.class));
    }

    @Test
    public void emptyGraph() {
        DirectedGraph<String, String> g =
                new DirectedGraph<String, String>();
        assertEquals("Initial graph has vertices", 0, g.vertexSize());
        assertEquals("Initial graph has edges", 0, g.edgeSize());
    }

    @Test
    public void testDirected() {
        DirectedGraph<String, String> g =
                new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("A");
        Graph<String, String>.Vertex v2 = g.add("B");
        g.add(v1, v2, "AB");
        assertEquals("Initial graph has edges", 1, g.edgeSize());
        assertTrue("add error", g.contains(v1, v2, "AB"));
        assertEquals("degree error", g.outDegree(v1), 1);
        assertEquals("degree error", g.inDegree(v1), 0);
        g.remove(v1, v2);
        assertEquals("Initial graph has edges", 0, g.edgeSize());
        assertEquals("Initial graph has vertices", 2, g.vertexSize());
        g.add(v1, v2, "AB");
        g.remove(v1);
        assertEquals("Initial graph has edges", 0, g.edgeSize());
        assertEquals("Initial graph has vertices", 1, g.vertexSize());
    }

    @Test
    public void testUndirected() {
        UndirectedGraph<String, String> g =
                new UndirectedGraph<String, String>();
        Graph<String, String>.Vertex v1 = g.add("A");
        Graph<String, String>.Vertex v2 = g.add("B");
        Graph<String, String>.Vertex v3 = g.add("C");
        g.add(v1, v2, "AB");
        g.add(v1, v3, "AC");
        assertEquals("Initial graph has edges", 2, g.edgeSize());
        assertTrue("add error", g.contains(v2, v1, "AB"));
        assertTrue("add error", g.contains(v3, v1, "AC"));
        assertEquals("degree error", g.degree(v1), 2);
    }

    /** Tested the traversals visually having preVisit, visit, and postVisit
     * print output. Also tests for lack of exceptions.
     */
    @Test
    public void testTraverse() {
        UndirectedGraph<String, String> g =
                new UndirectedGraph<String, String>();
        Graph<String, String>.Vertex D = g.add("D");
        Graph<String, String>.Vertex E = g.add("E");
        Graph<String, String>.Vertex F = g.add("F");
        Graph<String, String>.Vertex A = g.add("A");
        Graph<String, String>.Vertex B = g.add("B");
        Graph<String, String>.Vertex C = g.add("C");
        g.add(A, C, "B");
        g.add(A, D, "C");
        g.add(A, B, "A");
        g.add(D, E, "D");
        g.add(D, F, "E");
        Traversal<String, String> t = new Traversal<String, String>();
        Comparator<String> comp =
            new Comparator<String>() {
                @Override
                public int compare(String s1,
                        String s2) {
                    return s1.compareTo(s2);
                }
            };
        t.traverse(g, A, comp);
        t.depthFirstTraverse(g, A);
        t.breadthFirstTraverse(g, A);
    }
}
