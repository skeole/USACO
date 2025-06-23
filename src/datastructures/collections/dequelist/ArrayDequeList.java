package datastructures.collections.dequelist;

import java.util.*;

import datastructures.utility.Randomness;

public class ArrayDequeList<E> extends DequeList<E> {
    
    public static final int MINIMUM_CAPACITY = 4;
    
    // backwardHashMap<E, TreeSet<Integer>>, but you need to cast each time

    E[] array = (E[]) new Object[MINIMUM_CAPACITY];

    public ArrayDequeList() {
        super(Integer.class);
        growSize = MINIMUM_CAPACITY;
        shrinkSize = -1;
    }

    public ArrayDequeList(Collection<? extends E> c) { // huh
        super(Integer.class);
        growSize = MINIMUM_CAPACITY;
        shrinkSize = -1;
        c.forEach(this::addLast);
    }

    private void place(int i, E e) {
        array[convert(i)] = e;
        if (!backwardHashMap.containsKey(e)) {
            backwardHashMap.put(e, new TreeSet<>());
        }
        TreeSet<Object> preIndices = backwardHashMap.get(e);
        preIndices.add(i + firstIndex);
    }

    private E delete(int i) {
        E e = array[convert(i)];
        array[convert(i)] = null;
        TreeSet<Object> preIndices = backwardHashMap.get(e);
        preIndices.remove(i + firstIndex); // why did adding an assertion cause it to fail...
        if (preIndices.isEmpty()) {
            backwardHashMap.remove(e);
        }
        return e;
    }
    
    @Override
    protected int convert(Object o) {
        Integer i = (Integer) o;
        if (i < 0 || i >= size()) {
            throw new IndexOutOfBoundsException();
        }
        return ((i + firstIndex) % array.length + array.length) % array.length;
    }

    @Override
    protected E getHelper(int i) {
        return array[convert(i)];
    }

    @Override
    protected E setHelper(int i, E e) {
        E ret = array[convert(i)];
        if (Objects.equals(ret, e)) {
            return ret;
        }
        delete(i);
        place(i, e);
        return ret;
    }

    @Override
    protected void grow() {
        E[] arr = (E[]) new Object[2 * array.length]; // sigh

        for (int i = 0; i < size(); i += 1) {
            arr[((i + firstIndex) % arr.length + arr.length) % arr.length] = array[convert(i)];
        }

        growSize = arr.length;
        shrinkSize = arr.length / 4;

        array = arr;
    }

    @Override
    protected void shrink() {
        if (array.length <= MINIMUM_CAPACITY) {
            return;
        }

        E[] arr = (E[]) new Object[array.length / 2]; // sigh

        for (int i = 0; i < size(); i += 1) {
            arr[convert(i) % arr.length] = array[convert(i)];
        }

        growSize = arr.length;
        shrinkSize = arr.length / 4;

        array = arr;
    }

    @Override
    protected boolean validToInsert(int i) {
        return i == 0 || i == size();
    }

    @Override
    protected boolean validToRemove(int i) {
        return i == 0 || i == size() - 1;
    }
    
    @Override
    protected boolean addHelper(boolean beginning, E e) {
        if (beginning) {
            firstIndex -= 1;
            place(0, e);
            return true;
        } else {
            lastIndex += 1;
            place(size() - 1, e);
            return true;
        }
    };

    @Override
    protected boolean insertHelper(int i, E e) {
        if (i == 0) {
            return addHelper(true, e);
        } else if (i == size()) {
            return addHelper(false, e);
        } else {
            throw new IllegalArgumentException("Can only add to front or back of ArrayDequeList");
        }
    };

    @Override
    protected E removeHelper(boolean beginning) {
        if (beginning) {
            E ret = delete(0);
            firstIndex += 1;
            return ret;
        } else {
            E ret = delete(size() - 1);
            lastIndex -= 1;
            return ret;
        }
    };

    @Override
    protected E removeHelper(int i) {
        if (i == 0) {
            return removeHelper(true);
        } else if (i == size() - 1) {
            return removeHelper(false);
        } else {
            throw new IllegalArgumentException("Can only add to front or back of ArrayDequeList");
        }
    }

    @Override
    protected ListIterator<E> iterator(boolean forward, int index) {
        return new ListIterator<E>() {
            int currentCursorIndex = index; // get(index) --> next, get(index - 1) --> previous
            
            @Override
            public boolean hasNext() {
                return forward ? currentCursorIndex < size() : currentCursorIndex > 0;
            }

            @Override
            public boolean hasPrevious() {
                return forward ? currentCursorIndex > 0 : currentCursorIndex < size();
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No next element");
                }
                if (forward) {
                    currentCursorIndex += 1;
                    return get(currentCursorIndex - 1);
                } else {
                    currentCursorIndex -= 1;
                    return get(currentCursorIndex);
                }
            }

            @Override
            public E previous() {
                if (!hasPrevious()) {
                    throw new NoSuchElementException("No previous element");
                }
                if (forward) {
                    currentCursorIndex -= 1;
                    return get(currentCursorIndex);
                } else {
                    currentCursorIndex += 1;
                    return get(currentCursorIndex - 1);
                }
            }

            @Override
            public int nextIndex() {
                return currentCursorIndex;
            }

            @Override
            public int previousIndex() {
                return currentCursorIndex - 1;
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
    protected void clearHelper() {
        array = (E[]) new Object[MINIMUM_CAPACITY];
        backwardHashMap.clear();
        growSize = MINIMUM_CAPACITY;
        shrinkSize = -1;
    }

    /** start inclusive, end exclusive */
    public static ArrayDequeList<Integer> range(int end) {
        return range(0, end, 1);
    }

    /** start inclusive, end exclusive */
    public static ArrayDequeList<Integer> range(int start, int end) {
        return range(start, end, end < start ? -1 : 1);
    }

    /** start inclusive, end exclusive */
    public static ArrayDequeList<Integer> range(int start, int end, int skip) {
        if (skip == 0) throw new IllegalArgumentException();
        int flip = skip < 0 ? -1 : 1;
        start *= flip;
        end *= flip;
        skip *= flip;

        ArrayDequeList<Integer> result = new ArrayDequeList<>();
        if (end <= start) return result;

        for (int i = start; i < end; i += skip) {
            result.add(i);
        }
        return result;
    }

    private static <E> void assertDataMakesSense(ArrayDequeList<E> deque) {
        int calculatedSize = 0;
        for (Map.Entry<E, TreeSet<Object>> eList : deque.backwardHashMap.entrySet()) {
            calculatedSize += eList.getValue().size();
            int prevIndex = -1;
            for (Object o : eList.getValue()) {
                Integer index = (Integer) o;
                if (prevIndex >= index - deque.firstIndex) {
                    throw new IllegalStateException("BackwardHashMap nodes out of order");
                }
                prevIndex = index - deque.firstIndex;
                if (!Objects.equals(deque.get(prevIndex), eList.getKey())) { // huh
                    throw new IllegalStateException("Backward Hash Map not working");
                }
            }

        }
        if (deque.size() != calculatedSize) {
            throw new IllegalStateException("Tree size inconsistent");
        }
    }

    public static final double ADD_PROBABILITY = 0.45;
    public static final double NULL_PROBABILITY = 0.5;
    public static final int NUM_TESTS = 100;
    public static final int NUM_ITERATIONS = 10000;

    public static void testSame() {
        ArrayDequeList<Integer> testDeque = new ArrayDequeList<>();
        LinkedList<Integer> parallel = new LinkedList<>();

        for (int k = 0; k < NUM_TESTS; k += 1) {
            testDeque.clear();
            parallel.clear();
            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                if (testDeque.size() == 0 || Randomness.getDouble() < ADD_PROBABILITY) {
                    if (Randomness.chooseRandomly()) {
                        testDeque.addFirst(0);
                        parallel.addFirst(0);
                    } else {
                        testDeque.addLast(0);
                        parallel.addLast(0);
                    }
                } else {
                    if (Randomness.chooseRandomly()) {
                        testDeque.removeFirst();
                        parallel.removeFirst();
                    } else {
                        testDeque.removeFirst();
                        parallel.removeFirst();
                    }
                }
                assertDataMakesSense(testDeque);
                if (!Arrays.equals(testDeque.toArray(), parallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
                }
            }
            for (int j = testDeque.size(); j > 0; j -= 1) {
                if (Randomness.chooseRandomly()) {
                    testDeque.removeFirst();
                    parallel.removeFirst();
                } else {
                    testDeque.removeFirst();
                    parallel.removeFirst();
                }
                assertDataMakesSense(testDeque);
                if (!Arrays.equals(testDeque.toArray(), parallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
                }
            }
            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                if (testDeque.size() == 0 || Randomness.getDouble() < ADD_PROBABILITY) {
                    if (Randomness.chooseRandomly()) {
                        testDeque.addFirst(0);
                        parallel.addFirst(0);
                    } else {
                        testDeque.addLast(0);
                        parallel.addLast(0);
                    }
                } else {
                    if (Randomness.chooseRandomly()) {
                        testDeque.removeFirst();
                        parallel.removeFirst();
                    } else {
                        testDeque.removeFirst();
                        parallel.removeFirst();
                    }
                }
                assertDataMakesSense(testDeque);
                if (!Arrays.equals(testDeque.toArray(), parallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
                }
            }
        }
    }

    public static void testDifferent() {
        ArrayDequeList<Integer> testDeque = new ArrayDequeList<>();
        LinkedList<Integer> parallel = new LinkedList<>();

        for (int k = 0; k < NUM_TESTS; k += 1) {
            testDeque.clear();
            parallel.clear();
            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                if (testDeque.size() == 0 || Randomness.getDouble() < ADD_PROBABILITY) {
                    Integer toAdd = Randomness.getDouble() < NULL_PROBABILITY ? null : Randomness.getRandomInteger(-1024, 1024);
                    if (Randomness.chooseRandomly()) {
                        testDeque.addFirst(toAdd);
                        parallel.addFirst(toAdd);
                    } else {
                        testDeque.addLast(toAdd);
                        parallel.addLast(toAdd);
                    }
                } else {
                    if (Randomness.chooseRandomly()) {
                        testDeque.removeFirst();
                        parallel.removeFirst();
                    } else {
                        testDeque.removeFirst();
                        parallel.removeFirst();
                    }
                }
                assertDataMakesSense(testDeque);
                if (!Arrays.equals(testDeque.toArray(), parallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
                }
                if (testDeque.size() > 0) {
                    Integer toSet = Randomness.getDouble() < NULL_PROBABILITY ? null : Randomness.getRandomInteger(-1024, 1024);
                    int toSetIndex = Randomness.getRandomInteger(0, testDeque.size());
                    testDeque.set(toSetIndex, toSet);
                    parallel.set(toSetIndex, toSet);
                    assertDataMakesSense(testDeque);
                    if (!Arrays.equals(testDeque.toArray(), parallel.toArray())) {
                        throw new IllegalStateException("Arrays were inconsistent");
                    }
                }
            }
            for (int j = testDeque.size(); j > 0; j -= 1) {
                if (Randomness.chooseRandomly()) {
                    testDeque.removeFirst();
                    parallel.removeFirst();
                } else {
                    testDeque.removeFirst();
                    parallel.removeFirst();
                }
                assertDataMakesSense(testDeque);
                if (!Arrays.equals(testDeque.toArray(), parallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
                }
            }
            for (int j = 0; j < NUM_ITERATIONS; j += 1) {
                if (testDeque.size() == 0 || Randomness.getDouble() < ADD_PROBABILITY) {
                    Integer toAdd = Randomness.getDouble() < NULL_PROBABILITY ? null : Randomness.getRandomInteger(-1024, 1024);
                    if (Randomness.chooseRandomly()) {
                        testDeque.addFirst(toAdd);
                        parallel.addFirst(toAdd);
                    } else {
                        testDeque.addLast(toAdd);
                        parallel.addLast(toAdd);
                    }
                } else {
                    if (Randomness.chooseRandomly()) {
                        testDeque.removeFirst();
                        parallel.removeFirst();
                    } else {
                        testDeque.removeFirst();
                        parallel.removeFirst();
                    }
                }
                assertDataMakesSense(testDeque);
                if (!Arrays.equals(testDeque.toArray(), parallel.toArray())) {
                    throw new IllegalStateException("Arrays were inconsistent");
                }
                if (testDeque.size() > 0) {
                    Integer toSet = Randomness.getDouble() < NULL_PROBABILITY ? null : Randomness.getRandomInteger(-1024, 1024);
                    int toSetIndex = Randomness.getRandomInteger(0, testDeque.size());
                    testDeque.set(toSetIndex, toSet);
                    parallel.set(toSetIndex, toSet);
                    assertDataMakesSense(testDeque);
                    if (!Arrays.equals(testDeque.toArray(), parallel.toArray())) {
                        throw new IllegalStateException("Arrays were inconsistent");
                    }
                }
            }
        }
    }

    
    public static void main(String[] args) {

        testSame();
        testDifferent();
        System.out.println("ArrayDeque passed all tests :)"); // FUCKING HYPE LFG

        ArrayDequeList<Integer> mainArray = new ArrayDequeList<>();

        String s = mainArray.toString(); // used for debugging purposes
        for (int i = 0; i < 17; i += 1) {
            int toAdd = Randomness.getRandomInteger(0, 25);
            if (Randomness.getDouble() < 0.5) {
                mainArray.addFirst(toAdd);
                s = java.util.Arrays.toString(mainArray.array);
            } else {
                mainArray.addLast(toAdd);
                s = java.util.Arrays.toString(mainArray.array);
            }

            int toChange = Randomness.getRandomInteger(0, mainArray.size());
            int toChangeTo = Randomness.getRandomInteger(0, 25);
            mainArray.set(toChange, toChangeTo);
            s = java.util.Arrays.toString(mainArray.array);
        }

        for (int i = 0; i < 13; i += 1) {
            if (Randomness.getDouble() < 0.5) {
                mainArray.removeFirst();
                s = java.util.Arrays.toString(mainArray.array);
            } else {
                mainArray.removeLast();
                s = java.util.Arrays.toString(mainArray.array);
            }
        }

        System.out.println(s);
        
        System.out.println(mainArray);
        System.out.println(mainArray.sort());
        System.out.println(mainArray.rotate(1));
        System.out.println(mainArray.rotate(-1));
        System.out.println(mainArray.inPlaceTransform(x -> 2 * x));
        System.out.println(mainArray.foldRight((a, e) -> { e.addLast(a); return e; }, new ArrayDequeList<>()));
    } // hype
}
