package problems.gold;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class FavoriteColors { // 2020 Open Gold 2

    public final static TreeSet<Cow> allCows = new TreeSet<>();

    private static class Cow implements Comparable<Cow> {
        private TreeSet<Cow> children = new TreeSet<Cow>();
        private TreeSet<Cow> parents = new TreeSet<Cow>();

        private final int id;
        private final TreeSet<Integer> ids = new TreeSet<>();

        public Cow(int id) {
            this.id = id; // overall id
            this.ids.add(id);
        }

        @Override
        public int compareTo(Cow other) {
            return this.id - other.id;
        }

        @Override
        public String toString() {
            return Integer.toString(id);
        }

        public void merge(Cow other) {

            if (!allCows.contains(this)) {
                return;
            }

            if (!allCows.contains(other)) {
                return;
            }

            if (this.id == other.id) {
                throw new IllegalArgumentException("What da hell");
                // return;
            }

            if (this.id > other.id) {
                throw new IllegalArgumentException("What da hell");
                // other.merge(this);
                // return;
            }

            allCows.remove(other);

            for (Integer c : other.ids) {
                this.ids.add(c);
            }

            for (Cow c : other.parents) {
                c.children.remove(other);
                c.children.add(this);

                this.parents.add(c);
            }

            for (Cow c : other.children) {
                c.parents.remove(other);
                c.parents.add(this);

                this.children.add(c);
            }
        }

        public void stabilize() { // merge all the kids --> call stabilize on the child

            boolean cont = true;
            Cow curr = this;

            while (cont) { // if this fixes it im actually killing myself

                if (curr.children.size() <= 1) { // hehe jane street is calling
                    return;
                }
                if (!allCows.contains(curr)) {
                    return;
                }
                
                Cow superChild = null;
                LinkedList<Cow> queue = new LinkedList<>();
                
                for (Cow c : curr.children) {
                    if (superChild == null) {
                        superChild = c;
                    } else {
                        queue.add(c);
                    }
                }
                
                for (Cow c : queue) {
                    superChild.merge(c);
                }

                if (!allCows.contains(superChild)) {
                    throw new RuntimeException("superChild should be valid");
                }

                curr = superChild;
            }
        }

    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        BufferedReader br;
        
        if (submission) {
            br = new BufferedReader(new FileReader("fcolor.in"));
        } else {
            br = new BufferedReader(new FileReader("problemdata\\input.txt"));
        }

        StringTokenizer st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken()); // number of cows
        int m = Integer.parseInt(st.nextToken()); // number of pairs

        Cow[] cowsTemp = new Cow[n];

        for (int i = 0; i < n; i += 1) {
            cowsTemp[i] = new Cow(i);
            allCows.add(cowsTemp[i]);
        }

        for (int i = 0; i < m; i += 1) { // i mean we could keep BFSing until we reach an equilibrium I guess...
            
            st = new StringTokenizer(br.readLine());
            int admiree = Integer.parseInt(st.nextToken()) - 1; // number of cows
            int admirer = Integer.parseInt(st.nextToken()) - 1; // number of pairs

            cowsTemp[admiree].children.add(cowsTemp[admirer]); // duplicates don't fw with this :D
            cowsTemp[admirer].parents.add(cowsTemp[admiree]); // yooo aliasing is fucking goated
        }

        br.close();

        for (Cow c : cowsTemp) {
            c.stabilize();
        }

        int counter = 1;
        int[] colors = new int[n];
        for (Cow c : allCows) {
            for (int i : c.ids) {
                colors[i] = counter;
            }
            counter += 1;
        }

        String filename = "fcolor.out";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            
            for (int i = 0; i < colors.length; i += 1) {
                writer.write(Integer.toString(colors[i]));
                if (i != colors.length - 1) {
                    writer.newLine();
                }
            }

            writer.close();

        } catch (IOException e) {
            System.err.println("An error occurred writing to the file: " + e.getMessage());
            e.printStackTrace(); // Optionally print the stack trace for debugging
        }
        
    }

    public static boolean submission = true;
}
