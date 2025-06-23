package datastructures.utility;

import java.util.Map;
import java.util.Objects;

public class OrderedTuple<F, S> implements Map.Entry<F, S> { // can just chain pairs together

    protected F first;
    protected S second;
    protected final boolean unmodifiable;

    public OrderedTuple(F first, S second) {
        this(first, second, false);
    }

    public OrderedTuple(F first, S second, boolean unmodifiable) {
        this.first = first;
        this.second = second;
        this.unmodifiable = unmodifiable;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public F updateFirst(F first) {
        if (unmodifiable) {
            throw new IllegalArgumentException("Tuple set to unmodifiable");
        }
        F ret = first;
        this.first = first;
        return ret;
    }

    public S updateSecond(S second) {
        if (unmodifiable) {
            throw new IllegalArgumentException("Tuple set to unmodifiable");
        }
        S ret = second;
        this.second = second;
        return ret;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ")";
    }

    public OrderedTuple<F, S> copy() {
        return new OrderedTuple<F, S>(first, second);
    }

    @Override
    public F getKey() {
        return first;
    }

    @Override
    public S getValue() {
        return second;
    }

    @Override
    public S setValue(S value) {
        if (unmodifiable) {
            throw new IllegalArgumentException("Tuple set to unmodifiable");
        }
        S ret = second;
        this.second = value;
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderedTuple<F, S> t = (OrderedTuple<F, S>) o;
        return Objects.equals(getFirst(), t.getFirst()) && Objects.equals(getSecond(), t.getSecond());
    }

    @Override
    public int hashCode() {
        if (unmodifiable) {
            return Objects.hashCode(first) * 31 + Objects.hashCode(second);
        } else {
            return super.hashCode();
        }
    }
}
