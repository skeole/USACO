package problems.platinum;

import java.io.*;
import java.util.*;

public class BalancedSubsets { // 2021 Open Platinum 3

    public static final int modulo = 1000000007;

    public static int modsum(int... numbers) {
        int sum = 0;
        for (int number : numbers) {
            if (number < 0) {
                number += modulo;
            }
            if (number < 0) {
                number += modulo;
            }
            if (number >= modulo) {
                number -= modulo;
            }
            sum += number;
            if (sum > modulo) {
                sum -= modulo;
            }
        }
        return sum;
    }

    public static int rangesum(int[][][] dp1, int i, int a, int b, int c, int d) {
        return modsum(dp1[b + 1][d + 1][i], -dp1[a][d + 1][i], -dp1[b + 1][c][i], dp1[a][c][i]);
    }
    // then, the sum will be dp1[b + 1][d + 1] - dp1[a][d + 1] - dp1[b + 1][c] + dp1[a][c]

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

        ArrayList<Integer>[] grid = new ArrayList[n];

        for (int i = 0; i < n; i += 1) {
            grid[i] = new ArrayList<Integer>();
            char[] array = new StringTokenizer(inputReader.readLine()).nextToken().toCharArray();
            for (int j = 0; j < n; j += 1) {
                if (array[j] == 'G') {
                    if (j == 0 || array[j - 1] == '.') {
                        grid[i].add(j);
                    }
                    if (j == n - 1 || array[j + 1] == '.') {
                        grid[i].add(j);
                    }
                }
            }
        }

        inputReader.close();

        // Finish Parsing Input

        int count = 0;

        int from;
        int to;
        int[][][] dp = new int[n][n][4];
        int[][][] dp1 = new int[n + 1][n + 1][4];

            // dp[n][i][j][k] : number of sequences beginning at i, ending at j, at level n
            // k = 0: no restrictions; k = 1: left nonincreasing; k = 2: right nonincreasing; k = 3: both nonincreasing

        // need to find way to get sum of dp[n][a..b][c..d][i] for any pair of a, b and c, d
        // oh this is easy lol
        // wlog, dp1[i][j] = sum of dp[x][y] such that x < i and y < j
            // can be filled in O(n^2) time
        // then, the sum will be dp1[b + 1][d + 1] - dp1[a][d + 1] - dp1[b + 1][c] + dp1[a][c]

        for (int i = 0; i < n; i += 1) { // yeah we can't iterate, even with this optimization... maybe?

            for (int j = 0; j < grid[i].size(); j += 2) {
                from = grid[i].get(j);
                to = grid[i].get(j + 1);

                for (int k = from; k <= to; k += 1) {
                    for (int l = k; l <= to; l += 1) {
                        dp[k][l][0] = 1; // stuff starting here
                    }
                }

                if (i == 0) {
                    continue;
                }

                for (int k = from; k <= to; k += 1) {
                    for (int l = k; l <= to; l += 1) {
                        // 0: dp[n-1][k..l][k..l][0]
                        dp[k][l][0] = modsum(dp[k][l][0],
                                rangesum(dp1, 0, k, l, k, l)
                        );
                        // 1: dp[n-1][0..k-1][k..l][0] plus dp[n-1][0..k][k..l][1]
                        dp[k][l][1] = modsum(dp[k][l][1],
                                rangesum(dp1, 0, 0, k - 1, k, l),
                                rangesum(dp1, 1, 0, k, k, l)
                        );
                        // 2: dp[n-1][k..l][l+1..n-1][0] plus dp[n-1][k..l][l..n-1][2]
                        dp[k][l][2] = modsum(dp[k][l][2],
                                rangesum(dp1, 0, k, l, l + 1, n - 1),
                                rangesum(dp1, 2, k, l, l, n - 1)
                        );
                        // 3: dp[n-1][0..k-1][l+1..n-1][0] plus dp[n-1][0..k][l+1..n-1][1]
                             // plus dp[n-1][0..k-1][l..n-1][2] plus dp[n-1][0..k][l..n-1][3]
                        dp[k][l][3] = modsum(dp[k][l][3],
                                rangesum(dp1, 0, 0, k - 1, l + 1, n - 1),
                                rangesum(dp1, 1, 0, k, l + 1, n - 1),
                                rangesum(dp1, 2, 0, k - 1, l, n - 1),
                                rangesum(dp1, 3, 0, k, l, n - 1)
                        );
                    }
                }
            }

            for (int j = 0; j < n; j += 1) {
                for (int k = 0; k < n; k += 1) {
                    for (int l = 0; l < 4; l += 1) {
                        dp1[j + 1][k + 1][l] = modsum(
                                dp[j][k][l],
                                dp1[j][k + 1][l],
                                dp1[j + 1][k][l],
                                -dp1[j][k][l]
                        );
                        dp[j][k][l] = 0;
                    }
                }
            }
            for (int j = 0; j < 4; j += 1) {
                count = modsum(count, dp1[n][n][j]);
            }
        }

        // Begin Writing Output

        BufferedWriter ouputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        ouputWriter.write("" + count);
        ouputWriter.newLine();

        ouputWriter.close();

        // Finish Writing Output

    }

    public static final String problemName = "Problem";
    public static final boolean recent = true; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}