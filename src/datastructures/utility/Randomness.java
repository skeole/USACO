package datastructures.utility;

import java.util.Random;

public class Randomness {
    
    private static Random r;

    /** lower inclusive, upper exclusive */
    public static int getRandomInteger(int lower, int upper) {
        if (r == null) {
            r = new Random();
        }
        return (int) (lower + r.nextDouble() * (upper - lower));
    }
    
    /** lower inclusive, upper exclusive */
    public static double getDouble() {
        if (r == null) {
            r = new Random();
        }
        return r.nextDouble();
    }

    public static boolean chooseRandomly() {
        if (r == null) {
            r = new Random();
        }
        return r.nextDouble() > 0.5;
    }
}
