package problems.silver;
import java.io.*;
import java.util.*;

public class SpacedOut { // 2021 January Silver 3

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

        int[][] scores = new int[n][n];

        for (int i = 0; i < n; i += 1) {
            line = new StringTokenizer(inputReader.readLine());
            for (int j = 0; j < n; j += 1) {
                scores[i][j] = Integer.parseInt(line.nextToken());
            }
        }

        /* Note: if there is a "wall" - that "wall" must be repeated - i.e.
             X 0         -->        . . . X 0 X 0 X 0 . . .
             X 0                    . . . X 0 X 0 X 0 . . .

         This also means there are either horizontal bands or vertical bands (or neither, possibly) - but there can't be both

             */

        /* how about the other config? I'm guessing we'll see something similar ...

        case 1 : the row before or after is an "alternating row"

        . . . X 0 X 0 X 0 . . .       -->       . . . X 0 X 0 X 0 . . .
                  0 X                           . . . 0 X 0 X 0 X . . .

        okay... now we're balling. This means that, if there is a SINGLE alternating row/column - ALL rows/columns must be alternating. */

        /* but do there need to be any alternating rows at all?
            YES! if a row isn't alternating, that means there are 2 Xs or 2 0s in that row - which means the OTHER direction is alternating!
            actually the goat fr (not actually) */

        inputReader.close();

        // Finish Parsing Input

        // pretty easily DP-able

        // wait lmao this isn't even DP this is just easy

        int verticals = 0;
        int horizontals = 0;
        for (int i = 0; i < n; i += 1) {
            int first = 0;
            int second = 0;
            int firster = 0;
            int seconder = 0;
            for (int j = 0; j < n - 1; j += 2) {
                first += scores[i][j];
                second += scores[i][j + 1];
                firster += scores[j][i];
                seconder += scores[j + 1][i];
            }
            if (n % 2 == 1) {
                first += scores[i][n - 1];
                firster += scores[n - 1][i];
            }
            verticals += Math.max(first, second);
            horizontals += Math.max(firster, seconder);
        }

        // Begin Writing Output

        BufferedWriter ouputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        ouputWriter.write("" + Math.max(verticals, horizontals));
        ouputWriter.newLine();

        ouputWriter.close();

        // Finish Writing Output

    }

    public static final String problemName = "spacedout";
    public static final boolean recent = true; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}