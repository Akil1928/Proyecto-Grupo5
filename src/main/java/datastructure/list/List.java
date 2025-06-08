
package datastructure.list;

public interface List<T> {
    int size() throws ListException;
    void clear();
    boolean isEmpty();
    boolean contains(T element) throws ListException;
    void add(T element);
    void addFirst(T element);
    void addLast(T element);
    void addInSortedList(T element);
    void remove(T element) throws ListException;
    T removeFirst() throws ListException;
    T removeLast() throws ListException;
    void sort() throws ListException;
    int indexOf(T element) throws ListException;
    T getFirst() throws ListException;
    T getLast() throws ListException;
    Object getPrev(T element) throws ListException;
    Object getNext(T element) throws ListException;
    Node<T> getNode(int index) throws ListException;
}