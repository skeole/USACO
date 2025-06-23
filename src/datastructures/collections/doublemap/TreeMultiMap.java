package datastructures.collections.doublemap;

import datastructures.collections.dequelist.ArrayDequeList;
import datastructures.collections.dequelist.TreeDequeList;
import datastructures.collections.multiset.HashMultiSet;
import datastructures.collections.multiset.TreeMultiSet;
import datastructures.utility.OrderedTuple;
import datastructures.utility.Randomness;
import datastructures.utility.VisualizeTree;
import datastructures.utility.functionalinterfaces.onetoone.G2G;

import java.util.*;

public class TreeMultiMap<K, V> extends AbstractMap<K, V> { // can act as priority queue as well, which I think is really funny

    protected static final boolean RED = true;
    protected static final boolean BLACK = false;

    protected static class TreeMultiMapNode<K, V> implements Comparable<TreeMultiMapNode<K, V>> {

        private final K key;
        private final TreeDequeList<V> values = new TreeDequeList<>(); // to make it a multiheap, can make V a wrapper for V, int

        private TreeMultiMapNode<K, V> leftChild;
        private TreeMultiMapNode<K, V> rightChild;
        private TreeMultiMapNode<K, V> parent;

        private int leftSubtreeSize;
        private int rightSubtreeSize; // RIP

        private boolean color; // of link from parent to this

        private final TreeMultiMap<K, V> tree;

        protected TreeMultiMapNode(K key, V value, TreeMultiMapNode<K, V> parent, TreeMultiMap<K, V> tree) {
            this.tree = tree;

            this.key = key;
            values.add(value);
            this.parent = parent;
            this.color = RED;
        }

        protected int numSmaller() {
            TreeMultiMapNode<K, V> curr = this;
            int count = leftSubtreeSize;
            while (curr.parent != null) {
                if (curr.isRightChild()) {
                    count += curr.parent.leftSubtreeSize + curr.parent.values.size();
                }
                curr = curr.parent;
            }
            return count;
        }

        protected int numGreater() {
            TreeMultiMapNode<K, V> curr = this;
            int count = rightSubtreeSize;
            while (curr.parent != null) {
                if (curr.isLeftChild()) {
                    count += curr.parent.rightSubtreeSize + curr.parent.values.size();
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

        private TreeMultiMapNode<K, V> getSibling() { // assume null --> black for speed - means only RRR propagates up
            return parent == null ? null : this == parent.leftChild ? parent.rightChild : parent.leftChild;
        }

        protected static <K, V> boolean colorOf(TreeMultiMapNode<K, V> node) {
            return node == null ? BLACK : node.color;
        }

        private void rotateUp() {
            if (parent == null) {
                return; // can't rotate the top
            }
            TreeMultiMapNode<K, V> father = parent;

            if (isLeftChild()) {
                father.leftChild = rightChild;
                if (father.leftChild != null) {
                    father.leftChild.parent = father;
                }
                father.leftSubtreeSize = rightSubtreeSize;

                rightChild = father;
                rightSubtreeSize += father.rightSubtreeSize + father.values.size();
            } else {
                father.rightChild = leftChild;
                if (father.rightChild != null) {
                    father.rightChild.parent = father;
                }
                father.rightSubtreeSize = leftSubtreeSize;

                leftChild = father;
                leftSubtreeSize += father.leftSubtreeSize + father.values.size();
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
            TreeMultiMapNode<K, V> father = parent;
            TreeMultiMapNode<K, V> grandfather = father.parent;
            TreeMultiMapNode<K, V> uncle = father.getSibling();
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

        @Override
        public int compareTo(TreeMultiMapNode<K, V> other) {
            int diff = numSmaller() - other.numSmaller();
            return diff != 0 ? diff : (other.numGreater() - numGreater()); // i rlly hope this works :D
        }

        protected TreeMultiMapNode<K, V> next() {
            if (rightChild != null) {
                TreeMultiMapNode<K, V> next = rightChild;
                while (next.leftChild != null) {
                    next = next.leftChild;
                }
                return next;
            } else {
                TreeMultiMapNode<K, V> next = this;
                while (next.isRightChild()) {
                    next = next.parent;
                }
                return next.parent; // null -> last
            }
        }

        protected TreeMultiMapNode<K, V> prev() { // sigh there's so much copy/pasting here but I realized how to fix too late ...
            if (leftChild != null) {
                TreeMultiMapNode<K, V> next = leftChild;
                while (next.rightChild != null) {
                    next = next.rightChild;
                }
                return next;
            } else {
                TreeMultiMapNode<K, V> next = this;
                while (next.isLeftChild()) {
                    next = next.parent;
                }
                return next.parent; // null -> last
            }
        }

        public String toString() {
            return key.toString() + " : " + values.toString();
        }
    }

    protected final Comparator<K> comparator;

    public TreeMultiMap() {
        comparator = null;
    }

    // Pass Class<T> to constructor?
    public TreeMultiMap(Map<? extends K, ? extends V> m) { // huh
        comparator = null;
    }

    public V peekFirst() {
        return get(0);
    }

    public V removeFirst() {
        TreeMultiMapNode<K, V> firstNode = getNode(0);
        V ret = get(0);
        remove(firstNode.key, ret);
        return ret;
    }

    public V peekLast() {
        return get(size() - 1);
    }

    public V removeLast() {
        TreeMultiMapNode<K, V> lastNode = getNode(size() - 1);
        V ret = get(size() - 1);
        remove(lastNode.key, ret);
        return ret;
    }

    /** defaults to the minimum key associated */
    public void changeKey(K key, V value) {
        changeKey(key, firstKeyOfValue(value), value);
    }

    public void changeKey(K newKey, K oldKey, V value) {
        remove(oldKey, value);
        put(newKey, value);
    }

    private int size = 0;
    private TreeMultiMapNode<K, V> root = null;
    private final HashMap<V, TreeMultiSet<TreeMultiMapNode<K, V>>> backwardHashMap = new HashMap<>(); // holy buckets
    private final HashMultiSet<OrderedTuple<K, V>> pairs = new HashMultiSet<>();
    private final TreeMultiSet<K> keySet = new TreeMultiSet<>();

    private int compare(TreeMultiMapNode<K, V> node, K key) {
        if (comparator == null) {
            return ((Comparable<? super K>) node.key).compareTo(key);
        } else {
            return comparator.compare(node.key, key);
        }
    }

    private TreeMultiMapNode<K, V> getNode(int i) {
        if (i < 0 || i >= size()) {
            throw new IndexOutOfBoundsException();
        }
        TreeMultiMapNode<K, V> curr = root;
        int numSmaller = 0;
        while (numSmaller + curr.leftSubtreeSize + curr.values.size() <= i || numSmaller + curr.leftSubtreeSize > i) {
            if (numSmaller + curr.leftSubtreeSize > i) {
                curr = curr.leftChild;
            } else {
                numSmaller += curr.leftSubtreeSize + curr.values.size();
                curr = curr.rightChild;
            }
        }
        return curr;
    }

    public final V get(int i) {
        if (i < 0 || i >= size()) {
            throw new IndexOutOfBoundsException();
        }
        TreeMultiMapNode<K, V> curr = root;
        int numSmaller = 0;
        while (numSmaller + curr.leftSubtreeSize + curr.values.size() <= i || numSmaller + curr.leftSubtreeSize > i) {
            if (numSmaller + curr.leftSubtreeSize > i) {
                curr = curr.leftChild;
            } else {
                numSmaller += curr.leftSubtreeSize + curr.values.size();
                curr = curr.rightChild;
            }
        }
        return curr.values.get(i - (numSmaller + curr.leftSubtreeSize));
    }

    private TreeMultiMapNode<K, V> getNode(K key) {
        if (!keySet.contains(key)) {
            return null;
        }

        TreeMultiMapNode<K, V> node = root;
        while (true) {
            if (compare(node, key) < 0) {
                if (node.rightChild != null) {
                    node = node.rightChild;
                } else {
                    throw new IllegalStateException();
                }
            } else if ((compare(node, key) > 0)) {
                if (node.leftChild != null) {
                    node = node.leftChild;
                } else {
                    throw new IllegalStateException();
                }
            } else {
                return node;
            }
        }
    }


    @Override
    public final int size() {
        return size;
    }

    private void placeMapping(V value, TreeMultiMapNode<K, V> node) {
        if (!backwardHashMap.containsKey(value)) {
            backwardHashMap.put(value, new TreeMultiSet<>());
        }
        TreeMultiSet<TreeMultiMapNode<K, V>> preNodes = backwardHashMap.get(value);
        preNodes.add(node);
        pairs.add(new OrderedTuple<>(node.key, value, true));
    }

    private boolean deleteMapping(V value, TreeMultiMapNode<K, V> node) {
        TreeMultiSet<TreeMultiMapNode<K, V>> preNodes = backwardHashMap.get(value);
        preNodes.remove(node); // why did adding an assertion cause it to fail...
        if (preNodes.isEmpty()) {
            backwardHashMap.remove(value);
        }
        pairs.remove(new OrderedTuple<>(node.key, value, true));
        return preNodes.isEmpty(); // signifies if we need to delete the node
    }

    @Override
    public final V put(K key, V value) {
        size += 1;
        keySet.add(key);
        if (size() == 1) {
            root = new TreeMultiMapNode<>(key, value, null, this);
            placeMapping(value, root);
            root.insertionRebalance(); // not rlly needed but who gaf
            return null;
        }

        TreeMultiMapNode<K, V> parent = root;
        while (true) {
            if (compare(parent, key) < 0) {
                parent.rightSubtreeSize += 1;
                if (parent.rightChild != null) {
                    parent = parent.rightChild;
                } else {
                    parent.rightChild = new TreeMultiMapNode<>(key, value, parent, this);
                    placeMapping(value, parent.rightChild);
                    parent.rightChild.insertionRebalance();
                    return null;
                }
            } else if ((compare(parent, key) > 0)) {
                parent.leftSubtreeSize += 1;
                if (parent.leftChild != null) {
                    parent = parent.leftChild;
                } else {
                    parent.leftChild = new TreeMultiMapNode<>(key, value, parent, this);
                    placeMapping(value, parent.leftChild);
                    parent.leftChild.insertionRebalance();
                    return null;
                }
            } else {
                parent.values.add(value);
                placeMapping(value, parent);
                return null;
            }
        }
    }

    @Override
    public final V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean remove(Object key, Object value) {
        if (!pairs.contains(new OrderedTuple<>(key, value, true))) {
            return false;
        }
        keySet.remove(key);
        K keyy = (K) key;
        V valuee = (V) value;
        size -= 1;

        TreeMultiMapNode<K, V> toRemove = root;
        while (true) {
            if (compare(toRemove, keyy) < 0) {
                toRemove.rightSubtreeSize -= 1;
                if (toRemove.rightChild != null) {
                    toRemove = toRemove.rightChild;
                } else {
                    throw new IllegalStateException();
                }
            } else if ((compare(toRemove, keyy) > 0)) {
                toRemove.leftSubtreeSize -= 1;
                if (toRemove.leftChild != null) {
                    toRemove = toRemove.leftChild;
                } else {
                    throw new IllegalStateException();
                }
            } else {
                if (toRemove.values.remove(valuee)) {
                    deleteMapping(valuee, toRemove);
                    if (toRemove.values.isEmpty()) {
                        break;
                    }
                    return true;
                } else {
                    throw new IllegalStateException();
                }
            }
        }

        // need to delete node :(

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
            TreeMultiMapNode<K, V> toReplace = toRemove.leftChild;

            while (toReplace.rightChild != null) {
                toReplace = toReplace.rightChild;
            }

            int remove = toReplace.values.size();

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
            TreeMultiMapNode<K, V> temp3 = toReplace.parent;
            TreeMultiMapNode<K, V> temp4 = toReplace.leftChild;

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
    public final boolean containsKey(Object o) {
        return keySet.contains(o);
    }

    @Override
    public final boolean containsValue(Object value) {
        return backwardHashMap.containsKey(value);
    }

    public final int indexOfKey(K key) {
        return firstIndexOfKey(key);
    }

    public final int firstIndexOfKey(K key) {
        if (containsKey(key)) {
            return keySet.indexOf(key);
        } else {
            return -1;
        }
    }

    public final int lastIndexOfKey(K key) {
        if (containsKey(key)) {
            return keySet.indexOf(key) + keySet.count(key) - 1;
        } else {
            return -1;
        }
    }

    public final int indexOfValue(V value) {
        return firstIndexOfValue(value);
    }

    public final int firstIndexOfValue(V value) {
        if (containsValue(value)) {
            return backwardHashMap.get(value).get(0).numSmaller() + backwardHashMap.get(value).get(0).values.indexOf(value);
        } else {
            return -1;
        }
    }

    public final int lastIndexOfValue(V value) {
        if (containsValue(value)) {
            return backwardHashMap.get(value).get(backwardHashMap.get(value).size() - 1).numSmaller() + backwardHashMap.get(value).get(0).values.lastIndexOf(value);
        } else {
            return -1;
        }
    }

    public final K firstKeyOfValue(V value) {
        return backwardHashMap.get(value).get(0).key;
    }

    public final K lastKeyOfValue(V value) {
        return backwardHashMap.get(value).get(backwardHashMap.get(value).size() - 1).key;
    }

    public final TreeMultiSet<K> keysOfValue(V value) {
        TreeMultiSet<K> keys = new TreeMultiSet<>();
        for (TreeMultiMapNode<K, V> node : new TreeSet<>(backwardHashMap.get(value))) { // to ignore duplicates
            keys.add(node.key);
        }
        return keys;
    }

    public final ArrayDequeList<Integer> indicesOfValue(V value) {
        @SuppressWarnings("rawtypes")
        ArrayDequeList<Integer> indices = new ArrayDequeList();
        for (TreeMultiMapNode<K, V> node : new TreeSet<>(backwardHashMap.get(value))) { // to ignore duplicates
            for (int subindex : node.values.indicesOf(value)) {
                indices.add(node.numSmaller() + subindex);
            }
        }
        return indices;
    }

    public int countAsKey(K k) {
        if (keySet.count(k) != getNode(k).values.size()) {
            System.out.println(k);
            throw new IllegalStateException();
        }
        return keySet.count(k);
    }

    public int countAsValue(V v) {
        if (containsValue(v)) {
            return backwardHashMap.get(v).size();
        } else {
            return -1;
        }
    }

    @Override
    public final void clear() {
        size = 0;
        root = null;
        backwardHashMap.clear();
        pairs.clear();
        keySet.clear();
    }

    @Override
    public V get(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet() {
        return new Set<K>() {
            @Override
            public int size() {
                return keySet.uniqueElementCount();
            }

            @Override
            public boolean isEmpty() {
                return size() == 0;
            }

            @Override
            public boolean contains(Object o) {
                return keySet.contains(o);
            }

            @Override
            public Iterator<K> iterator() {
                return new Iterator<K>() {
                    TreeMultiMapNode<K, V> curr = size() > 0 ? getNode(0) : null;

                    @Override
                    public boolean hasNext() {
                        return curr != null;
                    }

                    @Override
                    public K next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        K ret = curr.key;
                        curr = curr.next();
                        return ret;
                    }
                };
            }

            @Override
            public Object[] toArray() {
                Object[] ret = new Object[size()];
                int i = 0;
                for (K key : this) {
                    ret[i] = key;
                    i += 1;
                }
                return ret;
            }

            @Override
            public <T> T[] toArray(T[] a) {
                int i = 0;
                for (K key : this) {
                    a[i] = (T) key;
                    i += 1;
                }
                return a;
            }

            @Override
            public boolean add(K k) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends K> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean equals(Object o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int hashCode() {
                return 0;
            }
        };
    }

    @Override
    public Collection<V> values() {
        return new Collection<V>() {

            @Override
            public int size() {
                return TreeMultiMap.this.size();
            }

            @Override
            public boolean isEmpty() {
                return TreeMultiMap.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                return TreeMultiMap.this.containsValue(o);
            }

            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    TreeMultiMapNode<K, V> curr = size() > 0 ? getNode(0) : null;
                    int currentCounter = 1;

                    @Override
                    public boolean hasNext() {
                        return curr != null && (curr.next() != null || currentCounter <= curr.values.size());
                    }

                    @Override
                    public V next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        V ret = curr.values.get(currentCounter - 1);
                        if (currentCounter >= curr.values.size()) {
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
            public Object[] toArray() {
                Object[] ret = new Object[size()];
                int i = 0;
                for (V value : this) {
                    ret[i] = value;
                    i += 1;
                }
                return ret;
            }

            @Override
            public <T> T[] toArray(T[] a) {
                int i = 0;
                for (V value : this) {
                    a[i] = (T) value;
                    i += 1;
                }
                return a;
            }

            @Override
            public boolean add(V v) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends V> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Set<Entry<K,V>> entrySet() {
        return new Set<>() {

            @Override
            public int size() {
                return pairs.size();
            }

            @Override
            public boolean isEmpty() {
                return size() == 0;
            }

            @Override
            public boolean contains(Object o) {
                return pairs.contains(o);
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<>() {
                    TreeMultiMapNode<K, V> curr = size() > 0 ? getNode(0) : null;
                    int currentCounter = 1;

                    @Override
                    public boolean hasNext() {
                        return curr != null && (curr.next() != null || currentCounter <= curr.values.size());
                    }

                    @Override
                    public Entry<K, V> next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        Entry<K, V> ret = new OrderedTuple<>(curr.key, curr.values.get(currentCounter - 1), true);
                        if (currentCounter >= curr.values.size()) {
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
            public Object[] toArray() {
                Object[] ret = new Object[size()];
                int i = 0;
                for (Entry<K, V> entry : this) {
                    ret[i] = entry;
                    i += 1;
                }
                return ret;
            }

            @Override
            public <T> T[] toArray(T[] a) {
                int i = 0;
                for (Entry<K, V> entry : this) {
                    a[i] = (T) entry;
                    i += 1;
                }
                return a;
            }

            @Override
            public boolean add(Entry<K, V> kvEntry) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean remove(Object o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean addAll(Collection<? extends Entry<K, V>> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private static <K, V> int bruteForceLeftSubtree(TreeMultiMapNode<K, V> x) {
        if (x == null) {
            return -1;
        }
        int ret = 0;
        if (x.leftChild != null) {
            ret = x.leftChild.values.size() + bruteForceLeftSubtree(x.leftChild) + bruteForceRightSubtree(x.leftChild);
        }
        if (ret != x.leftSubtreeSize) {
            throw new IllegalStateException("Left subtree size mismatch");
        }
        return ret;
    }

    private static <K, V> int bruteForceRightSubtree(TreeMultiMapNode<K, V> x) {
        if (x == null) {
            return -1;
        }
        int ret = 0;
        if (x.rightChild != null) {
            ret = x.rightChild.values.size() + bruteForceLeftSubtree(x.rightChild) + bruteForceRightSubtree(x.rightChild);
        }
        if (ret != x.rightSubtreeSize) {
            throw new IllegalStateException("Right subtree size mismatch");
        }
        return ret;
    }

    private static <K, V> void assertDataMakesSense(TreeMultiMap<K, V> tree) {
        int calculatedSize = 0;
        bruteForceLeftSubtree(tree.root);
        bruteForceRightSubtree(tree.root);
        if (tree.root != null && tree.root.color == RED) {
            throw new IllegalStateException("Tree root color is red");
        }
        int blackHeight = -1;
        for (Map.Entry<V, TreeMultiSet<TreeMultiMapNode<K, V>>> entry : tree.backwardHashMap.entrySet()) {
            calculatedSize += entry.getValue().size();
            int prevIndex = -1;
            for (TreeMultiMapNode<K, V> node : entry.getValue()) {
                if (prevIndex > node.numSmaller()) { // can't be >= because of doubled nodes lmao
                    throw new IllegalStateException("BackwardHashMap nodes out of order");
                }
                prevIndex = node.numSmaller();
                if (!Objects.equals(tree.get(prevIndex + node.values.indexOf(entry.getKey())), entry.getKey())) { // huh
                    throw new IllegalStateException("Backward Hash Map not working");
                }
                if (entry.getValue().count(node) != node.values.indicesOf(entry.getKey()).size()) {
                    throw new IllegalStateException("Repeated counts are inconsistent");
                }
                if (node.values.isEmpty()) {
                    throw new IllegalStateException("Size 0 still exists");
                }
                for (int i = 0; i < node.values.size(); i += 1) {
                    if (!Objects.equals(tree.get(prevIndex + i), node.values.get(i))) { // huh
                        throw new IllegalStateException("get(index) not working");
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
        }
        if (tree.size() != calculatedSize) {
            throw new IllegalStateException("Tree size inconsistent");
        }
    }

    public static final double ADD_PROBABILITY = 0.45;
    public static final double NULL_PROBABILITY = 0.05;
    public static final int NUM_TESTS = 100;
    public static final int NUM_ITERATIONS = 10000;

    public static void testAddRemoveSame() {
        TreeMultiMap<Integer, Integer> testTree = new TreeMultiMap<>();
        TreeMultiSet<Integer> parallel = new TreeMultiSet<>();
        TreeDequeList<Integer> otherParallel = new TreeDequeList<>();

        for (int k = 0; k < NUM_TESTS; k += 1) {
            testTree.clear();
            parallel.clear();
            otherParallel.clear();
            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                if (testTree.size() == 0 || Randomness.getDouble() < ADD_PROBABILITY) {
                    int toAdd = Randomness.getRandomInteger(-1024, 1024);
                    testTree.put(toAdd, 0);
                    parallel.add(toAdd);
                    otherParallel.add(testTree.lastIndexOfKey(toAdd), 0);
                } else {
                    int toRemoveIndex = Randomness.getRandomInteger(0, testTree.size());
                    int toRemove = parallel.get(toRemoveIndex);
                    Integer toRemove2 = testTree.get(toRemoveIndex);
                    testTree.remove(toRemove, toRemove2);
                    parallel.remove(toRemove);
                    if (!Objects.equals(toRemove2, otherParallel.remove(toRemoveIndex))) {
                        throw new IllegalStateException("Inconsistent arrays");
                    }
                    assertDataMakesSense(testTree);
                }
                assertDataMakesSense(testTree);
                if (parallel.size() != testTree.size()) {
                    throw new IllegalStateException("what");
                }
                for (Integer i : parallel) {
                    if (testTree.countAsKey(i) != parallel.count(i)) {
                        throw new IllegalStateException("Arrays were inconsistent");
                    }
                }
                if (!Arrays.equals(testTree.values().toArray(), otherParallel.toArray())) {
                    System.out.println(Arrays.toString(testTree.values().toArray()));
                    System.out.println(Arrays.toString(otherParallel.toArray()));
                    throw new IllegalStateException("Arrays were inconsistent");
                }
            }
            for (int j = testTree.size(); j > 0; j -= 1) {
                int toRemoveIndex = Randomness.getRandomInteger(0, testTree.size());
                int toRemove = parallel.get(toRemoveIndex);
                Integer toRemove2 = testTree.get(toRemoveIndex);
                testTree.remove(toRemove, toRemove2);
                parallel.remove(toRemove);
                if (!Objects.equals(toRemove2, otherParallel.remove(toRemoveIndex))) {
                    throw new IllegalStateException("Inconsistent arrays");
                }
                assertDataMakesSense(testTree);
                if (parallel.size() != testTree.size()) {
                    throw new IllegalStateException("what");
                }
                for (Integer i : parallel) {
                    if (testTree.countAsKey(i) != parallel.count(i)) {
                        throw new IllegalStateException("Arrays were inconsistent");
                    }
                }
                if (!Arrays.equals(testTree.values().toArray(), otherParallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
                }
            }
            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                if (testTree.size() == 0 || Randomness.getDouble() < ADD_PROBABILITY) {
                    int toAdd = Randomness.getRandomInteger(-1024, 1024);
                    testTree.put(toAdd, 0);
                    parallel.add(toAdd);
                    otherParallel.add(testTree.lastIndexOfKey(toAdd), 0);
                } else {
                    int toRemoveIndex = Randomness.getRandomInteger(0, testTree.size());
                    int toRemove = parallel.get(toRemoveIndex);
                    Integer toRemove2 = testTree.get(toRemoveIndex);
                    testTree.remove(toRemove, toRemove2);
                    parallel.remove(toRemove);
                    if (!Objects.equals(toRemove2, otherParallel.remove(toRemoveIndex))) {
                        throw new IllegalStateException("Inconsistent arrays");
                    }
                    assertDataMakesSense(testTree);
                }
                assertDataMakesSense(testTree);
                if (parallel.size() != testTree.size()) {
                    throw new IllegalStateException("what");
                }
                for (Integer i : parallel) {
                    if (testTree.countAsKey(i) != parallel.count(i)) {
                        throw new IllegalStateException("Arrays were inconsistent");
                    }
                }
                if (!Arrays.equals(testTree.values().toArray(), otherParallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
                }
            }
        }
    }

    public static void testAddRemoveDifferent() {
        TreeMultiMap<Integer, Integer> testTree = new TreeMultiMap<>();
        TreeMultiSet<Integer> parallel = new TreeMultiSet<>();
        TreeDequeList<Integer> otherParallel = new TreeDequeList<>();

        for (int k = 0; k < NUM_TESTS; k += 1) {
            testTree.clear();
            parallel.clear();
            otherParallel.clear();
            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                if (testTree.size() == 0 || Randomness.getDouble() < ADD_PROBABILITY) {
                    int toAdd = Randomness.getRandomInteger(-1024, 1024);
                    Integer thingyToAdd = Randomness.getDouble() < NULL_PROBABILITY ? null : Randomness.getRandomInteger(-1024, 1024);
                    testTree.put(toAdd, thingyToAdd);
                    parallel.add(toAdd);
                    otherParallel.add(testTree.lastIndexOfKey(toAdd), thingyToAdd);
                } else {
                    int toRemoveIndex = Randomness.getRandomInteger(0, testTree.size());
                    int toRemove = parallel.get(toRemoveIndex);
                    Integer toRemove2 = testTree.get(toRemoveIndex);
                    testTree.remove(toRemove, toRemove2);
                    parallel.remove(toRemove);
                    if (!Objects.equals(toRemove2, otherParallel.remove(toRemoveIndex))) {
                        throw new IllegalStateException("Inconsistent arrays");
                    }
                    assertDataMakesSense(testTree);
                }
                assertDataMakesSense(testTree);
                if (parallel.size() != testTree.size()) {
                    throw new IllegalStateException("what");
                }
                for (Integer i : parallel) {
                    if (testTree.countAsKey(i) != parallel.count(i)) {
                        throw new IllegalStateException("Arrays were inconsistent");
                    }
                }
                if (!Arrays.equals(testTree.values().toArray(), otherParallel.toArray())) {
                    System.out.println(Arrays.toString(testTree.values().toArray()));
                    System.out.println(Arrays.toString(otherParallel.toArray()));
                    throw new IllegalStateException("Arrays were inconsistent");
                }
            }
            for (int j = testTree.size(); j > 0; j -= 1) {
                int toRemoveIndex = Randomness.getRandomInteger(0, testTree.size());
                int toRemove = parallel.get(toRemoveIndex);
                Integer toRemove2 = testTree.get(toRemoveIndex);
                testTree.remove(toRemove, toRemove2);
                parallel.remove(toRemove);
                if (!Objects.equals(toRemove2, otherParallel.remove(toRemoveIndex))) {
                    throw new IllegalStateException("Inconsistent arrays");
                }
                assertDataMakesSense(testTree);
                if (parallel.size() != testTree.size()) {
                    throw new IllegalStateException("what");
                }
                for (Integer i : parallel) {
                    if (testTree.countAsKey(i) != parallel.count(i)) {
                        throw new IllegalStateException("Arrays were inconsistent");
                    }
                }
                if (!Arrays.equals(testTree.values().toArray(), otherParallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
                }
            }
            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                if (testTree.size() == 0 || Randomness.getDouble() < ADD_PROBABILITY) {
                    int toAdd = Randomness.getRandomInteger(-1024, 1024);
                    Integer thingyToAdd = Randomness.getDouble() < NULL_PROBABILITY ? null : Randomness.getRandomInteger(-1024, 1024);
                    testTree.put(toAdd, thingyToAdd);
                    parallel.add(toAdd);
                    otherParallel.add(testTree.lastIndexOfKey(toAdd), thingyToAdd);
                } else {
                    int toRemoveIndex = Randomness.getRandomInteger(0, testTree.size());
                    int toRemove = parallel.get(toRemoveIndex);
                    Integer toRemove2 = testTree.get(toRemoveIndex);
                    testTree.remove(toRemove, toRemove2);
                    parallel.remove(toRemove);
                    if (!Objects.equals(toRemove2, otherParallel.remove(toRemoveIndex))) {
                        throw new IllegalStateException("Inconsistent arrays");
                    }
                    assertDataMakesSense(testTree);
                }
                assertDataMakesSense(testTree);
                if (parallel.size() != testTree.size()) {
                    throw new IllegalStateException("what");
                }
                for (Integer i : parallel) {
                    if (testTree.countAsKey(i) != parallel.count(i)) {
                        throw new IllegalStateException("Arrays were inconsistent");
                    }
                }
                if (!Arrays.equals(testTree.values().toArray(), otherParallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
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
        Iterator<K> iterator = keySet().iterator();
        while (true) {
            K key = iterator.next();
            sb.append(key + " " + getNode(key).values);
            if (!iterator.hasNext()) {
                return sb.append(close).append(' ').toString();
            }
            sb.append(between);
        }
    }

    public String visualization() {
        return visualization(TreeMultiMapNode::toString);
    }

    private String visualization(G2G<TreeMultiMapNode<K, V>, String> instructions) {
        return VisualizeTree.stringOfTree(root,
                // x -> new ArrayList<>(Arrays.asList(x.leftChild, x.rightChild)),
                x -> x.leftChild, x -> x.rightChild,
                instructions, 10) + '\n' + this + '\n' + backwardHashMap;
    }

    public static void main(String[] args) {

        testAddRemoveSame();
        testAddRemoveDifferent();
        System.out.println("TreeMultiMap passed all tests :)"); // holy goated :D

    }
}
