import java.io.*;
import java.util.*;

/**
 * This program implements the string editing algorithm mentioned in section 5.6 of the book Computer Algorithms by
 * Horowitz, Sahni and Rajasekaran. It calculates and prints the cost matrix for converting one string into another
 * along with the possible decision sequences. Printing is skipped if length of either of the strings is greater than
 * 10. Costs for Insert, Delete and Change are set to be 0.5, 0.4 and 1.2 respectively. In addition to processing an
 * example, It accepts and processes any number of file paths as command-line arguments. Input files are expected to
 * have 2 lines - the first containing the initial string and the second containing the final string.
 */
class Code {
    public static void main(String[] args) throws FileNotFoundException {
        double insertCost = 0.5;
        double deleteCost = 0.4;
        double changeCost = 1.2; // 1.2 if x_i is not equal to y_j; else 0

        System.out.println("Example from slides (#1):");
        String x = "aabab";
        String y = "babb";

        runAndPrint(x, y, insertCost, deleteCost, changeCost);

        System.out.println("Provided test inputs (obtained from the following files):");
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i] + ":");

            Scanner inFile = new Scanner(new File(args[i]));
            x = inFile.nextLine();
            y = inFile.nextLine();

            runAndPrint(x, y, insertCost, deleteCost, changeCost);

            inFile.close();
        }
    }

    /**
     * Calls appropriate methods to generate the cost matrix, find decision sequences and print the results if both x
     * and y are less than or equal to 10 in length
     * @param x the initial string
     * @param y the final string
     * @param insertCost the cost of an insertion
     * @param deleteCost the cost of a deletion
     * @param changeCost the cost of making a change / replacement
     */
    private static void runAndPrint(String x, String y, double insertCost, double deleteCost, double changeCost) {
        double[][] matrix = generateMatrix(x, y, insertCost, deleteCost, changeCost);

        if (x.length() <= 10 && y.length() <= 10) {
            System.out.printf("Input Sequences:\nFrom: %s\nTo: %s\n\n", x, y);

            printMatrix(x, y, matrix);
            System.out.println();
        }

        System.out.printf("Final cost(n,m) = cost(%d,%d) = %.1f\n\n", x.length(), y.length(),
                matrix[x.length()][y.length()]);

        if (x.length() <= 10 && y.length() <= 10) {
            List<List<String>> decisionSequences = new ArrayList<>();
            findDecisionSequences(x, y, matrix, insertCost, deleteCost, changeCost, decisionSequences,
                    new ArrayList<>(), matrix.length - 1, matrix[matrix.length - 1].length - 1);

            System.out.println("Decision Sequences:\n" + decisionSequences + "\n");
        }
    }

    /**
     * Generates the cost matrix using the received arguments
     * @param x the initial string
     * @param y the final string
     * @param insertCost the cost of an insertion
     * @param deleteCost the cost of a deletion
     * @param changeCost the cost of making a change / replacement
     * @return the cost matrix
     */
    private static double[][] generateMatrix(String x, String y, double insertCost, double deleteCost,
                                             double changeCost) {
        double[][] matrix = new double[x.length() + 1][y.length() + 1];

        matrix[0][0] = 0;
        for (int i = 1; i < matrix.length; i++) matrix[i][0] = matrix[i - 1][0] + deleteCost;
        for (int i = 1; i < matrix[0].length; i++) matrix[0][i] = matrix[0][i - 1] + insertCost;

        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[i].length; j++) {
                List<Double> possibilities = new ArrayList<>();
                possibilities.add(matrix[i][j - 1] + insertCost);
                possibilities.add(matrix[i - 1][j] + deleteCost);
                possibilities.add(x.charAt(i - 1) != y.charAt(j - 1) ? matrix[i - 1][j - 1] + changeCost :
                        matrix[i - 1][j - 1]);

                matrix[i][j] = Collections.min(possibilities);
            }
        }

        return matrix;
    }

    /**
     * Prints a formatted version of the received cost matrix
     * @param x the initial string
     * @param y the final string
     * @param matrix the cost matrix
     */
    private static void printMatrix(String x, String y, double[][] matrix) {
        System.out.println("Matrix:");

        System.out.print(" ,    ,    ,   ");
        for (int i = 0; i < y.length(); i++) System.out.print(y.charAt(i) + (i != y.length() - 1 ? ",   " : ""));
        System.out.println();

        System.out.print(" , i/j, ");
        for (int i = 0; i <= y.length(); i++) System.out.printf((i != 10 ? "" : "\b") + "[%d]%s", i,
                (i != y.length() ? ", " : ""));
        System.out.println();

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (i >= 1 && j == 0) {
                    System.out.print(x.charAt(i - 1));
                } else if (j == 0) {
                    System.out.print(" ");
                }

                if (j == 0) System.out.printf(",%s[%d]", (i != 10 ? " " : ""), i);

                System.out.printf(",%4.1f", matrix[i][j]);
            }

            System.out.println();
        }
    }

    /**
     * Finds the decision sequences which could have transformed x into y by starting at the lower right value in the
     * received matrix and finding valid ways to top left.
     * @param x the initial string
     * @param y the final string
     * @param matrix the cost matrix
     * @param insertCost the cost of an insertion
     * @param deleteCost the cost of a deletion
     * @param changeCost the cost of making a change / replacement
     * @param decisionSequences the list to which possible/correct sequences are to be added
     * @param candidateSequence a list containing current candidate sequence - grows recursively
     * @param i current i index
     * @param j current j index
     */
    private static void findDecisionSequences(String x, String y, double[][] matrix, double insertCost,
                                              double deleteCost, double changeCost,
                                              List<List<String>> decisionSequences, List<String> candidateSequence,
                                              int i, int j) {
        if (matrix[i][j] > matrix[matrix.length - 1][matrix[matrix.length - 1].length - 1]) return;

        if (i == 0 && j == 0) {
            candidateSequence.add("[0,0]");
            Collections.reverse(candidateSequence);

            decisionSequences.add(candidateSequence);

            return;
        }

        if (j > 0 && doublesEqual(matrix[i][j - 1], matrix[i][j] - insertCost)) {
            List<String> updatedCandidateSequence = new ArrayList<>(candidateSequence);
            updatedCandidateSequence.add(String.format("[%d,%d]-I", i, j));

            findDecisionSequences(x, y, matrix, insertCost, deleteCost, changeCost, decisionSequences,
                    updatedCandidateSequence, i, j - 1);
        }

        if (i > 0 && doublesEqual(matrix[i - 1][j],matrix[i][j] - deleteCost)) {
            List<String> updatedCandidateSequence = new ArrayList<>(candidateSequence);
            updatedCandidateSequence.add(String.format("[%d,%d]-D", i, j));

            findDecisionSequences(x, y, matrix, insertCost, deleteCost, changeCost, decisionSequences,
                    updatedCandidateSequence, i - 1, j);
        }

        if (i > 0 && j > 0 && doublesEqual(matrix[i - 1][j - 1], matrix[i][j] - (x.charAt(i - 1) !=
                y.charAt(j - 1) ? changeCost : 0))) {
            List<String> updatedCandidateSequence = new ArrayList<>(candidateSequence);
            updatedCandidateSequence.add(String.format("[%d,%d]-C", i, j));

            findDecisionSequences(x, y, matrix, insertCost, deleteCost, changeCost, decisionSequences,
                    updatedCandidateSequence, i - 1, j - 1);
        }
    }

    /**
     * Compares two doubles after truncating them to 1 decimal place to avoid floating point error
     * @param one one of the doubles to compare
     * @param two another of the doubles to compare
     * @return true if equal; false otherwise
     */
    private static boolean doublesEqual(double one, double two) {
        return String.format("%.1f", one).equals(String.format("%.1f", two));
    }
}