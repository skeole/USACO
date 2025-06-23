package datastructures.utility;

import datastructures.utility.functionalinterfaces.onetoone.G2G;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;

public class VisualizeTree {

    public static String blankString(int length) {
        return " ".repeat(Math.max(0, length));
    }

    public static String dashString(int length) {
        return "-".repeat(Math.max(0, length));
    }

    public static <E> String stringOfTree(E rootNode, G2G<E, E> toLeftChild, G2G<E, E> toRightChild, G2G<E, String> stringOfNode, int neighborSpace) {
        neighborSpace /= 2;
        if (neighborSpace <= 0) {
            neighborSpace = 1;
        }
        if (rootNode == null) {
            return "Empty Tree";
        }
        int longestLength = 0;
        int length = 1;
        ArrayDeque<OrderedTuple<E, Integer>> queue = new ArrayDeque<>();
        queue.addLast(new OrderedTuple<>(rootNode, 1));
        while (!queue.isEmpty()) {
            OrderedTuple<E, Integer> first = queue.removeFirst();
            E node = first.getFirst();
            int currentLength = first.getSecond();
            longestLength = Math.max(longestLength, stringOfNode.function(node).length());
            length = Math.max(length, currentLength);
            E leftChild = toLeftChild.function(node);
            if (leftChild != null) {
                queue.addLast(new OrderedTuple<>(leftChild, 2 * currentLength + 1));
            }
            E rightChild = toRightChild.function(node);
            if (rightChild != null) {
                queue.addLast(new OrderedTuple<>(rightChild, 2 * currentLength + 1));
            }
        }
        longestLength += (longestLength % 2);

        if (length == 1) {
            return stringOfNode.function(rootNode);
        }

        String[] asArray = new String[length];
        for (int i = 0; i < length; i += 1) {
            asArray[i] = blankString(longestLength);
        }

        queue.clear();
        queue.addLast(new OrderedTuple<>(rootNode, 0));
        while (!queue.isEmpty()) {
            OrderedTuple<E, Integer> first = queue.removeFirst();
            E node = first.getFirst();
            int currentIndex = first.getSecond();
            String nodeString = stringOfNode.function(node);


            asArray[currentIndex] =
                blankString((longestLength - nodeString.length()) / 2) +
                nodeString +
                blankString((longestLength - nodeString.length() + 1) / 2);

            E leftChild = toLeftChild.function(node);
            if (leftChild != null) {
                queue.addLast(new OrderedTuple<>(leftChild, 2 * currentIndex + 1));
            }
            E rightChild = toRightChild.function(node);
            if (rightChild != null) {
                queue.addLast(new OrderedTuple<>(rightChild, 2 * currentIndex + 2));
            }
        }

        StringBuilder sb = new StringBuilder();

        String neighborString = "";
        int startNumber = asArray.length / 4;
        int secondNumber = asArray.length / 2;
        sb.append(blankString((longestLength) / 2 + neighborSpace + (longestLength + 2 * neighborSpace) * startNumber));
        int nextNewline = 1;

        for (int i = 0; i < asArray.length; i += 1) {
            if (i == nextNewline && i < asArray.length - 1) {
                sb.append('\n');
                if (startNumber != 0) {
                    startNumber /= 2;
                    sb.append(blankString((longestLength) / 2 + neighborSpace + (longestLength + 2 * neighborSpace) * startNumber));
                }
                secondNumber /= 2;
                neighborString = blankString(2 * neighborSpace + (longestLength + 2 * neighborSpace) * secondNumber);
                nextNewline = 2 * nextNewline + 1;
            }
            sb.append(asArray[i]).append(neighborString);
        }

        return sb.toString();
    }

    public static <E> String stringOfTree(E rootNode, G2G<E, Iterable<E>> toChildren, G2G<E, String> stringOfNode, int neighborSpace) {
        if (rootNode == null) {
            return "Empty Tree";
        }
        int longestLength = 0;
        ArrayDeque<OrderedTuple<E, OrderedTuple<Integer, Integer>>> queue = new ArrayDeque<>(); // node, parent, currentlevel
        ArrayList<ArrayList<OrderedTuple<E, Integer>>> table = new ArrayList<>();
        queue.add(new OrderedTuple<>(rootNode, new OrderedTuple<>(0, 0)));
        int valid = 0;
        while (!queue.isEmpty()) {
            OrderedTuple<E, OrderedTuple<Integer, Integer>> current = queue.removeFirst();
            E currentNode = current.getFirst();
            if (currentNode != null) {
                longestLength = Math.max(longestLength, stringOfNode.function(currentNode).length());
            }
            int parent = current.getSecond().getFirst();
            int level = current.getSecond().getSecond();
            if (valid < level) {
                break;
            }
            if (table.size() == level) {
                table.add(new ArrayList<>());
            }
            ArrayList<OrderedTuple<E, Integer>> currentRow = table.get(level);
            currentRow.add(new OrderedTuple<>(currentNode, parent));
            Iterable<E> children = currentNode == null ? new LinkedList<>() : toChildren.function(currentNode);
            boolean yay = false;
            for (E child : children) {
                if (child != null) {
                    yay = true;
                    valid = level + 1;
                    queue.add(new OrderedTuple<>(child, new OrderedTuple<>(currentRow.size() - 1, level + 1)));
                }
            }
            if (!yay) {
                currentRow.get(currentRow.size() - 1).updateSecond(-currentRow.get(currentRow.size() - 1).getSecond() - 1);
                queue.add(new OrderedTuple<>(null, new OrderedTuple<>(currentRow.size() - 1, level + 1)));
            }
        }

        ArrayList<ArrayList<OrderedTuple<String, Integer>>> tableData = new ArrayList<>();
        // first: children of; second: children before

        for (ArrayList<OrderedTuple<E, Integer>> row : table) {
            tableData.add(new ArrayList<>());
            for (OrderedTuple<E, Integer> element : row) {
                tableData.get(tableData.size() - 1).add(new OrderedTuple<>(element.getFirst() == null ? "" : stringOfNode.function(element.getFirst()), 0));
            }
        }

        for (OrderedTuple<String, Integer> element : tableData.get(tableData.size() - 1)) {
            element.updateSecond(1);
        }

        for (int i = tableData.size() - 1; i > 0; i -= 1) { // yes greater than zero - for reasons you shall see
            for (int j = 0; j < tableData.get(i).size(); j += 1) {
                int parent = table.get(i).get(j).getSecond();
                if (parent < 0) parent = -1 - parent;
                OrderedTuple<String, Integer> entry = tableData.get(i - 1).get(parent);
                entry.updateSecond(entry.getSecond() + tableData.get(i).get(j).getSecond());
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tableData.size(); i += 1) {
            int j = 0;
            for (OrderedTuple<String, Integer> element : tableData.get(i)) {
                int shouldBeLength = element.getSecond() * longestLength + (element.getSecond() - 1) * neighborSpace;
                if (table.get(i).get(j).getSecond() < 0) {
                    sb.append(blankString((shouldBeLength - element.getFirst().length()) / 2));
                    sb.append(element.getFirst());
                    sb.append(blankString((shouldBeLength + 1 - element.getFirst().length()) / 2));
                } else {
                    sb.append(dashString((shouldBeLength - element.getFirst().length()) / 2));
                    sb.append(element.getFirst());
                    sb.append(dashString((shouldBeLength + 1 - element.getFirst().length()) / 2));
                }
                sb.append(blankString(neighborSpace));
                j += 1;
            }
            if (i != tableData.size() - 1) {
                sb.append('\n');
            }
        }

        return sb.toString();
    }

}
