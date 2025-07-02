package problems.silver;

import java.io.*;
import java.util.*;

public class TheWinningGene { // 2024 Open Silver 3 - horribly misplaced IMO

    static int[] genes;

    public static int differentiation(int a, int b) {
        int depth = 0;
        while (true) {
            if (b + depth == genes.length || genes[a + depth] < genes[b + depth]) {
                return depth + 1;
            }
            if (genes[a + depth] > genes[b + depth]) {
                return -(depth + 1);
            }
            depth += 1;
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

        int n = Integer.parseInt(line.nextToken()); // number of genes

        line = new StringTokenizer(inputReader.readLine());

        String s = line.nextToken();

        genes = new int[n];

        for (int i = 0; i < n; i += 1) {
            genes[i] = s.charAt(i) - 'A';
        }

        inputReader.close();

        // yeah the graph solution is so cooked :(

        // count number of times a starting index will be represented for some L? We can still use the prefix sum trick
            // to get the ks

        //

        int[][] grid = new int[n][n];

        for (int a = 0; a < n; a += 1) {
            grid[a][a] = -1;
            for (int b = a + 1; b < n; b += 1) {
                int temp = differentiation(a, b);
                if (temp < 0) { // second one is smaller :o - ex. abcb vs abca --> temp = 4
                    grid[a][b] = -temp; // denotes that the 4th digit get blocked off
                    grid[b][a] = -temp; // denotes that, when going backward further, we need to do at least 4 digits
                } else { // first one is smaller - ex. abca vs abcb --> temp = -4
                    grid[a][b] = 0; // can ignore
                    grid[b][a] = 0; // must stop here
                }
            }
        }

        int[][] above = new int[n][n];
        int[][] below = new int[n][n];

        // abovenbelow[a][b][0] --> for a length of b + 1, starting from a, how far below and above must we go?
            // how far above or below must we go until we get to a positive number <= b + 1

        for (int a = 0; a < n; a += 1) {
            int counter = 1;
            for (int start = a - 1; start >= 0; start -= 1) {
                if (grid[a][start] == 0) {
                    while (counter <= n) {
                        below[a][counter - 1] = start + 1;
                        counter += 1;
                    }
                } else {
                    while (counter < grid[a][start]) {
                        below[a][counter - 1] = start + 1;
                        counter += 1;
                    }
                }
                if (start == 0) { // not really needed but fine to include
                    while (counter <= n) {
                        below[a][counter - 1] = 0;
                        counter += 1;
                    }
                }
                if (counter == n + 1) {
                    break;
                }
            }

            counter = n;
            for (int end = a + 1; end < n; end += 1) {
                if (grid[a][end] != 0) {
                    while (counter >= grid[a][end]) {
                        if (a + counter > n) {
                            above[a][counter - 1] = -1;
                        } else {
                            above[a][counter - 1] = Math.min(n - 1, end - 1 + counter - 1);
                        }
                        counter -= 1;
                    }
                }
                if (end == n - 1) {
                    while (counter > 0) {
                        if (a + counter > n) {
                            above[a][counter - 1] = -1;
                        } else {
                            above[a][counter - 1] = n - 1;
                        }
                        counter -= 1;
                    }
                }
                if (counter == 0) {
                    break;
                }
            }
        }

        above[n - 1][0] = n - 1;
        for (int b = 1; b < n; b += 1) {
            above[n - 1][b] = -1;
        }

        // I think we're good then :DDDDDDD

        // gonna need a 2 layer one, essentially
            // basically it says the rate of change
            //

        // time for the reverse dp

        int[][] prefixes = new int[n][n];

        for (int a = 0; a < n; a += 1) {
            for (int b = 0; b < n - a; b += 1) {
                // can go from below[a][b] to above[a][b]
                // this means K is from b to (above[a][b] - below[a][b] + 1)
                // has length b
                // this means L is
                prefixes[b][b] += 1;
                if (above[a][b] - below[a][b] + 2 < n) {
                    prefixes[b][above[a][b] - below[a][b] + 2] -= 1;
                }
            }
        }

        int[] numbers = new int[n];

        numbers[0] += n; // I genuinely have no idea why this is needed

        for (int l = 0; l < n; l += 1) {
            if (prefixes[l][l] != 0) {
                // numbers[prefixes[l][l] - 1] += 1; // also genuinely no idea why this is not needed
            }
            for (int k = l + 1; k < n; k += 1) {
                prefixes[l][k] += prefixes[l][k - 1];
                if (prefixes[l][k] != 0) {
                    numbers[prefixes[l][k] - 1] += 1;
                }
            }
        }

        // Begin Writing Output

        BufferedWriter outputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));


        for (int i : numbers) {
            outputWriter.write(String.valueOf(i));
            outputWriter.newLine();
        }

        outputWriter.close();

        // Finish Writing Output

    }

    public static final String problemName = "Problem";
    public static final boolean recent = true; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}