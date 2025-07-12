package problems.gold;

import java.io.*;
import java.util.*;

public class Cowreography {

    public static class ArrayListTuple {
        public final ArrayList<Integer> first;
        public final ArrayList<Integer> second;

        public ArrayListTuple(ArrayList<Integer> first, ArrayList<Integer> second) {
            this.first = new ArrayList<>(first);
            this.second = new ArrayList<>(second);
        }

        @Override
        public String toString() {
            return "(" + first.toString() + ", " + second.toString() + ")";
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
        int k = Integer.parseInt(line.nextToken()); // max jumps per move

        int[] cows = new int[n];

        char[] initialPositions = new StringTokenizer(inputReader.readLine()).nextToken().toCharArray();
        char[] finalPositions = new StringTokenizer(inputReader.readLine()).nextToken().toCharArray();
        for (int i = 0; i < n; i += 1) {
            if (initialPositions[i] == '1') {
                cows[i] += 1;
            }
            if (finalPositions[i] == '1') {
                cows[i] += 2; // lmao
            }
        }

        inputReader.close();

        ArrayList<ArrayListTuple> groups = new ArrayList<>();

        int difference = 0;
        ArrayList<Integer> currentUps = new ArrayList<>();
        ArrayList<Integer> currentDowns = new ArrayList<>();
        for (int i = 0; i < n; i += 1) { // yeah this finna be light work :DDDD :o or not :(
            if (cows[i] == 1) {
                difference += 1;
                currentUps.add(i);
                if (difference == 0) {
                    groups.add(new ArrayListTuple(currentDowns, currentUps)); // lol I had them switched but it ok
                    currentUps.clear();
                    currentDowns.clear();
                }
            } else if (cows[i] == 2) {
                difference -= 1;
                currentDowns.add(i);
                if (difference == 0) {
                    groups.add(new ArrayListTuple(currentUps, currentDowns)); // oh I see. No I don't I lied
                    currentUps.clear();
                    currentDowns.clear();
                }
            } else if (cows[i] == 3) {
                if (difference != 0) {
                    currentUps.add(i);
                    currentDowns.add(i);
                }
            }
        }

        long moves = 0;

        for (ArrayListTuple group : groups) {
            // ok so basically
            ArrayList<Integer> lower = group.first;
            ArrayList<Integer> upper = group.second;
            int lowerCounter = lower.size() - 1;
            int upperCounter = upper.size() - 1;
            TreeSet<Integer> runningListKeySet = new TreeSet<>(); // lmfao
            TreeMap<Integer, ArrayDeque<Integer>> runningList = new TreeMap<>();
            // key : modulus; value: yeah
            while (lowerCounter >= 0) {
                while (upperCounter >= 0 && upper.get(upperCounter) >= lower.get(lowerCounter)) {
                    if (!runningList.containsKey(upper.get(upperCounter) % k)) {
                        runningListKeySet.add(upper.get(upperCounter) % k);
                        runningList.put(upper.get(upperCounter) % k, new ArrayDeque<>());
                    }
                    runningList.get(upper.get(upperCounter) % k).add(upper.get(upperCounter));
                    upperCounter -= 1;
                }
                int removeFrom = runningListKeySet.last();
                if (!runningListKeySet.headSet(lower.get(lowerCounter) % k, true).isEmpty()) {
                    removeFrom = runningListKeySet.floor(lower.get(lowerCounter) % k); // not quite swag surfin'
                }
                ArrayDeque<Integer> valuesAtModulus = runningList.get(removeFrom);
                moves += (valuesAtModulus.pop() - lower.get(lowerCounter) + k - 1) / k; // + k - 1 --> ceil
                if (valuesAtModulus.isEmpty()) {
                    runningList.remove(removeFrom);
                    runningListKeySet.remove(removeFrom);
                }
                lowerCounter -= 1;
            }
        }

        // Finish Parsing Input

        // for each 1 in the original, we can determine if its final position will be to the left or to the right
            // based on

        // Begin Writing Output

        BufferedWriter outputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        outputWriter.write("" + moves);
        outputWriter.newLine();

        outputWriter.close();

        // Finish Writing Output

    }

    public static final String problemName = "Problem";
    public static final boolean recent = true; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}