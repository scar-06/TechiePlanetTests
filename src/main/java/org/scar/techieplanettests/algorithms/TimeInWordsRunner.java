package org.scar.techieplanettests.algorithms;

import java.util.Scanner;

/**
 * Console entry point for Question 1.
 *
 * <p>Collects input on two lines as required by the assessment:
 * the first line is H (the hours) and the second line is M (the minutes).
 * Invalid input is reported gracefully instead of crashing.
 *
 * <p>Run with: {@code mvn -q compile exec:java -Dexec.mainClass=org.scar.techieplanettests.algorithms.TimeInWordsRunner}
 */
public final class TimeInWordsRunner {

    private TimeInWordsRunner() {
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter H (hours, 1-12):");
            int hour = readInt(scanner, "H");
            System.out.println("Enter M (minutes, 0-59):");
            int minute = readInt(scanner, "M");
            System.out.println(TimeInWords.toSentence(hour, minute));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    private static int readInt(Scanner scanner, String label) {
        if (!scanner.hasNextLine()) {
            throw new IllegalArgumentException(label + " was not provided");
        }
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " must be a whole number but was '" + line + "'");
        }
    }
}
