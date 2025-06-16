package problems.bronze;

import java.io.*;
import java.util.*;


public class MilkingOrder { // 2018 Open Bronze 2

    public static void main(String[] args) throws FileNotFoundException, IOException {

        // Begin Parsing Input

        BufferedReader inputReader =
                submission ?
                        (recent ?
                                new BufferedReader(new InputStreamReader(System.in)) :
                                new BufferedReader(new FileReader(problemName + ".in"))) :
                        new BufferedReader(new FileReader("problemdata\\input.txt"));

        StringTokenizer line = new StringTokenizer(inputReader.readLine()); // need to do this for every single line
        int n = Integer.parseInt(line.nextToken()); // number of cows
        int m = Integer.parseInt(line.nextToken()); // length of hierarchy
        int k = Integer.parseInt(line.nextToken()); // number of demands

        int[] cowsToPositions = new int[n]; // cow i -> position cow[i]. 0 -> uninitialized, indexed starting at 1
        int[] positionsToCows = new int[n]; // this one is index by one... lmao
        ArrayList<Integer> hierarchyBefore = new ArrayList<>();
        ArrayList<Integer> hierarchyAfter = new ArrayList<>();
        boolean encounteredZero = false;

        for (int i = 0; i < n; i += 1) {
            cowsToPositions[i] = -1;
            positionsToCows[i] = -1;
        }

        line = new StringTokenizer(inputReader.readLine());
        for (int i = 0; i < m; i += 1) {
            int cow = Integer.parseInt(line.nextToken()) - 1;
            if (cow == 0) {
                encounteredZero = true;
            } else if (!encounteredZero) {
                hierarchyBefore.add(cow);
            } else {
                hierarchyAfter.add(cow);
            }
        }

        if (!encounteredZero) {
            hierarchyAfter = hierarchyBefore;
            hierarchyBefore = new ArrayList<>();
        }

        for (int i = 0; i < k; i += 1) {
            line = new StringTokenizer(inputReader.readLine());
            int cow = Integer.parseInt(line.nextToken()) - 1;
            int position = Integer.parseInt(line.nextToken()) - 1;
            cowsToPositions[cow] = position;
            positionsToCows[position] = cow;
        }

        inputReader.close();

        // Finish Parsing Input

        int minCounter = 0;
        for (int i = 0; i < hierarchyBefore.size(); i += 1) {
            if (cowsToPositions[hierarchyBefore.get(i)] != -1) { // already assigned
                minCounter = cowsToPositions[hierarchyBefore.get(i)] + 1;
            } else {
                while (positionsToCows[minCounter] != -1) {
                    minCounter += 1;
                }
                positionsToCows[minCounter] = hierarchyBefore.get(i);
                cowsToPositions[hierarchyBefore.get(i)] = minCounter;
                minCounter += 1;
            }
        }

        int maxCounter = n - 1;
        for (int i = hierarchyAfter.size() - 1; i >= 0; i -= 1) {
            if (cowsToPositions[hierarchyAfter.get(i)] != -1) { // already assigned
                maxCounter = cowsToPositions[hierarchyAfter.get(i)] - 1;
            } else {
                while (positionsToCows[maxCounter] != -1) {
                    maxCounter -= 1;
                }
                positionsToCows[maxCounter] = hierarchyAfter.get(i);
                cowsToPositions[hierarchyAfter.get(i)] = maxCounter;
                maxCounter -= 1;
            }
        }

        int targetPosition = -1;
        int maximumPosition = encounteredZero ? maxCounter : n - 1;
        int minimumPosition = encounteredZero ? minCounter : 0;

        for (int i = maximumPosition; i >= minimumPosition; i -= 1) {
            if (positionsToCows[i] == -1) {
                targetPosition = i + 1;
            }
        }

        for (int i = 0; i < n; i += 1) {
            System.out.println("Cow " + (i + 1) + " position: " + (cowsToPositions[i] + 1));
        }
        System.out.println();
        for (int i = 0; i < n; i += 1) {
            System.out.println("Position " + (i + 1) + " cow: " + (positionsToCows[i] + 1));
        }

        // Begin Writing Output

        BufferedWriter ouputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        ouputWriter.write("" + targetPosition);

        ouputWriter.close();

        // Finish Writing Output

    }

    public static final String problemName = "milkorder";
    public static final boolean recent = false; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}