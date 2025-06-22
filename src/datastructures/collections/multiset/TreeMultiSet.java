package datastructures.collections.multiset;

import datastructures.collections.dequelist.TreeDequeList;
import datastructures.utility.Randomness;
import datastructures.utility.VisualizeTree;
import datastructures.utility.functionalinterfaces.onetoone.G2G;

import java.util.*;

public class TreeMultiSet<E> extends AbstractCollection<E> { // for multimap: just implement a hashmap. highk better

    protected static final boolean RED = true;
    protected static final boolean BLACK = false;

    protected static class TreeMultiSetNode<E> {

        private E value;
        private int weight; // or arraydeque </3 lmao actually not real

        private TreeMultiSetNode<E> leftChild;
        private TreeMultiSetNode<E> rightChild;
        private TreeMultiSetNode<E> parent;

        private int leftSubtreeSize;
        private int rightSubtreeSize; // RIP

        private boolean color; // of link from parent to this

        private final TreeMultiSet<E> tree;

        protected TreeMultiSetNode(E e, TreeMultiSetNode<E> parent, TreeMultiSet<E> tree) {
            this.tree = tree;

            this.value = e;
            this.parent = parent;
            this.color = RED;
            this.weight = 1;
        }

        protected int numSmaller() {
            TreeMultiSetNode<E> curr = this;
            int count = leftSubtreeSize;
            while (curr.parent != null) {
                if (curr.isRightChild()) {
                    count += curr.parent.leftSubtreeSize + curr.parent.weight;
                }
                curr = curr.parent;
            }
            return count;
        }

        protected int numGreater() {
            TreeMultiSetNode<E> curr = this;
            int count = rightSubtreeSize;
            while (curr.parent != null) {
                if (curr.isLeftChild()) {
                    count += curr.parent.rightSubtreeSize + curr.parent.weight;
                }
                curr = curr.parent;
            }
            return count;
        }

        protected boolean isLeftChild() {
            return parent != null && this == parent.leftChild;
        }

        protected boolean isRightChild() {
            return parent != null && this == parent.rightChild;
        }

        private TreeMultiSetNode<E> getSibling() { // assume null --> black for speed - means only RRR propagates up
            return parent == null ? null : this == parent.leftChild ? parent.rightChild : parent.leftChild;
        }

        protected static <E> boolean colorOf(TreeMultiSetNode<E> node) {
            return node == null ? BLACK : node.color;
        }

        private void rotateUp() {
            if (parent == null) {
                return; // can't rotate the top
            }
            TreeMultiSetNode<E> father = parent;

            if (isLeftChild()) {
                father.leftChild = rightChild;
                if (father.leftChild != null) {
                    father.leftChild.parent = father;
                }
                father.leftSubtreeSize = rightSubtreeSize;

                rightChild = father;
                rightSubtreeSize += father.rightSubtreeSize + father.weight;
            } else {
                father.rightChild = leftChild;
                if (father.rightChild != null) {
                    father.rightChild.parent = father;
                }
                father.rightSubtreeSize = leftSubtreeSize;

                leftChild = father;
                leftSubtreeSize += father.leftSubtreeSize + father.weight;
            }

            parent = father.parent;
            if (father.parent == null) { // grandparent is root
                tree.root = this;
            } else {
                if (father.isLeftChild()) {
                    father.parent.leftChild = this;
                } else {
                    father.parent.rightChild = this;
                }
            }
            father.parent = this;

            boolean OFC = father.color;
            father.color = this.color;
            color = OFC;
        }

        private void insertionRebalance() {
            if (parent == null) {
                color = BLACK;
                return;
            }
            if (parent.color == BLACK) {
                return;
            }
            TreeMultiSetNode<E> father = parent;
            TreeMultiSetNode<E> grandfather = father.parent;
            TreeMultiSetNode<E> uncle = father.getSibling();
            if (colorOf(uncle) == RED) {
                father.color = BLACK;
                assert uncle != null;
                uncle.color = BLACK;
                if (grandfather != null) {
                    grandfather.color = RED;
                    grandfather.insertionRebalance();
                }
                return;
            }
            if (isLeftChild() == father.isLeftChild()) {
                father.rotateUp();
            } else {
                rotateUp(); // 120 lines --> 40 lines :D
                rotateUp();
            }
        }

        /**
         * which side has the double black
         */
        private void deletionRebalance(boolean right) {
            if (right) {
                if (colorOf(leftChild) == RED) {
                    leftChild.rotateUp();
                }
                if (color == RED) {
                    if (leftChild != null) {
                        if (colorOf(leftChild.rightChild) == RED) {
                            leftChild.rightChild.rotateUp();
                        }
                        if (colorOf(leftChild.leftChild) == RED) {
                            leftChild.rotateUp();
                            parent.leftChild.color = BLACK;
                            return;
                        }
                        leftChild.color = RED;
                    }
                    color = BLACK;
                    return;
                }
                if (leftChild == null || (colorOf(leftChild.leftChild) == BLACK && colorOf(leftChild.rightChild) == BLACK)) {
                    if (leftChild != null) {
                        leftChild.color = RED;
                    }
                    if (parent != null) {
                        parent.deletionRebalance(isRightChild());
                    }
                    return;
                }
                if (colorOf(leftChild.rightChild) == RED) { // 2 node or 3 node
                    leftChild.rightChild.rotateUp();
                }
                if (colorOf(leftChild.leftChild) == RED) { // 2 node
                    leftChild.rotateUp();
                    parent.leftChild.color = BLACK;
                }
            } else {
                if (colorOf(rightChild) == RED) {
                    rightChild.rotateUp();
                }
                if (color == RED) {
                    if (rightChild != null) {
                        if (colorOf(rightChild.leftChild) == RED) {
                            rightChild.leftChild.rotateUp();
                        }
                        if (colorOf(rightChild.rightChild) == RED) {
                            rightChild.rotateUp();
                            parent.rightChild.color = BLACK;
                            return;
                        }
                        rightChild.color = RED;
                    }
                    color = BLACK;
                    return;
                }
                if (rightChild == null || (colorOf(rightChild.rightChild) == BLACK && colorOf(rightChild.leftChild) == BLACK)) {
                    if (rightChild != null) {
                        rightChild.color = RED;
                    }
                    if (parent != null) {
                        parent.deletionRebalance(isRightChild());
                    }
                    return;
                }
                if (colorOf(rightChild.leftChild) == RED) { // 2 node or 3 node
                    rightChild.leftChild.rotateUp();
                }
                if (colorOf(rightChild.rightChild) == RED) { // 2 node
                    rightChild.rotateUp();
                    parent.rightChild.color = BLACK;
                }
            }
        }

        protected TreeMultiSetNode<E> next() {
            if (rightChild != null) {
                TreeMultiSetNode<E> next = rightChild;
                while (next.leftChild != null) {
                    next = next.leftChild;
                }
                return next;
            } else {
                TreeMultiSetNode<E> next = this;
                while (next.isRightChild()) {
                    next = next.parent;
                }
                return next.parent; // null -> last
            }
        }

        protected TreeMultiSetNode<E> prev() {
            if (leftChild != null) {
                TreeMultiSetNode<E> next = leftChild;
                while (next.rightChild != null) {
                    next = next.rightChild;
                }
                return next;
            } else {
                TreeMultiSetNode<E> next = this;
                while (next.isLeftChild()) {
                    next = next.parent;
                }
                return next.parent; // null -> last
            }
        }

        public String toString() {
            return Objects.toString(value) + " : " + weight;
        }
    }

    protected final Comparator<E> comparator;

    public TreeMultiSet() {
        comparator = null;
    }

    // Pass Class<T> to constructor?
    public TreeMultiSet(Collection<? extends E> c) { // huh
        comparator = null;
        c.forEach(this::add);
    }

    private int size = 0;
    private TreeMultiSetNode<E> root = null;
    private final HashMap<E, TreeMultiSetNode<E>> backwardHashMap = new HashMap<>();

    private int compare(TreeMultiSetNode<E> node, E e) {
        if (comparator == null) {
            return ((Comparable<? super E>) node.value).compareTo(e);
        } else {
            return comparator.compare(node.value, e);
        }
    }

    private TreeMultiSetNode<E> getNode(int i) {
        if (i < 0 || i >= size()) {
            throw new IndexOutOfBoundsException();
        }
        TreeMultiSetNode<E> curr = root;
        int numSmaller = 0;
        while (numSmaller + curr.leftSubtreeSize + curr.weight <= i || i < numSmaller + curr.leftSubtreeSize) {
            // numSmaller + curr.leftSubtreeSize : number of elements before
            // --> index ranges from numSmaller + curr.leftSubtreeSize to numSmaller + curr.leftSubtreeSize + curr.weight - 1
            // --> i must be within this range
            // --> numSmaller + curr.leftSubtreeSize <= i and i < numSmaller + curr.leftSubtreeSize + curr.weight
            // --> rightward condition: numSmaller + curr.leftSubtreeSize + curr.weight - 1 < i
            // leftward condition: numSmaller + curr.leftSubtreeSize > i
            // i = numSmaller + curr.leftSubtreeSize -> 0
            // this means, index i - (numSmaller + curr.leftSubtreeSize)
            if (numSmaller + curr.leftSubtreeSize > i) {
                curr = curr.leftChild;
            } else {
                numSmaller += curr.leftSubtreeSize + curr.weight;
                curr = curr.rightChild;
            }
        }
        return curr;
    }

    public final E get(int i) {
        return getNode(i).value;
    }

    @Override
    public final int size() {
        return size;
    }

    public final int uniqueElementCount() {
        return backwardHashMap.size();
    }

    @Override
    public final boolean add(E e) {
        size += 1;
        if (size() == 1) {
            root = new TreeMultiSetNode<>(e, null, this);
            backwardHashMap.put(e, root);
            root.insertionRebalance(); // not rlly needed but who gaf
            return true;
        }

        TreeMultiSetNode<E> parent = root;
        while (true) {
            if (compare(parent, e) < 0) {
                parent.rightSubtreeSize += 1;
                if (parent.rightChild != null) {
                    parent = parent.rightChild;
                } else {
                    parent.rightChild = new TreeMultiSetNode<>(e, parent, this);
                    backwardHashMap.put(e, parent.rightChild);
                    parent.rightChild.insertionRebalance();
                    return true;
                }
            } else if ((compare(parent, e) > 0)) {
                parent.leftSubtreeSize += 1;
                if (parent.leftChild != null) {
                    parent = parent.leftChild;
                } else {
                    parent.leftChild = new TreeMultiSetNode<>(e, parent, this);
                    backwardHashMap.put(e, parent.leftChild);
                    parent.leftChild.insertionRebalance();
                    return true;
                }
            } else {
                parent.weight += 1;
                return true;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public final boolean remove(Object o) {
        if (!contains(o)) {
            return false;
        }
        E e = (E) o;
        size -= 1;

        TreeMultiSetNode<E> toRemove = root;
        while (true) {
            if (compare(toRemove, e) < 0) {
                toRemove.rightSubtreeSize -= 1;
                if (toRemove.rightChild != null) {
                    toRemove = toRemove.rightChild;
                } else {
                    throw new IllegalStateException();
                }
            } else if ((compare(toRemove, e) > 0)) {
                toRemove.leftSubtreeSize -= 1;
                if (toRemove.leftChild != null) {
                    toRemove = toRemove.leftChild;
                } else {
                    throw new IllegalStateException();
                }
            } else {
                toRemove.weight -= 1;
                if (toRemove.weight == 0) {
                    break;
                }
                return true;
            }
        }

        // need to delete node :(

        E ret = toRemove.value;
        backwardHashMap.remove(ret);
        if (toRemove.leftChild == null) { // Don't need to do anything with leftSubtreeSizes
            boolean temp = toRemove.isRightChild();
            if (toRemove.parent == null) {
                root = toRemove.rightChild;
            } else if (toRemove.isRightChild()) {
                toRemove.parent.rightChild = toRemove.rightChild;
            } else {
                toRemove.parent.leftChild = toRemove.rightChild;
            }
            if (toRemove.rightChild != null) {
                toRemove.rightChild.parent = toRemove.parent;
            }
            if (toRemove.color == BLACK) { // only need to worry if we remove a black node
                if (toRemove.rightChild != null && toRemove.rightChild.color == RED) {
                    toRemove.rightChild.color = BLACK;
                } else if (toRemove.parent != null) {
                    toRemove.parent.deletionRebalance(temp); // sigh
                }
            }
        } else {
            TreeMultiSetNode<E> toReplace = toRemove.leftChild;

            while (toReplace.rightChild != null) {
                toReplace = toReplace.rightChild;
            }

            int remove = toReplace.weight;

            toRemove.leftSubtreeSize -= remove;

            toReplace = toRemove.leftChild;

            while (toReplace.rightChild != null) {
                toReplace.rightSubtreeSize -= remove;
                toReplace = toReplace.rightChild;
            }

            toReplace.leftSubtreeSize = toRemove.leftSubtreeSize;
            toReplace.rightSubtreeSize = toRemove.rightSubtreeSize;

            boolean temp = toReplace.isRightChild();
            boolean temp2 = toReplace.color == BLACK;
            TreeMultiSetNode<E> temp3 = toReplace.parent;
            TreeMultiSetNode<E> temp4 = toReplace.leftChild;

            toReplace.color = toRemove.color;

            if (toReplace.parent == toRemove) {
                toReplace.rightChild = toRemove.rightChild;
                if (toReplace.rightChild != null) {
                    toReplace.rightChild.parent = toReplace;
                }
                toReplace.parent = toRemove.parent;
                if (toRemove.parent == null) {
                    root = toReplace;
                } else if (toRemove.isRightChild()) {
                    toReplace.parent.rightChild = toReplace;
                } else {
                    toReplace.parent.leftChild = toReplace;
                }
                temp3 = toReplace;
            } else {
                toReplace.parent.rightChild = toReplace.leftChild;
                if (toReplace.leftChild != null) {
                    toReplace.leftChild.parent = toReplace.parent;
                }
                toReplace.rightChild = toRemove.rightChild;
                if (toReplace.rightChild != null) {
                    toReplace.rightChild.parent = toReplace;
                }
                toReplace.leftChild = toRemove.leftChild;
                if (toReplace.leftChild != null) {
                    toReplace.leftChild.parent = toReplace;
                }
                toReplace.parent = toRemove.parent;
                if (toRemove.parent == null) {
                    root = toReplace;
                } else if (toRemove.isRightChild()) {
                    toReplace.parent.rightChild = toReplace;
                } else {
                    toReplace.parent.leftChild = toReplace;
                }
            }

            if (temp2) { // only need to worry if we remove a black node
                if (temp4 != null && temp4.color == RED) {
                    temp4.color = BLACK; // lolz
                } else {
                    temp3.deletionRebalance(temp); // sigh
                }
            }
        }
        return true;
    }

    @Override
    public final Iterator<E> iterator() {
        return new Iterator<>() {
            TreeMultiSetNode<E> curr = size() > 0 ? getNode(0) : null;
            int currentCounter = 1;

            @Override
            public boolean hasNext() {
                return curr != null && (curr.next() != null || currentCounter <= curr.weight);
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                E ret = curr.value;
                if (currentCounter >= curr.weight) {
                    currentCounter = 1;
                    curr = curr.next();
                } else {
                    currentCounter += 1;
                }
                return ret;
            }
        };
    }

    @Override
    public final boolean contains(Object o) {
        return backwardHashMap.containsKey(o);
    }

    public final int indexOf(E e) {
        if (contains(e)) {
            return backwardHashMap.get(e).numSmaller();
        } else {
            return -1;
        }
    }

    public final int lastIndexOf(E e) {
        if (contains(e)) {
            return backwardHashMap.get(e).numSmaller() + backwardHashMap.get(e).weight - 1;
        } else {
            return -1;
        }
    }

    public int count(E e) {
        if (contains(e)) {
            return backwardHashMap.get(e).weight;
        } else {
            return -1;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void clear() {
        size = 0;
        root = null;
        backwardHashMap.clear();
    }

    @Override
    public final E[] toArray() {
        @SuppressWarnings("unchecked")
        E[] arr = (E[]) new Object[size()]; // much more interesting imo

        int i = 0;
        for (E e : this) {
            arr[i] = e;
            i += 1;
        }

        return arr;
    }

    private static <E> int bruteForceLeftSubtree(TreeMultiSetNode<E> x) {
        if (x == null) {
            return -1;
        }
        int ret = 0;
        if (x.leftChild != null) {
            ret = x.leftChild.weight + bruteForceLeftSubtree(x.leftChild) + bruteForceRightSubtree(x.leftChild);
        }
        if (ret != x.leftSubtreeSize) {
            throw new IllegalStateException("Left subtree size mismatch");
        }
        return ret;
    }

    private static <E> int bruteForceRightSubtree(TreeMultiSetNode<E> x) {
        if (x == null) {
            return -1;
        }
        int ret = 0;
        if (x.rightChild != null) {
            ret = x.rightChild.weight + bruteForceLeftSubtree(x.rightChild) + bruteForceRightSubtree(x.rightChild);
        }
        if (ret != x.rightSubtreeSize) {
            throw new IllegalStateException("Right subtree size mismatch");
        }
        return ret;
    }

    private static <E> void assertDataMakesSense(TreeMultiSet<E> tree) {
        int calculatedSize = 0;
        bruteForceLeftSubtree(tree.root);
        bruteForceRightSubtree(tree.root);
        if (tree.root != null && tree.root.color == RED) {
            throw new IllegalStateException("Tree root color is red");
        }
        E[] arr = tree.toArray();
        int blackHeight = -1;
        for (Map.Entry<E, TreeMultiSetNode<E>> pair : tree.backwardHashMap.entrySet()) {
            TreeMultiSetNode<E> node = pair.getValue();
            calculatedSize += node.weight;
            if (node.weight <= 0) {
                throw new IllegalStateException("Size 0 still exists");
            }
            int index = node.numSmaller();
            for (int i = 0; i < node.weight; i += 1) {
                if (!Objects.equals(tree.get(index + i), node.value)) { // huh
                    throw new IllegalStateException("Backward Hash Map not working");
                }
                if (!Objects.equals(arr[index + i], node.value)) {
                    throw new IllegalStateException("Backward Hash Map not working");
                }

            }
            if (node.color == RED && ((node.leftChild != null && node.leftChild.color == RED) || (node.rightChild != null && node.rightChild.color == RED))) {
                throw new IllegalStateException("Consecutive red nodes");
            }
            if (node.leftChild == null || node.rightChild == null) {
                int blackCount = 0;
                while (node != null) {
                    if (node.color == BLACK) {
                        blackCount += 1;
                    }
                    node = node.parent;
                }
                if (blackHeight == -1) {
                    blackHeight = blackCount;
                }
                if (blackHeight != blackCount) {
                    throw new IllegalStateException("Black height inconsistent");
                }
            }

        }
        if (tree.size() != calculatedSize) {
            throw new IllegalStateException("Tree size inconsistent");
        }
    }

    public static final double ADD_PROBABILITY = 0.45;
    public static final int NUM_TESTS = 100;
    public static final int NUM_ITERATIONS = 10000;

    public static void testAddRemove() {
        TreeMultiSet<Integer> testTree = new TreeMultiSet<>();
        HashMultiSet<Integer> parallel = new HashMultiSet<>();

        for (int k = 0; k < NUM_TESTS; k += 1) {
            testTree.clear();
            parallel.clear();
            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                if (testTree.size() == 0 || Randomness.getDouble() < ADD_PROBABILITY) {
                    int toAdd = Randomness.getRandomInteger(-1024, 1024);
                    testTree.add(toAdd);
                    parallel.add(toAdd);
                } else {
                    int toRemove = testTree.get(Randomness.getRandomInteger(0, testTree.size()));
                    testTree.remove(toRemove);
                    parallel.remove(toRemove);
                }
                assertDataMakesSense(testTree);
                for (Integer i : parallel) {
                    if (testTree.count(i) != parallel.count(i)) {
                        throw new IllegalStateException("Arrays were inconsistent");
                    }
                }
            }
            for (int j = testTree.size(); j > 0; j -= 1) {
                int toRemove = testTree.get(Randomness.getRandomInteger(0, testTree.size()));
                testTree.remove(toRemove);
                parallel.remove(toRemove);
                assertDataMakesSense(testTree);
                for (Integer i : parallel) {
                    if (testTree.count(i) != parallel.count(i)) {
                        throw new IllegalStateException("Arrays were inconsistent");
                    }
                }
            }
            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                if (testTree.size() == 0 || Randomness.getDouble() < ADD_PROBABILITY) {
                    int toAdd = Randomness.getRandomInteger(-1024, 1024);
                    testTree.add(toAdd);
                    parallel.add(toAdd);
                } else {
                    int toRemove = testTree.get(Randomness.getRandomInteger(0, testTree.size()));
                    testTree.remove(toRemove);
                    parallel.remove(toRemove);
                }
                assertDataMakesSense(testTree);
                for (Integer i : parallel) {
                    if (testTree.count(i) != parallel.count(i)) {
                        throw new IllegalStateException("Arrays were inconsistent");
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return toString("[", ", ", "]");
    }

    public final String toString(String open, String between, String close) {
        if (isEmpty()) {
            return open + close;
        }
        StringBuilder sb = new StringBuilder(open);
        Iterator<E> iterator = iterator();
        while (true) {
            sb.append(iterator.next());
            if (!iterator.hasNext()) {
                return sb.append(close).toString();
            }
            sb.append(between);
        }
    }

    public String visualization() {
        return visualization(TreeMultiSetNode::toString);
    }

    private String visualization(G2G<TreeMultiSetNode<E>, String> instructions) {
        return VisualizeTree.stringOfTree(root,
                // x -> new ArrayList<>(Arrays.asList(x.leftChild, x.rightChild)),
                x -> x.leftChild, x -> x.rightChild,
                instructions, 2) + '\n' + this + '\n' + backwardHashMap;
    }

    public static void main(String[] args) {

        testAddRemove();
        System.out.println("TreeMultiSet passed all tests :)");

    }
}
