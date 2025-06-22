package datastructure.graph;

import datastructure.list.ListException;
import datastructure.list.SinglyLinkedList;

public class DirectedSinglyLinkedListGraph implements Graph {
    private Vertex[] vertexList;
    private int n;
    private int counter;

    public DirectedSinglyLinkedListGraph(int n) {
        this.n = n;
        this.counter = 0;
        this.vertexList = new Vertex[n];
    }

    @Override
    public int size() throws ListException {
        return counter;
    }

    @Override
    public void clear() {
        this.counter = 0;
        this.vertexList = new Vertex[n];
    }

    @Override
    public boolean isEmpty() {
        return counter == 0;
    }

    @Override
    public boolean containsVertex(Object element) throws GraphException, ListException {
        if (isEmpty()) {
            throw new GraphException("El grafo está vacío");
        }
        for (int i = 0; i < counter; i++) {
            if (vertexList[i].data.equals(element)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsEdge(Object a, Object b) throws GraphException, ListException {
        if (isEmpty()) {
            throw new GraphException("El grafo está vacío");
        }

        int indexA = indexOf(a);
        int indexB = indexOf(b);

        if (indexA == -1 || indexB == -1) {
            return false;
        }

        // En un grafo dirigido, solo verificamos si hay un borde de A a B
        SinglyLinkedList<EdgeWeight> edges = vertexList[indexA].edgesList;
        if (edges.isEmpty()) return false;

        for (int i = 1; i <= edges.size(); i++) {
            EdgeWeight edge = edges.get(i);
            if (edge.getEdge().equals(b)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addVertex(Object element) throws GraphException, ListException {
        if (counter >= n) {
            throw new GraphException("El grafo está lleno");
        }
        if (containsVertex(element)) {
            throw new GraphException("El vértice ya existe en el grafo");
        }
        vertexList[counter++] = new Vertex(element);
    }

    @Override
    public void addEdge(Object a, Object b) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b)) {
            throw new GraphException("El vértice de origen o destino no existe");
        }

        // En un grafo dirigido, solo agregamos un borde de A a B
        int indexA = indexOf(a);
        int indexB = indexOf(b);

        // Verificar si el borde ya existe
        if (containsEdge(a, b)) {
            throw new GraphException("El borde ya existe");
        }

        // Agregar borde de A a B
        EdgeWeight edge = new EdgeWeight(b, null);
        vertexList[indexA].edgesList.add(edge);
    }

    @Override
    public void addWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b)) {
            throw new GraphException("El vértice de origen o destino no existe");
        }

        if (!containsEdge(a, b)) {
            throw new GraphException("El borde no existe");
        }

        // Actualizar el peso del borde de A a B
        updateEdgesListEdgeWeight(a, b, weight);
    }

    private void updateEdgesListEdgeWeight(Object a, Object b, Object weight) throws ListException {
        int indexA = indexOf(a);
        SinglyLinkedList<EdgeWeight> edges = vertexList[indexA].edgesList;

        for (int i = 1; i <= edges.size(); i++) {
            EdgeWeight edge = edges.get(i);
            if (edge.getEdge().equals(b)) {
                edge.setWeight(weight);
                return;
            }
        }
    }

    @Override
    public void addEdgeWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b)) {
            throw new GraphException("El vértice de origen o destino no existe");
        }

        // Si el borde no existe, lo creamos con el peso
        if (!containsEdge(a, b)) {
            int indexA = indexOf(a);
            EdgeWeight edge = new EdgeWeight(b, weight);
            vertexList[indexA].edgesList.add(edge);
        } else {
            // Si el borde ya existe, actualizamos su peso
            updateEdgesListEdgeWeight(a, b, weight);
        }
    }

    private int indexOf(Object element) {
        for (int i = 0; i < counter; i++) {
            if (vertexList[i].data.equals(element)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void removeVertex(Object element) throws GraphException, ListException {
        if (isEmpty()) {
            throw new GraphException("El grafo está vacío");
        }

        int index = indexOf(element);
        if (index == -1) {
            throw new GraphException("El vértice no existe");
        }

        // Eliminar todas las aristas que apuntan a este vértice
        for (int i = 0; i < counter; i++) {
            if (i != index) {
                SinglyLinkedList<EdgeWeight> edges = vertexList[i].edgesList;
                for (int j = 1; j <= edges.size(); j++) {
                    EdgeWeight edge = edges.get(j);
                    if (edge.getEdge().equals(element)) {
                        edges.remove(edge);
                        j--; // Ajustar el índice después de eliminar
                    }
                }
            }
        }

        // Desplazar todos los vértices restantes
        for (int i = index; i < counter - 1; i++) {
            vertexList[i] = vertexList[i + 1];
        }

        counter--;
    }

    @Override
    public void removeEdge(Object a, Object b) throws GraphException, ListException {
        if (isEmpty()) {
            throw new GraphException("El grafo está vacío");
        }

        if (!containsVertex(a) || !containsVertex(b)) {
            throw new GraphException("El vértice de origen o destino no existe");
        }

        if (!containsEdge(a, b)) {
            throw new GraphException("El borde no existe");
        }

        int indexA = indexOf(a);
        SinglyLinkedList<EdgeWeight> edges = vertexList[indexA].edgesList;

        for (int i = 1; i <= edges.size(); i++) {
            EdgeWeight edge = edges.get(i);
            if (edge.getEdge().equals(b)) {
                edges.remove(edge);
                return;
            }
        }
    }

    public Vertex getVertex(int index) throws ListException {
        if (index < 0 || index >= counter) {
            throw new ListException("Índice fuera de rango");
        }
        return vertexList[index];
    }

    public SinglyLinkedList<EdgeWeight> getSuccessors(Vertex v) throws ListException {
        if (v == null) {
            throw new ListException("El vértice es nulo");
        }
        return v.edgesList;
    }

    // Método para el algoritmo de Dijkstra
    public Object[] dijkstra(Object source) throws GraphException, ListException {
        if (isEmpty()) {
            throw new GraphException("El grafo está vacío");
        }

        if (!containsVertex(source)) {
            throw new GraphException("El vértice de origen no existe");
        }

        int sourceIndex = indexOf(source);
        double[] distance = new double[counter];
        Object[] previous = new Object[counter];
        boolean[] visited = new boolean[counter];

        // Inicializar distancias
        for (int i = 0; i < counter; i++) {
            distance[i] = Double.POSITIVE_INFINITY;
            previous[i] = null;
            visited[i] = false;
        }

        // La distancia al vértice de origen es 0
        distance[sourceIndex] = 0;

        // Procesar todos los vértices
        for (int count = 0; count < counter; count++) {
            // Encontrar el vértice con la distancia mínima
            int u = -1;
            double minDistance = Double.POSITIVE_INFINITY;

            for (int i = 0; i < counter; i++) {
                if (!visited[i] && distance[i] < minDistance) {
                    minDistance = distance[i];
                    u = i;
                }
            }

            // Si no hay más vértices alcanzables, salir
            if (u == -1) break;

            visited[u] = true;

            // Actualizar distancias a los vecinos
            SinglyLinkedList<EdgeWeight> edges = vertexList[u].edgesList;
            for (int i = 1; i <= edges.size(); i++) {
                EdgeWeight edge = edges.get(i);
                int v = indexOf(edge.getEdge());

                Object weightObj = edge.getWeight();
                if (weightObj == null) continue;

                double weight = Double.parseDouble(weightObj.toString());
                double totalDistance = distance[u] + weight;

                if (totalDistance < distance[v]) {
                    distance[v] = totalDistance;
                    previous[v] = vertexList[u].data;
                }
            }
        }

        // Devolver el array de predecesores para reconstruir caminos
        return previous;
    }

    // Método para obtener la distancia mínima después de ejecutar Dijkstra
    public double[] getDistances(Object source) throws GraphException, ListException {
        if (isEmpty()) {
            throw new GraphException("El grafo está vacío");
        }

        if (!containsVertex(source)) {
            throw new GraphException("El vértice de origen no existe");
        }

        int sourceIndex = indexOf(source);
        double[] distance = new double[counter];
        boolean[] visited = new boolean[counter];

        // Inicializar distancias
        for (int i = 0; i < counter; i++) {
            distance[i] = Double.POSITIVE_INFINITY;
            visited[i] = false;
        }

        // La distancia al vértice de origen es 0
        distance[sourceIndex] = 0;

        // Procesar todos los vértices
        for (int count = 0; count < counter; count++) {
            // Encontrar el vértice con la distancia mínima
            int u = -1;
            double minDistance = Double.POSITIVE_INFINITY;

            for (int i = 0; i < counter; i++) {
                if (!visited[i] && distance[i] < minDistance) {
                    minDistance = distance[i];
                    u = i;
                }
            }

            // Si no hay más vértices alcanzables, salir
            if (u == -1) break;

            visited[u] = true;

            // Actualizar distancias a los vecinos
            SinglyLinkedList<EdgeWeight> edges = vertexList[u].edgesList;
            for (int i = 1; i <= edges.size(); i++) {
                EdgeWeight edge = edges.get(i);
                int v = indexOf(edge.getEdge());

                Object weightObj = edge.getWeight();
                if (weightObj == null) continue;

                double weight = Double.parseDouble(weightObj.toString());
                double totalDistance = distance[u] + weight;

                if (totalDistance < distance[v]) {
                    distance[v] = totalDistance;
                }
            }
        }

        return distance;
    }

    @Override
    public String dfs() throws GraphException {
        throw new UnsupportedOperationException("Método no implementado para este tipo de grafo");
    }

    @Override
    public String bfs() throws GraphException {
        throw new UnsupportedOperationException("Método no implementado para este tipo de grafo");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DirectedSinglyLinkedListGraph:\n");

        for (int i = 0; i < counter; i++) {
            sb.append(vertexList[i].data).append(" -> ");
            try {
                SinglyLinkedList<EdgeWeight> edges = vertexList[i].edgesList;
                for (int j = 1; j <= edges.size(); j++) {
                    EdgeWeight edge = edges.get(j);
                    sb.append(edge.getEdge());
                    if (edge.getWeight() != null) {
                        sb.append("(").append(edge.getWeight()).append(")");
                    }
                    if (j < edges.size()) {
                        sb.append(", ");
                    }
                }
            } catch (ListException e) {
                sb.append("Error al obtener bordes");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}