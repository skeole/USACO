package problems.gold;

import java.io.*;
import java.util.*;

public class OohMooMilk { // 2025 Open Gold 3

    public static void main(String[] args) throws IOException {

        // Begin Parsing Input

        BufferedReader inputReader =
                submission ?
                        (recent ?
                                new BufferedReader(new InputStreamReader(System.in)) :
                                new BufferedReader(new FileReader(problemName + ".in"))) :
                        new BufferedReader(new FileReader("problemdata\\input.txt"));

        StringTokenizer line = new StringTokenizer(inputReader.readLine()); // need to do this for every single line

        int n = Integer.parseInt(line.nextToken()); // number of numbers
        long d = Integer.parseInt(line.nextToken()); // number of moves

        line = new StringTokenizer(inputReader.readLine());

        int a = Integer.parseInt(line.nextToken());
        int b = Integer.parseInt(line.nextToken());

        line = new StringTokenizer(inputReader.readLine());

        long[] array = new long[n];

        for (int i = 0; i < n; i += 1) {
            array[i] = Integer.parseInt(line.nextToken());
        }

        Arrays.sort(array);

        // idea: rather than doing both adding and subtracting, only simulate subtracting
        // --> store actual values for before, between value
        // for after, only store differences

        ArrayDeque<Long> before = new ArrayDeque<>();
        ArrayDeque<Long> after = new ArrayDeque<>();
        ArrayDeque<Long> afterlel = new ArrayDeque<>();

        for (int i = 0; i < a - b; i += 1) {
            before.add(array[n - a + i]);
        }

        for (int i = a - b; i < a; i += 1) {
            after.add(array[n - a + i]);
            afterlel.add(array[n - a + i]);
        }

        long betweenValue = after.getFirst();
        long betweenLength = 0;
        long betweenIndex = 0;

        while (!before.isEmpty() && before.getLast().equals(betweenValue)) {
            before.removeLast();
            betweenLength += 1;
        }

        while (!before.isEmpty() && before.getLast().equals(betweenValue - 1)) {
            before.removeLast();
            betweenIndex += 1;
            betweenLength += 1;
        }

        while (!after.isEmpty() && after.getFirst().equals(betweenValue)) {
            after.removeFirst();
            afterlel.removeFirst();
            betweenLength += 1;
        }

        betweenValue -= 1;

        after.addFirst(betweenValue);
        Long[] temp = after.toArray(new Long[0]);
        after.clear();
        for (int i = 1; i < temp.length; i += 1) {
            after.addLast(temp[i] - temp[i - 1]);
        }


        long movesRemaining = d;
        // note: for now, we're only simulating the decreases, imagining all the increases are coming at the end
        // this is why we store as a separate value
        while (movesRemaining > 0) {
            long movesToAssimilateBefore = Long.MAX_VALUE;
            if (!before.isEmpty()) {
                movesToAssimilateBefore = (
                        (betweenValue - before.getLast()) * betweenLength - betweenIndex +
                        b - after.size() - 1
                ) / (b - after.size()); // NOTE: TRUNCATE NOT TRUE DIVISION
            }

            long movesToAssimilateAfter = Long.MAX_VALUE;
            if (!after.isEmpty() && before.size() != a - b) {
                movesToAssimilateAfter = (
                        (after.getFirst() - 2) * betweenLength + betweenIndex
                ) / (a - b - before.size()) + 1; // NOTE: add one bc have to do one more if perfect
            }

            long movesToSimulate = Math.min(Math.min(movesToAssimilateBefore, movesToAssimilateAfter), movesRemaining);

            long betweenValueDecrease = ((b - after.size()) * movesToSimulate + betweenIndex) / betweenLength;
            // b * movesToAssimilate - betweenIndex
            // sigh ts too hard ~
            // if betweenIndex was 8 before, betweenLength is 10, and b * movesToSimulate = 18
            // --> go down by 1

            // hmmm... what if betweenIndex was 0 before, and now we decrease
            // yeah that's an edge case


            betweenIndex = ((b - after.size()) * movesToSimulate + betweenIndex) % betweenLength;
            // 0 before
            // should be 0 --> 2

            betweenValue -= betweenValueDecrease;

            if (!after.isEmpty()) {
                after.addFirst(after.removeFirst() - movesToSimulate + betweenValueDecrease); // hmm
            }

            // number of full integer multiples we skip over
            // each one causes us to decrease by one

            while (!before.isEmpty() && before.getLast() == betweenValue) {
                before.removeLast();
                betweenIndex += 1;
                betweenLength += 1;
            }

            if (!after.isEmpty() && 0 >= after.getFirst()) {
                throw new IllegalStateException();
            }

            if (!after.isEmpty() && 1 >= after.getFirst()) {
                after.removeFirst();
                afterlel.removeFirst();
                betweenLength += 1;
                while (!after.isEmpty() && (0 == after.getFirst())) {
                    after.removeFirst();
                    afterlel.removeFirst();
                    betweenLength += 1;
                    // oh i see this is the issue
                }

                if (!after.isEmpty()) {
                    after.addFirst(after.removeFirst() + 1);
                }
            }

            // add before and after
            movesRemaining -= movesToSimulate;
        }

        int index = n - a;
        for (long i : before) {
            array[index] = i + d;
            index += 1;
        }

        for (long i = 0; i < betweenIndex; i += 1) {
            array[index] = betweenValue + d;
            index += 1;
        }

        for (long i = betweenIndex; i < betweenLength; i += 1) {
            array[index] = betweenValue + 1 + d;
            index += 1;
        }

        for (long i : afterlel) {
            array[index] = i;
            index += 1;
        }

        inputReader.close();

        // Finish Parsing Input

        long result = 0;
        for (long i : array) {
            result = modsum(result, modprod(i, i));
        }

        // Begin Writing Output

        BufferedWriter outputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        outputWriter.write("" + result);
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
    public static final boolean submission = true;

    // lmfao holy cancer :sob:
}