package problems.gold;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

public class CowCamp { // 2022 February Gold 2

    public static double calculateEV(double[] baseProbabilities, double[] baseEVs, int t, long exponent) {
        long currentValue = 1;
        double currentEV = baseEVs[t];

        while (currentValue < exponent) {
            int number = (int) Math.floor(currentEV);
            double probabilityWeSkip = baseProbabilities[number];
            double EVWithStay = baseEVs[t] - baseEVs[number];

            double asymptoticValue = EVWithStay / (1 - probabilityWeSkip);

            /* our iteration is currentEV -> EVWithStay + probabilityWeSkip * currentEV
                what if instead we considered x = currentEV - asymptoticValue

                x -> EVWithStay + probabilityWeSkip * (x + asymptoticValue) - asymptoticValue
                   = EVWithStay + probabilityWeSkip * (x + EVWithStay / (1 - probabilityWeSkip)) - EVWithStay / (1 - probabilityWeSkip)
                   = EVWithStay * (1 - 1 / (1 - probabilityWeSkip)) + probabilityWeSkip * (x + EVWithStay / (1 - probabilityWeSkip))
                   = - EVWithStay * probabilityWeSkip / (1 - probabilityWeSkip) + probabilityWeSkip * (x + EVWithStay / (1 - probabilityWeSkip))
                   = probabilityWeSkip * (x + EVWithStay / (1 - probabilityWeSkip) - EVWithStay / (1 - probabilityWeSkip))
                   = probabilityWeSkip * x

                   same thing if x is negative of this, so do that b/c its easier to work with positive numbers */

            double difference = asymptoticValue - currentEV;
            double differenceForNext = asymptoticValue - (number + 1);
            // go until difference * (probabilityWeSkip) ^ k < differenceForNext
            // k * log(probabilityWeSkip) < log(differenceForNext) - log(difference)
            // k > (log(difference) - log(differenceForNext)) / log(probabilityWeSkip);
            long numMovesRequired = Math.min(exponent - currentValue, (long) Math.ceil((Math.log(differenceForNext) - Math.log(difference)) / Math.log(probabilityWeSkip)));
            currentValue += numMovesRequired;
            currentEV = asymptoticValue - difference * Math.pow(probabilityWeSkip, numMovesRequired);
        }

        return currentEV;
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

        int t = Integer.parseInt(line.nextToken()) - 1; // number of cases
        long n = Long.parseLong(line.nextToken()); // number of guesses

        inputReader.close();

        // Finish Parsing Input

        /* lmfao this just a math problem. doesn't mean it's easy though! */

        /* What would the naive solution be?
             - remember, Bessie only gets the last submission - NOT the most valuable one
             - in words: if you know the EV of n - 1 guesses, then you only keep the current guess if it has a higher
                proportion, otherwise you ball out
                 - Can be done in O(N) time using clever prefix sums
                     - for each advancement: use array properties to find target above in O(1) time
                     - use prefix probabilities to find probability that happens
                     - use prefix EVs to find EV of the stuff above it

             - However, we need sub-O(N) time. How can we advance multiple steps at once?
                 - Wait. Can't we do the same trick as we did for exponentiation?
                 - The probabilities are the probabilities we get those as the high

             - reminds me of exponentiation - another associative operation that can be chained

             - divide and conquer
             - we need to calculate the probabilities for one run. Not too hard to do
             - for P(N)
                - assume we recursively have the probabilities for P(N // 2)
                - we can then calculate all the probabilities for N
                - okay, now we have 3 sets of up to 1000 probabilities. What now?
                    - easy to combine with prefix sums
                    - P(1000) * P(at most 999) + P(at most 999) * P(1000) + P(1000) * P(1000)
                    - O(T) runtime

             - Never mind. It's not her highest scoring solution - its her last solution
             - This is actually a pretty cool problem

             - Overall runtime: O(T log N) */

        /* I swear to fucking god if we have to use BigInts T-T */

        BigInteger[] probabilityCounts = new BigInteger[t + 1]; // prefix probabilities :)
        BigInteger[] evCounts = new BigInteger[t + 1]; // prefix EVs :)
            // have to divide BOTH by exponent at the end
        BigInteger exponent = BigInteger.ONE;

        BigInteger runningCombination = BigInteger.ONE;
        probabilityCounts[0] = BigInteger.ONE;
        evCounts[0] = BigInteger.ZERO;

        for (int i = 1; i <= t; i += 1) {
            runningCombination = runningCombination.multiply(BigInteger.valueOf(t - i + 1));
            runningCombination = runningCombination.divide(BigInteger.valueOf(i));

            probabilityCounts[i] = runningCombination.add(probabilityCounts[i - 1]);
            evCounts[i] = runningCombination.multiply(BigInteger.valueOf(i)).add(evCounts[i - 1]);

            exponent = exponent.multiply(BigInteger.valueOf(2));
        }

        double[] probabilities = new double[t + 1];
        double[] evs = new double[t + 1];
        BigDecimal exp = new BigDecimal(exponent);
        int precision = 100;

        for (int i = 0; i <= t; i += 1) {
            probabilities[i] = new BigDecimal(probabilityCounts[i]).divide(exp, new MathContext(precision, RoundingMode.HALF_UP)).doubleValue();
            evs[i] = new BigDecimal(evCounts[i]).divide(exp, new MathContext(precision, RoundingMode.HALF_UP)).doubleValue();
        } // does NOT time out by the way

        System.out.println(1 + (calculateEV(probabilities, evs, t, n)));

        /* keep BigDecimal in our back pocket for if we run into accuracy issues */

    }

    public static final String problemName = "Problem";
    public static final boolean recent = true; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}