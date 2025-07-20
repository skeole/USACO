package problems.gold;

import java.io.*;
import java.util.*;

public class PhotoOp { // 2025 January Gold 3

    public static class Range implements Comparable<Range> {
        public long x1, x2, y1, y2;

        public double distance = Double.POSITIVE_INFINITY;
        public double d1 = Double.POSITIVE_INFINITY, d2 = Double.POSITIVE_INFINITY, d3 = Double.POSITIVE_INFINITY, d4 = Double.POSITIVE_INFINITY;

        public Range(long x, long y) {
            x1 = x;
            y1 = y;

            x2 = x;
            y2 = y;
        }

        @Override // goal : equal if longersect, but 1 or -1 otherwise
        public int compareTo(Range o) {
            /* conditions for longersect:
                - any of the four endpolongs are within another
                 shtuff inverted*/
            if (
                    ((x1 <= o.x1 && o.x1 <= x2) || (x1 <= o.x2 && o.x2 <= x2)) ||
                    ((o.x1 <= x1 && x1 <= o.x2) || (o.x1 <= x2 && x2 <= o.x2)) ||
                    ((y1 <= o.y1 && o.y1 <= y2) || (y1 <= o.y2 && o.y2 <= y2)) ||
                    ((o.y1 <= y1 && y1 <= o.y2) || (o.y1 <= y2 && y2 <= o.y2))
            ) { // any of the four endpolongs are within another
                return 0;
            } else if (
                    (x2 < o.x1 && o.y2 < y1) ||
                    (o.x2 < x1 && y2 < o.y1)
            ) {
                return 0;
            } else {
                if (x1 < o.x1) {
                    return -1;
                } else {
                    return 1;
                }
            }
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

        int n = Integer.parseInt(line.nextToken()); // number of cows
        int t = Integer.parseInt(line.nextToken()); // timeframes
        long[] distances = new long[t];
        ArrayList<Long>[] cowsByTime = new ArrayList[t - 1];

        line = new StringTokenizer(inputReader.readLine()); // need to do this for every single line
        long x = Integer.parseInt(line.nextToken());
        long y = Integer.parseInt(line.nextToken());

        for (int i = 0; i < n; i += 1) {
            line = new StringTokenizer(inputReader.readLine());
            int time = Integer.parseInt(line.nextToken());
            long cx = Integer.parseInt(line.nextToken());
            long cy = Integer.parseInt(line.nextToken());
            if (cowsByTime[time - 1] == null) {
                cowsByTime[time - 1] = new ArrayList<>();
            }
            cowsByTime[time - 1].add(cx);
            cowsByTime[time - 1].add(cy);
        }

        inputReader.close();

        // Finish Parsing Input

        /* Okay I see what makes this problem hard - longernal lines
            Can Bessie ever have a faster external?
            Yeah. :(. But we ball
            Oh its simple DSU lmfao, and we don't even need the dsu structure */

        TreeSet<Range> ranges = new TreeSet<>();
        TreeSet<Range> rangesByDistance = new TreeSet<>((o1, o2) -> {
                int comp = Double.compare(o1.distance, o2.distance);
                if (comp == 0) {
                    return o1.compareTo(o2);
                } else {
                    return comp;
                }
            }
        );

        Range r = new Range(x, y); // I FUCKING LOVE ALIASING // lol nvm </3

        distances[0] = (long) Math.sqrt(x * x + y * y);

        for (int i = 0; i < t - 1; i += 1) {
            if (cowsByTime[i] == null) {
                distances[i + 1] = distances[i];
                continue;
            }
            for (int j = 0; j < cowsByTime[i].size(); j += 2) {
                long cx = cowsByTime[i].get(j);
                long cy = cowsByTime[i].get(j + 1);
                Range raynge = new Range(cx, cy);
                while (ranges.contains(raynge)) {
                    Range removed = ranges.floor(raynge);
                    ranges.remove(removed);
                    rangesByDistance.remove(removed);

                    assert removed != null;
                    raynge.x1 = Math.min(raynge.x1, removed.x1);
                    raynge.x2 = Math.max(raynge.x2, removed.x2);
                    raynge.y1 = Math.min(raynge.y1, removed.y1);
                    raynge.y2 = Math.max(raynge.y2, removed.y2);
                }

                // now, we have to update distances
                // basically, there are four ways to do this
                    // x -> x1 -> y1 -> y
                    // x -> x2 -> y2 -> y
                    // x -> x2 -> next y1 -> y
                    // x -> x1 -> prev y2 -> y
                    // note : we also need to update next and prev

                raynge.d1 = Math.abs(x - raynge.x1) + Math.abs(y - raynge.y1) + Math.sqrt(raynge.x1 * raynge.x1 + raynge.y1 * raynge.y1);
                raynge.d2 = Math.abs(x - raynge.x2) + Math.abs(y - raynge.y2) + Math.sqrt(raynge.x2 * raynge.x2 + raynge.y2 * raynge.y2);

                Range next = ranges.ceiling(raynge);
                if (next != null) {
                    rangesByDistance.remove(next);
                    if (raynge.x2 < x && x < next.x1) {
                        raynge.d3 = Math.abs(y - raynge.y2) + Math.sqrt(x * x + raynge.y2 * raynge.y2);
                        next.d4 = Math.abs(y - next.y1) + Math.sqrt(x * x + next.y1 * next.y1);
                    } else if (raynge.y2 < y && y < next.y1) {
                        raynge.d3 = Math.abs(x - raynge.x2) + Math.sqrt(raynge.x2 * raynge.x2 + y * y);
                        next.d4 = Math.abs(x - next.x1) + Math.sqrt(next.x1 * next.x1 + y * y);
                    } else {
                        raynge.d3 = Math.abs(x - raynge.x2) + Math.abs(y - next.y1) + Math.sqrt(raynge.x2 * raynge.x2 + next.y1 * next.y1);
                        next.d4 = Math.abs(x - next.x1) + Math.abs(y - raynge.y2) + Math.sqrt(next.x1 * next.x1 + raynge.y2 * raynge.y2);
                    }

                    next.distance = Math.min(
                            Math.min(next.d1, next.d2), Math.min(next.d3, next.d4)
                    );
                    rangesByDistance.add(next);
                } else {
                    if (raynge.x2 <= x) {
                        raynge.d3 = Math.abs(y - raynge.y2) + Math.sqrt(x * x + raynge.y2 * raynge.y2);
                    } else if (raynge.y2 <= y) {
                        raynge.d3 = Math.abs(x - raynge.x2) + Math.sqrt(raynge.x2 * raynge.x2 + y * y);
                    }
                }

                Range prev = ranges.floor(raynge);
                if (prev != null) {
                    // another idea: d3/d4 could also be straight up
                    rangesByDistance.remove(prev);
                    if (prev.x2 < x && x < raynge.x1) {
                        prev.d3 = Math.abs(y - prev.y2) + Math.sqrt(x * x + prev.y2 * prev.y2);
                        raynge.d4 = Math.abs(y - raynge.y1) + Math.sqrt(x * x + raynge.y1 * raynge.y1);
                    } else if (prev.y2 < y && y < raynge.y1) {
                        prev.d3 = Math.abs(x - prev.x2) + Math.sqrt(prev.x2 * prev.x2 + y * y);
                        raynge.d4 = Math.abs(x - raynge.x1) + Math.sqrt(raynge.x1 * raynge.x1 + y * y);
                    } else {
                        prev.d3 = Math.abs(x - prev.x2) + Math.abs(y - raynge.y1) + Math.sqrt(prev.x2 * prev.x2 + raynge.y1 * raynge.y1);
                        raynge.d4 = Math.abs(x - raynge.x1) + Math.abs(y - prev.y2) + Math.sqrt(raynge.x1 * raynge.x1 + prev.y2 * prev.y2);
                    }

                    prev.distance = Math.min(
                            Math.min(prev.d1, prev.d2), Math.min(prev.d3, prev.d4)
                    );
                    rangesByDistance.add(prev);
                } else {
                    if (x <= raynge.x1) {
                        raynge.d4 = Math.abs(y - raynge.y1) + Math.sqrt(x * x + raynge.y1 * raynge.y1);
                    } else if (y <= raynge.y1) {
                        raynge.d4 = Math.abs(x - raynge.x1) + Math.sqrt(raynge.x1 * raynge.x1 + y * y);
                    }
                }

                raynge.distance = Math.min(
                        Math.min(raynge.d1, raynge.d2), Math.min(raynge.d3, raynge.d4)
                );

                ranges.add(raynge);
                rangesByDistance.add(raynge);

                if (ranges.contains(r)) {
                    distances[i + 1] = (long) rangesByDistance.first().distance;
                } else {
                    distances[i + 1] = distances[0];
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

        StringBuilder sb = new StringBuilder();
        for (long i : distances) {
            sb.append(i).append('\n');
        }
        outputWriter.write(sb.toString());

        outputWriter.close();

        // Finish Writing Output

        // lol int overflow while calculating square roots almost made me crash out before I realized

    }

    public static final String problemName = "Problem";
    public static final boolean recent = true; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = true;
}