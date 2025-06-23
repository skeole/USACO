package datastructures.utility;

public class ModularArithmetic {

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

    /** can be used to divide! uses Fermat's method */
    public static long inverse(long number) {
        // if a^p = a, then a^p-2 = a^-1
        // 0111011100110101100101000000111 is p
        // 0111011100110101100101000000101 is p - 2

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

}
