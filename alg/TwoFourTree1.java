import java.util.LinkedList;
import java.util.List;

public class TwoFourTree1<K extends Comparable<K>> {
    private static class Pair<K extends Comparable<K>> {
        private K value;
        private Node<K> child;

        Pair(K value, Node<K> child) {
            this.value = value;
            this.child = child;
        }
    }
    private static class Node<K extends Comparable<K>> {
        /**
         * Pairs of elements in the node, with the corresponding child. The child
         * should be followed for values smaller than the given element. If element is null,
         * it should be followed for values larger than the largest element in the node.
         */
        private final List<Pair<K>> pairs;

        /**
         * The parent node of this node.
         */
        private Node<K> parent = null;

        /**
         * Whether this node is an external node.
         */
        private boolean isExternal = true;

        /**
         * Create a new external node.
         */
        Node() {
            pairs = new LinkedList<>();
            pairs.add(new Pair<>(null, null));
        }

        /**
         * Create a new 2-node.
         */
        Node(K value1, Node<K> child1, Node<K> child2) {
            pairs = new LinkedList<>();
            pairs.add(new Pair<>(value1, child1));
            pairs.add(new Pair<>(null, child2));
            child1.parent = this;
            child2.parent = this;
            isExternal = false;
        }

        /**
         * Create a new 3-node.
         */
        Node(K value1, K value2, Node<K> child1, Node<K> child2, Node<K> child3) {
            pairs = new LinkedList<>();
            pairs.add(new Pair<>(value1, child1));
            pairs.add(new Pair<>(value2, child2));
            pairs.add(new Pair<>(null, child3));
            child1.parent = this;
            child2.parent = this;
            child3.parent = this;
            isExternal = false;
        }

        /**
         * Create a new 4-node.
         */
        Node(K value1, K value2, K value3, Node<K> child1, Node<K> child2, Node<K> child3, Node<K> child4) {
            pairs = new LinkedList<>();
            pairs.add(new Pair<>(value1, child1));
            pairs.add(new Pair<>(value2, child2));
            pairs.add(new Pair<>(value3, child3));
            pairs.add(new Pair<>(null, child4));
            child1.parent = this;
            child2.parent = this;
            child3.parent = this;
            child4.parent = this;
            isExternal = false;
        }

        /**
         * Insert a value and a corresponding child node (for children smaller than the value) in this node.
         * @param value Value to insert.
         * @param child Child node to insert.
         * @return Index of inserted value.
         */
        int insert(K value, Node<K> child) {
            if (child == null) {
                child = new Node<>();
            }
            Pair<K> pair = new Pair<>(value, child);
            int i = 0;
            if (pair.child != null) {
                pair.child.parent = this;
            }
            if (isExternal) {
                replaceChild(pairs.size() - 1, new Node<>());
            }
            isExternal = false;
            for (Pair<K> p : pairs) {
                if (p.value == null || p.value.compareTo(pair.value) >= 0) {
                    pairs.add(i, pair);
                    return i;
                }
                i++;
            }
            return -1;
        }

        /**
         * Set the child node for a given index.
         * @param i Index of the child.
         * @param child New child node.
         */
        void replaceChild(int i, Node<K> child) {
            pairs.get(i).child = child;
            child.parent = this;
        }

        /**
         * Check whether the value exists in this node.
         * @param value Value to check.
         * @return Whether the value exists.
         */
        boolean contains(K value) {
            for (Pair<K> pair : pairs) {
                if (pair.value != null && pair.value.compareTo(value) == 0) {
                    return true;
                }
            }
            return false;
        }

        K getValue(int index) {
            return pairs.get(index).value;
        }

        /**
         * Replace a value in this node with an equivalent version.
         * @param value The new value.
         * @return The old version that was previously stored in the node.
         */
        K replaceValue(K value) {
            for (Pair<K> pair : pairs) {
                if (pair.value.compareTo(value) == 0) {
                    K val = pair.value;
                    pair.value = value;
                    return val;
                }
            }
            return null;
        }

        /**
         * Get the child node to follow based on the given value.
         * @param value The value to compare to.
         * @return The child node to follow.
         */
        Node<K> findChildByValue(K value) {
            for (Pair<K> pair : pairs) {
                if (pair.value == null || pair.value.compareTo(value) > 0) {
                    return pair.child;
                }
            }
            return null;
        }

        Node<K> getChild(int index) {
            return pairs.get(index).child;
        }

        int size() {
            return pairs.size();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (Pair<K> p : pairs) {
                if (p.child != null) {
                    sb.append(p.child);
                }
                if (p.value != null) {
                    sb.append(' ');
                    sb.append(p.value);
                    sb.append(' ');
                }
            }
            sb.append(']');
            return sb.toString();
        }

        /**
         * Verify the invariants for this node in the 2-4 tree, and return the height of the node.
         * @param min The smallest legal value in this node.
         * @param max The largest legal value in this node.
         * @return Height, or -1 if the node violates any invariants.
         */
        private int verifyInvariants(K min, K max) {
            if (this.isExternal) {
                if (this.pairs.size() > 1 || this.pairs.get(0).value != null || this.pairs.get(0).child != null) {
                    System.out.println("External node with childen");
                    return -1;
                }
                return 0;
            } else {
                if (this.pairs.size() < 2 || this.pairs.size() > 4) {
                    System.out.println("2-4 invariant violated ("+this.pairs.size()+")");
                    return -1;
                }
                int i = 0;
                K last = null;
                int lastDepth = -1;
                for (Pair<K> pair : this.pairs) {
                    if (pair.child == null) {
                        System.out.println("Internal node with null child");
                        return -1;
                    }
                    if (pair.child.parent != this) {
                        System.out.println("Incorrect parent pointer");
                        return -1;
                    }
                    if (pair.value != null && i == this.pairs.size() - 1) {
                        System.out.println("Last value is not null");
                        return -1;
                    }
                    if (pair.value == null && i < this.pairs.size() - 1) {
                        System.out.println("Non-last value is null");
                        return -1;
                    }
                    if (pair.value != null && min != null && min.compareTo(pair.value) > 0) {
                        System.out.println("Value " + pair.value + " too small");
                        return -1;
                    }
                    if (pair.value != null && max != null && max.compareTo(pair.value) < 0) {
                        System.out.println("Value " + pair.value + " too large");
                        return -1;
                    }
                    if (pair.value != null && last != null && pair.value.compareTo(last) <= 0) {
                        System.out.println("Incorrect order between "+last+" and "+pair.value);
                        return -1;
                    }
                    int depth = pair.child.verifyInvariants(last == null ? min : last, pair.value == null ? max : pair.value);
                    if (depth == -1) {
                        System.out.println("Invariant violated in child node");
                        return -1;
                    }
                    if (lastDepth != -1 && depth != lastDepth) {
                        System.out.println("Depth not equal ("+depth+" and "+lastDepth+")");
                        return -1;
                    }
                    last = pair.value;
                    lastDepth = depth;
                    i++;
                }
                return lastDepth + 1;
            }
        }

        public boolean isExternal() {
            return isExternal;
        }

    }

    private Node<K> root = new Node<>();

    /**
     * Add a value to the tree.
     * @param value The value to add to the tree.
     * @return The old value that was stored in the node, if it was already present, or null.
     */
    public K add(K value) {
        if (root.isExternal()) {
        root = new Node<>(value, new Node<>(), new Node<>());
        return null;
    }
    Node<K> currentNode = root;
    while (!currentNode.isExternal()) {
        if (currentNode.contains(value)) {
            return currentNode.replaceValue(value);
        }
        currentNode = currentNode.findChildByValue(value);
    }

    currentNode.insert(value, null);

    while (currentNode.size() > 4) {
        int middle = (currentNode.size() - 1) / 2;
        K middleValue = currentNode.getValue(middle);
        Node<K> left = new Node<>();
        Node<K> right = new Node<>();

        for (int i = 0; i < middle; i++) {
            left.insert(currentNode.getValue(i), currentNode.getChild(i));
        }
        left.insert(null, currentNode.getChild(middle));

        for (int i = middle + 1; i < currentNode.size() - 1; i++) {
            right.insert(currentNode.getValue(i), currentNode.getChild(i));
        }
        right.insert(null, currentNode.getChild(currentNode.size() - 1));

        if (currentNode.parent == null) {
            root = new Node<>(middleValue, left, right);
            break;
        } else {
            currentNode = currentNode.parent;
            int indexOfMiddleValue = currentNode.insert(middleValue, left);
            currentNode.replaceChild(indexOfMiddleValue + 1, right);
        }
    }
    return null;
    }

    
    /**
     * Verify the invariants for the 2-4 tree.
     */
    public void verifyInvariants() {
        if (root.verifyInvariants(null, null) == -1) {
            throw new IllegalStateException();
        }
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public static void main(String[] args) {
        TwoFourTree1<Integer> tree = new TwoFourTree1<>();

        // try {
        //     for (int n : new int[] {5, 3, 7, 6, 1, 2, 9, 8, 10, 12, 11, 13}) {
        //         tree.add(n);
        //         tree.verifyInvariants();
        //         System.out.println(tree);
        //     }
        // } catch (Exception ex) {
        //     ex.printStackTrace(System.err);
        // }
        // expected: [[[[] 1 [] 2 []] 3 [[] 5 []] 6 [[] 7 [] 8 []]] 9 [[[] 10 [] 11 []] 12 [[] 13 []]]]
            
        try {
            for (int n : new int[] {5, 3, 7, 6, 1, 2, 9, 8, 10}) {
                tree.add(n);
                tree.verifyInvariants();
                System.out.println(tree);
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
