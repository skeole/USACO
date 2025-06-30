package datastructures.graphs;

import datastructures.collections.dequelist.TreeDequeList;
import datastructures.utility.ComplexNumber;
import datastructures.utility.OrderedTuple;

import java.util.*;

public class Graph<E> {

    /* Methods we should have
     * 
     * Dijkstra's
     * Floyd-Warshall
     * Bellman-Ford
     * 
     * GSCC - Kosaraju's
     * Topological Sort - Kahns
     * Topological Sort - Tarjan's
     * MST - Prim's
     * MST - Kruskal's
     * 
     * Karger's
     *
     * BFS Tree
     * DFS Forest
     *
     * CreateAdjacencyMatrix */

    private final TreeDequeList<E> vertices = new TreeDequeList<>();
    private final HashMap<E, HashMap<E, Integer>> forwardAdjacencyMap = new HashMap<>();
    private final HashMap<E, HashMap<E, Integer>> backwardAdjacencyMap = new HashMap<>();
        // first --> neighbor, second --> weight

    private int numNegatives = 0;

    public Graph() {}

    public int size() {
        return vertices.size();
    }

    public boolean addVertex(E vertex) {
        if (vertices.contains(vertex)) {
            throw new IllegalArgumentException("Vertex " + vertex + " is already in the graph");
        }
        vertices.add(vertex);
        forwardAdjacencyMap.put(vertex, new HashMap<>());
        backwardAdjacencyMap.put(vertex, new HashMap<>());
        return true;
    }

    public boolean removeVertex(E vertex) {
        if (!vertices.contains(vertex)) {
            throw new IllegalArgumentException("Vertex " + vertex + " is not in the graph");
        }

        for (Map.Entry<E, Integer> connection : forwardAdjacencyMap.get(vertex).entrySet()) {
            backwardAdjacencyMap.get(connection.getKey()).remove(vertex);
            if (connection.getValue() < 0) {
                numNegatives -= 1;
            }
        }

        for (Map.Entry<E, Integer> connection : backwardAdjacencyMap.get(vertex).entrySet()) {
            forwardAdjacencyMap.get(connection.getKey()).remove(vertex);
            if (connection.getValue() < 0) {
                numNegatives -= 1;
            }
        }

        vertices.remove(vertex);
        forwardAdjacencyMap.remove(vertex);
        backwardAdjacencyMap.remove(vertex);
        return true;
    }

    public boolean hasEdge(E u, E v) {
        if (!vertices.contains(u)) {
            throw new IllegalArgumentException("Vertex " + u + " does not exist!");
        }
        if (!vertices.contains(v)) {
            throw new IllegalArgumentException("Vertex " + v + " does not exist!");
        }
        return forwardAdjacencyMap.get(u).containsKey(v);
    }

    public int getWeight(E u, E v) {
        if (!hasEdge(u, v)) {
            throw new NoSuchElementException(
                    "There is no edge from vertex " + u + " to vertex " + v
            );
        }
        return forwardAdjacencyMap.get(u).get(v);
    }

    /** returns true if there was no edge beforehand. If there was, the edge updates. */
    public boolean addEdge(E u, E v, int weight) {
        if (!vertices.contains(u)) {
            addVertex(u);
        }

        if (!vertices.contains(v)) {
            addVertex(v);
        }

        if (weight < 0) {
            numNegatives += 1;
        }

        boolean ret = true;
        if (hasEdge(u, v)) {
            if (forwardAdjacencyMap.get(u).get(v) < 0) {
                numNegatives -= 1;
            }
            ret = false;
        }

        forwardAdjacencyMap.get(u).put(v, weight);
        backwardAdjacencyMap.get(v).put(u, weight);
        return ret;
    }

    public boolean removeEdge(E u, E v) {
        if (!hasEdge(u, v)) {
            throw new IllegalArgumentException("No edge between " + u + " and " + v);
        }

        if (forwardAdjacencyMap.get(u).get(v) < 0) {
            numNegatives -= 1;
        }

        forwardAdjacencyMap.get(u).remove(v);
        backwardAdjacencyMap.get(v).remove(u);
        return true;
    }

    public Set<E> inNeighbors(E v) {
        if (!vertices.contains(v)) {
            throw new IllegalArgumentException("Vertex " + v + "does not exist!");
        }
        return new HashSet<>(backwardAdjacencyMap.get(v).keySet());
    }

    public Set<Map.Entry<E, Integer>> inNeighborsAndWeights(E v) {
        if (!vertices.contains(v)) {
            throw new IllegalArgumentException("Vertex " + v + "does not exist!");
        }
        return new HashSet<>(backwardAdjacencyMap.get(v).entrySet());
    }

    public Set<E> outNeighbors(E v) {
        if (!vertices.contains(v)) {
            throw new IllegalArgumentException("Vertex " + v + "does not exist!");
        }
        return new HashSet<>(forwardAdjacencyMap.get(v).keySet());
    }

    public Set<Map.Entry<E, Integer>> outNeighborsAndWeights(E v) {
        if (!vertices.contains(v)) {
            throw new IllegalArgumentException("Vertex " + v + "does not exist!");
        }
        return new HashSet<>(forwardAdjacencyMap.get(v).entrySet());
    }

    public int[][] getAdjacencyMatrix() {
        return getAdjacencyMatrix(true, false);
    }

    /** sparse: iterates through edges. O(m logn). dense: iterates through vertices. O(n^2) */
    public int[][] getAdjacencyMatrix(boolean forward, boolean dense) {
        int[][] matrix = new int[vertices.size()][vertices.size()];
        if (dense) {
            int i = 0;
            for (E u : vertices) {
                int j = 0;
                for (E v : vertices) {
                    if (hasEdge(u, v)) {
                        if (forward) {
                            matrix[i][j] = forwardAdjacencyMap.get(u).get(v);
                        } else {
                            matrix[j][i] = forwardAdjacencyMap.get(u).get(v);
                        }
                    }
                    j += 1;
                }
                i += 1;
            }
        } else {
            int i = 0;
            for (E u : vertices) {
                for (Map.Entry<E, Integer> connection : forwardAdjacencyMap.get(u).entrySet()) {
                    if (forward) {
                        matrix[i][vertices.indexOf(connection.getKey())] = connection.getValue();
                    } else {
                        matrix[vertices.indexOf(connection.getKey())][i] = connection.getValue();
                    }
                }
                i += 1;
            }
        }
        return matrix;
    }

    // graph algorithms

    public TreeForest<E> bfsTree(E v, boolean forward) {
        return null;
    }

    public TreeForest<E> dfsForest(boolean forward) {
        return null;
    }

    public TreeForest<E> dfsForest(Comparator<E> order, boolean forward) {
        return null;
    }

    public TreeForest<E> dfsForest(Iterable<E> order, boolean forward) {
        return null;
    }

    // shortest path algorithms

    /** first: parent (can be used to trace path back); second: total path weight */
    public HashMap<E, OrderedTuple<E, Integer>> dijkstras(E v, boolean forward, boolean useHeap) { // parent, distance
        /** if (numNegatives != 0) {
            throw new IllegalArgumentException("Cannot use dijkstra's algorithm with negative edge weights");
        }
        // useHeap if not a very dense graph
        for (int i = 0; i < size; i += 1) {
            for (Coordinate neighbor : neighborsAndWeights(i, true)) {
                if (neighbor.getY() < 0) {
                    throw new IllegalArgumentException(
                            "Cannot run Dijkstra's algorithm on a graph with negative edge weights"
                    );
                }
            }
        }

        Object[] list = new Object[size];
        for (int i = 0; i < list.length; i += 1) {
            if (i != v) {
                list[i] = new BinaryMinHeapInterface.Entry<Integer, Integer>(Integer.MAX_VALUE, i);
            } else {
                list[i] = new BinaryMinHeapInterface.Entry<Integer, Integer>(0, i);
            }
        }
        BinaryMinHeapInterface<Integer, Integer> minHeap =
                BinaryMinHeapImplementation.createMinHeap(list);

        Coordinate[] dijkstrasTree = new Coordinate[size];

        for (int i = 0; i < dijkstrasTree.length; i += 1) {
            dijkstrasTree[i] = new Coordinate(-1, Integer.MAX_VALUE);
        }

        dijkstrasTree[v] = new Coordinate(v, 0);

        while (!minHeap.isEmpty() && minHeap.peek().getKey() < Integer.MAX_VALUE) {
            // need check to avoid integer overflow errors

            BinaryMinHeapInterface.Entry<Integer, Integer> vertex = minHeap.extractMin();
            // key -> distance, value -> index

            for (Coordinate neighbor : neighborsAndWeights(vertex.getValue(), forward)) {
                if (
                        dijkstrasTree[neighbor.getX()].getY() >
                                dijkstrasTree[vertex.getValue()].getY() + neighbor.getY()) {
                    dijkstrasTree[neighbor.getX()] = new Coordinate(
                            vertex.getValue(),
                            dijkstrasTree[vertex.getValue()].getY() + neighbor.getY()
                    );
                    minHeap.decreaseKey(neighbor.getX(), dijkstrasTree[neighbor.getX()].getY());
                    // if we get an error -> I messed up somewhere </3
                }
            }
        }
        return dijkstrasTree; */
        return null;
    }

    /** first: parent (can be used to trace path back); second: total path weight */
    public HashMap<E, OrderedTuple<E, Integer>> bellmanFord(E v, boolean forward) {
        return null;
    }

    public HashMap<E, HashMap<E, Integer>> floydWarshall(boolean forward) {
        return null;
    }

}