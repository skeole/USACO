package problems.silver;

import java.io.*;
import java.util.*;

public class Meetings { // 2019 December Silver 2

    // 10/10 problem

    public static class Tuple {
        final int weight;
        final int direction;
        public Tuple(int weight, int direction) {
            this.weight = weight;
            this.direction = direction;
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
        int n = Integer.parseInt(line.nextToken()); // number of cows
        int l = Integer.parseInt(line.nextToken()); // number of pairs

        TreeMap<Integer, Tuple> cows = new TreeMap<>();
        // key : x
        // value : (weight, direction)

        int numberMovingRight = 0;
        int tw = 0;

        for (int i = 0; i < n; i += 1) {
            line = new StringTokenizer(inputReader.readLine());
            int w = Integer.parseInt(line.nextToken());
            tw += w;
            int x = Integer.parseInt(line.nextToken());
            int d = Integer.parseInt(line.nextToken());
            if (d > 0) {
                numberMovingRight += 1;
            }
            cows.put(x, new Tuple(w, d));
        }

        inputReader.close();

        int[][] cowsByX = new int[n][3];
        int i = 0;
        for (Map.Entry<Integer, Tuple> cow : cows.entrySet()) {
            cowsByX[i][0] = cow.getKey();
            cowsByX[i][1] = cow.getValue().weight;
            cowsByX[i][2] = cow.getValue().direction;
            i += 1;
        }

        int[][] cowsByXMovingRight = new int[numberMovingRight][2]; // sorted by closest to leave - so by decreasing x
        int[][] cowsByXMovingLeft = new int[n - numberMovingRight][2];

        i = numberMovingRight;
        int j = 0;
        for (int[] cow : cowsByX) {
            if (cow[2] == 1) { // moving right
                i -= 1;
                cowsByXMovingRight[i][0] = cow[0];
                cowsByXMovingRight[i][1] = cow[1];
            } else {
                cowsByXMovingLeft[j][0] = cow[0];
                cowsByXMovingLeft[j][1] = cow[1];
                j += 1;
            }
        }

        int[][] cowsByReachTime = new int[n][2]; // time to reach, weight, direction
        i = 0;
        j = 0;
        while (i < numberMovingRight || j < n - numberMovingRight) {
            if (i == numberMovingRight || (j != n - numberMovingRight && cowsByXMovingLeft[j][0] <= l - cowsByXMovingRight[i][0])) {
                cowsByReachTime[i + j][0] = cowsByXMovingLeft[j][0];
                cowsByReachTime[i + j][1] = cowsByX[j][1];
                j += 1;
            } else {
                cowsByReachTime[i + j][0] = l - cowsByXMovingRight[i][0];
                cowsByReachTime[i + j][1] = cowsByX[n - i - 1][1];
                i += 1;
            }
        }

        i = 0;
        int aw = 0;
        while (aw * 2 < tw || (i != cowsByReachTime.length - 1 && cowsByReachTime[i][0] == cowsByReachTime[i + 1][0])) {
            aw += cowsByReachTime[i][1];
            i += 1;
        }
        int time = cowsByReachTime[i - 1][0];

        int[] righters = new int[cowsByXMovingRight.length];
        for (int k = 0; k < cowsByXMovingRight.length; k += 1) {
            righters[k] = cowsByXMovingRight[cowsByXMovingRight.length - k - 1][0];
        }

        int[] lefters = new int[cowsByXMovingLeft.length];
        for (int k = 0; k < cowsByXMovingLeft.length; k += 1) {
            lefters[k] = cowsByXMovingLeft[k][0];
        }

        i = 0;
        j = 0;
        int number = 0;
        for (int righter : righters) {
            while (j < lefters.length && lefters[j] - righter <= 2 * time) {
                j += 1;
            }
            while (i < lefters.length && lefters[i] < righter) {
                i += 1;
            }
            number += j - i;
        }


        // Finish Parsing Input

        // Obvious note: relative order of cows is constant
        // Another note: can look at the straight lines to sww which one is
        // oh this is not too bad lol
        // Another obvious note: once we know the time its pretty easy to see
        // jfc
        // common usaco W

        // figure out when
            // order cows by x coordinate
            // order cows by time to leave
                // this is x coordinate if direction = -1, or L - x coordinate if direction = 1
            // then, following that rule, order the 1st list by 2nds order
                // if the next cow is the list has direction 1, pull from the right side of the list; or vice versa
                // prefix sums --> gg
            // so we can do this in O(n logn) time

        // intersecions from time
        // 2 lines will intersect iff
            // opposite directions
            // x coordinate is close enough
            // can probably do a sliding window analysis
            // O(n)
            // yay

        // Begin Writing Output

        BufferedWriter outputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        outputWriter.write(String.valueOf(number));
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

    public static final String problemName = "meetings";
    public static final boolean recent = false; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}