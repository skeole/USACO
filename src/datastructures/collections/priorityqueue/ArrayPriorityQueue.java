package datastructures.collections.priorityqueue;

import datastructures.utility.OrderedTuple;
import datastructures.utility.Randomness;
import datastructures.utility.VisualizeTree;
import datastructures.utility.functionalinterfaces.onetoone.G2G;

import java.util.*;

public class ArrayPriorityQueue<K, V> {

    private final ArrayList<OrderedTuple<K, V>> array = new ArrayList<>(); // should be of type Entry<Key, V> but whatevs
    private final HashMap<V, Integer> map = new HashMap<>(); // key, index

    protected final Comparator<K> comparator;

    public ArrayPriorityQueue() {
        comparator = null;
    }

    public ArrayPriorityQueue(Comparator<K> comparator) {
        this.comparator = comparator;
    }

    public int size() {
        return array.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsValue(V value) {
        return map.containsKey(value);
    }

    private int compareNodes(OrderedTuple<K, V> nodeOne, OrderedTuple<K, V> nodeTwo) {
        if (comparator == null) {
            return ((Comparable<? super K>) nodeOne.getKey()).compareTo(nodeTwo.getKey());
        } else {
            return comparator.compare(nodeOne.getKey(), nodeTwo.getKey());
        }
    }

    private void switchValues(int nodeIndex, int replaceWithIndex) {
        OrderedTuple<K, V> node = array.get(nodeIndex);
        OrderedTuple<K, V> replaceNode = array.get(replaceWithIndex);

        map.replace(node.getValue(), replaceWithIndex);
        map.replace(replaceNode.getValue(), nodeIndex);

        array.set(nodeIndex, replaceNode);
        array.set(replaceWithIndex, node);

        reHeapify(replaceWithIndex);
    }

    private void reHeapify(int nodeIndex) { // invariant: node is the only node that breaks heap invariants
        int parentNodeIndex = (nodeIndex + 1) / 2 - 1,
            leftChildNodeIndex = nodeIndex * 2 + 1,
            rightChildNodeIndex = nodeIndex * 2 + 2;

        if (parentNodeIndex >= 0 && compareNodes(array.get(nodeIndex), array.get(parentNodeIndex)) < 0) { // parent greater
            switchValues(nodeIndex, parentNodeIndex);
        } else if (rightChildNodeIndex < size() && compareNodes(array.get(rightChildNodeIndex), array.get(nodeIndex)) < 0 &&
                compareNodes(array.get(rightChildNodeIndex), array.get(leftChildNodeIndex)) < 0) { // right child lesser
            switchValues(nodeIndex, rightChildNodeIndex);
        } else if (leftChildNodeIndex < size() && compareNodes(array.get(leftChildNodeIndex), array.get(nodeIndex)) < 0) {
            // left child lesser
            switchValues(nodeIndex, leftChildNodeIndex);
        }
    }

    private void makeHeapify(int nodeIndex) { // invariant: node is the only node that breaks heap invariants
        int leftChildNodeIndex = nodeIndex * 2 + 1,
            rightChildNodeIndex = nodeIndex * 2 + 2;

        if (rightChildNodeIndex < size() && compareNodes(array.get(rightChildNodeIndex), array.get(nodeIndex)) < 0 &&
                compareNodes(array.get(rightChildNodeIndex), array.get(leftChildNodeIndex)) < 0) {
            switchValues(nodeIndex, rightChildNodeIndex);
        } else if (leftChildNodeIndex < size() && compareNodes(array.get(leftChildNodeIndex), array.get(nodeIndex)) < 0) {
            switchValues(nodeIndex, leftChildNodeIndex);
        }
    }

    public void add(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Keys cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Values cannot be null");
        }
        if (containsValue(value)) {
            throw new IllegalArgumentException("Duplicate values not allowed");
        }

        map.put(value, size());
        array.add(new OrderedTuple<>(key, value));

        reHeapify(size() - 1);
    }

    public void changeKey(V value, K newKey) {
        if (newKey == null) {
            throw new IllegalArgumentException("Null keys not allowed");
        }
        if (!containsValue(value)) {
            throw new NoSuchElementException("Tried to update nonexistent value");
        }
        int index = map.get(value);
        array.get(index).updateFirst(newKey);
        reHeapify(index);
    }

    public V peekFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        return array.get(0).getValue();
    }

    public V removeFirst() {
        V min = peekFirst();
        map.remove(min);

        if (size() == 1) {
            array.remove(0);
            return min;
        }

        array.set(0, array.remove(array.size() - 1));
        map.replace(array.get(0).getValue(), 0);
        reHeapify(0);
        return min;
    }

    public Set<V> values() {
        return map.keySet();
    }

    public K keyOfValue(V value) {
        return array.get(map.get(value)).getKey();
    }

    public void clear() {
        array.clear();
        map.clear();
    }

    public static <K, V> ArrayPriorityQueue<K, V> createArrayPriorityQueue(
            Object[] list, G2G<Object, K> objectToKey, G2G<Object, V> objectToValue
    ) {
        return createArrayPriorityQueue(list, objectToValue, objectToKey, null);
    }

    public static <K, V> ArrayPriorityQueue<K, V> createArrayPriorityQueue(
            Object[] list, G2G<Object, V> objectToValue, G2G<Object, K> objectToKey, Comparator<K> comparator
    ) {
        ArrayPriorityQueue<K, V> priorityQueue = new ArrayPriorityQueue<K, V>(comparator);
        if (list.length == 0) {
            return priorityQueue;
        }

        for (int i = 0; i < list.length; i++) {
            K key = objectToKey.function(list[i]);
            V value = objectToValue.function(list[i]);
            priorityQueue.array.add(new OrderedTuple<>(key, value));
            if (priorityQueue.map.containsKey(value)) {
                throw new IllegalArgumentException("Duplicate values not allowed");
            }
            priorityQueue.map.put(value, i);
        }
        for (int i = list.length - 1; i >= 0; i--) {
            priorityQueue.makeHeapify(i);
        }
        return priorityQueue;
    }

    private void assertDataMakesSense() {
        if (array.size() != map.size()) {
            throw new IllegalStateException("Array and map inconsistent");
        }
        for (int i = 0; i < size(); i += 1) {
            if (2 * i + 1 < size() && compareNodes(array.get(i), array.get(2 * i + 1)) > 0) {
                throw new IllegalStateException("Heap property dissatisfied");
            }
            if (2 * i + 2 < size() && compareNodes(array.get(i), array.get(2 * i + 2)) > 0) {
                throw new IllegalStateException("Heap property dissatisfied");
            }
            if (map.get(array.get(i).getValue()) != i) {
                throw new IllegalStateException("Map not working");
            }
        }
    }

    public static final double ADD_PROBABILITY = 0.25;
    public static final double REMOVE_PROBABILITY = 0.25;
    public static final int NUM_TESTS = 100;
    public static final int NUM_ITERATIONS = 10000;

    public static void testAddRemoveMergeUndo() {
        ArrayPriorityQueue<Integer, Double> heap = new ArrayPriorityQueue<>();

        for (int k = 0; k < NUM_TESTS; k += 1) {
            heap.clear();

            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                double random = Randomness.getDouble();
                if (random < ADD_PROBABILITY) {
                    Integer keyToAdd = Randomness.getRandomInteger(-1024, 1024);
                    Double valueToAdd = (Randomness.getDouble() - 0.5) * 1024;
                    while (heap.containsValue(valueToAdd)) {
                        valueToAdd = (Randomness.getDouble() - 0.5) * 1024;
                    }
                    heap.add(keyToAdd, valueToAdd);
                } else if (random < ADD_PROBABILITY + REMOVE_PROBABILITY) {
                    if (heap.isEmpty()) {
                        j -= 1;
                    } else {
                        heap.removeFirst();
                    }
                } else {
                    if (heap.isEmpty()) {
                        j -= 1;
                    } else {
                        Double[] keys = heap.map.keySet().toArray(new Double[0]);
                        Integer newKey = Randomness.getRandomInteger(-1024, 1024);
                        heap.changeKey(keys[Randomness.getRandomInteger(0, keys.length)], newKey);
                    }
                }
                heap.assertDataMakesSense();
            }
            for (int j = heap.size(); j > 0; j -= 1) {
                heap.removeFirst();
                heap.assertDataMakesSense();
            }
            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                double random = Randomness.getDouble();
                if (random < ADD_PROBABILITY) {
                    Integer keyToAdd = Randomness.getRandomInteger(-1024, 1024);
                    Double valueToAdd = (Randomness.getDouble() - 0.5) * 1024;
                    while (heap.containsValue(valueToAdd)) {
                        valueToAdd = (Randomness.getDouble() - 0.5) * 1024;
                    }
                    heap.add(keyToAdd, valueToAdd);
                } else if (random < ADD_PROBABILITY + REMOVE_PROBABILITY) {
                    if (heap.isEmpty()) {
                        j -= 1;
                    } else {
                        heap.removeFirst();
                    }
                } else {
                    if (heap.isEmpty()) {
                        j -= 1;
                    } else {
                        Double[] keys = heap.map.keySet().toArray(new Double[0]);
                        Integer newKey = Randomness.getRandomInteger(-1024, 1024);
                        heap.changeKey(keys[Randomness.getRandomInteger(0, keys.length)], newKey);
                    }
                }
                heap.assertDataMakesSense();
            }
        }
    }

    @Override
    public String toString() {
        if (size() == 0) {
            return "Empty Heap";
        }
        StringBuilder sb = new StringBuilder("Keys : [");
        for (OrderedTuple<K, V> node : array) {
            sb.append(", (").append(node);
        }
        sb.delete(8, 10);
        sb.append("]");
        return VisualizeTree.stringOfTree(0,
                x -> 2 * x + 1 < size() ? 2 * x + 1 : null, x -> 2 * x + 2 < size() ? 2 * x + 2 : null,
                x -> array.get(x).toString(), 2) + '\n' + sb;
    }

    public static void main(String[] args) {
        testAddRemoveMergeUndo();
        System.out.println("ArrayHeap passed all tests :)");
    }

}