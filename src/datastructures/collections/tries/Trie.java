package datastructures.collections.tries;

import datastructures.collections.priorityqueue.ArrayPriorityQueue;
import datastructures.utility.IterableUtility;
import datastructures.utility.Randomness;
import datastructures.utility.VisualizeTree;

import java.util.*;
import static datastructures.utility.IterableUtility.*;

/** not just strings! */
public class Trie<E> {

    private static class TrieNode<E> {

        private final ArrayDeque<E> nodes = new ArrayDeque<>();
        private final HashMap<E, TrieNode<E>> children = new HashMap<>();
        private final ArrayPriorityQueue<Integer, TrieNode<E>> descendants = new ArrayPriorityQueue<>(Comparator.comparingInt(x -> -x));
        int numDescendants = 0;
        private TrieNode<E> parent;

        private TrieNode(TrieNode<E> parent) {
            this.parent = parent;
        }

        private void splinter(int i) {
            TrieNode<E> newFather = new TrieNode<>(parent);
            parent.children.replace(nodes.getFirst(), newFather);
            for (int j = 0; j < i; j += 1) {
                newFather.nodes.addLast(nodes.removeFirst());
            }
            newFather.children.put(nodes.getFirst(), this);
            newFather.numDescendants = numDescendants;
            for (TrieNode<E> node : descendants.values()) {
                newFather.descendants.add(descendants.keyOfValue(node), node);
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
            return numDescendants;
        }

        public ArrayDeque<E> asArray() {
            ArrayDeque<E> fullArray = new ArrayDeque<>();
            TrieNode<E> currentNode = this;
            while (currentNode != null) {
                Iterator<E> it = currentNode.nodes.descendingIterator();
                while (it.hasNext()) {
                    fullArray.addFirst(it.next());
                }
                currentNode = currentNode.parent;
            }
            return fullArray;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (E e : nodes) {
                sb.append(e);
            }
            if (children.isEmpty()) {
                sb.append(" : ").append(numDescendants());
            }
            return sb.toString();
        }

    }

    public Trie(E special) {
        this.special = special;
    }

    private final TrieNode<E> root = new TrieNode<>(null);
    private final E special;

    public int size() {
        return root.numDescendants();
    }

    public void add(Iterable<E> sequence) {
        int index = 0;
        Iterator<E> it = sequence.iterator();
        TrieNode<E> currentNode = root;
        Iterator<E> currentNodeIterator = currentNode.nodes.iterator();
        E currentValue = special;
        if (it.hasNext()) {
            currentValue = it.next();
            if (currentValue.equals(special)) {
                throw new IllegalArgumentException("Special cannot be a sequence character!");
            }
        }
        while (true) {
            if (currentNodeIterator.hasNext()) {
                if (Objects.equals(currentValue, currentNodeIterator.next())) {
                    index += 1;
                } else {
                    currentNode.splinter(index);
                    TrieNode<E> addition = new TrieNode<>(currentNode.parent);
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
                    currentNode.nodes.addLast(currentValue);
                    index += 1;
                } else {
                    if (!currentNode.children.containsKey(currentValue)) {
                        TrieNode<E> addition = new TrieNode<>(currentNode);
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
        TrieNode<E> addedNode = currentNode;
        while (currentNode != null) {
            currentNode.numDescendants += 1;
            if (currentNode.descendants.containsValue(addedNode)) {
                currentNode.descendants.changeKey(addedNode, addedNode.numDescendants);
            } else {
                currentNode.descendants.add(1, addedNode);
            }
            currentNode = currentNode.parent;
        }
    }

    public void remove(Iterable<E> sequence) {
        Iterator<E> it = sequence.iterator();
        TrieNode<E> currentNode = root;
        Iterator<E> currentNodeIterator = currentNode.nodes.iterator();
        E currentValue = it.hasNext() ? it.next() : special;
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

        TrieNode<E> removedNode = currentNode;
        while (currentNode != null) {
            currentNode.numDescendants -= 1;
            if (removedNode.numDescendants == 0) {
                currentNode.descendants.changeKey(removedNode, Integer.MAX_VALUE);
                currentNode.descendants.removeFirst(); // lol
            } else {
                currentNode.descendants.changeKey(removedNode, removedNode.numDescendants);
            }
            currentNode = currentNode.parent;
        }

        if (removedNode.numDescendants == 0) {
            currentNode = removedNode.parent;
            currentNode.children.remove(removedNode.nodes.getFirst());
            if (currentNode.parent != null && currentNode.children.size() == 1) {
                currentNode.children.entrySet().iterator().next().getValue().mergeWithParent(); // lmao
            }
        }
    }

    public boolean contains(Iterable<E> sequence) {
        Iterator<E> it = sequence.iterator();
        TrieNode<E> currentNode = root;
        Iterator<E> currentNodeIterator = currentNode.nodes.iterator();
        E currentValue = it.hasNext() ? it.next() : special;
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

    public ArrayDeque<E> longestContainedPrefix(Iterable<E> sequence) {
        Iterator<E> it = sequence.iterator();
        TrieNode<E> currentNode = root;
        Iterator<E> currentNodeIterator = currentNode.nodes.iterator();
        ArrayDeque<E> lcp = new ArrayDeque<>();
        E currentValue = it.hasNext() ? it.next() : special;
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

    public int countPrefixes(Iterable<E> sequence) {
        Iterator<E> it = sequence.iterator();
        TrieNode<E> currentNode = root;
        Iterator<E> currentNodeIterator = currentNode.nodes.iterator();
        if (!it.hasNext()) {
            return root.numDescendants();
        }
        E currentValue = it.next();
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

    public ArrayList<ArrayDeque<E>> allSequencesWithPrefix(Iterable<E> sequence) {
        Iterator<E> it = sequence.iterator();
        TrieNode<E> currentNode = root;
        Iterator<E> currentNodeIterator = currentNode.nodes.iterator();
        if (!it.hasNext()) {
            ArrayList<ArrayDeque<E>> swp = new ArrayList<>();
            for (TrieNode<E> descendant : currentNode.descendants.values()) {
                swp.add(descendant.asArray());
            }
            return swp;
        }
        E currentValue = it.next();
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
                ArrayList<ArrayDeque<E>> swp = new ArrayList<>();
                for (TrieNode<E> descendant : currentNode.descendants.values()) {
                    swp.add(descendant.asArray());
                }
                return swp;
            }
        }
    }

    /** special can just count as a wildcard */
    public ArrayList<ArrayDeque<E>> allCloseSequences(Iterable<E> sequence, int maxDifferences) {
        ArrayList<E> array = new ArrayList<>();
        for (E e : sequence) {
            array.add(e); // lol
        }
        if (!array.get(array.size() - 1).equals(special)) {
            array.add(special); // lol I mean that works :D
        }

        ArrayDeque<TrieNode<E>> nodeQueue = new ArrayDeque<>();
        ArrayDeque<Integer> otherQueue = new ArrayDeque<>();
        ArrayList<ArrayDeque<E>> closeSequences = new ArrayList<>();
        nodeQueue.add(root);
        otherQueue.addLast(0);
        otherQueue.addLast(maxDifferences);

        while (!nodeQueue.isEmpty()) {
            TrieNode<E> currentNode = nodeQueue.removeFirst();
            int sequenceIndex = otherQueue.removeFirst();
            int differencesLeft = otherQueue.removeFirst();
            boolean works = true;
            for (E element : currentNode.nodes) {
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
                    ArrayDeque<E> seq = currentNode.asArray();
                    seq.removeLast();
                    closeSequences.add(seq);
                } else {
                    for (TrieNode<E> child : currentNode.children.values()) {
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
    public ArrayList<ArrayDeque<E>> allCloseSequencesWithPrefix(Iterable<E> sequence, int maxDifferences) {
        ArrayList<E> array = new ArrayList<>();
        for (E e : sequence) {
            array.add(e); // lol
        }

        ArrayDeque<TrieNode<E>> nodeQueue = new ArrayDeque<>();
        ArrayDeque<Integer> otherQueue = new ArrayDeque<>();
        ArrayList<ArrayDeque<E>> closeSequences = new ArrayList<>();
        nodeQueue.add(root);
        otherQueue.addLast(0);
        otherQueue.addLast(maxDifferences);

        while (!nodeQueue.isEmpty()) {
            TrieNode<E> currentNode = nodeQueue.removeFirst();
            int sequenceIndex = otherQueue.removeFirst();
            int differencesLeft = otherQueue.removeFirst();
            boolean works = true;
            for (E element : currentNode.nodes) {
                if (sequenceIndex >= array.size()) {
                    for (TrieNode<E> node : currentNode.descendants.values()) {
                        ArrayDeque<E> seq = node.asArray();
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
                    for (TrieNode<E> node : currentNode.descendants.values()) {
                        ArrayDeque<E> seq = node.asArray();
                        seq.removeLast();
                        closeSequences.add(seq);
                    }
                } else {
                    for (TrieNode<E> child : currentNode.children.values()) {
                        nodeQueue.addLast(child);
                        otherQueue.addLast(sequenceIndex); // current index in sequence
                        otherQueue.addLast(differencesLeft); // differences remaining
                    }
                }
            }
        }
        return closeSequences;
    }

    // TODO: support for wildcards
    // TODO: double Trie (for prefix-suffix matching)
    // TODO: Trie Map

    public void add(E[] sequence) {
        add(asIterable(sequence));
    }

    public void remove(E[] sequence) {
        remove(asIterable(sequence));
    }

    public boolean contains(E[] sequence) {
        return contains(asIterable(sequence));
    }

    public ArrayDeque<E> longestContainedPrefix(E[] sequence) {
        return longestContainedPrefix(asIterable(sequence));
    }

    public int countPrefixes(E[] sequence) {
        return countPrefixes(asIterable(sequence));
    }

    public ArrayList<ArrayDeque<E>> allSequencesWithPrefix(E[] sequence) {
        return allSequencesWithPrefix(asIterable(sequence));
    }

    public ArrayList<ArrayDeque<E>> allCloseSequences(E[] sequence, int maxDifferences) {
        return allCloseSequences(asIterable(sequence), maxDifferences);
    }

    public ArrayList<ArrayDeque<E>> allCloseSequencesWithPrefix(E[] sequence, int maxDifferences) {
        return allCloseSequencesWithPrefix(asIterable(sequence), maxDifferences);
    }

    public void clear() {
        root.children.clear();
        root.nodes.clear(); // root.nodes should always be blank though
        root.descendants.clear();
        root.numDescendants = 0;
    }

    public static <E> Trie<E> buildSuffixTrie(E[] entry, E special) { // Ukonnen's algorithm
        return null;
    }

    private static <E> int bruteForceDescendants(TrieNode<E> x) {

        if (x.parent != null) {
            if (!x.parent.children.get(x.nodes.getFirst()).equals(x)) {
                throw new IllegalStateException("Parent-child relationship messed up");
            }
            if (x.nodes.getLast().equals('$') != x.children.isEmpty()) {
                throw new IllegalStateException("End should never have children, and non-ends should always have children");
            }
            if (x.children.isEmpty()) {
                TrieNode<E> ancestor = x;
                while (ancestor != null) {
                    if (ancestor.descendants.keyOfValue(x) != x.numDescendants) {
                        throw new IllegalStateException("Array messed up");
                    }
                    ancestor = ancestor.parent;
                }
            } else {
                if (x.children.size() <= 1) {
                    throw new IllegalStateException("Only leaves can have zero children");
                }
            }
        } else {
            if (x.numDescendants < 0 || x.numDescendants == 0 && !x.children.isEmpty()) {
                throw new IllegalStateException("Root can't have negative descendants");
            }
        }

        if (x.children.isEmpty()) {
            return x.numDescendants;
        }
        int ret = 0;
        for (TrieNode<E> child : x.children.values()) {
            ret += bruteForceDescendants(child);
        }
        if (ret != x.numDescendants) {
            throw new IllegalStateException("Descendant size mismatch");
        }
        return ret;
    }

    private void assertDataMakesSense() {
        bruteForceDescendants(root);
    }

    public static final double ADD_PROBABILITY = 0.5;
    public static final int NUM_TESTS = 100;
    public static final int NUM_ITERATIONS = 10000;

    public static final String[] dictionary = {
            "", "A", "B", "C", "AA", "AB", "AC", "BA", "BB", "BC", "CA", "CB", "CC", "AAA", "AAB", "AAC",
            "ABA", "ABB", "ABC", "ACA", "ACB", "ACC", "BAA", "BAB", "BAC", "BBA", "BBB", "BBC", "BCA", "BCB",
            "BCC", "CAA", "CAB", "CAC", "CBA", "CBB", "CBC", "CCA", "CCB", "CCC"
    };

    public static void testAddRemove() {
        Trie<Character> testTrie = new Trie<>('$');

        for (int k = 0; k < NUM_TESTS; k += 1) {
            testTrie.clear();

            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                int index = Randomness.getRandomInteger(0, dictionary.length);
                Iterable<Character> subject = IterableUtility.asIterable(dictionary[index]);
                if (Randomness.getDouble() < ADD_PROBABILITY) {
                    testTrie.add(subject);
                } else {
                    if (!testTrie.contains(subject)) {
                        j -= 1;
                    } else {
                        testTrie.remove(subject);
                    }
                }
                testTrie.assertDataMakesSense();
            }
            for (int j = testTrie.size(); j > 0; j -= 1) { // I question if this is the best way to do this but we ball lmao
                ArrayList<ArrayDeque<Character>> allDescendants = testTrie.allSequencesWithPrefix(IterableUtility.asIterable(""));
                Iterable<Character> subject = allDescendants.get(Randomness.getRandomInteger(0, allDescendants.size()));
                if (!testTrie.contains(subject)) {
                    throw new IllegalStateException("Test trie should contain this");
                } else {
                    testTrie.remove(subject);
                }
                testTrie.assertDataMakesSense();
            }

            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                Iterable<Character> subject = IterableUtility.asIterable(dictionary[Randomness.getRandomInteger(0, dictionary.length)]);
                if (Randomness.getDouble() < ADD_PROBABILITY) {
                    testTrie.add(subject);
                } else {
                    if (!testTrie.contains(subject)) {
                        j -= 1;
                    } else {
                        testTrie.remove(subject);
                    }
                }
                testTrie.assertDataMakesSense();
            }
        }
    }

    public String visualization() {
        return VisualizeTree.stringOfTree(
                root,
                x -> x.children.values(),
                x -> x == root ? "Trie" : x.toString(),
                2
        );
    }

    public static void main(String[] args) {
        testAddRemove();
        System.out.println("Trie passed all tests :)");

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

        Trie<Character> trie = new Trie<>('$');

        for (String sequence : sequences) {
            trie.add(IterableUtility.asIterable(sequence));
        }

        ArrayList<ArrayDeque<Character>> closeSequences = trie.allCloseSequences(IterableUtility.asIterable(target), 3);
        int num = 0;
        for (String sequence : sequences) {
            int differences = 0;
            for (int i = 0; i < sequence.length(); i++) {
                if (sequence.charAt(i) != target.charAt(i)) {
                    differences += 1;
                }
            }
            if (differences <= 3) {
                num += 1;
            }
        }

        if (num != closeSequences.size()) {
            throw new IllegalStateException("Number of sequences mismatch");
        }

        for (ArrayDeque<Character> closeSequence : closeSequences) {
            StringBuilder sb = new StringBuilder();
            for (Character c : closeSequence) {
                sb.append(c);
            }
            String sequence = sb.toString();

            int differences = 0;
            for (int i = 0; i < sequence.length(); i++) {
                if (sequence.charAt(i) != target.charAt(i)) {
                    differences += 1;
                }
            }
            if (differences > 3) {
                throw new IllegalStateException("Incorrect close sequence identified");
            }
        }

        System.out.println(trie.allCloseSequencesWithPrefix(asIterable("$CGT".toCharArray()), 1));

        System.out.println("Passed mutation-checking test");

    }

}
