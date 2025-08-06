package problems.gold;

import java.io.*;
import java.util.*;

public class ModernArtIII { // 2021 February Gold 2

    public static int fillDP(ArrayList<Integer> s, int first, int last, int base, int[][][] dp) {

        // sigh. choose one to be the last ?
        if (first > last) {
            throw new IllegalArgumentException();
        }

        if (dp[first][last][base] != 0) {
            return dp[first][last][base];
        }

        if (first == last) {
            if (base == s.get(first)) {
                dp[first][last][base] = 0;
                return 0;
            } else {
                dp[first][last][base] = 1;
                return 1;
            }
        }

        int min = 100000;

        min = Math.min(min, (base == s.get(first) ? 0 : 1) + fillDP(s, first + 1, last, base, dp));
        min = Math.min(min, (base == s.get(last) ? 0 : 1) + fillDP(s, first, last - 1, base, dp));

        for (int i = first + 1; i < last; i += 1) {
            if (Objects.equals(s.get(first), s.get(i))) {
                min = Math.min(min, (base == s.get(i) ? 0 : 1) + fillDP(s, first + 1, i - 1, s.get(i), dp) + fillDP(s, i + 1, last, base, dp));
            }
            if (Objects.equals(s.get(i), s.get(last))) {
                min = Math.min(min, (base == s.get(i) ? 0 : 1) + fillDP(s, first, i - 1, base, dp) + fillDP(s, i + 1, last - 1, s.get(i), dp));
            }
        }

        if (Objects.equals(s.get(first), s.get(last))) {
            min = Math.min(min, 1 + fillDP(s, first + 1, last - 1, s.get(first), dp));
        }

        dp[first][last][base] = min;

        return min;
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

        int lastColor = -1;
        ArrayList<Integer> colors = new ArrayList<>();
        HashMap<Integer, Integer> uniqueColors = new HashMap<>();

        line = new StringTokenizer(inputReader.readLine());
        for (int i = 0; i < n; i += 1) {
            int temp = Integer.parseInt(line.nextToken());
            if (!uniqueColors.containsKey(temp)) {
                uniqueColors.put(temp, uniqueColors.size() + 1);
            }

            if (temp != lastColor) {
                lastColor = temp;
                colors.add(uniqueColors.get(temp));
            }
        }

        int res = fillDP(colors, 0 ,colors.size() - 1, 0, new int[colors.size()][colors.size()][uniqueColors.size() + 1]);

        inputReader.close();

        // Finish Parsing Input

        // Begin Writing Output

        BufferedWriter outputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        outputWriter.write(String.valueOf(res));
        outputWriter.newLine();

        outputWriter.close();

        // Finish Writing Output

    }

    public static final String problemName = "Problem";
    public static final boolean recent = true; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}