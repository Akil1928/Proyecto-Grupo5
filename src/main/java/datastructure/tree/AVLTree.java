package datastructure.tree;

public class AVLTree implements Tree{
    private BTreeNode root;

    @Override
    public int size() throws TreeException {
        if(isEmpty())
            throw new TreeException("AVL Binary Search Tree is empty");
        return size(root);
    }

    private int size(BTreeNode node){
        if(node==null) return 0;
        else return 1 + size(node.left) + size(node.right);
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean isEmpty() {
        return root==null;
    }

    @Override
    public boolean contains(Object element) throws TreeException {
        if(isEmpty())
            throw new TreeException("AVL Binary Search Tree is empty");
        return binarySearch(root, element);
    }

    private boolean binarySearch(BTreeNode node, Object element){
        if(node==null) return false;
        else if(util.Utility.compare(node.data, element)==0) return true;
        else if(util.Utility.compare(element, node.data)<0)
            return binarySearch(node.left, element);
        else return binarySearch(node.right, element);
    }

    @Override
    public void add(Object element) {
        this.root = add(root, element, "root");
    }

    private BTreeNode add(BTreeNode node, Object element, String path){
        if(node==null)
            node = new BTreeNode(element, path);
        else if(util.Utility.compare(element, node.data)<0)
            node.left = add(node.left, element, path+"/left");
        else if(util.Utility.compare(element, node.data)>0)
            node.right = add(node.right, element, path+"/right");

        node = reBalance(node, element);
        return node;
    }

    private BTreeNode reBalance(BTreeNode node, Object element) {
        int balance = getBalanceFactor(node);

        // Caso-1. Left Left Case
        if (balance > 1 && util.Utility.compare(element, node.left.data)<0){
            node.path += "/Simple-Right-Rotate";
            return rightRotate(node);
        }

        // Caso-2. Right Right Case
        if (balance < -1 && util.Utility.compare(element, node.right.data)>0){
            node.path += "/Simple-Left-Rotate";
            return leftRotate(node);
        }

        // Caso-3. Left Right Case
        if (balance > 1 && util.Utility.compare(element, node.left.data)>0) {
            node.path += "/Double-Left-Right-Rotate";
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Caso-4. Right Left Case
        if (balance < -1 && util.Utility.compare(element, node.right.data)<0) {
            node.path += "/Double-Right-Left-Rotate";
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    private int getBalanceFactor(BTreeNode node){
        if(node==null){
            return 0;
        }else{
            return height(node.left) - height(node.right);
        }
    }

    private BTreeNode leftRotate(BTreeNode node) {
        BTreeNode node1 = node.right;
        if (node1 != null){
            BTreeNode node2 = node1.left;
            node1.left = node;
            node.right = node2;
        }
        return node1;
    }

    private BTreeNode rightRotate(BTreeNode node) {
        BTreeNode node1 = node.left;
        if (node1 != null) {
            BTreeNode node2 = node1.right;
            node1.right = node;
            node.left = node2;
        }
        return node1;
    }

    @Override
    public boolean remove(Object element) throws TreeException {
        if(isEmpty())
            throw new TreeException("AVL Binary Search Tree is empty");
        boolean elementExists = contains(element);
        if (elementExists) {
            root = remove(root, element);
            return true;
        }
        return false;
    }

    private BTreeNode remove(BTreeNode node, Object element) throws TreeException{
        if(node!=null){
            if(util.Utility.compare(element, node.data)<0)
                node.left = remove(node.left, element);
            else if(util.Utility.compare(element, node.data)>0)
                node.right = remove(node.right, element);
            else if(util.Utility.compare(node.data, element)==0){
                if(node.left==null && node.right==null) return null;
                else if (node.left!=null&&node.right==null) {
                    return node.left;
                }
                else if (node.left==null&&node.right!=null) {
                    return node.right;
                }
                else{
                    Object value = min(node.right);
                    node.data = value;
                    node.right = remove(node.right, value);
                }
            }
        }
        return node;
    }

    public void rebalance() throws TreeException {
        if (isEmpty()) {
            throw new TreeException("AVL Binary Search Tree is empty");
        }

        // Crear un nuevo árbol AVL balanceado con todos los elementos del árbol actual
        AVLTree newAVL = new AVLTree();

        // Obtener todos los elementos del árbol actual en orden (InOrder)
        String inOrderElements = inOrder();
        String[] elements = inOrderElements.trim().split(" ");

        // Insertar los elementos en el nuevo árbol de manera balanceada
        insertBalanced(newAVL, elements, 0, elements.length - 1);

        // Reemplazar el árbol actual con el nuevo árbol balanceado
        this.root = newAVL.root;
    }

    private void insertBalanced(AVLTree tree, String[] elements, int start, int end) {
        if (start <= end) {
            // Encontrar el punto medio
            int mid = (start + end) / 2;

            // Insertar el elemento del medio
            tree.add(Integer.parseInt(elements[mid]));

            // Insertar los elementos a la izquierda del medio
            insertBalanced(tree, elements, start, mid - 1);

            // Insertar los elementos a la derecha del medio
            insertBalanced(tree, elements, mid + 1, end);
        }
    }

    @Override
    public int height(Object element) throws TreeException {
        if(isEmpty())
            throw new TreeException("AVL Binary Search Tree is empty");
        return height(root, element, 0);
    }

    private int height(BTreeNode node, Object element, int level){
        if(node==null) return 0;
        else if(util.Utility.compare(node.data, element)==0) return level;
        else return Math.max(height(node.left, element, ++level),
                    height(node.right, element, level));
    }

    @Override
    public int height() throws TreeException {
        if(isEmpty())
            throw new TreeException("AVL Binary Search Tree is empty");
        return height(root);
    }

    private int height(BTreeNode node, int level){
        if(node==null) return level-1;
        return Math.max(height(node.left, ++level),
                height(node.right, level));
    }

    private int height(BTreeNode node){
        if(node==null) return -1;
        return Math.max(height(node.left), height(node.right)) + 1;
    }

    @Override
    public Object min() throws TreeException {
        if(isEmpty())
            throw new TreeException("AVL Binary Search Tree is empty");
        return min(root);
    }

    private Object min(BTreeNode node){
        if(node.left!=null)
            return min(node.left);
        return node.data;
    }

    @Override
    public Object max() throws TreeException {
        if(isEmpty())
            throw new TreeException("AVL Binary Search Tree is empty");
        return max(root);
    }

    private Object max(BTreeNode node){
        if(node.right!=null)
            return max(node.right);
        return node.data;
    }

    @Override
    public String preOrder() throws TreeException {
        if(isEmpty())
            throw new TreeException("AVL Binary Search Tree is empty");
        return preOrder(root);
    }

    private String preOrder(BTreeNode node){
        String result="";
        if(node!=null){
            result = node.data+" ";
            result += preOrder(node.left);
            result += preOrder(node.right);
        }
        return  result;
    }

    private String preOrderPath(BTreeNode node){
        String result="";
        if(node!=null){
            result  = node.data+"("+node.path+")"+" ";
            result += preOrderPath(node.left);
            result += preOrderPath(node.right);
        }
        return  result;
    }

    @Override
    public String inOrder() throws TreeException {
        if(isEmpty())
            throw new TreeException("AVL Binary Search Tree is empty");
        return inOrder(root);
    }

    private String inOrder(BTreeNode node){
        String result="";
        if(node!=null){
            result  = inOrder(node.left);
            result += node.data+" ";
            result += inOrder(node.right);
        }
        return  result;
    }

    @Override
    public String postOrder() throws TreeException {
        if(isEmpty())
            throw new TreeException("AVL Binary Search Tree is empty");
        return postOrder(root);
    }

    private String postOrder(BTreeNode node){
        String result="";
        if(node!=null){
            result  = postOrder(node.left);
            result += postOrder(node.right);
            result += node.data+" ";
        }
        return result;
    }

    public boolean isBalanced() {
        if (isEmpty()) {
            return true;
        }
        return isBalanced(root);
    }

    private boolean isBalanced(BTreeNode node) {
        if (node == null) {
            return true;
        }

        int leftHeight = height(node.left);
        int rightHeight = height(node.right);

        if (Math.abs(leftHeight - rightHeight) > 1) {
            return false;
        }

        return isBalanced(node.left) && isBalanced(node.right);
    }

    public Object father(Object element) throws TreeException {
        if (isEmpty()) {
            throw new TreeException("AVL Binary Search Tree is empty");
        }
        if (!contains(element)) {
            throw new TreeException("Element does not exist in the tree");
        }

        // Si el elemento es la raíz, no tiene padre
        if (util.Utility.compare(root.data, element) == 0) {
            return null;
        }

        return father(root, element);
    }

    private Object father(BTreeNode node, Object element) {
        if (node == null) {
            return null;
        }

        // Verificar si alguno de los hijos es el elemento buscado
        if ((node.left != null && util.Utility.compare(node.left.data, element) == 0) ||
                (node.right != null && util.Utility.compare(node.right.data, element) == 0)) {
            return node.data;
        }

        // Buscar en el subárbol correspondiente según el valor del elemento
        if (util.Utility.compare(element, node.data) < 0) {
            return father(node.left, element);
        } else {
            return father(node.right, element);
        }
    }

    public Object brother(Object element) throws TreeException {
        if (isEmpty()) {
            throw new TreeException("AVL Binary Search Tree is empty");
        }
        if (!contains(element)) {
            throw new TreeException("Element does not exist in the tree");
        }

        // Si el elemento es la raíz, no tiene hermano
        if (util.Utility.compare(root.data, element) == 0) {
            return null;
        }

        return brother(root, element);
    }

    private Object brother(BTreeNode node, Object element) {
        if (node == null) {
            return null;
        }

        // Verificar si alguno de los hijos es el elemento buscado
        if (node.left != null && util.Utility.compare(node.left.data, element) == 0) {
            // El elemento está en el hijo izquierdo, devolver el derecho si existe
            return node.right != null ? node.right.data : null;
        } else if (node.right != null && util.Utility.compare(node.right.data, element) == 0) {
            // El elemento está en el hijo derecho, devolver el izquierdo si existe
            return node.left != null ? node.left.data : null;
        }

        // Buscar en el subárbol correspondiente según el valor del elemento
        if (util.Utility.compare(element, node.data) < 0) {
            return brother(node.left, element);
        } else {
            return brother(node.right, element);
        }
    }

    public String children(Object element) throws TreeException {
        if (isEmpty()) {
            throw new TreeException("AVL Binary Search Tree is empty");
        }
        if (!contains(element)) {
            throw new TreeException("Element does not exist in the tree");
        }

        return children(root, element);
    }

    private String children(BTreeNode node, Object element) {
        if (node == null) {
            return "";
        }

        // Si encontramos el nodo, devolver sus hijos
        if (util.Utility.compare(node.data, element) == 0) {
            String result = "";
            if (node.left != null) {
                result += "Izquierdo: " + node.left.data + " ";
            }
            if (node.right != null) {
                result += "Derecho: " + node.right.data;
            }
            return result.trim();
        }

        // Buscar en el subárbol correspondiente según el valor del elemento
        if (util.Utility.compare(element, node.data) < 0) {
            return children(node.left, element);
        } else {
            return children(node.right, element);
        }
    }
    public BTreeNode getRoot() {
        return this.root;
    }
    @Override
    public String toString() {
        String result="AVL Binary Search Tree Content:";
        try {
            result = "PreOrder: "+preOrderPath(root);
            result+= "\nPreOrder: "+preOrder();
            result+= "\nInOrder: "+inOrder();
            result+= "\nPostOrder: "+postOrder();

        } catch (TreeException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
