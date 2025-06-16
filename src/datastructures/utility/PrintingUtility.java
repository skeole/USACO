package datastructures.utility;

public class PrintingUtility {
    
    public static void newline() {
        System.out.println();
    }
    
    public static void print(int i) {
        System.out.print(i);
        System.out.print(" ");
    }
    
    public static void println(int i) {
        System.out.println(i);
    }
    
    public static void print(long l) {
        System.out.print(l);
        System.out.print(" ");
    }
    
    public static void println(long l) {
        System.out.println(l);
    }

    public static String round(double d, int places) {
        return String.format("%." + places + "f", d);
    }
    
    public static void print(double d) {
        System.out.print(d);
        System.out.print(" ");
    }
    
    public static void print(double d, int places) {
        System.out.print(round(d, places));
        System.out.print(" ");
    }
    
    public static void println(double d) {
        System.out.println(d);
    }
    
    public static void println(double d, int places) {
        System.out.println(round(d, places));
    }

    public static void print(char c) {
        System.out.print(c);
        System.out.print(" ");
    }

    public static void println(char c) {
        System.out.println(c);
    }
    
    public static void print(boolean b) {
        System.out.print(b);
        System.out.print(" ");
    }
    
    public static void println(boolean b) {
        System.out.println(b);
    }

    public static void print(Object o) {
        System.out.print(o);
        System.out.print(" ");
    }

    public static void println(Object o) {
        System.out.println(o);
    }

    public static void println() {
        System.out.println();
    }

    public static void print(int[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            sb.append(array[i]);
            if (i == array.length - 1) {
                break;
            } else {
                sb.append(',').append(' ');
            }
        }
        sb.append(']').append(' ');
        System.out.print(sb);
    }

    public static void println(int[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            sb.append(array[i]);
            if (i == array.length - 1) {
                break;
            } else {
                sb.append(',').append(' ');
            }
        }
        sb.append(']');
        System.out.println(sb);
    }

    public static void print(long[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            sb.append(array[i]);
            if (i == array.length - 1) {
                break;
            } else {
                sb.append(',').append(' ');
            }
        }
        sb.append(']').append(' ');
        System.out.print(sb);
    }

    public static void println(long[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            sb.append(array[i]);
            if (i == array.length - 1) {
                break;
            } else {
                sb.append(',').append(' ');
            }
        }
        sb.append(']');
        System.out.println(sb);
    }

    public static void print(double[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            sb.append(array[i]);
            if (i == array.length - 1) {
                break;
            } else {
                sb.append(',').append(' ');
            }
        }
        sb.append(']').append(' ');
        System.out.print(sb);
    }

    public static void print(double[] array, int places) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            sb.append(round(array[i], places));
            if (i == array.length - 1) {
                break;
            } else {
                sb.append(',').append(' ');
            }
        }
        sb.append(']').append(' ');
        System.out.print(sb);
    }

    public static void println(double[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            sb.append(array[i]);
            if (i == array.length - 1) {
                break;
            } else {
                sb.append(',').append(' ');
            }
        }
        sb.append(']');
        System.out.println(sb);
    }

    public static void println(double[] array, int places) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            sb.append(round(array[i], places));
            if (i == array.length - 1) {
                break;
            } else {
                sb.append(',').append(' ');
            }
        }
        sb.append(']');
        System.out.println(sb);
    }

    public static void println(char[] array) {
        if (array.length == 0) {
            System.out.println("[]");
            return;
        }
        StringBuilder sb = new StringBuilder("[");
        for (char c : array) {
            sb.append(", ").append(c);
        }
        System.out.println(sb.delete(1, 3).append("]").toString());
    }

    public static void print(boolean[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            sb.append(array[i]);
            if (i == array.length - 1) {
                break;
            } else {
                sb.append(',').append(' ');
            }
        }
        sb.append(']').append(' ');
        System.out.print(sb);
    }

    public static void println(boolean[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            sb.append(array[i]);
            if (i == array.length - 1) {
                break;
            } else {
                sb.append(',').append(' ');
            }
        }
        sb.append(']');
        System.out.println(sb);
    }

    public static <E> void print(E[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            sb.append(array[i]);
            if (i == array.length - 1) {
                break;
            } else {
                sb.append(',').append(' ');
            }
        }
        sb.append(']').append(' ');
        System.out.print(sb);
    }

    public static <E> void println(E[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            sb.append(array[i]);
            if (i == array.length - 1) {
                break;
            } else {
                sb.append(',').append(' ');
            }
        }
        sb.append(']');
        System.out.println(sb);
    }

    public static void print(int[][] array) {
        int maxLength = 0;
        for (int i = 0; i < array.length; i += 1) {
            for (int j = 0; j < array.length; j += 1) {
                maxLength = Math.max(maxLength, Integer.toString(array[i][j]).length());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            if (i != 0) {
                sb.append('\n').append(' ').append(' ').append('[');
            } else {
                sb.append(' ').append('[');
            }
            for (int j = 0; j < array[i].length; j += 1) {
                for (int k = Integer.toString(array[i][j]).length(); k < maxLength; k += 1) {
                    sb.append(' ');
                }
                sb.append(array[i][j]);
                if (j == array[i].length - 1) {
                    break;
                } else {
                    sb.append(' ');
            }
            }
            sb.append(']');
            if (i == array.length - 1) {
                sb.append(' ');
            } else {
                sb.append(',');
            }
        }
        sb.append(']').append(' ');
        System.out.print(sb);
    }

    public static void println(int[][] array) {
        int maxLength = 0;
        for (int i = 0; i < array.length; i += 1) {
            for (int j = 0; j < array.length; j += 1) {
                maxLength = Math.max(maxLength, Integer.toString(array[i][j]).length());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            if (i != 0) {
                sb.append('\n').append(' ').append(' ').append('[');
            } else {
                sb.append(' ').append('[');
            }
            for (int j = 0; j < array[i].length; j += 1) {
                for (int k = Integer.toString(array[i][j]).length(); k < maxLength; k += 1) {
                    sb.append(' ');
                }
                sb.append(array[i][j]);
                if (j == array[i].length - 1) {
                    break;
                } else {
                    sb.append(' ');
            }
            }
            sb.append(']');
            if (i == array.length - 1) {
                sb.append(' ');
            } else {
                sb.append(',');
            }
        }
        sb.append(']');
        System.out.println(sb);
    }

    public static void print(long[][] array) {
        int maxLength = 0;
        for (int i = 0; i < array.length; i += 1) {
            for (int j = 0; j < array.length; j += 1) {
                maxLength = Math.max(maxLength, Long.toString(array[i][j]).length());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            if (i != 0) {
                sb.append('\n').append(' ').append(' ').append('[');
            } else {
                sb.append(' ').append('[');
            }
            for (int j = 0; j < array[i].length; j += 1) {
                for (int k = Long.toString(array[i][j]).length(); k < maxLength; k += 1) {
                    sb.append(' ');
                }
                sb.append(array[i][j]);
                if (j == array[i].length - 1) {
                    break;
                } else {
                    sb.append(' ');
            }
            }
            sb.append(']');
            if (i == array.length - 1) {
                sb.append(' ');
            } else {
                sb.append(',');
            }
        }
        sb.append(']').append(' ');
        System.out.print(sb);
    }

    public static void println(long[][] array) {
        int maxLength = 0;
        for (int i = 0; i < array.length; i += 1) {
            for (int j = 0; j < array.length; j += 1) {
                maxLength = Math.max(maxLength, Long.toString(array[i][j]).length());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            if (i != 0) {
                sb.append('\n').append(' ').append(' ').append('[');
            } else {
                sb.append(' ').append('[');
            }
            for (int j = 0; j < array[i].length; j += 1) {
                for (int k = Long.toString(array[i][j]).length(); k < maxLength; k += 1) {
                    sb.append(' ');
                }
                sb.append(array[i][j]);
                if (j == array[i].length - 1) {
                    break;
                } else {
                    sb.append(' ');
            }
            }
            sb.append(']');
            if (i == array.length - 1) {
                sb.append(' ');
            } else {
                sb.append(',');
            }
        }
        sb.append(']');
        System.out.println(sb);
    }

    public static void print(double[][] array) {
        int maxLength = 0;
        for (int i = 0; i < array.length; i += 1) {
            for (int j = 0; j < array.length; j += 1) {
                maxLength = Math.max(maxLength, Double.toString(array[i][j]).length());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            if (i != 0) {
                sb.append('\n').append(' ').append(' ').append('[');
            } else {
                sb.append(' ').append('[');
            }
            for (int j = 0; j < array[i].length; j += 1) {
                for (int k = Double.toString(array[i][j]).length(); k < maxLength; k += 1) {
                    sb.append(' ');
                }
                sb.append(array[i][j]);
                if (j == array[i].length - 1) {
                    break;
                } else {
                    sb.append(' ');
            }
            }
            sb.append(']');
            if (i == array.length - 1) {
                sb.append(' ');
            } else {
                sb.append(',');
            }
        }
        sb.append(']').append(' ');
        System.out.print(sb);
    }

    public static void print(double[][] array, int places) {
        int maxLength = 0;
        for (int i = 0; i < array.length; i += 1) {
            for (int j = 0; j < array.length; j += 1) {
                maxLength = Math.max(maxLength, round(array[i][j], places).length());
            }
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            if (i != 0) {
                sb.append('\n').append(' ').append(' ').append('[');
            } else {
                sb.append(' ').append('[');
            }
            for (int j = 0; j < array[i].length; j += 1) {
                for (int k = round(array[i][j], places).length(); k < maxLength; k += 1) {
                    sb.append(' ');
                }
                sb.append(round(array[i][j], places));
                if (j == array[i].length - 1) {
                    break;
                } else {
                    sb.append(' ');
            }
            }
            sb.append(']');
            if (i == array.length - 1) {
                sb.append(' ');
            } else {
                sb.append(',');
            }
        }
        sb.append(']').append(' ');
        System.out.print(sb);
    }

    public static void println(double[][] array) {
        int maxLength = 0;
        for (int i = 0; i < array.length; i += 1) {
            for (int j = 0; j < array.length; j += 1) {
                maxLength = Math.max(maxLength, Double.toString(array[i][j]).length());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            if (i != 0) {
                sb.append('\n').append(' ').append(' ').append('[');
            } else {
                sb.append(' ').append('[');
            }
            for (int j = 0; j < array[i].length; j += 1) {
                for (int k = Double.toString(array[i][j]).length(); k < maxLength; k += 1) {
                    sb.append(' ');
                }
                sb.append(array[i][j]);
                if (j == array[i].length - 1) {
                    break;
                } else {
                    sb.append(' ');
            }
            }
            sb.append(']');
            if (i == array.length - 1) {
                sb.append(' ');
            } else {
                sb.append(',');
            }
        }
        sb.append(']');
        System.out.println(sb);
    }

    public static void println(double[][] array, int places) {
        int maxLength = 0;
        for (int i = 0; i < array.length; i += 1) {
            for (int j = 0; j < array.length; j += 1) {
                maxLength = Math.max(maxLength, round(array[i][j], places).length());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            if (i != 0) {
                sb.append('\n').append(' ').append(' ').append('[');
            } else {
                sb.append(' ').append('[');
            }
            for (int j = 0; j < array[i].length; j += 1) {
                for (int k = round(array[i][j], places).length(); k < maxLength; k += 1) {
                    sb.append(' ');
                }
                sb.append(round(array[i][j], places));
                if (j == array[i].length - 1) {
                    break;
                } else {
                    sb.append(' ');
            }
            }
            sb.append(']');
            if (i == array.length - 1) {
                sb.append(' ');
            } else {
                sb.append(',');
            }
        }
        sb.append(']');
        System.out.println(sb);
    }

    public static void print(char[][] array) {
        int maxLength = 0;
        for (int i = 0; i < array.length; i += 1) {
            for (int j = 0; j < array.length; j += 1) {
                maxLength = Math.max(maxLength, Character.toString(array[i][j]).length());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            if (i != 0) {
                sb.append('\n').append(' ').append(' ').append('[');
            } else {
                sb.append(' ').append('[');
            }
            for (int j = 0; j < array[i].length; j += 1) {
                for (int k = Character.toString(array[i][j]).length(); k < maxLength; k += 1) {
                    sb.append(' ');
                }
                sb.append(array[i][j]);
                if (j == array[i].length - 1) {
                    break;
                } else {
                    sb.append(' ');
            }
            }
            sb.append(']');
            if (i == array.length - 1) {
                sb.append(' ');
            } else {
                sb.append(',');
            }
        }
        sb.append(']').append(' ');
        System.out.print(sb);
    }

    public static void println(char[][] array) {
        int maxLength = 0;
        for (int i = 0; i < array.length; i += 1) {
            for (int j = 0; j < array.length; j += 1) {
                maxLength = Math.max(maxLength, Character.toString(array[i][j]).length());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            if (i != 0) {
                sb.append('\n').append(' ').append(' ').append('[');
            } else {
                sb.append(' ').append('[');
            }
            for (int j = 0; j < array[i].length; j += 1) {
                for (int k = Character.toString(array[i][j]).length(); k < maxLength; k += 1) {
                    sb.append(' ');
                }
                sb.append(array[i][j]);
                if (j == array[i].length - 1) {
                    break;
                } else {
                    sb.append(' ');
            }
            }
            sb.append(']');
            if (i == array.length - 1) {
                sb.append(' ');
            } else {
                sb.append(',');
            }
        }
        sb.append(']');
        System.out.println(sb);
    }

    public static void print(boolean[][] array) {
        int maxLength = 0;
        for (int i = 0; i < array.length; i += 1) {
            for (int j = 0; j < array.length; j += 1) {
                maxLength = Math.max(maxLength, Boolean.toString(array[i][j]).length());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            if (i != 0) {
                sb.append('\n').append(' ').append(' ').append('[');
            } else {
                sb.append(' ').append('[');
            }
            for (int j = 0; j < array[i].length; j += 1) {
                for (int k = Boolean.toString(array[i][j]).length(); k < maxLength; k += 1) {
                    sb.append(' ');
                }
                sb.append(array[i][j]);
                if (j == array[i].length - 1) {
                    break;
                } else {
                    sb.append(' ');
            }
            }
            sb.append(']');
            if (i == array.length - 1) {
                sb.append(' ');
            } else {
                sb.append(',');
            }
        }
        sb.append(']').append(' ');
        System.out.print(sb);
    }

    public static void println(boolean[][] array) {
        int maxLength = 0;
        for (int i = 0; i < array.length; i += 1) {
            for (int j = 0; j < array.length; j += 1) {
                maxLength = Math.max(maxLength, Boolean.toString(array[i][j]).length());
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            if (i != 0) {
                sb.append('\n').append(' ').append(' ').append('[');
            } else {
                sb.append(' ').append('[');
            }
            for (int j = 0; j < array[i].length; j += 1) {
                for (int k = Boolean.toString(array[i][j]).length(); k < maxLength; k += 1) {
                    sb.append(' ');
                }
                sb.append(array[i][j]);
                if (j == array[i].length - 1) {
                    break;
                } else {
                    sb.append(' ');
            }
            }
            sb.append(']');
            if (i == array.length - 1) {
                sb.append(' ');
            } else {
                sb.append(',');
            }
        }
        sb.append(']');
        System.out.println(sb);
    }

    public static <E> void print(E[][] array) {
        int maxLength = 0;
        for (int i = 0; i < array.length; i += 1) {
            for (int j = 0; j < array.length; j += 1) {
                maxLength = Math.max(maxLength, array[i][j] != null ? array[i][j].toString().length() : 4);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            if (i != 0) {
                sb.append('\n').append(' ').append(' ').append('[');
            } else {
                sb.append(' ').append('[');
            }
            for (int j = 0; j < array[i].length; j += 1) {
                for (int k = array[i][j] != null ? array[i][j].toString().length() : 4; k < maxLength; k += 1) {
                    sb.append(' ');
                }
                sb.append(array[i][j]);
                if (j == array[i].length - 1) {
                    break;
                } else {
                    sb.append(' ');
            }
            }
            sb.append(']');
            if (i == array.length - 1) {
                sb.append(' ');
            } else {
                sb.append(',');
            }
        }
        sb.append(']').append(' ');
        System.out.print(sb);
    }

    public static <E> void println(E[][] array) {
        int maxLength = 0;
        for (int i = 0; i < array.length; i += 1) {
            for (int j = 0; j < array.length; j += 1) {
                maxLength = Math.max(maxLength, array[i][j] != null ? array[i][j].toString().length() : 4);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < array.length; i += 1) {
            if (i != 0) {
                sb.append('\n').append(' ').append(' ').append('[');
            } else {
                sb.append(' ').append('[');
            }
            for (int j = 0; j < array[i].length; j += 1) {
                for (int k = array[i][j] != null ? array[i][j].toString().length() : 4; k < maxLength; k += 1) {
                    sb.append(' ');
                }
                sb.append(array[i][j]);
                if (j == array[i].length - 1) {
                    break;
                } else {
                    sb.append(' ');
            }
            }
            sb.append(']');
            if (i == array.length - 1) {
                sb.append(' ');
            } else {
                sb.append(',');
            }
        }
        sb.append(']');
        System.out.println(sb);
    }

    public static void printAll(Object... toPrint) {
        StringBuilder sb = new StringBuilder();
        for (Object o : toPrint) {
            sb.append(o).append(' ');
        }
        System.out.print(sb);
    }

    public static void printlnAll(Object... toPrint) {
        StringBuilder sb = new StringBuilder();
        for (Object o : toPrint) {
            sb.append(o).append(' ');
        }
        System.out.println(sb);
    }

    public static int to_ascii(char c) {
        return (int) c;
    }
}
