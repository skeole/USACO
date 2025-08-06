package problems.gold;

import java.io.*;
import java.util.*;

public class CountTheCows {

    public static long solve(long number, HashSet<Integer> excluding) {
        if (number < 0) {
            return 0;
        }
        // basically
        // we want to find the number of ternary strings abcd... < number, with all the "excluded" indices set to 0
        int index = 0;
        long answer = 1;
        long radix = 1;
        while (number > 0) {
            if (excluding.contains(index)) {
                if (number % 3 != 0) {
                    answer = radix; // sigh
                }
                number /= 3;
                index += 1;
                continue;
            }
            answer += (number % 3) * radix;
            radix *= 3;
            number /= 3;
            index += 1;
        }
        return answer;
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
        int q = Integer.parseInt(line.nextToken()); // number of queries
        long[] result = new long[q];

        for (int i = 0; i < q; i += 1) {
            line = new StringTokenizer(inputReader.readLine());
            long d = Long.parseLong(line.nextToken()); // lol
            long x = Long.parseLong(line.nextToken());
            long y = Long.parseLong(line.nextToken());

            if (x > y) { // in place swap
                x = x + y;
                y = x - y;
                x = x - y;
            }

            long halfDiff = y - x;

            if ((halfDiff & 1) == 1) {
                continue;
            }

            halfDiff >>= 1;

            int index = 0;
            HashSet<Integer> takenIndices = new HashSet<>();
            long smallerAdd = 0;
            long largerAdd = 0;
            long radix = 2;
            while (halfDiff > 0) {
                // either -1, 0, or +1
                if (halfDiff % 3 == 2) { // -1
                    takenIndices.add(index);
                    smallerAdd += radix;
                    halfDiff += 1;
                } else if (halfDiff % 3 == 1) {
                    takenIndices.add(index);
                    largerAdd += radix;
                    halfDiff -= 1;
                }
                halfDiff /= 3;
                radix *= 3;
                index += 1;
            }

            if (x - smallerAdd != y - largerAdd) {
                throw new IllegalArgumentException();
            }

            long lower = x - smallerAdd - 1;
            long upper = x - smallerAdd + d;
            // now basically, we want to find yeah

            result[i] = solve(upper, takenIndices) - solve(lower, takenIndices);
        }

        /* so the key idea is that x - y is constant
            for all k : when does (x, y) fit when x - y = k?
                remember: 1s always the same, 0s can be swapped with 2s
                this means the difference has to be represented as 2 * (+/-3^a +/-3^b +/- ... )
                - is there always a unique representation for that number? I think so...

            x - y = 0 : always
            x - y = 2 :

            sigh

            technically had to look at the test cases, but only for bugs - got the algorithm omo */



        inputReader.close();

        // Finish Parsing Input

        // Begin Writing Output

        BufferedWriter outputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        for (long l : result) {
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