package problems.utility;

public class UsefulFunctions {

    public final static int MODULO = 1000000007; // 10^9 + 7
    public static final int[] MODULO_AS_BINARY = {0, 2, 9, 11, 14, 15, 17, 19, 20, 23, 24, 25, 27, 28, 29};

    /** expectation: largest magnitude less than 10^9 + 7 */
    public static long modsum(long... numbers) {
        long sum = 0;
        for (long number : numbers) {
            if (number < 0) {
                number += MODULO;
            }
            sum += number;
            if (sum >= MODULO) {
                sum -= MODULO;
            }
        }
        return sum;
    }

    /** expectation: magnitude less than 10^9 + 7 */
    public static long inverse(long number) {
        if (number < 0) {
            number += MODULO;
        }

        long result = 1;
        long register = number;

        int currentExponent = 0;
        for (int i : MODULO_AS_BINARY) {
            for (int exponent = currentExponent; exponent < i; exponent += 1) {
                register = (register * register) % MODULO;
            }
            result = (result * register) % MODULO;
            currentExponent = i;
        }

        return result;
    }

    /** expectation: largest magnitude less than 10^9 + 7 */
    public static long modprod(long... numbers) {
        long product = 1;
        for (long number : numbers) {
            if (number < 0) {
                number += MODULO;
            }
            product *= number;
            if (product >= MODULO) {
                product %= MODULO;
            }
        }
        return product;
    }

    /** expectation: largest magnitude less than 10^9 + 7 */
    public static long modpow(long number, long exponent) {
        if (exponent == 0) {
            return 1;
        }
        long prev = modpow(number, exponent / 2);
        if (exponent % 2 == 0) {
            return modprod(prev, prev);
        } else {
            return modprod(number, prev, prev);
        }
    }

    public interface I2B {
        boolean function(int i);
    }

    public interface I2I {
        int function(int i);
    }

    /** maximum value that works. for minimum value, flip the logic */
    public static int binSearchOnAnswer(int min, int max, I2B query) {
        if (max == min) {
            return max;
        }

        int median = (min + max + 1) / 2;

        if (query.function(median)) {
            return binSearchOnAnswer(median, max, query);
        } else {
            return binSearchOnAnswer(min, median - 1, query);
        }
    }

    /** maximum entry within range. for minimum value, flip the logic */
    public static int ternarySearchForMaximum(int min, int max, I2I query) {
        if (max == min) {
            return max;
        }

        return ternarySearchForMaximum(min, max, query.function(min), query.function(max), query);
    }

    private static int ternarySearchForMaximum(int min, int max, int minQuery, int maxQuery, I2I query) {

        if (max - min < 4) { // idek bro magic number
            int ret = minQuery > maxQuery ? min : max;
            int retQuery = Math.max(minQuery, maxQuery);
            for (int i = min + 1; i < max; i += 1) {
                int temp = query.function(i);
                if (temp > ret) {
                    ret = i;
                    retQuery = temp;
                }
            }
        }

        // annoying, will do later

        int lowerMedian = (2 * min + max + 2) / 3;
        int lmq = query.function(lowerMedian);

        int upperMedian = (min + 2 * max + 2) / 3;
        int umq = query.function(upperMedian);

        return -1;
    }


}
