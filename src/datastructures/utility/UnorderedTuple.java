package datastructures.utility;

import java.util.Objects;

public class UnorderedTuple<E> {

    protected E first;
    protected E second;
    protected final boolean unmodifiable;

    public UnorderedTuple(E first, E second) {
        this(first, second, false);
    }

    public UnorderedTuple(E first, E second, boolean unmodifiable) {
        this.first = first;
        this.second = second;
        this.unmodifiable = unmodifiable;
    }

    public E getFirst() {
        return first;
    }

    public E getSecond() {
        return second;
    }

    public E updateFirst(E first) {
        if (unmodifiable) {
            throw new IllegalArgumentException("Tuple set to unmodifiable");
        }
        E ret = first;
        this.first = first;
        return ret;
    }

    public E updateSecond(E second) {
        if (unmodifiable) {
            throw new IllegalArgumentException("Tuple set to unmodifiable");
        }
        E ret = second;
        this.second = second;
        return ret;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ")";
    }

    public UnorderedTuple<E> copy() {
        return new UnorderedTuple<E>(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UnorderedTuple<E> t = (UnorderedTuple<E>) o;
        return Objects.equals(getFirst(), t.getFirst()) && Objects.equals(getSecond(), t.getSecond());
    }

    @Override
    public int hashCode() {
        if (unmodifiable) {
            return Objects.hashCode(first) ^ Objects.hashCode(second);
        } else {
            return super.hashCode();
        }
    }
}
