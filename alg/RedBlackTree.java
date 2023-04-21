public class RedBlackTree<K extends Comparable<K>> {
    public enum Color {
        RED, BLACK;
    }

    private K element;
    private Color color;
    private RedBlackTree<K> left;
    private RedBlackTree<K> right;
    private RedBlackTree<K> parent = null;

    /**
     * Create a new node in a Red-Black tree.
     * @param element
     * @param color
     * @param left
     * @param right
     */
    public RedBlackTree(K element, Color color, RedBlackTree<K> left, RedBlackTree<K> right) {
        this.element = element;
        this.color = color;
        this.left = left;
        this.right = right;
        if (left != null) {
            left.parent = this;
        }
        if (right != null) {
            right.parent = this;
        }
    }

    /**
     * Verify the invariants for this node, and return the black height of the node.
     * @param isRoot
     * @param min
     * @param max
     * @return Black height, or -1 if the node violates any invariants.
     */
    private int verifyInvariants(boolean isRoot, K min, K max) {
        if (isRoot && color != Color.BLACK) {
            System.out.println("Root is not black");
            return -1;
        }
        if ((left != null || right != null) && element == null) {
            System.out.println("Internal node with null value");
            return -1;
        }
        if (left == null && right == null && element != null) {
            System.out.println("External node with non-null value");
            return -1;
        }
        if (left == null && right == null && color != Color.BLACK) {
            System.out.println("External node is not black");
            return -1;
        }
        if (left != null && left.parent != this) {
            System.out.println("Incorrect parent pointer");
            return -1;
        }
        if (right != null && right.parent != this) {
            System.out.println("Incorrect parent pointer");
            return -1;
        }
        if (color == Color.RED && left != null && left.color != Color.BLACK) {
            System.out.println("Chlild of red node is not black");
            return -1;
        }
        if (color == Color.RED && right != null && right.color != Color.BLACK) {
            System.out.println("Chlild of red node is not black");
            return -1;
        }
        if (element != null && min != null && min.compareTo(element) > 0) {
            System.out.println("Element " + element + " too small");
            return -1;
        }
        if (element != null && max != null && max.compareTo(element) < 0) {
            System.out.println("Element " + element + " too large");
            return -1;
        }
        int leftDepth = left != null ? left.verifyInvariants(false, min, element) : 0;
        if (leftDepth == -1) {
            System.out.println("Invariant violated in child node");
            return -1;
        }
        int rightDepth = right != null ? right.verifyInvariants(false, element, max) : 0;
        if (rightDepth == -1) {
            System.out.println("Invariant violated in child node");
            return -1;
        }
        if (leftDepth != rightDepth) {
            System.out.println("Black depth not equal (" + leftDepth + " and " + rightDepth + ")");
        }
        return leftDepth + (color == Color.BLACK ? 1 : 0);
    }

    /**
     * Verify the invariants for this node.
     */
    public void verifyInvariants() {
        if (verifyInvariants(true, null, null) == -1) {
            throw new IllegalStateException();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(color == Color.RED ? '<' : '[');
        if (left != null) {
            sb.append(left);
            sb.append(' ');
        }
        if (element != null) {
            sb.append(element);
        }
        if (right != null) {
            sb.append(' ');
            sb.append(right);
        }
        sb.append(color == Color.RED ? '>' : ']');
        return sb.toString();
    }
}
