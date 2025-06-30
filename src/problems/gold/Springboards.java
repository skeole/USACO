package problems.gold;

import java.io.*;
import java.util.*;

public class Springboards { // 2020 January Gold 3

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
        int p = Integer.parseInt(line.nextToken()); // number of pairs

        class Point implements Comparable<Point> {
            final int x, y;
            int saved;

            public Point(int x, int y) {
                this.x = x;
                this.y = y;
            }

            @Override
            public int compareTo(Point o) {
                if (x < o.x || (x == o.x && y < o.y)) {
                    return -1;
                }
                if (x == o.x && y == o.y) {
                    return 0;
                }
                return 1;
            }

            @Override
            public String toString() {
                if (saved == 0) {
                    return "(" + x + ", " + y + ")";
                } else {
                    return "(" + x + ", " + y + "), saves : " + saved;
                }
            }
        }

        class Springboard implements Comparable<Springboard> {
            final Point p1, p2;
            final int savedTime;

            public Springboard(int x1, int y1, int x2, int y2) {
                p1 = new Point(x1, y1);
                p2 = new Point(x2, y2);
                savedTime = y2 + x2 - y1 - x1;
            }

            @Override
            public int compareTo(Springboard o) {
                return p1.compareTo(o.p1);
            }

            @Override
            public String toString() {
                return p1 + " to " + p2 + " saves " + savedTime;
            }
        }

        TreeSet<Springboard> boards = new TreeSet<>();

        for (int i = 0; i < p; i += 1) {
            line = new StringTokenizer(inputReader.readLine());
            boards.add(new Springboard(Integer.parseInt(line.nextToken()),
                    Integer.parseInt(line.nextToken()),
                    Integer.parseInt(line.nextToken()),
                    Integer.parseInt(line.nextToken())));
        }

        inputReader.close();

        // Finish Parsing Input

        // Oh this is just a graph problem lol

        /* ok... kind of spoiled by the problem type... but also, O(n^2) will be too slow so how about O(n logn)
            --> probably inserting in O(n logn) time --> some kind of augmented tree structure
            --> idea: repeatedtly add into the structure (only need to store the exit node of the slide, but process
                    based on both the entry node (for quickest access) and the exit node (to update later ones)
            --> Obviously, it's easy to do this in O(n^2). But this is a gold problem - I know the solution is going
                    to be O(n logn). How do we augment the tree to handle this ...

           Strategy: think about what the tree solution would be in 1D, before how we can extend it to 2D

           A-ha

           Store a treemap of y-indices to "time saved"
            - invariant: lower y indices must have lower time saved
            - to ensure this, just remove higher y indices with equal or lower time saved while readding

           Also store a treemap/treeset of entries to add to the above treemap, sorted by x coordinate

           Sort the springboards by x-index of the starting block
           When we iterate to the next one
            1. Add potential elements from the treeset
            2. do the floor entry of the y block to find the maximum possible time saved
            3. combine and add to the treemap of entries to add

           Runtime analysis:
            - adding potential elements from the treeset:
                - each element can only be deleted once --> O(n logn)
                - each element can only be added once --> O(n logn)
            - doing the floor entry n times: O(n logn)
            - combining n times: O(n)
            - adding to the treemap of entries to add: each element can only be added once --> O(n logn)

           Total: O(n logn), as desired :)

           Kind of had to cheat: needed to see test data to find edge case (didn't check upon insertion) */

        TreeMap<Integer, Integer> bestSavedTimes = new TreeMap<>();
        bestSavedTimes.put(0, 0);
        TreeSet<Point> savedTimes = new TreeSet<>();

        for (Springboard board : boards) {
            while (!savedTimes.isEmpty() && savedTimes.first().x <= board.p1.x) {
                Point pont = savedTimes.first();
                savedTimes.remove(pont);
                while (bestSavedTimes.ceilingEntry(pont.y) != null && bestSavedTimes.ceilingEntry(pont.y).getValue() <= pont.saved) {
                    bestSavedTimes.remove(bestSavedTimes.ceilingKey(pont.y));
                } // delete worse times
                if (bestSavedTimes.floorEntry(pont.y) == null || bestSavedTimes.floorEntry(pont.y).getValue() < pont.saved) {
                    bestSavedTimes.put(pont.y, pont.saved);
                } // (potentially) add this one
            }
            board.p2.saved = bestSavedTimes.floorEntry(board.p1.y).getValue() + board.savedTime;
            savedTimes.add(board.p2);
        }

        for (Point pont : savedTimes) {
            while (bestSavedTimes.ceilingEntry(pont.y) != null && bestSavedTimes.ceilingEntry(pont.y).getValue() <= pont.saved) {
                bestSavedTimes.remove(bestSavedTimes.ceilingKey(pont.y));
            } // delete worse times
            if (bestSavedTimes.floorEntry(pont.y) == null || bestSavedTimes.floorEntry(pont.y).getValue() < pont.saved) {
                bestSavedTimes.put(pont.y, pont.saved);
            } // (potentially) add this one
        }

        // Begin Writing Output

        BufferedWriter ouputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        ouputWriter.write("" + (n + n - bestSavedTimes.lastEntry().getValue()));
        ouputWriter.newLine();

        ouputWriter.close();

        // Finish Writing Output

    }

    public static final String problemName = "boards";
    public static final boolean recent = false; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}