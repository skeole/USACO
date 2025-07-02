import java.io.*;
import java.util.*;

public class ForkliftCertified {

    public static void main(String[] args) throws IOException {

        // Begin Parsing Input

        BufferedReader inputReader =
                submission ?
                        (recent ?
                                new BufferedReader(new InputStreamReader(System.in)) :
                                new BufferedReader(new FileReader(problemName + ".in"))) :
                        new BufferedReader(new FileReader("problemdata\\input.txt"));

        StringTokenizer line = new StringTokenizer(inputReader.readLine()); // need to do this for every single line
        int n = Integer.parseInt(line.nextToken()); // number of cows
        int m = Integer.parseInt(line.nextToken()); // number of pairs

        for (int i = 0; i < m; i += 1) {
            line = new StringTokenizer(inputReader.readLine());
            int first = Integer.parseInt(line.nextToken());
            int second = Integer.parseInt(line.nextToken());
        }

        inputReader.close();

        // Finish Parsing Input

        /* plan - order boxes by corners

        Part One:
         - order boxes by NE x-coordinate
         - find leftmost such box
         - find lowermost box with SW corner x-coordinate < this one's NE corner x-coordinate

        // for type 1 (removing all): let's see if a proof gives us a solution
            // assume there were no answer
            // choose the one with the leftmost right side
            // repeatedly replace with the lowest one in its "shade", until there are none in its shade
            // --> done :)

        // so what we want for this is
            // able to easily determine the lowest "rightmost side" --> easy: that's just a bst


        // worse case scenario: staircase --> O(n^2) --> try to limit re-computation
        // a-ha!
            // when we find one, there's no need to recalculate everything: just do essentially a DFS

        // I think part 2 is easy:
            // note that the requirements can be satisfied to no SW corners being in the region
            // --> only need to remember SW corners of boxes :)
                // actually, we only need SW corners for part 1 as well... hmm...

         */


        // Begin Writing Output

        BufferedWriter outputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        outputWriter.write("HI!");
        outputWriter.newLine();

        outputWriter.close();

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

    public static final String problemName = "Problem";
    public static final boolean recent = true; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = false;
}