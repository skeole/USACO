package datastructures.collections.multiset;

import java.util.*;

public class HashMultiSet<E> extends AbstractCollection<E> implements Set<E> {

    private int size = 0;
    private final HashMap<E, Integer> map;

    public HashMultiSet() {
        this.map = new HashMap<E, Integer>();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            final Iterator<E> keys = map.keySet().iterator();
            E current = null;
            int currentCounter = 0;
            int jumpCounter = 0;

            @Override
            public boolean hasNext() {
                return keys.hasNext() || (currentCounter < jumpCounter);
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                if (currentCounter == jumpCounter) {
                    current = keys.next();
                    currentCounter = 0;
                    jumpCounter = map.get(current);
                }
                currentCounter += 1;
                return current;
            }
        };
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
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean add(E e) {
        if (!map.containsKey(e)) {
            map.put(e, 0);
        }
        map.replace(e, map.get(e) + 1);
        size += 1;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (map.containsKey(o)) { // c.isInstance(o);
            map.replace(o == null ? null : (E) o, map.get(o) - 1);
            size -= 1;
            if (map.get(o) == 0) {
                map.remove(o);
            }
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        size = 0;
        map.clear();
    }

    public int count(E e) {
        if (contains(e)) {
            return map.get(e);
        } else {
            return -1;
        }
    }
}