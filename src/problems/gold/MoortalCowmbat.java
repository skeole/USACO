package problems.gold;
import java.io.*;
import java.util.*;

public class MoortalCowmbat { // 2019 December Gold 3

    public static void main(String[] args) throws FileNotFoundException, IOException {

        // Begin Parsing Input

        BufferedReader inputReader = 
            submission ? 
                (recent ? 
                    new BufferedReader(new InputStreamReader(System.in)) : 
                    new BufferedReader(new FileReader(problemName + ".in"))) : 
                new BufferedReader(new FileReader("problemdata\\input.txt"));

        StringTokenizer line = new StringTokenizer(inputReader.readLine()); // need to do this for every single line

        int n = Integer.parseInt(line.nextToken()); // length of sequence
        int m = Integer.parseInt(line.nextToken()); // number of letters
        int k = Integer.parseInt(line.nextToken()); // in-row length

        line = new StringTokenizer(inputReader.readLine());
        char[] hi = line.nextToken().toCharArray();
        int[] sequence = new int[hi.length];
    
        for (int i = 0; i < hi.length; i += 1) {
            sequence[i] = hi[i] - 'a';
        }

        int[][] matrix = new int[m][m];
        for (int i = 0; i < m; i += 1) {
            line = new StringTokenizer(inputReader.readLine());
            for (int j = 0; j < m; j += 1) {
                int c = Integer.parseInt(line.nextToken());
                matrix[i][j] = c;
            }
        }

        inputReader.close();

        for (int l = 0; l < m; l += 1) {
            for (int i = 0; i < m; i += 1) {
                for (int j = 0; j < m; j += 1) {
                    int distance = matrix[i][l] + matrix[l][j];
                    if (distance < matrix[i][j]) {
                        matrix[i][j] = distance;
                    }
                    // reason it works: consider last edge in shortest path from u to v --> yay
                }
            }
        }

        // Finish Parsing Input

        // runtime: O(nk) which is O(n^2) when k is close to n. rip. hmm........

        int[][] prefixcosts = new int[n + 1][m];

        for (int i = 0; i < n; i += 1) {
            for (int j = 0; j < m; j += 1) {
                prefixcosts[i + 1][j] = prefixcosts[i][j] + matrix[sequence[i]][j];
            }
        }

        // by math, we only need to go up to 2k spots
        int[][] minimumscores = new int[n + 1][m];
        int[] minimumminimumscores = new int[n + 1];
        for (int i = 1; i < n + 1; i += 1) {
            for (int j = 0; j < m; j += 1) {
                minimumscores[i][j] = 999999999; // idk
            }
            minimumminimumscores[i] = 999999999;
        }
        
        for (int i = 0; i < n; i += 1) {
            for (int j = 0; j < m; j += 1) {

                if (i <= n - k) {
                    minimumscores[i + k][j] = Math.min(
                        minimumscores[i + k][j], 
                        minimumminimumscores[i] + prefixcosts[i + k][j] - prefixcosts[i][j]
                    );
                    minimumminimumscores[i + k] = Math.min(minimumminimumscores[i + k], minimumscores[i + k][j]);
                }

                if (i > 0) {

                    minimumscores[i + 1][j] = Math.min(
                        minimumscores[i + 1][j], 
                        minimumscores[i][j] + prefixcosts[i + 1][j] - prefixcosts[i][j]
                    );
                    
                    minimumminimumscores[i + 1] = Math.min(minimumminimumscores[i + 1], minimumscores[i + 1][j]);
                }
            }
        }

        int answer = minimumminimumscores[n];

        // Begin Writing Output

        BufferedWriter ouputWriter = 
            submission ? 
                (recent ? 
                    new BufferedWriter(new OutputStreamWriter(System.out)) : 
                    new BufferedWriter(new FileWriter(problemName + ".out"))) : 
                new BufferedWriter(new OutputStreamWriter(System.out));

        ouputWriter.write(("" + answer));
        ouputWriter.newLine();

        ouputWriter.close();

        // Finish Writing Output

    }

    public static final String problemName = "cowmbat";
    public static final boolean recent = false; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}