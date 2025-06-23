package datastructures.collections.setunion;

import datastructures.utility.Randomness;
import datastructures.utility.VisualizeTree;

import java.util.*;

public class UnionFind<E> {

    private static interface Move<E> {
        abstract void undo(UnionFind<E> dsu);
    }

    private static class Merge<E> implements Move<E> {
        private final UnionFindNode<E> first, second;
        private final int firstRank, secondRank;
        private final ArrayList<UnionFindNode<E>> originalParentages = new ArrayList<>();

        public Merge(UnionFindNode<E> first, UnionFindNode<E> second) {
            this.first = first;
            this.second = second;

            this.firstRank = first.rank;
            this.secondRank = second.rank;
        }

        @Override
        public void undo(UnionFind<E> dsu) {
            for (int i = 0; i < originalParentages.size(); i += 2) {
                originalParentages.get(i).setParent(originalParentages.get(i + 1));
            }

            first.setParent(first);
            second.setParent(second);

            second.rank = secondRank;
            first.rank = firstRank;

            dsu.count += 1;
        }

        @Override
        public String toString() {
            return "Undoing: merge " + first.element + " with " + second.element;
        }
    }

    private static class Deletion<E> implements Move<E> {
        private final UnionFindNode<E> removedNode, changedNode;
        private final E originalValue;

        public Deletion(UnionFindNode<E> changedNode, UnionFindNode<E> removedNode, E originalValue) {
            this.changedNode = changedNode;
            this.removedNode = removedNode;

            this.originalValue = originalValue;
        }

        @Override
        public void undo(UnionFind<E> dsu) {
            dsu.ancestryMap.put(removedNode.element, removedNode);
            if (removedNode.parent != removedNode) {
                removedNode.setParent(removedNode.parent);
                if (changedNode != null) {
                    changedNode.element = originalValue;
                    dsu.ancestryMap.put(changedNode.element, changedNode);
                }
            } else {
                dsu.count += 1;
            }
        }

        @Override
        public String toString() {
            return "Undoing: delete " + originalValue;
        }
    }

    private static class Addition<E> implements Move<E> {
        private final E addedElement;

        public Addition(E addedElement) {
            this.addedElement = addedElement;
        }

        @Override
        public void undo(UnionFind<E> dsu) {
            dsu.ancestryMap.remove(addedElement);
            dsu.count -= 1;
        }

        @Override
        public String toString() {
            return "Undoing: add " + addedElement;
        }
    }

    private static class UnionFindNode<E> {
        private UnionFindNode<E> parent = this;
        private int rank = 0;
        private final HashSet<UnionFindNode<E>> children = new HashSet<>();
        private E element;

        UnionFindNode(E element) {
            this.element = element;
        }

        private void setParent(UnionFindNode<E> newParent) {
            if (parent != this) {
                parent.children.remove(this);
            }
            parent = newParent;
            if (parent != this) {
                parent.children.add(this);
            }
        }

        private UnionFindNode<E> findRoot() {
            UnionFindNode<E> node = this;
            while (node.parent != node) { // while node.parent is not the root
                node = node.parent;
            }
            return node;
        }

        private void compress(UnionFindNode<E> root, Merge<E> currentMerge) {
            UnionFindNode<E> node = this;
            while (node.parent != root) { // while node.parent is not the root
                currentMerge.originalParentages.add(node);
                currentMerge.originalParentages.add(node.parent);

                UnionFindNode<E> temp = node;

                node = node.parent;

                temp.parent.children.remove(temp);

                temp.parent = root;
                root.children.add(temp);
            }
        }

        @Override
        public String toString() {
            return Objects.toString(element); // lmfao this was actually pretty funny
        }

        // can't override hash code lol
    }

    public UnionFind() {
        count = 0;
    }

    public UnionFind(Collection<E> collection) {
        for (E e : collection) {
            ancestryMap.put(e, new UnionFindNode<>(e));
        }
        count = ancestryMap.size();
    }

    int count;
    private final HashMap<E, UnionFindNode<E>> ancestryMap = new HashMap<>();
    private final ArrayList<Move<E>> moves = new ArrayList<>();

    public boolean add(E e) {
        if (ancestryMap.containsKey(e)) {
            return false;
        }

        UnionFindNode<E> node = new UnionFindNode<>(e);
        moves.add(new Addition<>(e));

        ancestryMap.put(e, node);
        count += 1;

        return true;
    }

    public boolean delete(E e) {
        if (!ancestryMap.containsKey(e)) {
            return false;
        }

        UnionFindNode<E> node = ancestryMap.get(e);

        ancestryMap.remove(e);

        if (node.parent == node && node.children.isEmpty()) {
            count -= 1;
            moves.add(new Deletion<>(null, node, e));
            return true;
        }
        if (node.children.isEmpty()) {
            node.parent.children.remove(node);
            moves.add(new Deletion<>(null, node, e));
            return true;
        }
        UnionFindNode<E> replaceWith = node;
        while (!replaceWith.children.isEmpty()) {
            replaceWith = replaceWith.children.iterator().next();
        }
        replaceWith.parent.children.remove(replaceWith);
        ancestryMap.replace(replaceWith.element, node);
        node.element = replaceWith.element;
        moves.add(new Deletion<>(node, replaceWith, e));
        return true;
    }

    public int getCount() {
        return count;
    }

    private UnionFindNode<E> findRoot(E e) {
        return ancestryMap.get(e).findRoot();
    }

    public boolean merged(E x, E y) {
        if (!ancestryMap.containsKey(x) || !ancestryMap.containsKey(y)) {
            return false;
        }
        return findRoot(x) == findRoot(y);
    }

    /** returns true if they weren't already merged */
    public boolean merge(E x, E y) {
        if (!ancestryMap.containsKey(x) || !ancestryMap.containsKey(y)) {
            return false;
        }
        UnionFindNode<E> xNode = ancestryMap.get(x);
        UnionFindNode<E> yNode = ancestryMap.get(y);
        UnionFindNode<E> xRoot = xNode.findRoot();
        UnionFindNode<E> yRoot = yNode.findRoot();
        if (xRoot == yRoot) {
            return false;
        }
        count -= 1;
        Merge<E> merge = new Merge<>(xRoot, yRoot);

        if (xRoot.rank < yRoot.rank) {
            xRoot.setParent(yRoot);
        } else {
            yRoot.setParent(xRoot);
            if (xRoot.rank == yRoot.rank) {
                xRoot.rank += 1;
            }
        }

        xNode.compress(xRoot.parent, merge);
        yNode.compress(xRoot.parent, merge);

        moves.add(merge);

        return true;
    }

    public void undo() {
        moves.remove(moves.size() - 1).undo(this);
    }

    public HashSet<HashSet<E>> disjointUnions() {
        HashSet<HashSet<E>> nodes = new HashSet<>();
        ArrayDeque<UnionFindNode<E>> queue = new ArrayDeque<>();
        for (UnionFindNode<E> node : ancestryMap.values()) {
            if (node.parent == node) {
                HashSet<E> set = new HashSet<>();
                queue.add(node);
                while (!queue.isEmpty()) {
                    UnionFindNode<E> curr = queue.removeFirst();
                    set.add(curr.element);
                    for (UnionFindNode<E> child : curr.children) {
                        queue.add(child);
                    }
                }
                nodes.add(set);
            }
        }
        return nodes;
    }

    public void clear() {
        count = 0;
        ancestryMap.clear();
        moves.clear();
    }

    public void clearHistory() {
        moves.clear();
    }

    private void assertDataMakesSense() {
        for (Map.Entry<E, UnionFindNode<E>> entry : ancestryMap.entrySet()) {
            if (!entry.getValue().element.equals(entry.getKey())) {
                throw new IllegalStateException("Hash Map not working");
            }
        }
        if (disjointUnions().size() != count) {
            throw new IllegalStateException("Disjoint Unions not working");
        }
    }

    public static final double ADD_PROBABILITY = 0.4;
    public static final double REMOVE_PROBABILITY = 0.25;
    public static final double MERGE_PROBABILITY = 0.25;
    public static final int NUM_TESTS = 10;
    public static final int NUM_ITERATIONS = 10000;

    public static void testAddRemoveMergeUndo() {
        UnionFind<Integer> dsu = new UnionFind<>();

        for (int k = 0; k < NUM_TESTS; k += 1) {
            dsu.clear();

            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                double random = Randomness.getDouble();
                if (random < ADD_PROBABILITY) {
                    Integer toAdd = Randomness.getRandomInteger(-1024, 1024);
                    if (dsu.ancestryMap.containsKey(toAdd)) {
                        j -= 1;
                    } else {
                        dsu.add(toAdd);
                    }
                } else if (random < ADD_PROBABILITY + REMOVE_PROBABILITY) {
                    if (dsu.ancestryMap.isEmpty()) {
                        j -= 1;
                    } else {
                        Integer[] arr = dsu.ancestryMap.keySet().toArray(new Integer[0]);
                        Integer toRemove = arr[Randomness.getRandomInteger(0, arr.length)];
                        dsu.delete(toRemove);
                    }
                } else if (random < ADD_PROBABILITY + REMOVE_PROBABILITY + MERGE_PROBABILITY) {
                    if (dsu.ancestryMap.isEmpty()) {
                        j -= 1;
                    } else {
                        Integer[] arr = dsu.ancestryMap.keySet().toArray(new Integer[0]);
                        Integer toMergeOne = arr[Randomness.getRandomInteger(0, arr.length)];
                        Integer toMergeTwo = arr[Randomness.getRandomInteger(0, arr.length)];
                        if (dsu.ancestryMap.get(toMergeOne).findRoot() == dsu.ancestryMap.get(toMergeTwo)) {
                            j -= 1;
                        } else {
                            dsu.merge(toMergeOne, toMergeTwo);
                        }
                    }
                } else {
                    if (dsu.moves.isEmpty()) {
                        j -= 1;
                    } else {
                        dsu.undo();
                    }
                }
                dsu.assertDataMakesSense();
            }
            for (int j = dsu.ancestryMap.size(); j > 0; j -= 1) {
                Integer[] arr = dsu.ancestryMap.keySet().toArray(new Integer[0]);
                Integer toRemove = arr[Randomness.getRandomInteger(0, arr.length)];
                dsu.delete(toRemove);
                dsu.assertDataMakesSense();
            }

            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                double random = Randomness.getDouble();
                if (random < ADD_PROBABILITY) {
                    Integer toAdd = Randomness.getRandomInteger(-1024, 1024);
                    if (dsu.ancestryMap.containsKey(toAdd)) {
                        j -= 1;
                    } else {
                        dsu.add(toAdd);
                    }
                } else if (random < ADD_PROBABILITY + REMOVE_PROBABILITY) {
                    if (dsu.ancestryMap.isEmpty()) {
                        j -= 1;
                    } else {
                        Integer[] arr = dsu.ancestryMap.keySet().toArray(new Integer[0]);
                        Integer toRemove = arr[Randomness.getRandomInteger(0, arr.length)];
                        dsu.delete(toRemove);
                    }
                } else if (random < ADD_PROBABILITY + REMOVE_PROBABILITY + MERGE_PROBABILITY) {
                    if (dsu.ancestryMap.isEmpty()) {
                        j -= 1;
                    } else {
                        Integer[] arr = dsu.ancestryMap.keySet().toArray(new Integer[0]);
                        Integer toMergeOne = arr[Randomness.getRandomInteger(0, arr.length)];
                        Integer toMergeTwo = arr[Randomness.getRandomInteger(0, arr.length)];
                        if (dsu.ancestryMap.get(toMergeOne).findRoot() == dsu.ancestryMap.get(toMergeTwo)) {
                            j -= 1;
                        } else {
                            dsu.merge(toMergeOne, toMergeTwo);
                        }
                    }
                } else {
                    if (dsu.moves.isEmpty()) {
                        j -= 1;
                    } else {
                        dsu.undo();
                    }
                }
                dsu.assertDataMakesSense();
            }
            for (int j = dsu.ancestryMap.size(); j > 0; j -= 1) {
                Integer[] arr = dsu.ancestryMap.keySet().toArray(new Integer[0]);
                Integer toRemove = arr[Randomness.getRandomInteger(0, arr.length)];
                dsu.delete(toRemove);
                dsu.assertDataMakesSense();
            }
            while (!dsu.moves.isEmpty()) {
                dsu.undo();
                dsu.assertDataMakesSense();
            }

            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                double random = Randomness.getDouble();
                if (random < ADD_PROBABILITY) {
                    Integer toAdd = Randomness.getRandomInteger(-1024, 1024);
                    if (dsu.ancestryMap.containsKey(toAdd)) {
                        j -= 1;
                    } else {
                        dsu.add(toAdd);
                    }
                } else if (random < ADD_PROBABILITY + REMOVE_PROBABILITY) {
                    if (dsu.ancestryMap.isEmpty()) {
                        j -= 1;
                    } else {
                        Integer[] arr = dsu.ancestryMap.keySet().toArray(new Integer[0]);
                        Integer toRemove = arr[Randomness.getRandomInteger(0, arr.length)];
                        dsu.delete(toRemove);
                    }
                } else if (random < ADD_PROBABILITY + REMOVE_PROBABILITY + MERGE_PROBABILITY) {
                    if (dsu.ancestryMap.isEmpty()) {
                        j -= 1;
                    } else {
                        Integer[] arr = dsu.ancestryMap.keySet().toArray(new Integer[0]);
                        Integer toMergeOne = arr[Randomness.getRandomInteger(0, arr.length)];
                        Integer toMergeTwo = arr[Randomness.getRandomInteger(0, arr.length)];
                        if (dsu.ancestryMap.get(toMergeOne).findRoot() == dsu.ancestryMap.get(toMergeTwo)) {
                            j -= 1;
                        } else {
                            dsu.merge(toMergeOne, toMergeTwo);
                        }
                    }
                } else {
                    if (dsu.moves.isEmpty()) {
                        j -= 1;
                    } else {
                        dsu.undo();
                    }
                }
                dsu.assertDataMakesSense();
            }
        }
    }

    @Override
    public String toString() {
        return disjointUnions().toString();
    }

    public String visualization() {
        UnionFindNode<E> root = new UnionFindNode<>(null);
        for (UnionFindNode<E> node : ancestryMap.values()) {
            if (node.parent == node) {
                root.children.add(node);
            }
        }
        return VisualizeTree.stringOfTree(root,
                x -> new LinkedList<>(x.children),
                x -> (x == root ? "UF" : Objects.toString(x)),
                2) + '\n' + this;
    }

    public static void main(String[] args) {

        testAddRemoveMergeUndo();
        System.out.println("UnionFind passed all tests :)"); // not too sure why this one takes so much longer than the others

    }
}
