package datastructure.circular;

public class DoublyLinkedList<T> implements List<T> {
    private Node<T> first;
    private Node<T> last;
    private int size;

    public DoublyLinkedList() {
        this.first = null;
        this.last = null;
        this.size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        this.first = null;
        this.last = null;
        this.size = 0;
    }

    @Override
    public boolean isEmpty() {
        return first == null;
    }

    @Override
    public void add(T element) {
        Node<T> newNode = new Node<>(element);
        if (isEmpty()) {
            first = last = newNode;
        } else {
            newNode.setPrev(last);
            last.setNext(newNode);
            last = newNode;
        }
        size++;
    }

    @Override
    public void addFirst(Object element) {

    }

    @Override
    public void addLast(Object element) {

    }

    @Override
    public void addInSortedList(Object element) {

    }

    @Override
    public void remove(T element) throws ListException {
        if (isEmpty()) {
            throw new ListException("The list is empty");
        }

        // Si el elemento a eliminar es el primero
        if (util.Utility.compare(first.getData(), element) == 0) {
            first = first.getNext();
            if (first != null) {
                first.setPrev(null);
            } else {
                last = null;
            }
            size--;
            return;
        }

        // Si el elemento a eliminar es el último
        if (util.Utility.compare(last.getData(), element) == 0) {
            last = last.getPrev();
            if (last != null) {
                last.setNext(null);
            } else {
                first = null;
            }
            size--;
            return;
        }

        // Si el elemento está en medio de la lista
        Node<T> current = first.getNext();
        while (current != null) {
            if (util.Utility.compare(current.getData(), element) == 0) {
                current.getPrev().setNext(current.getNext());
                if (current.getNext() != null) {
                    current.getNext().setPrev(current.getPrev());
                }
                size--;
                return;
            }
            current = current.getNext();
        }

        throw new ListException("Element not found in the list");
    }

    @Override
    public boolean contains(T element) throws ListException {
        if (isEmpty()) {
            throw new ListException("The list is empty");
        }

        Node<T> current = first;
        while (current != null) {
            if (util.Utility.compare(current.getData(), element) == 0) {
                return true;
            }
            current = current.getNext();
        }

        return false;
    }

    @Override
    public T get(int index) throws ListException {
        if (isEmpty()) {
            throw new ListException("The list is empty");
        }

        if (index < 0 || index >= size) {
            throw new ListException("Index out of bounds");
        }

        Node<T> current = first;
        int i = 0;

        while (i < index) {
            current = current.getNext();
            i++;
        }

        return current.getData();
    }

    @Override
    public void set(int index, T element) throws ListException {
        if (isEmpty()) {
            throw new ListException("The list is empty");
        }

        if (index < 0 || index >= size) {
            throw new ListException("Index out of bounds");
        }

        Node<T> current = first;
        int i = 0;

        while (i < index) {
            current = current.getNext();
            i++;
        }

        current.setData(element);
    }

    @Override
    public Object removeFirst() throws ListException {
        return null;
    }

    @Override
    public Object removeLast() throws ListException {
        return null;
    }

    @Override
    public void sort() throws ListException {

    }

    @Override
    public int indexOf(T element) throws ListException {
        if (isEmpty()) {
            throw new ListException("The list is empty");
        }

        Node<T> current = first;
        int index = 0;

        while (current != null) {
            if (util.Utility.compare(current.getData(), element) == 0) {
                return index;
            }
            current = current.getNext();
            index++;
        }

        return -1; // Element not found
    }

    @Override
    public Object getFirst() throws ListException {
        return null;
    }

    @Override
    public Object getLast() throws ListException {
        return null;
    }

    @Override
    public Object getPrev(Object element) throws ListException {
        return null;
    }

    @Override
    public Object getNext(Object element) throws ListException {
        return null;
    }

    @Override
    public Node getNode(int index) throws ListException {
        if (isEmpty()) {
            throw new ListException("The list is empty");
        }

        if (index < 0 || index >= size) {
            throw new ListException("Index out of bounds: " + index);
        }

        Node<T> current = first;
        int i = 0;

        while (i < index) {
            current = current.getNext();
            i++;
        }

        return current;
    }
    @Override
    public String toString() {
        if (isEmpty()) {
            return "Empty list";
        }

        StringBuilder sb = new StringBuilder();
        Node<T> current = first;

        while (current != null) {
            sb.append(current.getData().toString()).append("\n");
            current = current.getNext();
        }

        return sb.toString();
    }
}