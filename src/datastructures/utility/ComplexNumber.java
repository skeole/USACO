package datastructures.utility;

public class ComplexNumber {
    public final int real;
    public final int imaginary;

    public ComplexNumber(int real, int imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public ComplexNumber rotateCounterClockwise() {
        return new ComplexNumber(-imaginary, real);

    }

    public ComplexNumber rotateClockwise() {
        // return this.rotateCounterClockwise().rotateCounterClockwise().rotateCounterClockwise(); // CIS 1200 reference
        return new ComplexNumber(imaginary, -real);
    }

    public ComplexNumber plus(ComplexNumber c) {
        return new ComplexNumber(real + c.real, imaginary + c.imaginary);
    }

    public ComplexNumber minus(ComplexNumber c) {
        return new ComplexNumber(real - c.real, imaginary - c.imaginary);
    }

    public ComplexNumber times(int i) {
        return new ComplexNumber(real * i, imaginary * i);
    }

    public ComplexNumber times(ComplexNumber c) {
        return new ComplexNumber(real * c.real - imaginary * c.imaginary, imaginary * c.real + real * c.imaginary);
    }

    /** (0, 0) refers to the bottom left corner of the graph. 
     * This means (x + yi) -> 
     * @return graph[length - 1 - y][x], or null if out of bounds. Note: null does not necessarily mean out of bounds */
    public <E> E getPositionInGraph(E[][] graph) {
        if (imaginary < 0 || imaginary >= graph.length || real < 0 || real >= graph[graph.length - 1 - imaginary].length) {
            return null;
        } else {
            return graph[graph.length - 1 - imaginary][real];
        }
    }

    @Override
    public String toString() {
        return String.valueOf(real) + (imaginary < 0 ? " - " : " + ") + String.valueOf(Math.abs(imaginary)) + "i";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ComplexNumber cn = (ComplexNumber) o;
        return (real == cn.real) && (imaginary == cn.imaginary);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(real) + 31 * Integer.hashCode(imaginary);
    }

}
