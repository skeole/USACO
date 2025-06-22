package datastructures.utility;

import java.util.Iterator;

public class IterableUtility {

    public static Iterable<Integer> asIterable(int[] array) {
        return () -> new Iterator<>() {
            int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < array.length;
            }

            @Override
            public Integer next() {
                return array[currentIndex++];
            }
        };
    }

    public static Iterable<Long> asIterable(long[] array) {
        return () -> new Iterator<>() {
            int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < array.length;
            }

            @Override
            public Long next() {
                return array[currentIndex++];
            }
        };
    }

    public static Iterable<Double> asIterable(double[] array) {
        return () -> new Iterator<>() {
            int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < array.length;
            }

            @Override
            public Double next() {
                return array[currentIndex++];
            }
        };
    }

    public static Iterable<Character> asIterable(char[] array) {
        return () -> new Iterator<>() {
            int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < array.length;
            }

            @Override
            public Character next() {
                return array[currentIndex++];
            }
        };
    }
    public static Iterable<Character> asIterable(String string) {
        return () -> new Iterator<>() {
            int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < string.length();
            }

            @Override
            public Character next() {
                return string.charAt(currentIndex++);
            }
        };
    }

    public static Iterable<Boolean> asIterable(boolean[] array) {
        return () -> new Iterator<>() {
            int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < array.length;
            }

            @Override
            public Boolean next() {
                return array[currentIndex++];
            }
        };

    }

    public static <E> Iterable<E> asIterable(E[] array) {
        return () -> new Iterator<>() {
            int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < array.length;
            }

            @Override
            public E next() {
                return array[currentIndex++];
            }
        };
    }
}
