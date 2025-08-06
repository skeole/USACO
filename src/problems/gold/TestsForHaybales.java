package problems.gold;

import java.io.*;
import java.util.*;

public class TestsForHaybales {

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

        line = new StringTokenizer(inputReader.readLine());
        int[] x = new int[n];

        for (int i = 0; i < n; i += 1) {
            x[i] = Integer.parseInt(line.nextToken()) - 1;
        }

        inputReader.close();

        // Finish Parsing Input

        /* Okay. When is a difference of one NOT sufficient?

            Lets look at the "bucket sizes" that each one must make up
            sum of bucket sizes of descendants
           */

        long[] bucketSizes = new long[n];
        long k = 0;
        for (int i = 0; i < n; i += 1) {
            bucketSizes[i] += 1;
            k = Math.max(k, bucketSizes[i]);
            if (x[i] != i) {
                bucketSizes[x[i]] += bucketSizes[i];
            }
        }

        ArrayList<Integer>[] reverseRelation = new ArrayList[n];
        for (int i = 0; i < n; i += 1) {
            reverseRelation[i] = new ArrayList<>();
        }
        for (int i = 0; i < n; i += 1) {
            if (x[i] != i) {
                reverseRelation[x[i]].add(i);
            }
        }

        long[] xs = new long[n];
        xs[n - 1] = 1000000000000000000L; // lol

        for (int i = n - 1; i >= 0; i -= 1) {
            if (xs[i] == 0) {
                xs[i] = xs[i + 1] - (k + 1);
            }
            long ctr = 0;
            for (int j : reverseRelation[i]) {
                xs[j] = xs[i] - k + ctr;
                ctr += bucketSizes[j];
            }
        }

        long first = xs[0];
        for (int i = 0; i < n; i += 1) {
            xs[i] -= first;
        }

        // Begin Writing Output

        BufferedWriter outputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        outputWriter.write(String.valueOf(k));
        outputWriter.newLine();
        for (long l : xs) {
            outputWriter.write(String.valueOf(l));
            outputWriter.newLine();
        }

        outputWriter.close();

        // Finish Writing Output

    }

    public static final String problemName = "Problem";
    public static final boolean recent = true; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}