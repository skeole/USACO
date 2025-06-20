package datastructures.collections.doublemap;

import java.util.HashMap;

public class HashDoubleMap<A, B> extends DoubleMap<A, B> {
    public HashDoubleMap() {
        super(new HashMap<>());
    }

    public HashDoubleMap(boolean comparableA) {
        super(new HashMap<>(), comparableA);
    }
}
