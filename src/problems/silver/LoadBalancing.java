package problems.silver;

import java.io.*;
import java.util.*;

public class LoadBalancing { // 2016 February Silver 2


    public static abstract class Point implements Comparable<Point> {
        final int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    public static class PointByX extends Point {

        public PointByX(int x, int y) {
            super(x, y);
        }

        public int compareTo(Point p) { // points can never be equal... can they?
            if (this.x < p.x) {
                return -1;
            } else if (this.x == p.x && this.y < p.y) {
                return -1;
            }
            return 1; // it is given all the ti, xi are distinct
        }
    }

    public static class PointByY extends Point {

        public PointByY(int x, int y) {
            super(x, y);
        }

        public int compareTo(Point p) { // points can never be equal... can they?
            if (this.y < p.y) {
                return -1;
            } else if (this.y == p.y && this.x < p.x) {
                return -1;
            }
            return 1; // it is given all the ti, xi are distinct
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

        TreeSet<Point> pbx = new TreeSet<>();
        TreeSet<Point> pby = new TreeSet<>();

        for (int i = 0; i < n; i += 1) {
            line = new StringTokenizer(inputReader.readLine());
            int x = Integer.parseInt(line.nextToken());
            int y = Integer.parseInt(line.nextToken());
            pbx.add(new PointByX(x, y));
            pby.add(new PointByY(x, y));
        }

        int m = 10000;

        int prev = -1;
        for (Point p : pbx) {
            if (pbx.higher(p) != null && p.x == pbx.higher(p).x) {
                continue;
            }
            prev = p.x;
            int division = p.x + 1;
            int numLessXGreaterY = 0;
            int numGreaterXGreaterY = 0;

            for (Point q : pby) {
                if (q.x < division) {
                    numLessXGreaterY += 1;
                } else {
                    numGreaterXGreaterY += 1;
                }
            }

            int numLessXLessY = 0;
            int numGreaterXLessY = 0;
            for (Point q : pby) {
                if (q.x < division) {
                    numLessXLessY += 1;
                    numLessXGreaterY -= 1;
                } else {
                    numGreaterXLessY += 1;
                    numGreaterXGreaterY -= 1;
                }
                if (pby.higher(q) != null && q.y == pby.higher(q).y) {
                    continue;
                }
                m = Math.min(m, Math.max(Math.max(numLessXGreaterY, numLessXLessY), Math.max(numGreaterXGreaterY, numGreaterXLessY)));
            }
        }

        // it's not just the median of medians right?
        // assume we partition as so
        /*      A | B
                - + -
                C | D

            Assume this is not the optimal solution. Wlog, let the optimal solution have A be the largest region
            If we move to the right: either A or C will increase
            If we move up: either C or D will increase
        * */

        inputReader.close();

        // Finish Parsing Input

        // Begin Writing Output

        BufferedWriter ouputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        ouputWriter.write(String.valueOf(m));
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

    public static final String problemName = "balancing";
    public static final boolean recent = false; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}