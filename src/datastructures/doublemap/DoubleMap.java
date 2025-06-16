package datastructures.doublemap;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public abstract class DoubleMap<A, B> extends AbstractMap<A, B> { // once again, can make abstract. However, the reverse should still be a HashMap
    private final Map<A, B> first;
    private HashMap<B, Set<A>> second = new HashMap<>();

    private final boolean comparableA;

    protected DoubleMap(Map<A, B> first) {
        this.first = first;
        comparableA = false;
    }

    protected DoubleMap(Map<A, B> first, boolean comparableA) {
        this.first = first;
        this.comparableA = comparableA;
    }

    @Override
    public int size() {
        return first.size();
    }

    @Override
    public boolean containsKey(Object key) {
        return first.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return second.containsKey(value);
    }

    @Override
    public B get(Object key) {
        return first.get(key);
    };

    public Collection<A> getKeysOfValue(Object value) {
        return second.get(value);
    }

    public A getFirstKeyOfValue(Object value) {
        if (comparableA) {
            if (containsValue(value)) {
                return ((TreeSet<A>) second.get(value)).first();
            } else {
                return null;
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public A getLastKeyOfValue(Object value) {
        if (comparableA) {
            if (containsValue(value)) {
                return ((TreeSet<A>) second.get(value)).last();
            } else {
                return null;
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public B put(A key, B value) {

        if (!second.containsKey(value)) {
            second.put(value, comparableA ? new TreeSet<>() : new HashSet<>());
        }
        second.get(value).add(key);

        if (!first.containsKey(key)) {
            first.put(key, value);
            return null;
        } else {
            B old = first.replace(key, value);
            second.get(old).remove(key);
            if (second.get(old).size() == 0) {
                second.remove(old);
            }
            return old;
        }
    }

    @Override
    public B remove(Object key) {
        if (first.containsKey(key)) {
            B old = first.remove(key);
            second.get(old).remove(key);
            if (second.get(old).size() == 0) {
                second.remove(old);
            }
            return old;
        }
        return null;
    }

    @Override
    public void clear() {
        first.clear();
        second.clear();
    }

    @Override
    public Set<Entry<A, B>> entrySet() {
        return first.entrySet();
    }
}