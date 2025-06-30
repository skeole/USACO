package problems.gold;

import java.io.*;
import java.util.*;

public class ElectionQueries { // 2025 Open Gold 2

    public static class TreeMultiSet implements Iterable<Integer> {
        public TreeMap<Integer, Integer> map = new TreeMap<>();

        public void add(int i) {
            if (!map.containsKey(i)) {
                map.put(i, 0);
            }
            map.put(i, map.get(i) + 1);
        }

        public void remove(int i) {
            map.put(i, map.get(i) - 1);
            if (map.get(i) == 0) {
                map.remove(i);
            }
        }

        public Iterator<Integer> iterator() {
            return map.keySet().iterator();
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
        int q = Integer.parseInt(line.nextToken()); // number of queries

        int[] votes = new int[n];
        int[] frequencies = new int[n];
        ArrayList<TreeSet<Integer>> mountain = new ArrayList<>();
        TreeMultiSet ascent = new TreeMultiSet();
        TreeMultiSet descent = new TreeMultiSet();

        line = new StringTokenizer(inputReader.readLine());

        for (int i = 0; i < n; i += 1) {
            int vote = Integer.parseInt(line.nextToken()) - 1;
            votes[i] = vote;
            frequencies[vote] += 1;
            if (mountain.size() < frequencies[vote]) {
                mountain.add(new TreeSet<>());
            }

            TreeSet<Integer> currentLayer = mountain.get(frequencies[vote] - 1);
            if (!currentLayer.isEmpty()) {
                ascent.remove(currentLayer.first());
                descent.remove(currentLayer.last());
            }
            currentLayer.add(vote);
            ascent.add(currentLayer.first());
            descent.add(currentLayer.last());
        }

        BufferedWriter outputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));


        for (int i = 0; i < q; i += 1) {
            line = new StringTokenizer(inputReader.readLine());
            int cow = Integer.parseInt(line.nextToken()) - 1;
            int oldVote = votes[cow];

            TreeSet<Integer> oldLayer = mountain.get(frequencies[oldVote] - 1);
            ascent.remove(oldLayer.first());
            descent.remove(oldLayer.last());
            oldLayer.remove(oldVote);
            if (oldLayer.isEmpty()) {
                mountain.remove(mountain.size() - 1);
            } else {
                ascent.add(oldLayer.first());
                descent.add(oldLayer.last());
            }
            frequencies[oldVote] -= 1;

            int newVote = Integer.parseInt(line.nextToken()) - 1;
            votes[cow] = newVote;
            frequencies[newVote] += 1;
            if (mountain.size() < frequencies[newVote]) {
                mountain.add(new TreeSet<>());
            }

            TreeSet<Integer> currentLayer = mountain.get(frequencies[newVote] - 1);
            if (!currentLayer.isEmpty()) {
                ascent.remove(currentLayer.first());
                descent.remove(currentLayer.last());
            }
            currentLayer.add(newVote);
            ascent.add(currentLayer.first());
            descent.add(currentLayer.last());

            int maximum = mountain.size();

            int largestDifference = 0;

            Iterator<Integer> ascentIterator = ascent.iterator();
            Iterator<Integer> descentIterator = descent.iterator();

            int currentAscent = ascentIterator.next();
            int currentDescent = descentIterator.next();

            while (true) {
                if (frequencies[currentAscent] + frequencies[currentDescent] >= maximum) {
                    largestDifference = Math.max(currentDescent - currentAscent, largestDifference);
                    if (descentIterator.hasNext()) {
                        currentDescent = descentIterator.next();
                    } else {
                        break;
                    }
                } else {
                    if (ascentIterator.hasNext()) {
                        currentAscent = ascentIterator.next();
                    } else {
                        break;
                    }
                }
            }

            outputWriter.write("" + largestDifference);
            outputWriter.newLine();
        }

        inputReader.close();
        outputWriter.close();

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
}