package datastructures.collections.querytree;

import datastructures.collections.dequelist.DequeList;
import datastructures.collections.dequelist.TreeDequeList;
import datastructures.utility.Randomness;
import datastructures.utility.VisualizeTree;
import datastructures.utility.functionalinterfaces.onetoone.G2G;
import datastructures.utility.functionalinterfaces.twotoone.GG2G;

import java.util.*;

public class SegmentTree<E> extends DequeList<E> { // hmm

    // deque list but with extra steps essentially
    // each node stores the leftmost + rightmost indices, and the inorder computation

    protected static final boolean RED = true;
    protected static final boolean BLACK = false;

    protected static class SegmentTreeNode<E> implements Comparable<SegmentTreeNode<E>> {

        private E value;
        private E computation;
        private SegmentTreeNode<E> leftChild;
        private SegmentTreeNode<E> rightChild;
        private SegmentTreeNode<E> parent;

        private int leftSubtreeSize;
        private int rightSubtreeSize; // RIP

        private boolean color; // of link from parent to this

        private final SegmentTree<E> tree;

        protected SegmentTreeNode(E e, SegmentTreeNode<E> parent, SegmentTree<E> tree) {
            this.tree = tree;

            this.value = e;
            computation = e;
            this.parent = parent;
            this.color = RED;
        }

        private void recalculate() {
            if (leftChild == null) {
                if (rightChild == null) {
                    computation = value;
                } else {
                    computation = tree.operator.function(value, rightChild.computation);
                }
            } else {
                if (rightChild == null) {
                    computation = tree.operator.function(leftChild.computation, value);
                } else {
                    computation = tree.operator.function(tree.operator.function(leftChild.computation, value), rightChild.computation);
                }
            }
        }

        private void recursivelyRecalculate() {
            SegmentTreeNode<E> node = this;
            node.recalculate();
            while (node.parent != null) {
                node = node.parent;
                node.recalculate();
            }
        }

        protected int numSmaller() {
            SegmentTreeNode<E> curr = this;
            int count = leftSubtreeSize;
            while (curr.parent != null) {
                if (curr.isRightChild()) {
                    count += curr.parent.leftSubtreeSize + 1;
                }
                curr = curr.parent;
            }
            return count;
        }

        protected int numGreater() {
            SegmentTreeNode<E> curr = this;
            int count = rightSubtreeSize;
            while (curr.parent != null) {
                if (curr.isLeftChild()) {
                    count += curr.parent.rightSubtreeSize + 1;
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

        private SegmentTreeNode<E> getSibling() { // assume null --> black for speed - means only RRR propagates up
            return parent == null ? null : this == parent.leftChild ? parent.rightChild : parent.leftChild;
        }

        protected static <E> boolean colorOf(SegmentTreeNode<E> node) {
            return node == null ? BLACK : node.color;
        }

        private void rotateUp() {
            if (parent == null) {
                return; // can't rotate the top
            }
            SegmentTreeNode<E> father = parent;

            if (isLeftChild()) {
                father.leftChild = rightChild;
                if (father.leftChild != null) {
                    father.leftChild.parent = father;
                }
                father.leftSubtreeSize = rightSubtreeSize;

                rightChild = father;
                rightSubtreeSize += father.rightSubtreeSize + 1;
            } else {
                father.rightChild = leftChild;
                if (father.rightChild != null) {
                    father.rightChild.parent = father;
                }
                father.rightSubtreeSize = leftSubtreeSize;

                leftChild = father;
                leftSubtreeSize += father.leftSubtreeSize + 1;
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

            father.recalculate();
            this.recalculate();
        }

        private void insertionRebalance() {
            if (parent == null) {
                color = BLACK;
                return;
            }
            if (parent.color == BLACK) {
                return;
            }
            SegmentTreeNode<E> father = parent;
            SegmentTreeNode<E> grandfather = father.parent;
            SegmentTreeNode<E> uncle = father.getSibling();
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

        /** which side has the double black */
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

        @Override
        public int compareTo(SegmentTreeNode<E> other) {
            int diff = numSmaller() - other.numSmaller();
            return diff != 0 ? diff : (other.numGreater() - numGreater()); // i rlly hope this works :D
        }

        protected SegmentTreeNode<E> next() {
            if (rightChild != null) {
                SegmentTreeNode<E> next = rightChild;
                while (next.leftChild != null) {
                    next = next.leftChild;
                }
                return next;
            } else {
                SegmentTreeNode<E> next = this;
                while (next.isRightChild()) {
                    next = next.parent;
                }
                return next.parent; // null -> last
            }
        }

        protected SegmentTreeNode<E> prev() {
            if (leftChild != null) {
                SegmentTreeNode<E> next = leftChild;
                while (next.rightChild != null) {
                    next = next.rightChild;
                }
                return next;
            } else {
                SegmentTreeNode<E> next = this;
                while (next.isLeftChild()) {
                    next = next.parent;
                }
                return next.parent; // null -> last
            }
        }

        public String toString() {
            return value + " " + computation;
        }

        private E query(int relativeLower, int relativeUpper) {
            if (relativeLower == 0 && relativeUpper == leftSubtreeSize + rightSubtreeSize) {
                return computation;
            }
            if (relativeLower < leftSubtreeSize) {
                if (relativeUpper < leftSubtreeSize) {
                    return leftChild.query(relativeLower, relativeUpper);
                } else if (relativeUpper == leftSubtreeSize) {
                    return tree.operator.function(leftChild.query(relativeLower, leftSubtreeSize - 1), value);
                } else {
                    return tree.operator.function(tree.operator.function(leftChild.query(relativeLower, leftSubtreeSize - 1), value), rightChild.query(0, relativeUpper - leftSubtreeSize - 1));
                }
            } else if (relativeLower == leftSubtreeSize) {
                if (relativeUpper == leftSubtreeSize) {
                    return value;
                } else {
                    return tree.operator.function(value, rightChild.query(0, relativeUpper - leftSubtreeSize - 1));
                }
            } else {
                return rightChild.query(relativeLower - leftSubtreeSize - 1, relativeUpper - leftSubtreeSize - 1);
            }
        }

    }

    /** the operator must be associative, but does not need to be invertible */
    public SegmentTree(GG2G<E, E, E> operator) {
        super(SegmentTreeNode.class);
        this.operator = operator;
    }

    // add all: O(n + log m). hmm...
    public SegmentTree(Collection<? extends E> c, GG2G<E, E, E> operator) { // huh
        super(SegmentTreeNode.class);
        this.operator = operator;
        root = collectionToTreeNode(c, operator);
    }

    private final GG2G<E, E, E> operator;

    private int size = 0;
    private SegmentTreeNode<E> root = null;

    // each node: stores value applied to descendants, in order --> sigh

    /** both inclusive */
    public E queryRange(int lowerIndex, int upperIndex) {
        if (lowerIndex < 0 || upperIndex >= size() || lowerIndex > upperIndex) {
            throw new IllegalArgumentException("Illegal range");
        }
        return root.query(lowerIndex, upperIndex);
    }

    protected SegmentTreeNode<E> getNode(int i) {
        if (i < 0 || i >= size()) {
            throw new IndexOutOfBoundsException();
        }
        SegmentTreeNode<E> curr = root;
        int numSmaller = 0;
        while (numSmaller + curr.leftSubtreeSize != i) {
            if (numSmaller + curr.leftSubtreeSize < i) {
                numSmaller += curr.leftSubtreeSize + 1;
                curr = curr.rightChild;
            } else {
                curr = curr.leftChild;
            }
        }
        return curr;
    }

    private void placeNode(SegmentTreeNode<E> node) {
        if (!backwardHashMap.containsKey(node.value)) {
            backwardHashMap.put(node.value, new TreeSet<>());
        }
        TreeSet<Object> preNodes = backwardHashMap.get(node.value);
        preNodes.add(node);
    }

    private void deleteNode(SegmentTreeNode<E> node) {
        TreeSet<Object> preNodes = backwardHashMap.get(node.value);
        preNodes.remove(node); // why did adding an assertion cause it to fail...
        if (preNodes.isEmpty()) {
            backwardHashMap.remove(node.value);
        }
    }

    private boolean insertHelper(SegmentTreeNode<E> parent, E e) {
        while (parent.rightChild != null) {
            parent.rightSubtreeSize += 1;
            parent = parent.rightChild;
        }
        parent.rightSubtreeSize += 1;
        SegmentTreeNode<E> toAdd = new SegmentTreeNode<>(e, parent, this);
        parent.rightChild = toAdd;
        placeNode(toAdd);
        parent.recursivelyRecalculate();
        toAdd.insertionRebalance();
        return true;
    }

    @Override
    protected boolean insertHelper(int i, E e) {
        lastIndex += 1;
        if (size() == 1) {
            root = new SegmentTreeNode<>(e, null, this);
            placeNode(root);
            root.insertionRebalance();
            return true;
        }
        if (i == size() - 1) {
            return insertHelper(root, e);
        }

        SegmentTreeNode<E> parent = root;
        int numSmaller = 0;
        while (numSmaller + parent.leftSubtreeSize != i) {
            if (numSmaller + parent.leftSubtreeSize < i) {
                numSmaller += parent.leftSubtreeSize + 1;
                parent.rightSubtreeSize += 1;
                parent = parent.rightChild;
            } else {
                parent.leftSubtreeSize += 1;
                parent = parent.leftChild;
            }
        }
        parent.leftSubtreeSize += 1;

        if (parent.leftChild == null) {
            SegmentTreeNode<E> toAdd = new SegmentTreeNode<>(e, parent, this);
            parent.leftChild = toAdd;
            placeNode(toAdd);
            parent.recursivelyRecalculate();
            toAdd.insertionRebalance();
            return true;
        }

        return insertHelper(parent.leftChild, e);
    }

    @Override
    protected E removeHelper(int i) {
        lastIndex -= 1;

        SegmentTreeNode<E> toRemove = root;
        int numSmaller = 0;
        while (numSmaller + toRemove.leftSubtreeSize != i) {
            if (numSmaller + toRemove.leftSubtreeSize < i) {
                numSmaller += toRemove.leftSubtreeSize + 1;
                toRemove.rightSubtreeSize -= 1;
                toRemove = toRemove.rightChild;
            } else {
                toRemove.leftSubtreeSize -= 1;
                toRemove = toRemove.leftChild;
            }
        }
        E ret = toRemove.value;
        deleteNode(toRemove);
        if (toRemove.leftChild == null) { // Don't need to do anything with leftSubtreeSizes
            boolean temp = toRemove.isRightChild();
            if (toRemove.parent == null) {
                root = toRemove.rightChild;
            } else if (toRemove.isRightChild()) {
                toRemove.parent.rightChild = toRemove.rightChild;
            } else {
                toRemove.parent.leftChild = toRemove.rightChild;
            }
            if (toRemove.rightChild != null)  {
                toRemove.rightChild.parent = toRemove.parent;
            }
            if (toRemove.parent != null) {
                toRemove.parent.recursivelyRecalculate();
            }
            if (toRemove.color == BLACK) { // only need to worry if we remove a black node
                if (toRemove.rightChild != null && toRemove.rightChild.color == RED) {
                    toRemove.rightChild.color = BLACK;
                } else if (toRemove.parent != null) {
                    toRemove.parent.deletionRebalance(temp); // sigh
                }
            }
        } else {
            toRemove.leftSubtreeSize -= 1;

            SegmentTreeNode<E> toReplace = toRemove.leftChild;

            while (toReplace.rightChild != null) {
                toReplace.rightSubtreeSize -= 1;
                toReplace = toReplace.rightChild;
            }

            toReplace.leftSubtreeSize = toRemove.leftSubtreeSize;
            toReplace.rightSubtreeSize = toRemove.rightSubtreeSize;

            boolean temp = toReplace.isRightChild();
            boolean temp2 = toReplace.color == BLACK;
            SegmentTreeNode<E> temp3 = toReplace.parent;
            SegmentTreeNode<E> temp4 = toReplace.leftChild;

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
                toReplace.recursivelyRecalculate();
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
                temp3.recursivelyRecalculate();
            }

            if (temp2) { // only need to worry if we remove a black node
                if (temp4 != null && temp4.color == RED) {
                    temp4.color = BLACK; // lolz
                } else {
                    temp3.deletionRebalance(temp); // sigh
                }
            }
        }

        return ret;
    }

    @Override
    protected ListIterator<E> iterator(boolean forward, int index) {
        return new ListIterator<>() {
            int i = index;
            SegmentTreeNode<E> curr = index == size() ? null : getNode(index);

            @Override
            public boolean hasNext() {
                return forward ? curr != null : size() != 0 && curr.prev() != null;
            }

            @Override
            public boolean hasPrevious() {
                return forward ? size() != 0 && curr.prev() != null : curr != null;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No next element");
                }
                if (forward) {
                    E ret = curr.value;
                    i += 1;
                    curr = curr.next();
                    return ret;
                } else {
                    i += 1;
                    curr = curr.prev();
                    return curr.value;
                }
            }

            @Override
            public E previous() {
                if (!hasPrevious()) {
                    throw new NoSuchElementException("No previous element");
                }
                if (forward) {
                    i += 1;
                    curr = curr.prev();
                    return curr.value;
                } else {
                    E ret = curr.value;
                    i += 1;
                    curr = curr.next();
                    return ret;
                }
            }

            @Override
            public int nextIndex() {
                return i;
            }

            @Override
            public int previousIndex() {
                return i - 1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(E e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(E e) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    protected int convert(Object o) {
        SegmentTreeNode<E> i = (SegmentTreeNode<E>) o;
        return i.numSmaller();
    }

    @Override
    protected E getHelper(int i) {
        return getNode(i).value;
    }

    @Override
    protected E setHelper(int i, E e) {
        SegmentTreeNode<E> node = getNode(i);
        E ret = node.value;
        deleteNode(node);
        node.value = e;
        placeNode(node); // lmao
        node.recursivelyRecalculate();
        return ret;
    }

    @Override
    protected void grow() { /* not needed :) */ }

    @Override
    protected void shrink() { /* not needed :) */ }


    @Override
    protected boolean validToInsert(int i) {
        return 0 <= i && i <= size();
    }

    @Override
    protected boolean validToRemove(int i) {
        return 0 <= i && i < size();
    }

    @Override
    protected boolean addHelper(boolean beginning, E e) {
        if (beginning) {
            return insertHelper(0, e);
        } else {
            return insertHelper(size(), e);
        }
    }

    @Override
    protected E removeHelper(boolean beginning) {
        if (beginning) {
            return removeHelper(0);
        } else {
            return removeHelper(size() - 1);
        }
    }

    @Override
    protected void clearHelper() {
        root = null;
    }

    // TODO: implement these functions
    public void mergeLeft(TreeDequeList<E> left) {

    }

    public void mergeRight(TreeDequeList<E> right) {

    }

    public static <E> TreeDequeList<E> merge(TreeDequeList<E> left, TreeDequeList<E> right) {
        return null;
    }

    private static <E> SegmentTreeNode<E> collectionToTreeNode(Collection<? extends E> c, GG2G<E, E, E> operator) {
        return null;
    }

    public static <E> SegmentTree<E> collectionToTree(Collection<? extends E> c, GG2G<E, E, E> operator) {
        SegmentTree<E> tree = new SegmentTree<E>(operator);
        tree.root = collectionToTreeNode(c, operator);
        return tree;
    }

    private static <E> int bruteForceLeftSubtree(SegmentTreeNode<E> x) {
        if (x == null) {
            return -1;
        }
        int ret = 0;
        if (x.leftChild != null) {
            ret = 1 + bruteForceLeftSubtree(x.leftChild) + bruteForceRightSubtree(x.leftChild);
        }
        if (ret != x.leftSubtreeSize) {
            throw new IllegalStateException("Left subtree size mismatch");
        }
        return ret;
    }

    private static <E> int bruteForceRightSubtree(SegmentTreeNode<E> x) {
        if (x == null) {
            return -1;
        }
        int ret = 0;
        if (x.rightChild != null) {
            ret = 1 + bruteForceLeftSubtree(x.rightChild) + bruteForceRightSubtree(x.rightChild);
        }
        if (ret != x.rightSubtreeSize) {
            throw new IllegalStateException("Right subtree size mismatch");
        }
        return ret;
    }

    public static <E> E bruteForceComputation(SegmentTreeNode<E> x) {
        if (x == null) {
            return null;
        }
        E result;
        if (x.leftChild != null) {
            if (x.rightChild != null) {
                result = x.tree.operator.function(bruteForceComputation(x.leftChild), x.tree.operator.function(x.value, bruteForceComputation(x.rightChild)));
            } else {
                result = x.tree.operator.function(bruteForceComputation(x.leftChild), x.value);
            }
        } else {
            if (x.rightChild != null) {
                result = x.tree.operator.function(x.value, bruteForceComputation(x.rightChild));
            } else {
                result = x.value;
            }
        }
        if (!Objects.equals(x.computation, result)) {
            throw new IllegalStateException("Computation invalid");
        }
        return result;
    }


    private static <E> void assertDataMakesSense(SegmentTree<E> tree) {
        int calculatedSize = 0;
        bruteForceLeftSubtree(tree.root);
        bruteForceRightSubtree(tree.root);
        bruteForceComputation(tree.root);

        for (int i = 0; i < tree.size(); i += 1) {
            E bruteForce = tree.get(i);
            for (int j = i; j < tree.size(); j += 1) {
                if (i != j) {
                    bruteForce = tree.operator.function(bruteForce, tree.get(j));
                }
                if (!Objects.equals(bruteForce, tree.queryRange(i, j))) {
                    throw new IllegalStateException("Inconsistent query");
                }
            }
        }

        if (tree.root != null && tree.root.color == RED) {
            throw new IllegalStateException("Tree root color is red");
        }
        int blackHeight = -1;
        for (Map.Entry<E, TreeSet<Object>> eList : tree.backwardHashMap.entrySet()) {
            calculatedSize += eList.getValue().size();
            int prevIndex = -1;
            for (Object o : eList.getValue()) {
                SegmentTreeNode<E> node = (SegmentTreeNode<E>) o;
                if (prevIndex >= node.numSmaller()) {
                    throw new IllegalStateException("BackwardHashMap nodes out of order");
                }
                prevIndex = node.numSmaller();
                if (!Objects.equals(tree.get(prevIndex), eList.getKey())) { // huh
                    throw new IllegalStateException("Backward Hash Map not working");
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

        }
        if (tree.size() != calculatedSize) {
            throw new IllegalStateException("Tree size inconsistent");
        }
    }

    public static final double ADD_PROBABILITY = 0.45;
    public static final double NULL_PROBABILITY = 0.05;
    public static final int NUM_TESTS = 100;
    public static final int NUM_ITERATIONS = 10000;
    public static final GG2G<Integer, Integer, Integer> sum = (x, y) -> x == null ? (y == null ? 0 : y) : (y == null ? x : x + y);
    public static final GG2G<Integer, Integer, Integer> returnRight = (x, y) -> y; // lmfao

    public static void testAddRemoveSame(GG2G<Integer, Integer, Integer> associativeOperation) {
        SegmentTree<Integer> testTree = new SegmentTree<>(associativeOperation);
        LinkedList<Integer> parallel = new LinkedList<>();

        for (int k = 0; k < NUM_TESTS; k += 1) {
            testTree.clear();
            parallel.clear();
            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                if (testTree.size() == 0 || Randomness.getDouble() < ADD_PROBABILITY) {
                    int toAddIndex = Randomness.getRandomInteger(0, testTree.size() + 1);
                    testTree.add(toAddIndex, 0);
                    parallel.add(toAddIndex, 0);
                } else {
                    int toRemoveIndex = Randomness.getRandomInteger(0, testTree.size());
                    testTree.remove(toRemoveIndex);
                    parallel.remove(toRemoveIndex);
                }
                assertDataMakesSense(testTree);
                if (!Arrays.equals(testTree.toArray(), parallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
                }
            }
            for (int j = testTree.size(); j > 0; j -= 1) {
                int toRemoveIndex = Randomness.getRandomInteger(0, testTree.size());
                testTree.remove(toRemoveIndex);
                parallel.remove(toRemoveIndex);
                assertDataMakesSense(testTree);
                if (!Arrays.equals(testTree.toArray(), parallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
                }
            }
            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                if (testTree.size() == 0 || Randomness.getDouble() < ADD_PROBABILITY) {
                    int toAddIndex = Randomness.getRandomInteger(0, testTree.size() + 1);
                    testTree.add(toAddIndex, 0);
                    parallel.add(toAddIndex, 0);
                } else {
                    int toRemoveIndex = Randomness.getRandomInteger(0, testTree.size());
                    testTree.remove(toRemoveIndex);
                    parallel.remove(toRemoveIndex);
                }
                assertDataMakesSense(testTree);
                if (!Arrays.equals(testTree.toArray(), parallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
                }
            }
        }
    }

    public static void testAddRemoveDifferent(GG2G<Integer, Integer, Integer> associativeOperation) {
        SegmentTree<Integer> testTree = new SegmentTree<>(associativeOperation);
        LinkedList<Integer> parallel = new LinkedList<>();

        for (int k = 0; k < NUM_TESTS; k += 1) {
            testTree.clear();
            parallel.clear();

            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                if (testTree.size() == 0 || Randomness.getDouble() < ADD_PROBABILITY) {
                    Integer toAdd = Randomness.getDouble() < NULL_PROBABILITY ? null : Randomness.getRandomInteger(-1024, 1024);
                    int toAddIndex = Randomness.getRandomInteger(0, testTree.size() + 1);
                    testTree.add(toAddIndex, toAdd);
                    parallel.add(toAddIndex, toAdd);
                } else {
                    int toRemoveIndex = Randomness.getRandomInteger(0, testTree.size());
                    testTree.remove(toRemoveIndex);
                    parallel.remove(toRemoveIndex);
                }
                assertDataMakesSense(testTree);
                if (!Arrays.equals(testTree.toArray(), parallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
                }
                if (testTree.size() > 0) {
                    Integer toSet = Randomness.getRandomInteger(0, testTree.size());
                    int toSetIndex = Randomness.getRandomInteger(0, testTree.size());
                    testTree.set(toSetIndex, toSet);
                    parallel.set(toSetIndex, toSet);
                    assertDataMakesSense(testTree);
                    if (!Arrays.equals(testTree.toArray(), parallel.toArray())) {
                        throw new IllegalStateException("Arrays were inconsistent");
                    }
                }
            }
            for (int j = testTree.size(); j > 0; j -= 1) {
                int toRemoveIndex = Randomness.getRandomInteger(0, testTree.size());
                testTree.remove(toRemoveIndex);
                parallel.remove(toRemoveIndex);
                assertDataMakesSense(testTree);
                if (!Arrays.equals(testTree.toArray(), parallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
                }
            }
            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                if (testTree.size() == 0 || Randomness.getDouble() < ADD_PROBABILITY) {
                    Integer toAdd = Randomness.getDouble() < NULL_PROBABILITY ? null : Randomness.getRandomInteger(-1024, 1024);
                    int toAddIndex = Randomness.getRandomInteger(0, testTree.size() + 1);
                    testTree.add(toAddIndex, toAdd);
                    parallel.add(toAddIndex, toAdd);
                } else {
                    int toRemoveIndex = Randomness.getRandomInteger(0, testTree.size());
                    testTree.remove(toRemoveIndex);
                    parallel.remove(toRemoveIndex);
                }
                assertDataMakesSense(testTree);
                if (!Arrays.equals(testTree.toArray(), parallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
                }
                if (testTree.size() > 0) {
                    Integer toSet = Randomness.getRandomInteger(0, testTree.size());
                    int toSetIndex = Randomness.getRandomInteger(0, testTree.size());
                    testTree.set(toSetIndex, toSet);
                    parallel.set(toSetIndex, toSet);
                    assertDataMakesSense(testTree);
                    if (!Arrays.equals(testTree.toArray(), parallel.toArray())) {
                        throw new IllegalStateException("Arrays were inconsistent");
                    }
                }
            }
        }
    }

    public String visualization() {
        return visualization(x -> x.toString());
    }

    private String visualization(G2G<SegmentTreeNode<E>, String> instructions) {
        return VisualizeTree.stringOfTree(root,
                // x -> new ArrayList<>(Arrays.asList(x.leftChild, x.rightChild)),
                x -> x.leftChild, x -> x.rightChild,
                instructions, 2) + '\n' + this + '\n' + backwardHashMap;
    }

    public static void main(String[] args) {

        testAddRemoveSame(sum);
        testAddRemoveDifferent(sum);
        testAddRemoveSame(returnRight);
        testAddRemoveDifferent(returnRight);
        System.out.println("SegmentTree passed all tests :)"); // FUCKING HYPE LFG

    } // hype
}