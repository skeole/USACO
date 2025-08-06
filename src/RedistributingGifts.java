import java.io.*;
import java.util.*;

public class RedistributingGifts { // 2022 February Gold 1

    public static HashSet<Integer> indexToData(int index) {
        HashSet<Integer> set = new HashSet<>();
        for (int i = 0; index > 0; i += 1) {
            if (index % 2 == 1) {
                set.add(i);
            }
            index >>= 1;
        }
        return set;
    }

    public static int dataToIndex(HashSet<Integer> data) {
        int index = 0;
        for (int i : data) {
            index ^= (1 << i); // lawls
        }
        return index;
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

        HashSet<Integer>[][] edges = new HashSet[n][2];
            // edges we need to add :o
            // edges[n][0] : edges from n
            // edges[n][1] : edges into n

        for (int i = 0; i < n; i += 1) {
            edges[i][0] = new HashSet();
            edges[i][1] = new HashSet();
        }

        for (int i = 0; i < n; i += 1) {
            line = new StringTokenizer(inputReader.readLine());
            for (int j = 0; j < n; j += 1) {
                int pref = Integer.parseInt(line.nextToken()) - 1;
                if (pref == i) {
                    break;
                } else if (pref > i) {
                    edges[pref][1].add(i);
                } else {
                    edges[i][0].add(pref);
                }
            }
        }

        line = new StringTokenizer(inputReader.readLine()); // need to do this for every single line

        // bitmask dp

        int[] cycles = new int[1 << n];
        for (int i = 0; i < cycles.length; i += 1) {
            HashSet<Integer> elements = indexToData(i);
        }

        // sigh. This is just a cycle counting question - I'll do this laterr

        int q = Integer.parseInt(line.nextToken()); // number of queries

        HashSet<Integer>[][] queries = new HashSet[q][2];

        for (int i = 0; i < q; i += 1) {
            queries[i][0] = new HashSet<>();
            queries[i][1] = new HashSet<>();
            line = new StringTokenizer(inputReader.readLine());
            char[] breeds = line.nextToken().toCharArray();
            for (int j = 0; j < breeds.length; j += 1) {
                if (breeds[j] == 'G') {
                    queries[i][0].add(j);
                } else {
                    queries[i][1].add(j);
                }
            }
        }

        inputReader.close();

        // Finish Parsing Input

        int[] results = new int[q];

        // Begin Writing Output

        BufferedWriter outputWriter =
                submission ?
                        (recent ?
                                new BufferedWriter(new OutputStreamWriter(System.out)) :
                                new BufferedWriter(new FileWriter(problemName + ".out"))) :
                        new BufferedWriter(new FileWriter("problemdata\\output.txt"));

        for (int i : results) {
            outputWriter.write(String.valueOf(i));
            outputWriter.newLine();
        }

        outputWriter.close();

        // Finish Writing Output

    }

    public static final String problemName = "Problem";
    public static final boolean recent = true; // false if Open 2020 or before, true if December 2020 or after
    public static final boolean submission = false;
}