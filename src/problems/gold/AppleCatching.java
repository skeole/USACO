package problems.gold;

import java.io.*;
import java.util.*;

public class AppleCatching { // 2022 Open Gold 1

    public static final boolean APPLE = false, COW = true;

    public static class Point implements Comparable<Point> {
        private final int x, y;
        private final boolean type;
        private final int count;

        public Point(int x, int y, boolean type, int count) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.count = count;
        }

        public int compareTo(Point p) { // points can never be equal... can they?
            if (this.x < p.x) {
                return -1;
            } else if (this.x == p.x && this.y < p.y) {
                return -1;
            }
            return 1; // it is given all the ti, xi are distinct
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
        int n = Integer.parseInt(line.nextToken());

        TreeSet<Point> points = new TreeSet<>();

        for (int i = 0; i < n; i += 1) {
            line = new StringTokenizer(inputReader.readLine());
            int type = Integer.parseInt(line.nextToken());
            int y = Integer.parseInt(line.nextToken());
            int x = Integer.parseInt(line.nextToken());
            int count = Integer.parseInt(line.nextToken());
            points.add(new Point(
                    y + x,
                    y - x,
                    type == 1 ? COW : APPLE,
                    count
            ));
        }

        // (x, y) rotated 45 degrees --> (y + x, y - x) (ish)

        inputReader.close();

        // Finish Parsing Input

        /* Idea: yes, represent as cones. Deal with weird constraints later
            - slight cheating: rotate 45 degrees

        I assume the greedy algorithm deals with assigning the lower/left cows first
        Or maybe... it deals with assigning the lower/left apples first? a-ha
            - take the leftmost apple
            - give it to the topmost cow that can reach it
            - lol gg, ez

        OK, so I half-figured it out. The insight to rotate gave me the geometric intuition to finish */

        /* ok: now begins arguably the harder part: coding it :D

         Idea: put everything in a BST by x-coordinate then y coordinate
            - then, iterate through, adding a y-coordinate BST as we progress

         Putting everything in BST: n logn
         Iterating through: n
         Putting everything in y-BST: n logn

         Overall RT: n logn */

        TreeMap<Integer, Integer> cowsByY = new TreeMap<>();

        int count = 0;
        for (Point p : points) {
            if (p.type == COW) {
                if (!cowsByY.containsKey(p.y)) {
                    cowsByY.put(p.y, 0);
                }
                cowsByY.put(p.y, cowsByY.get(p.y) + p.count);
            } else {
                int number = p.count;
                int y = p.y;
                while (number > 0 && cowsByY.floorKey(y) != null) {
                    int removeFrom = cowsByY.floorKey(y);
                    int numberAtY = cowsByY.get(removeFrom);
                    if (numberAtY > number) {
                        count += number;
                        cowsByY.put(removeFrom, numberAtY - number);
                        number = 0;
                    } else if (numberAtY == number) {
                        cowsByY.remove(removeFrom);
                        count += number;
                        number = 0;
                    } else {
                        count += numberAtY;
                        cowsByY.remove(removeFrom);
                        number = number - numberAtY;
                    }
                }
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