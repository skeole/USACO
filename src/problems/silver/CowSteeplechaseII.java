package problems.silver;

import java.io.*;
import java.util.*;

public class CowSteeplechaseII { // 2019 Open Silver 2

    public static class Line {
        final long x1, y1, x2, y2;

        final long cx1, cy1, cx2, cy2;

        public Line(long x1, long y1, long x2, long y2) {

            x1 <<= 2;
            y1 <<= 2;
            x2 <<= 2;
            y2 <<= 2; // just to make calculations easier since its all even numbers :)
            if (x1 < x2 || (x1 == x2 && y1 < y2)) {
                this.x1 = x1;
                this.y1 = y1;
                this.x2 = x2;
                this.y2 = y2;
            } else {
                this.x1 = x2;
                this.y1 = y2;
                this.x2 = x1;
                this.y2 = y1;
            } // order the points nicely :)


            long cx = (x1 + x2) / 2;
            long cy = (y1 + y2) / 2;
            long dx = (x2 - x1) / 2;
            long dy = (y2 - y1) / 2;

            this.cx1 = cx - dy;
            this.cy1 = cy + dx;

            this.cx2 = cx + dy;
            this.cy2 = cy - dx;
        }

        public int side(long x, long y) {
            // values for x, y: between 0 and 2 billion
            // difference also between 0 and 2 billion
            // um
            // ok that's annoying

            long fd = (x - cx1) * (x - cx1) + (y - cy1) * (y - cy1);
            // LMAO BECAUSE LONGS ARE 2^63 AND INTS ARE 2^31 IT'S OKAY
            // LMAOOOOOOOOOOOO
            long sd = (x - cx2) * (x - cx2) + (y - cy2) * (y - cy2);

            return Long.compare(fd, sd);
        }


        public boolean intersects(Line other) {

            int side1 = side(other.x1, other.y1);
            int side2 = side(other.x2, other.y2);

            if (side1 == 0 && side2 == 0) { // collinear
                // x1, other.x1, other.x2, x2
                if (x1 == x2) { // vertical
                    return y1 <= other.y2 && other.y1 <= y2;
                } else {
                    return x1 <= other.x2 && other.x1 <= x2;
                }
            }

            return (this.side(other) == 0 && other.side(this) == 0);
        }

        public int side(Line other) {
            int side1 = side(other.x1, other.y1);
            int side2 = side(other.x2, other.y2);
            if (side1 == side2) {
                return side1;
            } else {
                return 0;
            }
        }

    }

    public static Line findIntersection(ArrayList<Line> lines) {
        if (lines.size() < 2) {
            return null;
        }
        Line pivot = lines.get(0);
        ArrayList<Line> sideOne = new ArrayList<>();
        ArrayList<Line> sideTwo = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            int side = pivot.side(lines.get(i));
            if (pivot.intersects(lines.get(i))) {
                return pivot;
            }
            if (side != -1) {
                sideOne.add(lines.get(i));
            }
            if (side != 1) {
                sideTwo.add(lines.get(i));
            }
        }
        Line partOne = findIntersection(sideOne);
        if (partOne != null) {
            return partOne;
        }
        return findIntersection(sideTwo);
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
        ArrayList<Line> lines = new ArrayList<>();

        for (int i = 0; i < n; i += 1) {
            line = new StringTokenizer(inputReader.readLine());
            lines.add(new Line(
                    Long.parseLong(line.nextToken()),
                    Long.parseLong(line.nextToken()),
                    Long.parseLong(line.nextToken()),
                    Long.parseLong(line.nextToken())
            ));
        }

        ArrayList<Line> linesCopy = new ArrayList<>(lines);
        Collections.shuffle(lines);

        Line intersection = findIntersection(lines);
        int intersectionWith = -1;
        int answer = -1;
        int numberOfIntersections = 0;

        for (int i = 0; i < linesCopy.size(); i += 1) {
            if (intersection == linesCopy.get(i)) {
                answer = i + 1;
            } else if (intersection.intersects(linesCopy.get(i))) {
                numberOfIntersections += 1;
                intersectionWith = i;
            }
        }

        if (numberOfIntersections == 0) {
            throw new IllegalStateException();
        }

        if (numberOfIntersections == 1) { // have to check the other one - it could be

            // yeah obvs more likely to be el otro
            intersection = linesCopy.get(intersectionWith);
            numberOfIntersections = 0;
            for (int i = 0; i < linesCopy.size(); i += 1) {
                if (intersection != linesCopy.get(i)) {
                    if (intersection.intersects(linesCopy.get(i))) {
                        numberOfIntersections += 1;
                    }
                }
            }
            if (numberOfIntersections == 1) {
                answer = Math.min(answer, intersectionWith + 1);
            } else {
                answer = intersectionWith + 1;
            }
        }

        inputReader.close();

        // Finish Parsing Input

        // Begin Writing Output

        BufferedWriter outputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        outputWriter.write("" + answer);
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

    public static final String problemName = "cowjump";
    public static final boolean recent = false; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}