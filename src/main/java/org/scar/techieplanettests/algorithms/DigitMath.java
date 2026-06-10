package org.scar.techieplanettests.algorithms;

/**
 * Programming &amp; Algorithm — Question 3.
 *
 * <p>Part A: {@link #sumDigits(String)} — a recursive function that sums the
 * digits of a non-negative number supplied as a string (the input can be up to
 * 100 digits, far beyond the range of long, hence the string representation).
 *
 * <p>Part B: {@link #digitalRoot(String)} — extends part A by repeatedly
 * summing the digits of the result until a single-digit value (&lt; 10) remains.
 */
public final class DigitMath {

    private DigitMath() {
    }

    /**
     * Recursively sums the digits of the given non-negative number.
     *
     * @param digits a string of decimal digits, e.g. {@code "1234445"}
     * @return the sum of all digits
     * @throws IllegalArgumentException if the input is null, empty or contains a non-digit
     */
    public static long sumDigits(String digits) {
        validate(digits);
        return sumDigitsRecursive(digits, 0);
    }

    /**
     * Returns the digital root: keeps summing the digits of the result until a
     * single-digit value remains, e.g. {@code 1234445 -> 23 -> 5}.
     *
     * @param digits a string of decimal digits
     * @return the digital root, a value between 0 and 9
     * @throws IllegalArgumentException if the input is null, empty or contains a non-digit
     */
    public static long digitalRoot(String digits) {
        long sum = sumDigits(digits);
        return sum < 10 ? sum : digitalRoot(Long.toString(sum));
    }

    private static long sumDigitsRecursive(String digits, int index) {
        if (index == digits.length()) {
            return 0;
        }
        return (digits.charAt(index) - '0') + sumDigitsRecursive(digits, index + 1);
    }

    private static void validate(String digits) {
        if (digits == null || digits.isEmpty()) {
            throw new IllegalArgumentException("Input must be a non-empty string of digits");
        }
        for (int i = 0; i < digits.length(); i++) {
            char c = digits.charAt(i);
            if (c < '0' || c > '9') {
                throw new IllegalArgumentException(
                        "Input must contain only digits 0-9 but found '" + c + "' at position " + i);
            }
        }
    }
}
