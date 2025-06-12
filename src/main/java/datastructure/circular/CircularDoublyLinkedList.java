package datastructure.circular;

public class CircularDoublyLinkedList<T> {
    private Node head;  // Nodo de inicio
    private Node tail;  // Nodo final
    private int size;   // Número de elementos en la lista

    public CircularDoublyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // Nodo de la lista
    private class Node {
        T data;
        Node next;
        Node prev;

        public Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    // Agregar un vuelo al final de la lista
    public void addFlight(T data) {
        Node newNode = new Node(data);
        if (size == 0) {
            head = newNode;
            tail = newNode;
            newNode.next = newNode;
            newNode.prev = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            newNode.next = head;
            head.prev = newNode;
            tail = newNode;
        }
        size++;
    }

    // Eliminar un vuelo
    public void removeFlight(T data) {
        if (size == 0) return;

        Node current = head;
        do {
            if (current.data.equals(data)) {
                if (current == head) {
                    head = head.next;
                    tail.next = head;
                    head.prev = tail;
                } else if (current == tail) {
                    tail = tail.prev;
                    tail.next = head;
                    head.prev = tail;
                } else {
                    current.prev.next = current.next;
                    current.next.prev = current.prev;
                }
                size--;
                return;
            }
            current = current.next;
        } while (current != head);
    }

    // Mostrar todos los vuelos
    public void displayFlights() {
        if (size == 0) {
            System.out.println("No flights available.");
            return;
        }

        Node current = head;
        do {
            System.out.println(current.data);
            current = current.next;
        } while (current != head);
    }

    // Obtener el tamaño de la lista
    public int size() {
        return size;
    }
}
