package datastructure.circular;

public interface List<T> {
    int size() throws ListException;
    void clear();
    boolean isEmpty();
    void add(T element);

    void addFirst(Object element);

    void addLast(Object element);

    void addInSortedList(Object element);

    void remove(T element) throws ListException;
    boolean contains(T element) throws ListException;
    T get(int index) throws ListException;
    void set(int index, T element) throws ListException;

    Object removeFirst() throws ListException;

    Object removeLast() throws ListException;

    void sort() throws ListException;

    int indexOf(T element) throws ListException;

    Object getFirst() throws ListException;

    Object getLast() throws ListException;

    Object getPrev(Object element) throws ListException;

    Object getNext(Object element) throws ListException;

    Node getNode(int index) throws ListException;
}