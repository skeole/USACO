package problems.gold;

import java.io.*;
import java.util.*;

public class SmallerAverages { // 2024 Open Gold 3

    public static class Fraction implements Comparable<Fraction> {
        final int numerator;
        final int denominator;
        int tuple = 0;

        public Fraction(int numerator, int denominator) {
            int gcd = gcd(numerator, denominator);
            if (denominator < 0) {
                this.numerator = -numerator / gcd;
                this.denominator = -denominator / gcd;
            } else {
                this.numerator = numerator / gcd;
                this.denominator = denominator / gcd;
            }
        }

        public static int gcd(int a, int b) {
            a = a < 0 ? -a : a;
            b = b < 0 ? -b : b;

            while (b != 0) {
                int c = a % b;
                a = b;
                b = c;
            }

            return a;
        }

        public int compareTo(Fraction other) {
            return Long.compare((long) this.numerator * other.denominator, (long) this.denominator * other.numerator);
        }

        public String toString() {
            return "" + Math.round(100.0 * this.numerator / this.denominator) / 100.0;
        }

    }

    public static void main(String[] args) throws IOException {

        // Begin Parsing Input

        BufferedReader inputReader =
                submission ?
                        (recent ?
                                new BufferedReader(new InputStreamReader(System.in)) :
                                new BufferedReader(new FileReader(problemName + ".in"))) :
                        new BufferedReader(new FileReader("problemdata\\input.txt"));

        StringTokenizer line = new StringTokenizer(inputReader.readLine()); // need to do this for every single line

        int n = Integer.parseInt(line.nextToken());

        int[] first = new int[n]; // 500 * 10^6 is less than the integer limit
        int[] second = new int[n];

        line = new StringTokenizer(inputReader.readLine());
        for (int i = 0; i < n; i += 1) {
            first[i] = Integer.parseInt(line.nextToken());
        }

        line = new StringTokenizer(inputReader.readLine());
        for (int i = 0; i < n; i += 1) {
            second[i] = Integer.parseInt(line.nextToken());
        }

        inputReader.close();

        // Finish Parsing Input

        /* Okay, this seems like nonstandard DP

            But basically

            Process it in a zigzag fashion

            When we go right:
                - see if there are any current ends there (to start, there will only be one
                - if any current does end there, then "temporarily" go as far right as possible, adding to an OST as we go

                - additionally, see if there are any bends there. When we do, add to dp[i][j] if it's valid
                    - we need the OST for reasons :)
        */

        /* I think we gotta do some more setup :D */

        Fraction[][][] cache = new Fraction[n][2][];
        // cache[n][n][0] : fwaccshuns

        for (int i = 0; i < n; i += 1) {
            cache[i][0] = new Fraction[n - i];
            cache[i][1] = new Fraction[n - i];
            int hsum = 0;
            int vsum = 0;
            for (int j = i; j < n; j += 1) {
                hsum += first[j];
                vsum += second[j];

                cache[i][0][j - i] = new Fraction(hsum, j - i + 1);
                cache[i][0][j - i].tuple = j;

                cache[i][1][j - i] = new Fraction(vsum, j - i + 1);
                cache[i][1][j - i].tuple = j;
            }

            Arrays.sort(cache[i][0]);
            Arrays.sort(cache[i][1]);

            // first needs to be leq second
        }

        int[][] dp = new int[n + 1][n + 1]; // dp[i][j] : number of valid sequences that end before (j, i) modulo ofc

        dp[0][0] = 1;

        for (int j = 0; j < n; j += 1) { // starting index of the second one - the row number

            Fraction[] seconds = cache[j][1]; // hmm... possibly something with reverse prefix sums
            int[][] nextJS = new int[n][n - j + 1]; // rps sigh; that's why we need

            for (int i = 0; i < n; i += 1) { // starting index of the second one

                int firstIndex = 0;
                int secondIndex = 0;
                Fraction[] firsts = cache[i][0]; // we need firsts to be less than or equal to seconds

                while (firstIndex + secondIndex < firsts.length + seconds.length) {
                    if (secondIndex == seconds.length || (firstIndex != firsts.length && firsts[firstIndex].compareTo(seconds[secondIndex]) <= 0)) {
                        nextJS[firsts[firstIndex].tuple][secondIndex] += dp[i][j];
                        if (nextJS[firsts[firstIndex].tuple][secondIndex] >= MODULO) {
                            nextJS[firsts[firstIndex].tuple][secondIndex] -= MODULO;
                        }
                        firstIndex += 1;
                    } else {
                        secondIndex += 1;
                    }
                }

                // now we treat it as the closingIndex of the first one

                int[] unraveled = new int[n - j];

                unraveled[0] = nextJS[i][0];

                for (int k = 1; k < seconds.length; k += 1) {
                    unraveled[k] = unraveled[k - 1] + nextJS[i][k];
                    if (unraveled[k] >= MODULO) {
                        unraveled[k] -= MODULO;
                    }
                }

                for (int k = 0; k < seconds.length; k += 1) {
                    dp[i + 1][seconds[k].tuple + 1] += unraveled[k];
                    if (dp[i + 1][seconds[k].tuple + 1] >= MODULO) {
                        dp[i + 1][seconds[k].tuple + 1] -= MODULO;
                    }
                }

            }
        }

        System.out.println(dp[n][n]);

    }

    public final static int MODULO = 1000000007; // 10^9 + 7

    public static final String problemName = "Problem";
    public static final boolean recent = true; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;

    /* First problem I admit defeat on

     - got to the n^3 logn optimization (snake rather than do the entire quadrant)
     - however, I tried to implement as an order statistic tree. Note for future: use arrays lol
         - these were way too messy to work with, and converting to the n^3 solution would NOT have been possible
     - had to look at solution to see the insight: pre-compute with arrays
         - from here, it was fairly quickly to see the double computation I was overlooking: rather than going up
            the first list in sorted order and computing numSmaller() each time, which would take O(n logn) time,
            I could just zip-traverse the first and second lists' sorted orders in a zip, taking O(n) time
             - this required reverse prefix sums to implement in O(n) rather than O(n^2) */
}