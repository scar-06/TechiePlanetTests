package org.scar.techieplanettests.algorithms;

import java.util.HashSet;
import java.util.Set;

/**
 * Programming &amp; Algorithm — Question 2.
 *
 * <p>Removes duplicates from each row of a jagged multidimensional array,
 * replacing every duplicate occurrence with {@code 0}. The assessment forbids
 * built-in membership methods ({@code contains}, {@code containsKey}, etc.);
 * {@link HashSet#add} is used instead — it returns {@code false} on a repeat
 * without ever calling {@code contains}.
 *
 * <p><b>Approach:</b> for each row, walk the elements left to right and offer
 * each value to a fresh {@link HashSet}. If {@code add} returns {@code false}
 * the value was already seen in that row and is replaced with {@code 0}.
 *
 * <p><b>Time complexity:</b> O(N) expected, where N is the total number of
 * elements across all rows.
 *
 * <p><b>Space complexity:</b> O(m) per row for the set, where m is the length
 * of the longest row, plus O(N) for the result copy (the input is not mutated).
 */
public final class ArrayDeduplicator {

    private ArrayDeduplicator() {
    }

    /**
     * Returns a new array in which every duplicate within a row is replaced by 0.
     * The first occurrence of each value is kept; the input array is left untouched.
     *
     * @param input a jagged array; rows may have different lengths, may be empty or null
     * @return a new array with row-level duplicates replaced by 0
     */
    public static int[][] removeDuplicates(int[][] input) {
        if (input == null) {
            return new int[0][];
        }
        int[][] result = new int[input.length][];
        for (int row = 0; row < input.length; row++) {
            result[row] = deduplicateRow(input[row]);
        }
        return result;
    }

    private static int[] deduplicateRow(int[] row) {
        if (row == null) {
            return new int[0];
        }
        int[] deduplicated = new int[row.length];
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < row.length; i++) {
            deduplicated[i] = seen.add(row[i]) ? row[i] : 0;
        }
        return deduplicated;
    }
}
