package datastructures.collections.tries;

import datastructures.utility.IterableUtility;
import datastructures.utility.Randomness;
import datastructures.utility.VisualizeTree;

import java.util.*;
import static datastructures.utility.IterableUtility.*;

/** not just strings! */
public class TrieMap<K, V> {

    private static class TrieMapNode<K, V> {

        private final ArrayDeque<K> nodes = new ArrayDeque<>();
        private final HashMap<K, TrieMapNode<K, V>> children = new HashMap<>();
        private final HashSet<TrieMapNode<K, V>> descendants = new HashSet<>();
        private TrieMapNode<K, V> parent;
        private V value;

        private TrieMapNode(TrieMapNode<K, V> parent) {
            this.parent = parent;
        }

        private void splinter(int i) {
            TrieMapNode<K, V> newFather = new TrieMapNode<>(parent);
            parent.children.replace(nodes.getFirst(), newFather);
            for (int j = 0; j < i; j += 1) {
                newFather.nodes.addLast(nodes.removeFirst());
            }
            newFather.children.put(nodes.getFirst(), this);
            for (TrieMapNode<K, V> node : descendants) {
                newFather.descendants.add(node);
            }
            parent = newFather;
        }

        private void mergeWithParent() {
            if (parent == null || parent.parent == null) { // can't merge with root so just a safeguard
                return;
            }
            if (parent.children.size() != 1) { // again, just a safeguard
                return;
            }
            for (int j = parent.nodes.size(); j > 0; j -= 1) {
                nodes.addFirst(parent.nodes.removeLast()); // can speed this up by iteration
            }
            parent.parent.children.replace(nodes.getFirst(), this);
            parent = parent.parent;
        }

        private int numDescendants() {
            return descendants.size();
        }

        public ArrayDeque<K> asArray() {
            ArrayDeque<K> fullArray = new ArrayDeque<>();
            TrieMapNode<K, V> currentNode = this;
            while (currentNode != null) {
                Iterator<K> it = currentNode.nodes.descendingIterator();
                while (it.hasNext()) {
                    fullArray.addFirst(it.next());
                }
                currentNode = currentNode.parent;
            }
            return fullArray;
        }

        public String visualization() {
            StringBuilder sb = new StringBuilder();
            for (K k : nodes) {
                sb.append(k);
            }
            if (children.isEmpty()) {
                sb.append(" : ").append(value);
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (K k : asArray()) {
                sb.append(k);
            }
            return sb.toString();
        }

    }

    public TrieMap(K special) {
        this.special = special;
    }

    private final TrieMapNode<K, V> root = new TrieMapNode<>(null);
    private final HashMap<V, HashSet<TrieMapNode<K, V>>> backwardHashMap = new HashMap<>();
    private final K special;

    public int size() {
        return root.numDescendants();
    }

    public void add(Iterable<K> sequence, V value) {

        int index = 0;
        Iterator<K> it = sequence.iterator();
        TrieMapNode<K, V> currentNode = root;
        Iterator<K> currentNodeIterator = currentNode.nodes.iterator();
        K currentValue = special;
        if (it.hasNext()) {
            currentValue = it.next();
            if (currentValue.equals(special)) {
                throw new IllegalArgumentException("Special cannot be a sequence character!");
            }
        }
        boolean created = false;
        while (true) {
            if (currentNodeIterator.hasNext()) {
                if (Objects.equals(currentValue, currentNodeIterator.next())) {
                    index += 1;
                } else {
                    created = true;
                    currentNode.splinter(index);
                    TrieMapNode<K, V> addition = new TrieMapNode<>(currentNode.parent);
                    currentNode = currentNode.parent;
                    currentNode.children.put(currentValue, addition);
                    currentNode = currentNode.children.get(currentValue);
                    currentNode.nodes.addLast(currentValue);
                    currentNodeIterator = currentNode.nodes.iterator();
                    currentNodeIterator.next();
                    index = 1;
                }
            } else {
                if (currentNode.children.isEmpty() && currentNode.parent != null) {
                    if (!created) {
                        throw new IllegalStateException("not sure what's going on");
                    }
                    currentNode.nodes.addLast(currentValue);
                    index += 1;
                } else {
                    if (!currentNode.children.containsKey(currentValue)) {
                        created = true;
                        TrieMapNode<K, V> addition = new TrieMapNode<>(currentNode);
                        currentNode.children.put(currentValue, addition);
                        index = 1;
                        currentNode = currentNode.children.get(currentValue);
                        currentNode.nodes.addLast(currentValue);
                        currentNodeIterator = currentNode.nodes.iterator();
                        currentNodeIterator.next();
                    } else {
                        currentNode = currentNode.children.get(currentValue);
                        currentNodeIterator = currentNode.nodes.iterator();
                        index = 1;
                        currentNodeIterator.next();
                    }
                }
            }
            if (it.hasNext()) {
                currentValue = it.next();
                if (currentValue.equals(special)) {
                    throw new IllegalArgumentException("Special cannot be a sequence character!");
                }
            } else if (Objects.equals(currentValue, special)) {
                break;
            } else {
                currentValue = special;
            }
        }

        if (created) {
            TrieMapNode<K, V> addedNode = currentNode;

            while (currentNode != null) {
                currentNode.descendants.add(addedNode);
                currentNode = currentNode.parent;
            }

            currentNode = addedNode;

        } else {
            backwardHashMap.get(currentNode.value).remove(currentNode);
            if (backwardHashMap.get(currentNode.value).isEmpty()) {
                backwardHashMap.remove(currentNode.value);
            }
        }
        currentNode.value = value;

        if (!backwardHashMap.containsKey(value)) {
            backwardHashMap.put(value, new HashSet<>());
        }
        backwardHashMap.get(value).add(currentNode);
    }

    private TrieMapNode<K, V> removeNode(TrieMapNode<K, V> nodeToRemove) {
        TrieMapNode<K, V> currentNode = nodeToRemove;

        backwardHashMap.get(nodeToRemove.value).remove(nodeToRemove);
        if (backwardHashMap.get(nodeToRemove.value).isEmpty()) {
            backwardHashMap.remove(nodeToRemove.value);
        }

        while (currentNode != null) {
            currentNode.descendants.remove(nodeToRemove);
            currentNode = currentNode.parent;
        }

        currentNode = nodeToRemove.parent;
        currentNode.children.remove(nodeToRemove.nodes.getFirst());
        if (currentNode.parent != null && currentNode.children.size() == 1) {
            currentNode.children.entrySet().iterator().next().getValue().mergeWithParent(); // lmao
        }

        return nodeToRemove;
    }

    public V removeSequence(Iterable<K> sequence) {
        Iterator<K> it = sequence.iterator();
        TrieMapNode<K, V> currentNode = root;
        Iterator<K> currentNodeIterator = currentNode.nodes.iterator();
        K currentValue = it.hasNext() ? it.next() : special;
        while (true) {
            if (currentNodeIterator.hasNext()) {
                if (!Objects.equals(currentValue, currentNodeIterator.next())) {
                    throw new IllegalArgumentException("sequence not in trie");
                }
            } else {
                if (currentNode.children.isEmpty() && currentNode.parent != null) {
                    throw new IllegalArgumentException("sequence not in trie");
                } else {
                    if (!currentNode.children.containsKey(currentValue)) {
                        throw new IllegalArgumentException("sequence not in trie");
                    } else {
                        currentNode = currentNode.children.get(currentValue);
                        currentNodeIterator = currentNode.nodes.iterator();
                        currentNodeIterator.next();
                    }
                }
                // bazinga we at the end
            }
            if (it.hasNext()) {
                currentValue = it.next();
            } else if (Objects.equals(currentValue, special)) {
                break;
            } else {
                currentValue = special;
            }
        }

        return removeNode(currentNode).value;
    }

    public boolean containsSequence(Iterable<K> sequence) {
        Iterator<K> it = sequence.iterator();
        TrieMapNode<K, V> currentNode = root;
        Iterator<K> currentNodeIterator = currentNode.nodes.iterator();
        K currentValue = it.hasNext() ? it.next() : special;
        while (true) {
            if (currentNodeIterator.hasNext()) {
                if (!Objects.equals(currentValue, currentNodeIterator.next())) {
                    return false;
                }
            } else {
                if (currentNode.children.isEmpty() && currentNode.parent != null) {
                    return false; // shouldn't really happen but whatever
                } else {
                    if (!currentNode.children.containsKey(currentValue)) {
                        return false;
                    } else {
                        currentNode = currentNode.children.get(currentValue);
                        currentNodeIterator = currentNode.nodes.iterator();
                        currentNodeIterator.next();
                    }
                }
                // bazinga we at the end
            }
            if (it.hasNext()) {
                currentValue = it.next();
            } else if (Objects.equals(currentValue, special)) {
                return true;
            } else {
                currentValue = special;
            }
        }
    }

    public V valueOfSequence(Iterable<K> sequence) {
        Iterator<K> it = sequence.iterator();
        TrieMapNode<K, V> currentNode = root;
        Iterator<K> currentNodeIterator = currentNode.nodes.iterator();
        K currentValue = it.hasNext() ? it.next() : special;
        while (true) {
            if (currentNodeIterator.hasNext()) {
                if (!Objects.equals(currentValue, currentNodeIterator.next())) {
                    throw new IllegalArgumentException("sequence not in trie");
                }
            } else {
                if (currentNode.children.isEmpty() && currentNode.parent != null) {
                    throw new IllegalArgumentException("sequence not in trie");
                } else {
                    if (!currentNode.children.containsKey(currentValue)) {
                        throw new IllegalArgumentException("sequence not in trie");
                    } else {
                        currentNode = currentNode.children.get(currentValue);
                        currentNodeIterator = currentNode.nodes.iterator();
                        currentNodeIterator.next();
                    }
                }
                // bazinga we at the end
            }
            if (it.hasNext()) {
                currentValue = it.next();
            } else if (Objects.equals(currentValue, special)) {
                return currentNode.value;
            } else {
                currentValue = special;
            }
        }
    }

    public boolean containsValue(V value) {
        return backwardHashMap.containsKey(value);
    }

    public ArrayDeque<K> longestContainedPrefix(Iterable<K> sequence) {
        Iterator<K> it = sequence.iterator();
        TrieMapNode<K, V> currentNode = root;
        Iterator<K> currentNodeIterator = currentNode.nodes.iterator();
        ArrayDeque<K> lcp = new ArrayDeque<>();
        K currentValue = it.hasNext() ? it.next() : special;
        while (true) {
            if (currentNodeIterator.hasNext()) {
                if (Objects.equals(currentValue, currentNodeIterator.next())) {
                    lcp.addLast(currentValue);
                } else {
                    return lcp;
                }
            } else {
                if (currentNode.children.isEmpty() && currentNode.parent != null) {
                    return lcp;
                } else {
                    if (!currentNode.children.containsKey(currentValue)) {
                        return lcp;
                    } else {
                        currentNode = currentNode.children.get(currentValue);
                        currentNodeIterator = currentNode.nodes.iterator();
                        lcp.addLast(currentValue);
                        currentNodeIterator.next();
                    }
                }
                // bazinga we at the end
            }
            if (it.hasNext()) {
                currentValue = it.next();
            } else {
                return lcp;
            }
        }
    }

    public int countPrefixes(Iterable<K> sequence) {
        Iterator<K> it = sequence.iterator();
        TrieMapNode<K, V> currentNode = root;
        Iterator<K> currentNodeIterator = currentNode.nodes.iterator();
        if (!it.hasNext()) {
            return root.numDescendants();
        }
        K currentValue = it.next();
        while (true) {
            if (currentNodeIterator.hasNext()) {
                if (!Objects.equals(currentValue, currentNodeIterator.next())) {
                    return 0;
                }
            } else {
                if (currentNode.children.isEmpty() && currentNode.parent != null) {
                    return 0;
                } else {
                    if (!currentNode.children.containsKey(currentValue)) {
                        return 0;
                    } else {
                        currentNode = currentNode.children.get(currentValue);
                        currentNodeIterator = currentNode.nodes.iterator();
                        currentNodeIterator.next();
                    }
                }
                // bazinga we at the end
            }
            if (it.hasNext()) {
                currentValue = it.next();
            } else {
                return currentNode.descendants.size();
            }
        }
    }

    public ArrayList<ArrayDeque<K>> allSequencesWithPrefix(Iterable<K> sequence) {
        Iterator<K> it = sequence.iterator();
        TrieMapNode<K, V> currentNode = root;
        Iterator<K> currentNodeIterator = currentNode.nodes.iterator();
        if (!it.hasNext()) {
            ArrayList<ArrayDeque<K>> swp = new ArrayList<>();
            for (TrieMapNode<K, V> descendant : currentNode.descendants) {
                swp.add(descendant.asArray());
            }
            return swp;
        }
        K currentValue = it.next();
        while (true) {
            if (currentNodeIterator.hasNext()) {
                if (!Objects.equals(currentValue, currentNodeIterator.next())) {
                    return new ArrayList<>();
                }
            } else {
                if (currentNode.children.isEmpty() && currentNode.parent != null) {
                    return new ArrayList<>();
                } else {
                    if (!currentNode.children.containsKey(currentValue)) {
                        return new ArrayList<>();
                    } else {
                        currentNode = currentNode.children.get(currentValue);
                        currentNodeIterator = currentNode.nodes.iterator();
                        currentNodeIterator.next();
                    }
                }
                // bazinga we at the end
            }
            if (it.hasNext()) {
                currentValue = it.next();
            } else {
                ArrayList<ArrayDeque<K>> swp = new ArrayList<>();
                for (TrieMapNode<K, V> descendant : currentNode.descendants) {
                    swp.add(descendant.asArray());
                }
                return swp;
            }
        }
    }

    public ArrayList<V> allValuesWithPrefix(Iterable<K> sequence) {
        Iterator<K> it = sequence.iterator();
        TrieMapNode<K, V> currentNode = root;
        Iterator<K> currentNodeIterator = currentNode.nodes.iterator();
        if (!it.hasNext()) {
            ArrayList<V> swp = new ArrayList<>();
            for (TrieMapNode<K, V> descendant : currentNode.descendants) {
                swp.add(descendant.value);
            }
            return swp;
        }
        K currentValue = it.next();
        while (true) {
            if (currentNodeIterator.hasNext()) {
                if (!Objects.equals(currentValue, currentNodeIterator.next())) {
                    return new ArrayList<>();
                }
            } else {
                if (currentNode.children.isEmpty() && currentNode.parent != null) {
                    return new ArrayList<>();
                } else {
                    if (!currentNode.children.containsKey(currentValue)) {
                        return new ArrayList<>();
                    } else {
                        currentNode = currentNode.children.get(currentValue);
                        currentNodeIterator = currentNode.nodes.iterator();
                        currentNodeIterator.next();
                    }
                }
                // bazinga we at the end
            }
            if (it.hasNext()) {
                currentValue = it.next();
            } else {
                ArrayList<V> swp = new ArrayList<>();
                for (TrieMapNode<K, V> descendant : currentNode.descendants) {
                    swp.add(descendant.value);
                }
                return swp;
            }
        }
    }

    /** special can just count as a wildcard */
    public ArrayList<ArrayDeque<K>> allCloseSequences(Iterable<K> sequence, int maxDifferences) {
        ArrayList<K> array = new ArrayList<>();
        for (K k : sequence) {
            array.add(k); // lol
        }
        if (!array.get(array.size() - 1).equals(special)) {
            array.add(special); // lol I mean that works :D
        }

        ArrayDeque<TrieMapNode<K, V>> nodeQueue = new ArrayDeque<>();
        ArrayDeque<Integer> otherQueue = new ArrayDeque<>();
        ArrayList<ArrayDeque<K>> closeSequences = new ArrayList<>();
        nodeQueue.add(root);
        otherQueue.addLast(0);
        otherQueue.addLast(maxDifferences);

        while (!nodeQueue.isEmpty()) {
            TrieMapNode<K, V> currentNode = nodeQueue.removeFirst();
            int sequenceIndex = otherQueue.removeFirst();
            int differencesLeft = otherQueue.removeFirst();
            boolean works = true;
            for (K element : currentNode.nodes) {
                if (sequenceIndex >= array.size()) {
                    works = false;
                    break; // can't work </3
                }
                if (!array.get(sequenceIndex).equals(element)) {
                    differencesLeft -= 1;
                    if (differencesLeft < 0) {
                        works = false;
                        break;
                    }
                }
                sequenceIndex += 1;
            }
            if (works) {
                if (sequenceIndex == array.size() && currentNode.children.isEmpty()) {
                    ArrayDeque<K> seq = currentNode.asArray();
                    seq.removeLast();
                    closeSequences.add(seq);
                } else {
                    for (TrieMapNode<K, V> child : currentNode.children.values()) {
                        nodeQueue.addLast(child);
                        otherQueue.addLast(sequenceIndex); // current index in sequence
                        otherQueue.addLast(differencesLeft); // differences remaining
                    }
                }
            }
        }
        return closeSequences;
    }

    /** special can just count as a wildcard */
    public ArrayList<V> allCloseValues(Iterable<K> sequence, int maxDifferences) {
        ArrayList<K> array = new ArrayList<>();
        for (K k : sequence) {
            array.add(k); // lol
        }
        if (!array.get(array.size() - 1).equals(special)) {
            array.add(special); // lol I mean that works :D
        }

        ArrayDeque<TrieMapNode<K, V>> nodeQueue = new ArrayDeque<>();
        ArrayDeque<Integer> otherQueue = new ArrayDeque<>();
        ArrayList<V> closeValues = new ArrayList<>();
        nodeQueue.add(root);
        otherQueue.addLast(0);
        otherQueue.addLast(maxDifferences);

        while (!nodeQueue.isEmpty()) {
            TrieMapNode<K, V> currentNode = nodeQueue.removeFirst();
            int sequenceIndex = otherQueue.removeFirst();
            int differencesLeft = otherQueue.removeFirst();
            boolean works = true;
            for (K element : currentNode.nodes) {
                if (sequenceIndex >= array.size()) {
                    works = false;
                    break; // can't work </3
                }
                if (!array.get(sequenceIndex).equals(element)) {
                    differencesLeft -= 1;
                    if (differencesLeft < 0) {
                        works = false;
                        break;
                    }
                }
                sequenceIndex += 1;
            }
            if (works) {
                if (sequenceIndex == array.size() && currentNode.children.isEmpty()) {
                    closeValues.add(currentNode.value);
                } else {
                    for (TrieMapNode<K, V> child : currentNode.children.values()) {
                        nodeQueue.addLast(child);
                        otherQueue.addLast(sequenceIndex); // current index in sequence
                        otherQueue.addLast(differencesLeft); // differences remaining
                    }
                }
            }
        }
        return closeValues;
    }

    /** special can just count as a wildcard */
    public ArrayList<ArrayDeque<K>> allCloseSequencesWithPrefix(Iterable<K> sequence, int maxDifferences) {
        ArrayList<K> array = new ArrayList<>();
        for (K k : sequence) {
            array.add(k); // lol
        }

        ArrayDeque<TrieMapNode<K, V>> nodeQueue = new ArrayDeque<>();
        ArrayDeque<Integer> otherQueue = new ArrayDeque<>();
        ArrayList<ArrayDeque<K>> closeSequences = new ArrayList<>();
        nodeQueue.add(root);
        otherQueue.addLast(0);
        otherQueue.addLast(maxDifferences);

        while (!nodeQueue.isEmpty()) {
            TrieMapNode<K, V> currentNode = nodeQueue.removeFirst();
            int sequenceIndex = otherQueue.removeFirst();
            int differencesLeft = otherQueue.removeFirst();
            boolean works = true;
            for (K element : currentNode.nodes) {
                if (sequenceIndex >= array.size()) {
                    for (TrieMapNode<K, V> node : currentNode.descendants) {
                        ArrayDeque<K> seq = node.asArray();
                        seq.removeLast();
                        closeSequences.add(seq);
                    }
                    works = false;
                    break;
                }
                if (!array.get(sequenceIndex).equals(element)) {
                    differencesLeft -= 1;
                    if (differencesLeft < 0) {
                        works = false;
                        break;
                    }
                }
                sequenceIndex += 1;
            }
            if (works) {
                if (sequenceIndex == array.size()) {
                    for (TrieMapNode<K, V> node : currentNode.descendants) {
                        ArrayDeque<K> seq = node.asArray();
                        seq.removeLast();
                        closeSequences.add(seq);
                    }
                } else {
                    for (TrieMapNode<K, V> child : currentNode.children.values()) {
                        nodeQueue.addLast(child);
                        otherQueue.addLast(sequenceIndex); // current index in sequence
                        otherQueue.addLast(differencesLeft); // differences remaining
                    }
                }
            }
        }
        return closeSequences;
    }

    /** special can just count as a wildcard */
    public ArrayList<V> allCloseValuesWithPrefix(Iterable<K> sequence, int maxDifferences) {
        ArrayList<K> array = new ArrayList<>();
        for (K k : sequence) {
            array.add(k); // lol
        }

        ArrayDeque<TrieMapNode<K, V>> nodeQueue = new ArrayDeque<>();
        ArrayDeque<Integer> otherQueue = new ArrayDeque<>();
        ArrayList<V> closeValues = new ArrayList<>();
        nodeQueue.add(root);
        otherQueue.addLast(0);
        otherQueue.addLast(maxDifferences);

        while (!nodeQueue.isEmpty()) {
            TrieMapNode<K, V> currentNode = nodeQueue.removeFirst();
            int sequenceIndex = otherQueue.removeFirst();
            int differencesLeft = otherQueue.removeFirst();
            boolean works = true;
            for (K element : currentNode.nodes) {
                if (sequenceIndex >= array.size()) {
                    for (TrieMapNode<K, V> node : currentNode.descendants) {
                        closeValues.add(node.value);
                    }
                    works = false;
                    break;
                }
                if (!array.get(sequenceIndex).equals(element)) {
                    differencesLeft -= 1;
                    if (differencesLeft < 0) {
                        works = false;
                        break;
                    }
                }
                sequenceIndex += 1;
            }
            if (works) {
                if (sequenceIndex == array.size()) {
                    for (TrieMapNode<K, V> node : currentNode.descendants) {
                        closeValues.add(node.value);
                    }
                } else {
                    for (TrieMapNode<K, V> child : currentNode.children.values()) {
                        nodeQueue.addLast(child);
                        otherQueue.addLast(sequenceIndex); // current index in sequence
                        otherQueue.addLast(differencesLeft); // differences remaining
                    }
                }
            }
        }
        return closeValues;
    }

    public void add(K[] sequence, V value) {
        add(asIterable(sequence), value);
    }

    public V removeSequence(K[] sequence) {
        return removeSequence(asIterable(sequence));
    }

    public boolean containsSequence(K[] sequence) {
        return containsSequence(asIterable(sequence));
    }

    public V valueOfSequence(K[] sequence) {
        return valueOfSequence(asIterable(sequence));
    }

    public ArrayDeque<K> longestContainedPrefix(K[] sequence) {
        return longestContainedPrefix(asIterable(sequence));
    }

    public int countPrefixes(K[] sequence) {
        return countPrefixes(asIterable(sequence));
    }

    public ArrayList<ArrayDeque<K>> allSequencesWithPrefix(K[] sequence) {
        return allSequencesWithPrefix(asIterable(sequence));
    }

    public ArrayList<V> allValuesWithPrefix(K[] sequence) {
        return allValuesWithPrefix(asIterable(sequence));
    }

    public ArrayList<ArrayDeque<K>> allCloseSequences(K[] sequence, int maxDifferences) {
        return allCloseSequences(asIterable(sequence), maxDifferences);
    }

    public ArrayList<V> allCloseValues(K[] sequence, int maxDifferences) {
        return allCloseValues(asIterable(sequence), maxDifferences);
    }

    public ArrayList<ArrayDeque<K>> allCloseSequencesWithPrefix(K[] sequence, int maxDifferences) {
        return allCloseSequencesWithPrefix(asIterable(sequence), maxDifferences);
    }

    public ArrayList<V> allCloseValuesWithPrefix(K[] sequence, int maxDifferences) {
        return allCloseValuesWithPrefix(asIterable(sequence), maxDifferences);
    }

    public ArrayList<ArrayDeque<K>> allSequencesOfValue(V value) {
        ArrayList<ArrayDeque<K>> asov = new ArrayList<>();
        for (TrieMapNode<K, V> node : backwardHashMap.get(value)) {
            asov.add(node.asArray());
        }
        return asov;
    }

    public void clear() {
        root.children.clear();
        root.nodes.clear(); // root.nodes should always be blank though
        root.descendants.clear();
        backwardHashMap.clear();
    }

    public static <K, V> TrieMap<K, V> buildSuffixTrieMap(K[] entry, K special) { // values --> starting index
        return null;
    }

    private static <K, V> int bruteForceDescendants(TrieMapNode<K, V> x, HashMap<V, HashSet<TrieMapNode<K, V>>> map) {

        if (x.parent != null) {
            if (!x.parent.children.get(x.nodes.getFirst()).equals(x)) {
                throw new IllegalStateException("Parent-child relationship messed up");
            }
            if (x.nodes.getLast().equals('$') != x.children.isEmpty()) {
                throw new IllegalStateException("End should never have children, and non-ends should always have children");
            }
            if (x.children.isEmpty()) {
                TrieMapNode<K, V> ancestor = x;
                while (ancestor != null) {
                    if (!ancestor.descendants.contains(x)) {
                        throw new IllegalStateException("Array messed up");
                    }
                    ancestor = ancestor.parent;
                }
                if (!map.containsKey(x.value)) {
                    throw new IllegalStateException("Map messed up");
                }
            } else {
                if (x.children.size() <= 1) {
                    throw new IllegalStateException("Only leaves can have zero children");
                }
            }
        } else {
            if (x.descendants.isEmpty() != x.children.isEmpty()) {
                throw new IllegalStateException("Root can't have negative descendants");
            }
        }

        if (x.children.isEmpty()) {
            return 1;
        }
        int ret = 0;
        for (TrieMapNode<K, V> child : x.children.values()) {
            ret += bruteForceDescendants(child, map);
        }
        if (ret != x.numDescendants()) {
            throw new IllegalStateException("Descendant size mismatch");
        }
        return ret;
    }

    private void assertDataMakesSense() {
        bruteForceDescendants(root, backwardHashMap);
        for (V value : backwardHashMap.keySet()) {
            for (TrieMapNode<K, V> node : backwardHashMap.get(value)) {
                if (!valueOfSequence(node.asArray()).equals(value)) {
                    throw new IllegalStateException("map not working");
                }
                if (!node.children.isEmpty()) {
                    throw new IllegalStateException("map shouldn't map to non-leaf nodes");
                }
            }
        }
    }

    public static final double ADD_PROBABILITY = 0.5;
    public static final int NUM_TESTS = 100;
    public static final int NUM_ITERATIONS = 10000;

    public static final String[] dictionary = {
            "", "A", "B", "C", "AA", "AB", "AC", "BA", "BB", "BC", "CA", "CB", "CC", "AAA", "AAB", "AAC",
            "ABA", "ABB", "ABC", "ACA", "ACB", "ACC", "BAA", "BAB", "BAC", "BBA", "BBB", "BBC", "BCA", "BCB",
            "BCC", "CAA", "CAB", "CAC", "CBA", "CBB", "CBC", "CCA", "CCB", "CCC"
    };

    public static void testAddRemoveSame() {
        TrieMap<Character, Integer> TrieMap = new TrieMap<>('$');

        for (int k = 0; k < NUM_TESTS; k += 1) {
            TrieMap.clear();

            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                int index = Randomness.getRandomInteger(0, dictionary.length);
                Iterable<Character> subject = IterableUtility.asIterable(dictionary[index]);
                if (Randomness.getDouble() < ADD_PROBABILITY) {
                    TrieMap.add(subject, 0);
                } else {
                    if (!TrieMap.containsSequence(subject)) {
                        j -= 1;
                    } else {
                        TrieMap.removeSequence(subject);
                    }
                }
                TrieMap.assertDataMakesSense();
            }
            for (int j = TrieMap.size(); j > 0; j -= 1) { // I question if this is the best way to do this but we ball lmao
                ArrayList<ArrayDeque<Character>> allDescendants = TrieMap.allSequencesWithPrefix(IterableUtility.asIterable(""));
                Iterable<Character> subject = allDescendants.get(Randomness.getRandomInteger(0, allDescendants.size()));
                if (!TrieMap.containsSequence(subject)) {
                    throw new IllegalStateException("Test trie should contain this");
                } else {
                    TrieMap.removeSequence(subject);
                }
                TrieMap.assertDataMakesSense();
            }

            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                Iterable<Character> subject = IterableUtility.asIterable(dictionary[Randomness.getRandomInteger(0, dictionary.length)]);
                if (Randomness.getDouble() < ADD_PROBABILITY) {
                    TrieMap.add(subject, 0);
                } else {
                    if (!TrieMap.containsSequence(subject)) {
                        j -= 1;
                    } else {
                        TrieMap.removeSequence(subject);
                    }
                }
                TrieMap.assertDataMakesSense();
            }
        }
    }

    public static void testAddRemoveDifferent() {
        TrieMap<Character, Integer> TrieMap = new TrieMap<>('$');

        for (int k = 0; k < NUM_TESTS; k += 1) {
            TrieMap.clear();

            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                int index = Randomness.getRandomInteger(0, dictionary.length);
                Iterable<Character> subject = IterableUtility.asIterable(dictionary[index]);
                if (Randomness.getDouble() < ADD_PROBABILITY) {
                    TrieMap.add(subject, Randomness.getRandomInteger(-1024, 1024));
                } else {
                    if (!TrieMap.containsSequence(subject)) {
                        j -= 1;
                    } else {
                        TrieMap.removeSequence(subject);
                    }
                }
                TrieMap.assertDataMakesSense();
            }
            for (int j = TrieMap.size(); j > 0; j -= 1) { // I question if this is the best way to do this but we ball lmao
                ArrayList<ArrayDeque<Character>> allDescendants = TrieMap.allSequencesWithPrefix(IterableUtility.asIterable(""));
                Iterable<Character> subject = allDescendants.get(Randomness.getRandomInteger(0, allDescendants.size()));
                if (!TrieMap.containsSequence(subject)) {
                    throw new IllegalStateException("Test trie should contain this");
                } else {
                    TrieMap.removeSequence(subject);
                }
                TrieMap.assertDataMakesSense();
            }

            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                Iterable<Character> subject = IterableUtility.asIterable(dictionary[Randomness.getRandomInteger(0, dictionary.length)]);
                if (Randomness.getDouble() < ADD_PROBABILITY) {
                    TrieMap.add(subject, Randomness.getRandomInteger(-1024, 1024));
                } else {
                    if (!TrieMap.containsSequence(subject)) {
                        j -= 1;
                    } else {
                        TrieMap.removeSequence(subject);
                    }
                }
                TrieMap.assertDataMakesSense();
            }
        }
    }

    public String visualization() {
        return VisualizeTree.stringOfTree(
                root,
                x -> x.children.values(),
                x -> x == root ? "TrieMap" : x.visualization(),
                2
        ) + '\n' + backwardHashMap;
    }

    public static void main(String[] args) {
        testAddRemoveSame();
        testAddRemoveDifferent();
        System.out.println("TrieMap passed all tests :)");

        // example mutation-checking problem
        String target = "ACGTACGTAC";
        String[] sequences = {
                "ACGTACGTAC",
                "ACGTACGTTC",
                "ACGTACCTAC",
                "ACGTACGGAC",
                "ACGTACGTAG",
                "TTTTTTTTTT",
                "ACGTTCGAGG",
                "GCGGACGTTC",
                "TCGAACGTGC",
                "CCGTGCGTCC",
                "ACCTGCGTGC",
                "AGCTACGGGC",
                "CGGTACGTTA",
                "ACGTGCGGTT",
                "ACGAGCGTGG",
                "AACCTCGTAT",
                "CCTTACGATC",
                "TGGTACCTCC",
                "ACGAAGGTAA",
                "ACCTACGGTT"
        };

        TrieMap<Character, Integer> trie = new TrieMap<>('$');

        for (String sequence : sequences) {
            int differences = 0;
            for (int i = 0; i < sequence.length(); i += 1) {
                if (target.charAt(i) != sequence.charAt(i)) {
                    differences += 1;
                }
            }
            trie.add(IterableUtility.asIterable(sequence), differences);
        }

        System.out.println(trie.visualization());

        System.out.println(trie.allCloseSequencesWithPrefix(asIterable("$CGT".toCharArray()), 1));
        System.out.println(trie.allCloseValuesWithPrefix(asIterable("$CGT".toCharArray()), 1));

        System.out.println("Passed mutation-checking test");

    }

}
