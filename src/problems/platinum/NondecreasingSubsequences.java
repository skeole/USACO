package problems.platinum;

import java.io.*;
import java.util.*;

public class NondecreasingSubsequences { // 2020 January Platinum 2

    public static void main(String[] args) throws FileNotFoundException, IOException {

        // Begin Parsing Setup

        BufferedReader inputReader = 
            submission ? 
                (recent ? 
                    new BufferedReader(new InputStreamReader(System.in)) : 
                    new BufferedReader(new FileReader(problemName + ".in"))) : 
                new BufferedReader(new FileReader("problemdata\\input.txt"));

        StringTokenizer line = new StringTokenizer(inputReader.readLine());

        int n = Integer.parseInt(line.nextToken());
        int k = Integer.parseInt(line.nextToken());

        line = new StringTokenizer(inputReader.readLine());

        int[] sequence = new int[n];

        for (int i = 0; i < n; i += 1) {
            sequence[i] = Integer.parseInt(line.nextToken()) - 1;
        }

        // Finish Parsing Setup

        /* Let's think the other way... what would give us an O(f(k)) way to evaluate any individual query

          what if... instead of just all routes that end at k, we do all routes that start at k1 and end at k2

            --> the paths become multiplicative
                consider two regions, r1 and r2
                then, for a path to go from a to b, it can go to just one of the pre-defined routes
                 --> many more equations than variables :) which ones to use...

            let c be the whole region, a be the left part, and b be the target region part (what we want)

            c44 - a44 = b44 * (a44 + 1)
            c34 - a34 = b44 * (a33 + a34) + b34 * (a33 + 1)
            c24 - a24 = b44 * (a22 + a23 + a24) + b34 * (a22 + a23) + b24 * (a22 + 1)
            c14 - a14 = b44 * (a11 + a12 + a13 + a14) + b34 * (a11 + a12 + a13) + b24 * (a11 + a12) + b14 * (a11 + 1)

            we can quickly see a pattern emerge. however, this is O(k^3), even with a prefix optimization
            not good enough, I believe, as there will likely be a nontrivial constant factor

            how can we combine them? remember, we only need the sum of all bxy, not the individuals

            let's think about how (a11 + a12 + ... + a43 + a44 + 1) * (b11 + b12 + ... + b43 + b44 + 1) - 1 is represented
                 - the ones that aren't represented are the ones like a23 * b12

            what if we had one for (a13 + a23 + a33) - prefix counts - and one for (a22 + a23 + a24) - suffix counts
                - um. obviously this won't work - but it is actually an interesting idea.
                - OHHHH - ADD FOR ALL THE SECOND VALUES. no. yes? yoooooooooooooooooooo

            c44 - a44 = b44 * (a44 + 1)
            --> c44 - a44 = (b44) * (a44 + 1).

            c34 - a34 = b44 * (a33 + a34) + b34 * (a33 + 1)
            c33 - a33 =                     b33 * (a33 + 1)
            --> c34 + c33 - a34 - a33 = (b44) * (a33 + a34) + (b34 + b33) * (a33 + 1)

            c24 - a24 = b44 * (a22 + a23 + a24) + b34 * (a22 + a23) + b24 * (a22 + 1)
            c23 - a23 =                           b33 * (a22 + a23) + b23 * (a22 + 1)
            c22 - a22 =                                               b22 * (a22 + 1)

            can obviously do the same thing for the 1s.
            Variables become b44; b33 + b34; b22 + b23 + b24; and b11 + b12 + b13 + b14
                - these are the same as the variables we need for a and c
                - wait what? we do only need the starting point? no that's not true
                - we need a few more variables for a
                --> A[i][j] : a[i][j] + a[i][j + 1] + ... + a[i][4]
                --> can subtract like that. We can also just subtract iteratively

            c4 - a4 = b4 (a4 + 1)
            c3 - a3 = b4 * a3 + b3 *

            for the as and cs,
                - ok, that won't work - at least not */

        long[][][] startingSequences = new long[n + 1][k][k];

        // startingSequences[i][a][b] :
            // all indices < i
            // first at a
            // currently at b

        for (int i = 0; i < n; i += 1) {
            for (int a = 0; a < k; a += 1) {
                for (int b = 0; b < k; b += 1) {
                    startingSequences[i + 1][a][b] += startingSequences[i][a][b];
                    if (startingSequences[i + 1][a][b] >= MODULO) {
                        startingSequences[i + 1][a][b] -= MODULO;
                    }
                }
            }

            int current = sequence[i];

            for (int a = 0; a <= current; a += 1) {
                for (int b = a; b <= current; b += 1) {
                    startingSequences[i + 1][a][current] += startingSequences[i][a][b]; // continue
                    if (startingSequences[i + 1][a][current] >= MODULO) {
                        startingSequences[i + 1][a][current] -= MODULO;
                    }
                }
            }

            startingSequences[i + 1][current][current] += 1; // start a new one
            if (startingSequences[i + 1][current][current] >= MODULO) {
                startingSequences[i + 1][current][current] -= MODULO;
            }
        }

        // Begin Parsing + Processing Input + Writing Output

        BufferedWriter ouputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        line = new StringTokenizer(inputReader.readLine());
        int q = Integer.parseInt(line.nextToken()); // number of entries

        for (int i = 0; i < q; i += 1) {
            line = new StringTokenizer(inputReader.readLine());

            // now we have to ball based on first and second

            long[][] a = startingSequences[Integer.parseInt(line.nextToken()) - 1]; // lmao
            long[][] c = startingSequences[Integer.parseInt(line.nextToken())];

            // computations we need:
                // [4][4], [3][3] + [3][4], [2][2] + [2][3] + [2][4], [1][1] + [1][2] + [1][3] + [1][4]

            long[] bks = new long[k];

            for (int bk = k - 1; bk >= 0; bk -= 1) {
                /* bks[bk] * (1 + a[bk][bk]) = c[bk][bk] - a[bk][bk] +

                        c[bk][bk + 1] - a[bk][bk + 1] - bks[bk + 1] * (a[bk][bk] + a[bk][bk + 1] + ... ) */

                long product = modsum(c[bk][bk], -a[bk][bk]);
                long runningAs = a[bk][bk];
                long inverse = inverse(a[bk][bk] + 1); // I swear to fucking god if this bugs out
                if (inverse == 0) {
                    System.out.println("RIP");
                    // actually so cooked if this happens
                    // SUUUUUURELY the usaco would not do this to us (update: they didn't :) )
                }
                for (int bkk = bk + 1; bkk < k; bkk += 1) {
                    runningAs = modsum(runningAs, a[bk][bkk]);
                    product = modsum(product, c[bk][bkk], -a[bk][bkk], -modprod(bks[bkk], runningAs));
                }
                bks[bk] = modprod(product, inverse);
            }
            bks[0] = modsum(bks[0], 1); // lolz
            ouputWriter.write(String.valueOf(modsum(bks)));
            ouputWriter.newLine();
        }

        inputReader.close();
        ouputWriter.close();

        // Finish Parsing + Processing Input + Writing Output

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

    /** can be used to divide! */
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

    public static final String problemName = "nondec";
    public static final boolean recent = false; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}