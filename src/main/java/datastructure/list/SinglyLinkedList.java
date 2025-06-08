package datastructure.list;

public class SinglyLinkedList<T> implements List<T> {
    private Node<T> first; //apuntador al inicio de la lista

    public SinglyLinkedList() {
        this.first = null; //la lista no existe
    }

    @Override
    public int size() throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is empty");
        }
        Node<T> aux = first;
        int count=0;
        while(aux!=null){
            count++;
            aux = aux.next; //lo movemos al sgte nodo
        }
        return count;
    }

    @Override
    public void clear() {
        this.first = null; //anulamos la lista
    }

    @Override
    public boolean isEmpty() {
        return this.first == null; //si es nulo está vacía
    }

    @Override
    public boolean contains(T element) throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is empty");
        }
        Node<T> aux = first;
        while(aux!=null){
            if(util.Utility.compare(aux.data, element)==0){
                return true;
            }
            aux = aux.next; //lo movemos al sgte nodo
        }
        return false; //indica q el elemento no existe
    }

    @Override
    public void add(T element) {
        Node<T> newNode = new Node<>(element);
        if(isEmpty()){
            first = newNode;
        }else{
            Node<T> aux = first;
            //mientras no llegue al ult nodo
            while(aux.next!=null){
                aux=aux.next;
            }
            //una vez que se sale del while, quiere decir q
            //aux esta en el ult nodo, por lo q lo podemos enlazar
            //con el nuevo nodo
            aux.next = newNode;
        }
    }

    @Override
    public void addFirst(T element) {
        Node<T> newNode = new Node<>(element);
        if(isEmpty()){
            first = newNode;
        }else{
            newNode.next = first;
            first = newNode;
        }
    }

    @Override
    public void addLast(T element) {
        add(element);
    }

    @Override
    public void addInSortedList(T element) {
        // Implementación pendiente
    }

    @Override
    public void remove(T element) throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        //Caso 1. El elemento a suprimir esta al inicio
        if(util.Utility.compare(first.data, element)==0){
            first = first.next; //saltamos el primer nodo
        }else{  //Caso 2. El elemento a suprimir puede estar al medio o final
            Node<T> prev = first; //dejo un apuntador al nodo anterior
            Node<T> aux = first.next;
            while(aux!=null && !(util.Utility.compare(aux.data, element)==0)){
                prev = aux;
                aux = aux.next;
            }
            //se sale cuando alcanza nulo o cuando encuentra el elemento
            if(aux!=null && util.Utility.compare(aux.data, element)==0){
                //ya lo encontro, procedo a desenlazar el nodo
                prev.next = aux.next;
            }
        }
    }

    @Override
    public T removeFirst() throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        T element = first.data;
        first = first.next;
        return element;
    }

    @Override
    public T removeLast() throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }

        if(first.next == null){
            T element = first.data;
            first = null;
            return element;
        }

        Node<T> aux = first;
        Node<T> prev = null;

        while(aux.next != null){
            prev = aux;
            aux = aux.next;
        }

        T element = aux.data;
        prev.next = null;
        return element;
    }

    @Override
    public void sort() throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        for (int i = 1; i <= size() ; i++) {
            for (int j = i+1; j <= size() ; j++) {
                if(util.Utility.compare(getNode(j).data, getNode(i).data)<0){
                    T aux = getNode(i).data;
                    getNode(i).data = getNode(j).data;
                    getNode(j).data = aux;
                }
            }
        }
    }

    @Override
    public int indexOf(T element) throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        Node<T> aux = first;
        int index=1; //la lista inicia en 1
        while(aux!=null){
            if(util.Utility.compare(aux.data, element)==0){
                return index;
            }
            index++; //incremento el indice
            aux=aux.next; //muevo aux al sgte nodo
        }
        return -1; //indica q el elemento no existe
    }

    @Override
    public T getFirst() throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        return first.data;
    }

    @Override
    public T getLast() throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        Node<T> aux = first;
        //mientras no llegue al ult nodo
        while(aux.next!=null){
            aux=aux.next;
        }
        //se sale del while cuando aux esta en el ult nodo
        return aux.data;
    }

    @Override
    public Object getPrev(T element) throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        if(util.Utility.compare(first.data, element)==0){
            return "It's the first, it has no previous";
        }
        Node<T> aux = first;
        //mientras no llegue al ult nodo
        while(aux.next!=null){
            if(util.Utility.compare(aux.next.data, element)==0){
                return aux.data; //retornamos la data del nodo actual
            }
            aux=aux.next;
        }
        return "Does not exist in Single Linked List";
    }

    @Override
    public Object getNext(T element) throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }

        Node<T> aux = first;
        while(aux!=null){
            if(util.Utility.compare(aux.data, element)==0 && aux.next != null){
                return aux.next.data;
            }
            aux = aux.next;
        }
        return "Does not exist or is the last element in Single Linked List";
    }

    @Override
    public Node<T> getNode(int index) throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        Node<T> aux = first;
        int i = 1; // pos del primer nodo
        while(aux!=null){
            if(util.Utility.compare(i, index)==0) {  //ya encontro el indice
                return aux;
            }
            i++; //incremento la var local
            aux = aux.next; //muevo aux al sgte nodo
        }
        return null; //si llega aqui es xq no encontro el index
    }


    public Node<T> getNode(T element) throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        Node<T> aux = first;
        while(aux!=null){
            if(util.Utility.compare(aux.data, element)==0) {  //ya encontro el elemento
                return aux;
            }
            aux = aux.next; //muevo aux al sgte nodo
        }
        return null; //si llega aqui es xq no encontro el index
    }

    @Override
    public String toString() {
        String result = "";
        Node<T> aux = first;
        while(aux!=null){
            result+= "\n"+aux.data;
            aux = aux.next;
        }
        return result;
    }
}