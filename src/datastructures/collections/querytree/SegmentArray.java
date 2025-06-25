package datastructures.collections.querytree;

import datastructures.utility.Randomness;
import datastructures.utility.functionalinterfaces.twotoone.GG2G;

import java.util.Arrays;
import java.util.Collection;

public class SegmentArray<E> {

    private final E[] array;
    private final E[][] dp;
    private final GG2G<E, E, E> operator;

    public static int log2(int number) { // 1 -> 0; 2 -> 1; 3 -> 2; 4 -> 2; 5 -> 3; 6 -> 3; ...; 0 -> 0 I guess
        /* my old way before looking up hardware integer operations
        boolean add = false;
        int count = 0;
        while (number > 1) {
            if ((number & 1) == 1) { // hehe
                add = true;
            }
            number >>= 1; // hehe part 2
            count += 1;
        }

        return count + (add ? 1 : 0); */

        return number == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(number - 1);
    }

    /* layers are as follows:
        layer 0 : 1 + 2n
        layer 1 : 2 + 4n
        layer 2 : 4 + 8n             ... and so on

        because each one is an interval, they round up; i.e., the nearest layer 2 entry to n = 2 is 4 */

    public static int nearestEntry(int number, int layer) {
        return ((number >> (layer + 1)) << (layer + 1)) + (1 << layer); // LMAO
    }

    public static int dividingLayer(int a, int b) {
        // want the first layer where they're on the same interval
        // e.g. 1, 3 --> layer 1
        // essentially, this is just the most significant but of divergence

        /* int layer = -1; this was my code, and it's technically O(log n)
        while (a != b) { // chat GPT gives a way to use hardware operations to make this a true O(1)
            a >>= 1;
            b >>= 1;
            layer += 1;
        }
        return layer; */

        return 31 - Integer.numberOfLeadingZeros(a ^ b); // chat GPT one liner lol
    }

    /** the operator must be both associative and invertible */
    public SegmentArray(GG2G<E, E, E> operator, Collection<E> values) {
        array = (E[]) new Object[values.size()];
        dp = (E[][]) new Object[log2(values.size())][values.size()];
        this.operator = operator;

        int i = 0;
        for (E e : values) {
            array[i] = e;
            i += 1;
        }

        int offset = 1;
        int length = 2;
        for (i = 0; i < dp.length; i += 1) {
            int register = offset;
            while (register < array.length) {
                E below = array[register - 1];
                dp[i][register - 1] = below;
                for (int j = 1; j < offset; j += 1) {
                    below = operator.function(array[register - j - 1], below);
                    dp[i][register - j - 1] = below;
                }
                E above = array[register];
                dp[i][register] = above;
                for (int j = 1; j < offset && j + register < array.length; j += 1) {
                    above = operator.function(above, array[j + register]);
                    dp[i][j + register] = above;
                }

                register += length;
            }
            offset <<= 1;
            length <<= 1;
        }
    }

    /** the operator must be both associative and invertible */
    public SegmentArray(GG2G<E, E, E> operator, E[] values) {
        array = (E[]) new Object[values.length];
        dp = (E[][]) new Object[log2(values.length)][values.length];
        this.operator = operator;

        System.arraycopy(values, 0, array, 0, values.length);

        int offset = 1;
        int length = 2;
        for (int i = 0; i < dp.length; i += 1) {
            int register = offset;
            while (register < array.length) {
                E below = array[register - 1];
                dp[i][register - 1] = below;
                for (int j = 1; j < offset; j += 1) {
                    below = operator.function(array[register - j - 1], below);
                    dp[i][register - j - 1] = below;
                }
                E above = array[register];
                dp[i][register] = above;
                for (int j = 1; j < offset && j + register < array.length; j += 1) {
                    above = operator.function(above, array[j + register]);
                    dp[i][j + register] = above;
                }

                register += length;
            }
            offset <<= 1;
            length <<= 1;
        }
    }

    /** both inclusive! */
    public E evaluateRange(int lowerIndex, int upperIndex) {
        if (lowerIndex < 0 || array.length <= lowerIndex) {
            throw new IllegalArgumentException("Lower index out of bounds");
        }
        if (upperIndex < 0 || array.length <= upperIndex) {
            throw new IllegalArgumentException("Upper index out of bounds");
        }
        if (upperIndex < lowerIndex) {
            throw new IllegalArgumentException("Lower index must be less than or equal to upper index");
        }
        if (lowerIndex == upperIndex) {
            return array[lowerIndex];
        }
        int layer = dividingLayer(lowerIndex, upperIndex);
        return operator.function(dp[layer][lowerIndex], dp[layer][upperIndex]);
    }

    public static void main(String[] args) {

        Integer[][] values = new Integer[50000][4]; // 2x2 matrices essentially

        for (int i = 0; i < values.length; i += 1) {
            values[i] = new Integer[] {
                    Randomness.getRandomInteger(-1 << 24, 1 >> 24),
                    Randomness.getRandomInteger(-1 << 24, 1 >> 24),
                    Randomness.getRandomInteger(-1 << 24, 1 >> 24),
                    Randomness.getRandomInteger(-1 << 24, 1 >> 24)
            };
        }

        GG2G<Integer[], Integer[], Integer[]> operator = (x, y) -> new Integer[] {
                x[0] * y[0] + x[1] * y[2],
                x[0] * y[1] + x[1] * y[3],
                x[2] * y[0] + x[3] * y[2],
                x[2] * y[1] + x[3] * y[3]
        }; // associative but not commutative :)

        SegmentArray<Integer[]> segmentArray = new SegmentArray<>(operator, values);

        for (int i = 0; i < values.length; i += 1) {
            Integer[] bruteForced = values[i];
            for (int j = i; j < values.length; j += 1) {
                Integer[] result = segmentArray.evaluateRange(i, j);
                if (j != i) {
                    bruteForced = operator.function(bruteForced, values[j]);
                }
                if (!Arrays.equals(bruteForced, result)) {
                    throw new IllegalStateException("Segment array inconsistent");
                } // segment
            }
        }

        System.out.println("Segment array passed all tests :)");

        // segment array takes virtually no time compared to brute forcing (as expected, of course)
        // to test, replace O(1) amortized calculation with O(n) brute force calculation
        // should probably also reduce the range from 50000 to something like 2500 lol
    }
    
}
