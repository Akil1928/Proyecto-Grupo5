package datastructure.graph;

import datastructure.list.ListException;
import datastructure.list.SinglyLinkedList;
import datastructure.queue.LinkedQueue;
import datastructure.queue.QueueException;
import datastructure.stack.LinkedStack;
import datastructure.stack.StackException;

public class AdjacencyListGraph implements Graph {
    private Vertex[] vertexList;
    private int n;
    private int counter;
    private LinkedStack stack;
    private LinkedQueue queue;

    public AdjacencyListGraph(int n) {
        if (n <= 0) System.exit(1);
        this.n = n;
        this.counter = 0;
        this.vertexList = new Vertex[n];
        this.stack = new LinkedStack();
        this.queue = new LinkedQueue();
    }

    @Override
    public int size() throws ListException {
        return counter;
    }

    @Override
    public void clear() {
        this.vertexList = new Vertex[n];
        this.counter = 0;
    }

    @Override
    public boolean isEmpty() {
        return counter == 0;
    }

    @Override
    public boolean containsVertex(Object element) throws GraphException, ListException {
        if (isEmpty()) throw new GraphException("Adjacency List Graph is Empty");
        return indexOf(element) != -1;
    }

    @Override
    public boolean containsEdge(Object a, Object b) throws GraphException, ListException {
        if (isEmpty()) throw new GraphException("Adjacency List Graph is Empty");
        return !vertexList[indexOf(a)].edgesList.isEmpty()
                && vertexList[indexOf(a)].edgesList.contains(new EdgeWeight(b, null));
    }

    @Override
    public void addVertex(Object element) throws GraphException, ListException {
        if (counter >= vertexList.length) throw new GraphException("Adjacency List Graph is Full");
        vertexList[counter++] = new Vertex(element);
    }

    @Override
    public void addEdge(Object a, Object b) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b))
            throw new GraphException("Cannot add edge between vertexes [" + a + "] y [" + b + "]");
        vertexList[indexOf(a)].edgesList.add(new EdgeWeight(b, null));
        vertexList[indexOf(b)].edgesList.add(new EdgeWeight(a, null));
    }

    private int indexOf(Object element) {
        for (int i = 0; i < counter; i++) {
            if (util.Utility.compare(vertexList[i].data, element) == 0)
                return i;
        }
        return -1;
    }

    @Override
    public void addWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if (!containsEdge(a, b))
            throw new GraphException("There is no edge between the vertexes[" + a + "] y [" + b + "]");
        updateEdgesListEdgeWeight(a, b, weight);
        updateEdgesListEdgeWeight(b, a, weight);
    }

    private void updateEdgesListEdgeWeight(Object a, Object b, Object weight) throws ListException {
        // Implementar si se requiere actualización del peso
    }

    @Override
    public void addEdgeWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b))
            throw new GraphException("Cannot add edge between vertexes [" + a + "] y [" + b + "]");
        if (!containsEdge(a, b)) {
            vertexList[indexOf(a)].edgesList.add(new EdgeWeight(b, weight));
            vertexList[indexOf(b)].edgesList.add(new EdgeWeight(a, weight));
        }
    }

    @Override
    public void removeVertex(Object element) throws GraphException, ListException {
        if (isEmpty()) throw new GraphException("Adjacency List Graph is Empty");
        if (containsVertex(element)) {
            for (int i = 0; i < counter; i++) {
                if (util.Utility.compare(vertexList[i].data, element) == 0) {
                    for (int j = 0; j < counter; j++) {
                        if (containsEdge(vertexList[j].data, element))
                            removeEdge(vertexList[j].data, element);
                    }
                    for (int j = i; j < counter - 1; j++) {
                        vertexList[j] = vertexList[j + 1];
                    }
                    counter--;
                }
            }
        }
    }

    @Override
    public void removeEdge(Object a, Object b) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b))
            throw new GraphException("There's no some of the vertexes");
        if (!vertexList[indexOf(a)].edgesList.isEmpty()) {
            vertexList[indexOf(a)].edgesList.remove(new EdgeWeight(b, null));
        }
        if (!vertexList[indexOf(b)].edgesList.isEmpty()) {
            vertexList[indexOf(b)].edgesList.remove(new EdgeWeight(a, null));
        }
    }

    @Override
    public String dfs() throws GraphException, StackException, ListException {
        setVisited(false);
        String info = vertexList[0].data + ", ";
        vertexList[0].setVisited(true);
        stack.clear();
        stack.push(0);
        while (!stack.isEmpty()) {
            int index = adjacentVertexNotVisited((int) stack.top());
            if (index == -1)
                stack.pop();
            else {
                vertexList[index].setVisited(true);
                info += vertexList[index].data + ", ";
                stack.push(index);
            }
        }
        return info;
    }

    @Override
    public String bfs() throws GraphException, QueueException, ListException {
        setVisited(false);
        String info = vertexList[0].data + ", ";
        vertexList[0].setVisited(true);
        queue.clear();
        queue.enQueue(0);
        int v2;
        while (!queue.isEmpty()) {
            int v1 = (int) queue.deQueue();
            while ((v2 = adjacentVertexNotVisited(v1)) != -1) {
                vertexList[v2].setVisited(true);
                info += vertexList[v2].data + ", ";
                queue.enQueue(v2);
            }
        }
        return info;
    }

    private void setVisited(boolean value) {
        for (int i = 0; i < counter; i++) {
            vertexList[i].setVisited(value);
        }
    }

    private int adjacentVertexNotVisited(int index) throws ListException {
        Object vertexData = vertexList[index].data;
        for (int i = 0; i < counter; i++) {
            if (!vertexList[index].edgesList.isEmpty()
                    && vertexList[i].edgesList.contains(new EdgeWeight(vertexData, null))
                    && !vertexList[i].isVisited())
                return i;
        }
        return -1;
    }

    public Vertex getVertex(int index) throws ListException {
        if (index < 0 || index >= counter) {
            throw new ListException("Índice fuera de rango");
        }
        return vertexList[index];
    }

    public SinglyLinkedList getSuccessors(Vertex v) throws ListException {
        if (v == null) {
            throw new ListException("Vértice nulo");
        }
        return v.getSuccessors();
    }

    public void insertVertex(Vertex vertex) {
        if (counter < vertexList.length) {
            vertexList[counter++] = vertex;
        } else {
            System.err.println("No se pueden insertar más vértices, capacidad máxima alcanzada.");
        }
    }

    public void insertEdge(Vertex origin, Vertex destination, int weight) throws ListException {
        if (origin == null || destination == null) {
            throw new ListException("Origen o destino no puede ser nulo");
        }
        origin.getSuccessors().add(new EdgeWeight(destination, weight));
    }

    @Override
    public String toString() {
        String result = "Adjacency List Graph Content...";
        for (int i = 0; i < counter; i++) {
            result += "\nThe vertex in the position: " + i + " is: " + vertexList[i].data;
            if (!vertexList[i].edgesList.isEmpty())
                result += "\n......EDGES AND WEIGHTS: " + vertexList[i].edgesList.toString();
        }
        return result;
    }
}