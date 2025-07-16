package problems.gold;

import java.io.*;
import java.util.*;

public class StoneGame { // 2021 February Gold 1

    public static void main(String[] args) throws IOException {

        // Begin Parsing Input

        BufferedReader inputReader =
                submission ?
                        (recent ?
                                new BufferedReader(new InputStreamReader(System.in)) :
                                new BufferedReader(new FileReader(problemName + ".in"))) :
                        new BufferedReader(new FileReader("problemdata\\input.txt"));

        StringTokenizer line = new StringTokenizer(inputReader.readLine()); // need to do this for every single line

        int n = Integer.parseInt(line.nextToken()); // number of piles
        int[] piles = new int[n + 2 + n % 2]; // to even it out + make sure we have a few zeroes

        line = new StringTokenizer(inputReader.readLine());
        for (int i = 0; i < n; i += 1) {
            piles[i] = Integer.parseInt(line.nextToken());
        }

        Arrays.sort(piles);
        Arrays.parallelSetAll(piles, x -> piles[x]);

        TreeMap<Integer, Integer> indices = new TreeMap<>();
        TreeSet<Integer> keysDescending = new TreeSet<>();

        for (int i = 0; i < piles.length; i += 1) {
            indices.put(piles[i], i + 1);
            keysDescending.add(piles[i]);
        } // ts ac pmo sm icl
        // wait lmao nvm this easy
        indices.put(-1000000000, 0); // just because I'm lazy and don't wanna deal with NPEs and shit

        inputReader.close();

        // Finish Parsing Input

        /* I'm not exactly sure how to solve this. However, I also wasn't sure how to solve nim originally. And how did
            we do that? Right, by starting small.

           Let's assume we have only one pile. Kind of stupid, but - we always win
           How about two piles? Obviously, if the piles are different sizes, we win, but if they're the same we lose
             - We also need information about what moves win though - and the moves that win for us are
             - We can view "removing x" as floor-dividing all things by x, then removing one
                - e.g. if the piles are 17, 15, 3, and 9, and we remove 5 from the 17 pile
                - transform the piles to 3, 3, 0, 0
                - remove one from the first pile
             - We win iff we leave the opponent with (x, x)
                - but how do we ensure this?
                    - we want to make it so the piles right now are (x + 1, x) for some x
                    - basically, x such that x >= b - a and x has a multiple on (a, b]
                        - actually, this isn't necessarily true: 5 and (1, 9) works even though 5 < 8
                    - genuinely not sure how do to this ;-; at least not in an efficient manner. However, it we only
                        need to do this once... it could be doable

           Hmm. So far, this seems awfully similar to nim. Let's try three piles and see if the pattern still holds

           Piles with a, b, and c stones. okkay now what
             - nim was (a xor b xor c) == 0, and the reasoning was as follows: opponent can always duplicate the move.
             - 4, 13, 14 is a win for you: take 4 then the piles are (1 -> 0, 3, 3) which is a win
             - however, we can also take 3: (1 -> 0, 4, 4) is also a win

             hrmm. instead of seeing cases where you win, look at the cases where you're bound to lose (and of course,
                we hand this losing case to our opponent)

             wait a minute... isn't that... literally only when everything is doubled up? ... I might need to check my
                meds, but I believe this is correct
                - OMFG YES IT IS!
                - Obviously the forward is trivial (mirroring)
                - for the backward, assume you don't have a perfect mirroring. Take the first that isn't a mirror, then
                    gg ez
                - holy shit lmfao that's actually insane */

        /* so basically, we want to remove numbers where we give the opponent a perfect mirroring. This was a much
            easier solution than I was fearing, but we still have the issue of implementation. */

        /* However, I don't think it should be too bad. We can basically do filtering:
            - range that makes the first two numbers agree
            - range that makes the next two numbers agree
                - even it out by making the last number 0 if there are an odd number
            - etc.
            - actually, I don't think they have to be pairs. for example, if the list is (13, 12, 11, 10), then
                we can take 5 away from any of the first three
                 - instead, let's try segmenting: find the number at each mod class. ok now we cooking

                 */

        /*

        function to find n such that x / n == y / n
          - iterate over n; smallest: floor(x / n) to ceil(y / n)

          - e.g. 16, 21 -->

        function to find n such that x / n == y / n + 1
          - note this is mutually exclusive with case I

        */

        long count = 0;
        for (int i = 1; i <= piles[piles.length - 1]; i += 1) {
            int numTriggers = 0;

            int current = 1000000000;

            while (current > 0) {
                current = keysDescending.lower(current);
                int k = current / i;
                int numberAtK = indices.lowerEntry((k + 1) * i).getValue() - indices.lowerEntry(k * i).getValue();
                current = k * i;
                if (numberAtK % 2 == 1) {
                    if (numTriggers != 0) {
                        numTriggers = 0;
                        break;
                    } // potential er
                    int numberAtKMinusOne = indices.lowerEntry(k * i).getValue() - indices.lowerEntry((k - 1) * i).getValue();
                    if (numberAtKMinusOne % 2 == 1) {
                        numTriggers = numberAtK;
                        current = (k - 1) * i;
                    } else {
                        numTriggers = 0; // technically not needed as IntelliJ so helpfully pointed out
                        break;
                    }
                } else if (numberAtK == 0) {
                    break; // just to short circuit lol
                }
            }

            count += numTriggers;
        }

        System.out.println(count);

    }

    public final static int MODULO = 1000000007; // 10^9 + 7
    public static final int[] MODULO_AS_BINARY = {0, 2, 9, 11, 14, 15, 17, 19, 20, 23, 24, 25, 27, 28, 29};

    /** expectation: largest magnitude less than 10^9 + 7 */
    public static long modsum(long... numbers) {
        long sum = 0;
        for (long number : numbers) {
            if (number < 0) {
                number += MODULO;
            }
            sum += number;
            if (sum >= MODULO) {
                sum -= MODULO;
            }
        }
        return sum;
    }

    /** expectation: magnitude less than 10^9 + 7 */
    public static long inverse(long number) {
        if (number < 0) {
            number += MODULO;
        }

        long result = 1;
        long register = number;

        int currentExponent = 0;
        for (int i : MODULO_AS_BINARY) {
            for (int exponent = currentExponent; exponent < i; exponent += 1) {
                register = (register * register) % MODULO;
            }
            result = (result * register) % MODULO;
            currentExponent = i;
        }

        return result;
    }

    /** expectation: largest magnitude less than 10^9 + 7 */
    public static long modprod(long... numbers) {
        long product = 1;
        for (long number : numbers) {
            if (number < 0) {
                number += MODULO;
            }
            product *= number;
            if (product >= MODULO) {
                product %= MODULO;
            }
        }
        return product;
    }

    /** expectation: largest magnitude less than 10^9 + 7 */
    public static long modpow(long number, long exponent) {
        if (exponent == 0) {
            return 1;
        }
        long prev = modpow(number, exponent / 2);
        if (exponent % 2 == 0) {
            return modprod(prev, prev);
        } else {
            return modprod(number, prev, prev);
        }
    }

    public static final String problemName = "Problem";
    public static final boolean recent = true; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}