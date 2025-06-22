package problems.gold;

import java.io.*;
import java.util.*;

public class BovineGenetics { // 2020 December Gold 2

    public static void main(String[] args) throws FileNotFoundException, IOException {

        // Begin Parsing Input

        BufferedReader inputReader = 
            submission ? 
                (recent ? 
                    new BufferedReader(new InputStreamReader(System.in)) : 
                    new BufferedReader(new FileReader(problemName + ".in"))) : 
                new BufferedReader(new FileReader("problemdata\\input.txt"));

        StringTokenizer line = new StringTokenizer(inputReader.readLine()); // need to do this for every single line
        char[] genome = line.nextToken().toCharArray();
        int[] genomeIntegers = new int[genome.length];

        for (int i = 0; i < genome.length; i += 1) {
            genomeIntegers[i] = 
                genome[i] == 'A' ? 0 : 
                genome[i] == 'C' ? 1 : 
                genome[i] == 'T' ? 2 : 
                genome[i] == 'G' ? 3 : 
                4;
        }

        inputReader.close();

        // Finish Parsing Input

        /* Observations

        1. Inversions are not unique:
            - GGAGTT -> G|GAGT|T -> GTGAGT
            - GGTTGA -> G|GT|TGA -> GTGAGT
            - GTGAGT -> GTGAGT lol

        2. Consider what happens to splits.
            - When we split, we take two consecutives then push them as far apart as possible
            - Consider where the last one's start flipped
                - Could have been an A, C, T, or G
                - Then, it can flip at any future thing of the same - NOT necessarily the same one

        3. First and last flips?
            - For the last one, it's easy: we don't need to do anything with the start (i.e. we can end on nothing)
                - obviously need to make sure the end works, but that's just the normal appending rules

            - For the first one, it's not quite as easy. Obviously, the ending point of the next must start with the
                first character, but the division can be anywhere in between!
                - GCCAGC -> could have been
                    - G | CCAG | C
                    - GC | CAG | C
                - one fewer restriction since we're adding two new sequences, not one, when we do this
                - instead of this, just choose the first, not the first two, with no restrictions (besides the
                        no-doubles rule below)

        4. What if we encounter a double character?
            - Must they have been separated originally? Yes. If they weren't, contradiction
                --> Can terminate expansion from a double character */

        /* What are the states we need?

        - Ending character
        - Next ending character
            --> ending character: 4 choices (A, C, T, G)
                --> at the start, just make 4 states :D
            --> next ending character: 4 choices

        What is the integer stored at each state? the number of sequences that could have led to a thing ENDING at the
            encoded value */

        int[][][] dp = new int[4][4][4];
            // current ending character, next ending character, previous character
                // previous character important for wild card purposes lol

        // dp[i][j] : # of running sequences, all of whose indices are less than i, with j's params

        if (genomeIntegers[0] == 4) {
            for (int wcValue = 0; wcValue < 4; wcValue += 1) {
                for (int endingChar = 0; endingChar < 4; endingChar += 1) {
                    dp[endingChar][wcValue][wcValue] = 1;
                }
            }
        } else {
            for (int endingChar = 0; endingChar < 4; endingChar += 1) {
                dp[endingChar][genomeIntegers[0]][genomeIntegers[0]] = 1;
            }
        }

        for (int index = 1; index < genome.length; index += 1) {
            int currentChar = genomeIntegers[index];
            int[][][] dpNext = new int[4][4][4];

            for (int endingChar = 0; endingChar < 4; endingChar += 1) {
                for (int nextEndingChar = 0; nextEndingChar < 4; nextEndingChar += 1) {
                    for (int previousChar = 0; previousChar < 4; previousChar += 1) {
                        if (previousChar == endingChar) { // simulate ending previously
                            if (currentChar == 4) {
                                for (int wcValue = 0; wcValue < 4; wcValue += 1) {
                                    dpNext[nextEndingChar][wcValue][wcValue] = modsum(
                                            dpNext[nextEndingChar][wcValue][wcValue],
                                            dp[endingChar][nextEndingChar][previousChar]);
                                }
                            } else {
                                dpNext[nextEndingChar][currentChar][currentChar] = modsum(
                                        dpNext[nextEndingChar][currentChar][currentChar],
                                        dp[endingChar][nextEndingChar][previousChar]);
                            }
                        }

                        if (currentChar == 4) {
                            for (int wcValue = 0; wcValue < 4; wcValue += 1) {
                                if (wcValue == previousChar) {
                                    continue;
                                }
                                dpNext[endingChar][nextEndingChar][wcValue] = modsum(
                                        dpNext[endingChar][nextEndingChar][wcValue],
                                        dp[endingChar][nextEndingChar][previousChar]);
                            }
                        } else if (currentChar != previousChar) {
                            dpNext[endingChar][nextEndingChar][currentChar] = modsum(
                                    dpNext[endingChar][nextEndingChar][currentChar],
                                    dp[endingChar][nextEndingChar][previousChar]);
                        }
                    }
                }
            }

            dp = dpNext;
        }

        int result = 0;
        for (int endingChar = 0; endingChar < 4; endingChar += 1) {
            for (int nextEndingChar = 0; nextEndingChar < 4; nextEndingChar += 1) {
                result = modsum(result, dp[endingChar][nextEndingChar][endingChar]);
            }
        }

        // lmao yay O(n^2) is fine

        // Begin Writing Output

        BufferedWriter ouputWriter = 
            submission ? 
                (recent ? 
                    new BufferedWriter(new OutputStreamWriter(System.out)) : 
                    new BufferedWriter(new FileWriter(problemName + ".out"))) : 
                new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        ouputWriter.write("" + result);
        ouputWriter.newLine();

        ouputWriter.close();

        // Finish Writing Output

    }

    public static int modsum(int... numbers) { // for modprod need to convert to longs
        int sum = 0;
        int modulo = 1000000007; // 10^9 + 7
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

    public static final String problemName = "BovineGenetics";
    public static final boolean recent = true; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}