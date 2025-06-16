package datastructures.doublemap;

import java.util.Comparator;
import java.util.TreeMap;

public class TreeDoubleMap<A extends Comparable<? super A>, B> extends DoubleMap<A, B> {
    public TreeDoubleMap() {
        super(new TreeMap<>(), true);
    }

    public TreeDoubleMap(Comparator<A> c) {
        super(new TreeMap<>(c), true);
    }
}
