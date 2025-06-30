package problems.gold;

import java.io.*;
import java.util.*;

public class MooDecomposition { // 2025 Open Gold 1

    public static void main(String[] args) throws IOException { // extremely easy what

        // Begin Parsing Input

        BufferedReader inputReader =
                submission ?
                        (recent ?
                                new BufferedReader(new InputStreamReader(System.in)) :
                                new BufferedReader(new FileReader(problemName + ".in"))) :
                        new BufferedReader(new FileReader("problemdata\\input.txt"));

        StringTokenizer line = new StringTokenizer(inputReader.readLine()); // need to do this for every single line
        int k = Integer.parseInt(line.nextToken()); // number of o's in subsequence
        int n = Integer.parseInt(line.nextToken()); // length of sequence
        long l = Long.parseLong(line.nextToken()); // number of repetitions (aka power)

        int m = n / (k + 1);
        int[] mars = new int[m];

        line = new StringTokenizer(inputReader.readLine());
        String str = line.nextToken();

        int nos = -k;
        int dex = m - 1;
        int maxnos = 0;
        for (int i = str.length() - 1; i >= 0; i -= 1) {
            if (str.charAt(i) == 'M') {
                mars[dex] = nos;
                maxnos = Math.max(nos, maxnos);
                dex -= 1;
                nos -= k;
            } else {
                nos += 1;
            }
        }

        long[] combinations = new long[maxnos + 1];
            // combinations[i] = (i + k) choose k
        combinations[0] = 1;
        for (int i = 1; i < combinations.length; i += 1) {
            // n = i + k
            // (n + 1 choose k) = (n choose k) * (n + 1) / (n - k + 1)
            // n + 1 * n * ... * n - k + 2             / k!
            //         n * ... * n - k + 2 * n - k + 1 / k!
            combinations[i] = modprod(combinations[i - 1], i + k, inverse(i));
        }

        inputReader.close();

        long prod = 1;
        for (int i : mars) {
            prod = modprod(prod, combinations[i]);
        }

        prod = modpow(prod, l);

        // Finish Parsing Input

        /* infinite time solution
            - go to the last m
            - see how many os there are
            - (n choose r)
            - continue
            - (n choose r) again
            - raise to the power of L

         - example for MMOOOO (answer: 4)
            - (4 choose 2) * (2 choose 2) = 6

         - example for MMOO x 100 (answer: 4)
            - 4 ^ 100 yep

         - I don't see any faster way to do this for a single block of N <= 10^6
            - runtime: at most 10^6 combinations */

        // Begin Writing Output

        BufferedWriter ouputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        ouputWriter.write("" + prod);
        ouputWriter.newLine();

        ouputWriter.close();

        // Finish Writing Output

    }

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

    public static final String problemName = "Problem";
    public static final boolean recent = true; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}