package datastructure.tree;

public class AVLTree {
    private BTreeNode root;

    public BTreeNode getRoot() {
        return this.root;
    }

    public int size() {
        return size(root);
    }

    private int size(BTreeNode node) {
        if (node == null) return 0;
        else return 1 + size(node.left) + size(node.right);
    }
    private int height(BTreeNode node) {
        if (node == null) return -1;
        return 1 + Math.max(height(node.left), height(node.right));
    }
    public void clear() {
        root = null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public boolean contains(Object element) {
        return binarySearch(root, element);
    }

    private boolean binarySearch(BTreeNode node, Object element) {
        if (node == null) return false;
        else if (util.Utility.compare(node.data, element) == 0) return true;
        else if (util.Utility.compare(element, node.data) < 0)
            return binarySearch(node.left, element);
        else return binarySearch(node.right, element);
    }

    public void add(Object element) {
        this.root = add(root, element, "root");
    }

    private BTreeNode add(BTreeNode node, Object element, String path) {
        if (node == null)
            node = new BTreeNode(element, path);
        else if (util.Utility.compare(element, node.data) < 0)
            node.left = add(node.left, element, path + "/left");
        else if (util.Utility.compare(element, node.data) > 0)
            node.right = add(node.right, element, path + "/right");

        node = reBalance(node, element);
        return node;
    }

    private BTreeNode reBalance(BTreeNode node, Object element) {
        int balance = getBalanceFactor(node);

        // Left Left Case
        if (balance > 1 && util.Utility.compare(element, node.left.data) < 0) {
            node.path += "/Simple-Right-Rotate";
            return rightRotate(node);
        }

        // Right Right Case
        if (balance < -1 && util.Utility.compare(element, node.right.data) > 0) {
            node.path += "/Simple-Left-Rotate";
            return leftRotate(node);
        }

        // Left Right Case
        if (balance > 1 && util.Utility.compare(element, node.left.data) > 0) {
            node.path += "/Double-Left-Right-Rotate";
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && util.Utility.compare(element, node.right.data) < 0) {
            node.path += "/Double-Right-Left-Rotate";
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    private int getBalanceFactor(BTreeNode node) {
        if (node == null) return 0;
        else return height(node.left) - height(node.right);
    }

    private BTreeNode leftRotate(BTreeNode node) {
        BTreeNode node1 = node.right;
        if (node1 != null) {
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

    public boolean remove(Object element) {
        if (isEmpty()) return false;
        boolean elementExists = contains(element);
        if (elementExists) {
            root = remove(root, element);
            return true;
        }
        return false;
    }

    private BTreeNode remove(BTreeNode node, Object element) {
        if (node != null) {
            if (util.Utility.compare(element, node.data) < 0)
                node.left = remove(node.left, element);
            else if (util.Utility.compare(element, node.data) > 0)
                node.right = remove(node.right, element);
            else if (util.Utility.compare(node.data, element) == 0) {
                if (node.left == null && node.right == null) return null;
                else if (node.left != null && node.right == null) return node.left;
                else if (node.left == null && node.right != null) return node.right;
                else {
                    Object value = min(node.right);
                    node.data = value;
                    node.right = remove(node.right, value);
                }
            }
        }
        return node;
    }

    public Object min() {
        if (isEmpty()) return null;
        return min(root);
    }

    private Object min(BTreeNode node) {
        if (node.left != null)
            return min(node.left);
        return node.data;
    }

    public Object max() {
        if (isEmpty()) return null;
        return max(root);
    }

    private Object max(BTreeNode node) {
        if (node.right != null)
            return max(node.right);
        return node.data;
    }

    public String inOrder() {
        return inOrder(root);
    }

    private String inOrder(BTreeNode node) {
        String result = "";
        if (node != null) {
            result = inOrder(node.left);
            result += node.data + " ";
            result += inOrder(node.right);
        }
        return result;
    }

    // Método para guardar el árbol en una lista inorden
    public void inOrderList(java.util.List<Object> result) {
        inOrderList(this.root, result);
    }

    private void inOrderList(BTreeNode node, java.util.List<Object> result) {
        if (node != null) {
            inOrderList(node.left, result);
            result.add(node.data);
            inOrderList(node.right, result);
        }
    }
}