package datastructures.collections.dequelist;

import java.util.*;

import datastructures.utility.functionalinterfaces.onetoone.G2D;
import datastructures.utility.functionalinterfaces.onetoone.G2G;
import datastructures.utility.functionalinterfaces.threetoone.IGG2G;
import datastructures.utility.functionalinterfaces.twotoone.GG2G;
import datastructures.utility.functionalinterfaces.twotoone.IG2G;
import datastructures.utility.Randomness;

public abstract class DequeList<E> extends AbstractCollection<E> implements Deque<E>, List<E>, Cloneable {

    protected int firstIndex = 0;
    protected int lastIndex = 0;
    protected int growSize = -1;
    protected int shrinkSize = -1;

    protected final Class<?> type;
    protected final HashMap<E, TreeSet<Object>> backwardHashMap = new HashMap<>();

    protected abstract int convert(Object o);

    protected abstract E getHelper(int i);

    protected abstract E setHelper(int i, E e);

    protected abstract void grow();

    protected abstract void shrink();

    protected abstract boolean validToInsert(int i);

    protected abstract boolean validToRemove(int i);

    /** false: first; true: last */
    protected abstract boolean addHelper(boolean beginning, E e);

    protected abstract boolean insertHelper(int i, E e);

    /** false: first; true: last */
    protected abstract E removeHelper(boolean beginning);

    protected abstract E removeHelper(int i);

    protected abstract void clearHelper();

    protected abstract ListIterator<E> iterator(boolean forward, int index);

    protected DequeList(Class<?> type) {
        this.type = type;
    }

    // Pass Class<T> to constructor?
    protected DequeList(Class<?> type, Collection<? extends E> c) { // huh
        this.type = type;
        c.forEach(this::addLast);
    }

    public final E get(int i) {
        if (i < 0 || i >= size()) {
            throw new IndexOutOfBoundsException();
        }
        return getHelper(i);
    }

    @Override
    public final E set(int i, E e) {
        if (i < 0 || i >= size()) {
            throw new IndexOutOfBoundsException();
        }
        return setHelper(i, e);
    }

    public final void swap(int i, int j) {
        if (i == j) {
            return;
        }
        E cache = get(i);
        set(i, get(j));
        set(j, cache);
    }

    @Override
    public final int size() {
        return lastIndex - firstIndex;
    }

    @Override
    public final void addFirst(E e) {
        if (size() == growSize) {
            grow();
        }
        addHelper(true, e);
    }

    @Override
    public final boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    @Override
    public final void push(E e) {
        addFirst(e);
    }

    @Override
    public final void addLast(E e) {
        if (size() == growSize) {
            grow();
        }
        addHelper(false, e);
    }

    @Override
    public final boolean add(E e) {
        addLast(e);
        return true;
    }

    @Override
    public final boolean offerLast(E e) {
        return add(e);
    }

    @Override
    public final boolean offer(E e) {
        return offerLast(e);
    }

    @Override
    public final E pollFirst() {
        if (size() == 0) {
            return null;
        }
        E ret = removeHelper(true);
        if (size() == shrinkSize) {
            shrink();
        }
        return ret;
    }

    @Override
    public final E poll() {
        return pollFirst();
    }

    @Override
    public final E removeFirst() {
        if (size() == 0) {
            throw new NoSuchElementException("Queue is empty, no first element");
        }
        return pollFirst();
    }
    
    @Override
    public final E pop() {
        return removeFirst();
    }

    @Override
    public final E remove() {
        return removeFirst();
    }

    @Override
    public final E pollLast() {
        if (size() == 0) {
            return null;
        }
        E ret = removeHelper(false);
        if (size() == shrinkSize) {
            shrink();
        }
        return ret;
    }

    @Override
    public final E removeLast() {
        if (size() == 0) {
            throw new NoSuchElementException("Queue is empty, no first element");
        }
        return pollLast();
    }

    @Override
    public final E peekFirst() {
        if (size() == 0) {
            return null;
        }
        return get(0);
    }

    @Override
    public final E peek() {
        return peekFirst();
    }

    @Override
    public final E getFirst() {
        if (size() == 0) {
            throw new NoSuchElementException("Queue is empty, no first element");
        }
        return get(0);
    }

    @Override
    public final E element() {
        return getFirst();
    }

    @Override
    public final E peekLast() {
        if (size() == 0) {
            return null;
        }
        return get(size() - 1);
    }

    @Override
    public final E getLast() {
        if (size() == 0) {
            throw new NoSuchElementException("Queue is empty, no last element");
        }
        return get(size() - 1);
    }

    @Override
    public final Iterator<E> iterator() {
        return listIterator();
    }

    @Override
    public final boolean contains(Object o) {
        return backwardHashMap.containsKey(o);
    }

    @Override
    public final boolean remove(Object o) {
        if (contains(o)) {
            int index = indexOf(o);
            if (validToRemove(index)) {
                remove(index);
                return true;
            }
        }
        return false;
    }

    @Override
    public final int indexOf(Object o) {
        if (contains(o)) {
            return convert(backwardHashMap.get(o).first());
        } else {
            return -1;
        }
    }

    public final Collection<Integer> indicesOf(Object o) {
        if (contains(o)) {
            ArrayList<Integer> indices = new ArrayList<>();
            for (Object i : backwardHashMap.get(o)) {
                indices.add(convert(i));
            }
            return indices;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public final int lastIndexOf(Object o) {
        if (contains(o)) {
            return convert(backwardHashMap.get(o).last());
        } else {
            return -1;
        }
    }

    @Override
    public final E remove(int i) {
        if (validToRemove(i)) {
            return removeHelper(i);
        }
        return null;
    }

    @Override
    public final boolean removeFirstOccurrence(Object o) {
        int i = indexOf(o);
        if (validToRemove(i)) {
            removeHelper(i);
            return true;
        }
        return false;
    }

    @Override
    public final boolean removeLastOccurrence(Object o) {
        int i = lastIndexOf(o);
        if (validToRemove(i)) {
            removeHelper(i);
            return true;
        }
        return false;
    }

    @Override
    public final boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void add(int i, E e) {
        if (validToInsert(i)) {
            insertHelper(i, e);
        }
    }

    @Override
    public final ListIterator<E> listIterator(int i) {
        return iterator(true, i);
    }

    @Override
    public final ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public final Iterator<E> descendingIterator() {
        return iterator(false, size() - 1);
    }

    @Override
    public final List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void clear() {
        firstIndex = 0;
        lastIndex = 0;
        clearHelper();
        backwardHashMap.clear();
    }
    
    @Override
    public final E[] toArray() {
        E[] arr = (E[]) new Object[size()]; // much more interesting imo

        int i = 0;
        for (E e : this) {
            arr[i] = e;
            i += 1;
        }

        return arr;
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

    /** returns itself */
    public final DequeList<E> reverse() {
        for (int i = 0; i < size() / 2; i += 1) {
            swap(i, size() - 1 - i);
        }
        return this;
    }

    /** if rotateBy is 1, then [0, 1, 2, 3, 4] shifts to [4, 0, 1, 2, 3]. rotateBy can be negative. returns itself */
    public final DequeList<E> rotate(int rotateBy) {
        if (size() == 0) return this;
        rotateBy = ((rotateBy % size()) + size()) % size();
        if (rotateBy <= size() / 2) {
            for (int i = 0; i < rotateBy; i += 1) {
                addFirst(removeLast());
            }
        } else {
            rotateBy = size() - rotateBy;
            for (int i = 0; i < rotateBy; i += 1) {
                addLast(removeFirst());
            }
        }
        return this;
    }

    public final DequeList<E> inPlaceTransform(G2G<E, E> map) {
        for (int i = 0; i < size(); i += 1) {
            set(i, map.function(get(i)));
        }
        return this;
    }

    /** first input: current index; second input: current value */
    public final DequeList<E> inPlaceTransformWithIndex(IG2G<E, E> map) {
        for (int i = 0; i < size(); i += 1) {
            set(i, map.function(i, get(i)));
        }
        return this;
    }
    
    public final <F> DequeList<F> transform(G2G<E, F> map) {
        if (type == Integer.class) {
            return transformToHashDequeList(map);
        } else if (type == TreeDequeList.TreeDequeListNode.class) {
            return transformToTreeDequeList(map);
        } else {
            throw new RuntimeException("This shouldn't happen");
        }
    }
    
    public final <F> ArrayDequeList<F> transformToHashDequeList(G2G<E, F> map) {
        ArrayDequeList<F> transform = new ArrayDequeList<>();

        for (int i = 0; i < size(); i += 1) {
            transform.addLast(map.function(get(i)));
        }

        return transform;
    }

    public final <F> TreeDequeList<F> transformToTreeDequeList(G2G<E, F> map) {
        TreeDequeList<F> transform = new TreeDequeList<>();

        for (int i = 0; i < size(); i += 1) {
            transform.addLast(map.function(get(i)));
        }

        return transform;
    }
    
    /** defaults to using HashDequeList */
    public final <F> DequeList<F> transformWithIndex(IG2G<E, F> map) {
        if (type == Integer.class) {
            return transformWithIndexToHashDequeList(map);
        } else if (type == TreeDequeList.TreeDequeListNode.class) {
            return transformWithIndexToTreeDequeList(map);
        } else {
            throw new RuntimeException("This shouldn't happen");
        }
    }
    
    public final <F> ArrayDequeList<F> transformWithIndexToHashDequeList(IG2G<E, F> map) {
        ArrayDequeList<F> transform = new ArrayDequeList<F>();

        for (int i = 0; i < size(); i += 1) {
            transform.addLast(map.function(i, get(i)));
        }

        return transform;
    }

    public final <F> TreeDequeList<F> transformWithIndexToTreeDequeList(IG2G<E, F> map) {
        TreeDequeList<F> transform = new TreeDequeList<F>();

        for (int i = 0; i < size(); i += 1) {
            transform.addLast(map.function(i, get(i)));
        }

        return transform;
    }

    /** To make the object update, can return same reference */
    public final <F> F foldRight(GG2G<E, F, F> combine, F base) {
        for (int i = size() - 1; i >= 0; i -= 1) {
            base = combine.function(get(i), base);
        }
        return base;
    }

    /** first input: current index; second input: current list value; third input: current accumulated value. To make the object update, can return same reference */
    public final <F> F foldRightWithIndex(IGG2G<E, F, F> combine, F base) {
        for (int i = size() - 1; i >= 0; i -= 1) {
            base = combine.function(i, get(i), base);
        }
        return base;
    }

    /** To make the object update, can return same reference */
    public final <F> F foldLeft(GG2G<E, F, F> combine, F base) {
        for (int i = 0; i < size(); i += 1) {
            base = combine.function(get(i), base);
        }
        return base;
    }

    /** first input: current index; second input: current list value; third input: current accumulated value. To make the object update, can return same reference */
    public final <F> F foldLeftWithIndex(IGG2G<E, F, F> combine, F base) {
        for (int i = 0; i < size(); i += 1) {
            base = combine.function(i, get(i), base);
        }
        return base;
    }

    private static <E> int firstMinusSecond(E first, E second) throws ClassCastException {
        if (first == null) {
            return second == null ? 0 : -1;
        }
        if (second == null) {
            return 1;
        }
        try {
            return ((Comparable<? super E>) first).compareTo(second);
        } catch (ClassCastException e) {
            throw new ClassCastException("Tried to sort non-comparable array");
        }
    }
    
    private static <E> int firstMinusSecond(E first, E second, G2D<E> conversion) {
        if (first == null) {
            return second == null ? 0 : -1;
        }
        if (second == null) {
            return 1;
        }
        double res = conversion.function(first) - conversion.function(second);
        return res < 0 ? -1 : res > 0 ? 1 : 0;
    }
    
    private static <E> int firstMinusSecond(E first, E second, Comparator<? super E> c, boolean overrideNull) {
        if (!overrideNull) {
            if (first == null) {
                return second == null ? 0 : -1;
            }
            if (second == null) {
                return 1;
            }
        }
        return c.compare(first, second);
    }

    private static final int LIMIT = 10; // huh

    private <F> void bruteForceSort(GG2G<E, E, Integer> swap, int lowerIndex, int upperIndex, DequeList<F> parallelArray) { // for length < LIMIT
        for (int i = lowerIndex; i < upperIndex; i += 1) {
            for (int j = i; j > lowerIndex; j -= 1) {
                if (swap.function(get(j), get(j - 1)) < 0) {
                    swap(j - 1, j);
                    if (parallelArray != null) {
                        parallelArray.swap(j - 1, j);
                    }
                } else {
                    break;
                }
            }
        }
    }
    
    private <F> DequeList<E> baseInPlaceSort(GG2G<E, E, Integer> comparison, DequeList<F> parallelArray) {
        
        if (parallelArray != null) {
            while (parallelArray.size() < size()) {
                parallelArray.addLast(null);
            };
            while (size() < parallelArray.size()) {
                addLast(null);
            };
        }

        ArrayDeque<Integer> queue = new ArrayDeque<>();
        queue.add(0);
        queue.add(size());

        while (!queue.isEmpty()) {
            int lower = queue.removeFirst();
            int upper = queue.removeFirst();

            while (upper - lower > LIMIT) {
                int first = Randomness.getRandomInteger(lower, upper);
                int second = Randomness.getRandomInteger(lower, upper);
                int third = Randomness.getRandomInteger(lower, upper);

                boolean firstMinusSecond = comparison.function(get(first), get(second)) > 0;
                boolean firstMinusThird = comparison.function(get(first), get(second)) > 0;
                boolean secondMinusThird = comparison.function(get(first), get(second)) > 0;

                int partition = firstMinusSecond ? (
                    secondMinusThird ? second : 
                    firstMinusThird ? third : 
                    first
                ) : (
                    firstMinusThird ? first : 
                    secondMinusThird ? third : 
                    second
                );

                swap(lower, partition);

                if (parallelArray != null) {
                    parallelArray.swap(lower, partition);
                }

                int greater = upper - 1;
                int pivot = lower;
                while (pivot < greater) {
                    if (comparison.function(get(pivot + 1), get(pivot)) < 0) {
                        swap(pivot, ++pivot);
                        if (parallelArray != null) {
                            parallelArray.swap(pivot - 1, pivot);
                        }
                    } else {
                        swap(greater--, pivot + 1);
                        if (parallelArray != null) {
                            parallelArray.swap(greater + 1, pivot + 1);
                        }
                    }
                }

                if (upper - pivot > pivot - lower) {
                    queue.add(pivot + 1);
                    queue.add(upper);
                    upper = pivot;
                } else {
                    queue.add(lower);
                    queue.add(pivot);
                    lower = pivot + 1;
                }
            }

            bruteForceSort(comparison, lower, upper, parallelArray);
        }

        return this;
    }

    private <F> DequeList<E> baseStableSort(GG2G<E, E, Integer> comparison, DequeList<F> parallelArray) {

        if (parallelArray != null) {
            while (parallelArray.size() < size()) {
                parallelArray.addLast(null);
            };
            while (size() < parallelArray.size()) {
                addLast(null);
            };
        }

        DequeList<Integer> indexArray = new ArrayDequeList<>();
        DequeList<E> copy = cloneToHashDequeList();

        for (int i = 0; i < size(); i += 1) {
            indexArray.addLast(i);
        }

        indexArray.baseInPlaceSort((x, y) -> {
            int difference = comparison.function(get(x), get(y));
            return difference != 0 ? difference : x - y;
        }, null);

        for (int i = 0; i < size(); i += 1) {
            set(i, copy.get(indexArray.get(i)));
        }

        if (parallelArray != null) {
            F[] copy2 = parallelArray.toArray();

            for (int i = 0; i < size(); i += 1) {
                parallelArray.set(i, copy2[i]);
            }
        }

        return this;
    }

    /** By convention, null keys will be sorted to the front. If you want otherwise, make a null wrapper or use a custom comparator */
    public final DequeList<E> inPlaceSort() { // genuine retardation but we ball
        return baseInPlaceSort((x, y) -> firstMinusSecond(x, y), null);
    }

    /** By convention, null keys will be sorted to the front. If you want otherwise, make a null wrapper or use a custom comparator */
    public final DequeList<E> inPlaceSort(G2D<E> conversion) {
        return baseInPlaceSort((x, y) -> firstMinusSecond(x, y, conversion), null);
    }

    /** by default, overrideNull is set to true */
    public final DequeList<E> inPlaceSort(Comparator<? super E> c) {
        return baseInPlaceSort((x, y) -> firstMinusSecond(x, y, c, true), null);
    }

    public final DequeList<E> inPlaceSort(Comparator<? super E> c, boolean overrideNull) {
        return baseInPlaceSort((x, y) -> firstMinusSecond(x, y, c, overrideNull), null);
    }

    /** By convention, null keys will be sorted to the front. If you want otherwise, make a null wrapper or use a custom comparator */
    public final <F> DequeList<F> parallelInPlaceSort(DequeList<F> parallelArray) { // genuine retardation but we ball
        baseInPlaceSort((x, y) -> firstMinusSecond(x, y), parallelArray);
        return parallelArray;
    }
    
    /** By convention, null keys will be sorted to the front. If you want otherwise, make a null wrapper or use a custom comparator */
    public final <F> DequeList<F> parallelInPlaceSort(DequeList<F> parallelArray, G2D<E> conversion) {
        baseInPlaceSort((x, y) -> firstMinusSecond(x, y, conversion), parallelArray);
        return parallelArray;
    }

    /** by default, overrideNull is set to true */
    public final <F> DequeList<F> parallelInPlaceSort(DequeList<F> parallelArray, Comparator<? super E> c) {
        baseInPlaceSort((x, y) -> firstMinusSecond(x, y, c, true), parallelArray);
        return parallelArray;
    }

    public final <F> DequeList<F> parallelInPlaceSort(DequeList<F> parallelArray, Comparator<? super E> c, boolean overrideNull) {
        baseInPlaceSort((x, y) -> firstMinusSecond(x, y, c, overrideNull), parallelArray);
        return parallelArray;
    }

    /** By convention, null keys will be sorted to the front. If you want otherwise, make a null wrapper or use a custom comparator */
    public final DequeList<E> stableSort() { // genuine retardation but we ball
        return baseStableSort((x, y) -> firstMinusSecond(x, y), null);
    }
    
    /** By convention, null keys will be sorted to the front. If you want otherwise, make a null wrapper or use a custom comparator */
    public final DequeList<E> stableSort(G2D<E> conversion) {
        return baseStableSort((x, y) -> firstMinusSecond(x, y, conversion), null);
    }

    /** by default, overrideNull is set to true */
    public final DequeList<E> stableSort(Comparator<? super E> c) {
        return baseStableSort((x, y) -> firstMinusSecond(x, y, c, true), null);
    }

    public final DequeList<E> stableSort(Comparator<? super E> c, boolean overrideNull) {
        return baseStableSort((x, y) -> firstMinusSecond(x, y, c, overrideNull), null);
    }

    /** By convention, null keys will be sorted to the front. If you want otherwise, make a null wrapper or use a custom comparator */
    public final <F> DequeList<F> parallelStableSort(DequeList<F> parallelArray) { // genuine retardation but we ball
        baseStableSort((x, y) -> firstMinusSecond(x, y), parallelArray);
        return parallelArray;
    }
    
    /** By convention, null keys will be sorted to the front. If you want otherwise, make a null wrapper or use a custom comparator */
    public final <F> DequeList<F> parallelStableSort(DequeList<F> parallelArray, G2D<E> conversion) {
        baseStableSort((x, y) -> firstMinusSecond(x, y, conversion), parallelArray);
        return parallelArray;
    }

    /** by default, overrideNull is set to true */
    public final <F> DequeList<F> parallelStableSort(DequeList<F> parallelArray, Comparator<? super E> c) {
        baseStableSort((x, y) -> firstMinusSecond(x, y, c, true), parallelArray);
        return parallelArray;
    }

    public final <F> DequeList<F> parallelStableSort(DequeList<F> parallelArray, Comparator<? super E> c, boolean overrideNull) {
        baseStableSort((x, y) -> firstMinusSecond(x, y, c, overrideNull), parallelArray);
        return parallelArray;
    }

    public final DequeList<E> sort() {
        return stableSort();
    }

    public final DequeList<E> sort(G2D<E> conversion) {
        return stableSort(conversion);
    }

    @Override /** returns void bc super implementation is stupid */
    public final void sort(Comparator<? super E> c) {
        stableSort(c);
    }

    public final DequeList<E> sort(Comparator<? super E> c, boolean overrideNull) {
        return stableSort(c, overrideNull);
    }

    public final <F> DequeList<F> sort(DequeList<F> parallelArray) {
        return parallelStableSort(parallelArray);
    }

    public final <F> DequeList<F> sort(G2D<E> conversion, DequeList<F> parallelArray) {
        return parallelStableSort(parallelArray, conversion);
    }

    public final <F> DequeList<F> sort(Comparator<? super E> c, DequeList<F> parallelArray) {
        return parallelStableSort(parallelArray, c);
    }

    public final <F> DequeList<F> sort(Comparator<? super E> c, boolean overrideNull, DequeList<F> parallelArray) {
        return parallelStableSort(parallelArray, c, overrideNull);
    }

    public final E[] getElementsByCriteria(int[] array, int number, Comparator<? super E> c) { // n log k --> yey
        return null;
    }

    @Override
    public final DequeList<E> clone() {
        if (type == Integer.class) {
            return cloneToHashDequeList();
        } else if (type == TreeDequeList.TreeDequeListNode.class) {
            return cloneToTreeDequeList();
        } else {
            throw new RuntimeException("This shouldn't happen");
        }
    }

    public final ArrayDequeList<E> cloneToHashDequeList() {
        return new ArrayDequeList<E>(this);
    };

    public final TreeDequeList<E> cloneToTreeDequeList() {
        return new TreeDequeList<E>(this);
    };

    /* Cool functions that are totally useless! */

    public static <A, B, C> DequeList<C> combineToHashDequeList(DequeList<A> first, DequeList<B> second, GG2G<A, B, C> combine) {
        return first.foldLeftWithIndex(
            (IGG2G<A, G2G<DequeList<B>, ArrayDequeList<C>>, G2G<DequeList<B>, ArrayDequeList<C>>>) (x, y, z) -> {
                ArrayDequeList<C> res = z.function(second);
                if (x >= second.size()) {
                    return a -> res;
                }
                res.addLast(combine.function(y, second.get(x))); 
                return a -> res;
            }, // combine
            (G2G<DequeList<B>, ArrayDequeList<C>>) x -> new ArrayDequeList<>() // base
        ).function(second);
    }
}
