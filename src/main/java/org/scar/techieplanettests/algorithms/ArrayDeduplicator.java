package org.scar.techieplanettests.algorithms;

/**
 * Programming &amp; Algorithm — Question 2.
 *
 * <p>Removes duplicates from each row of a jagged multidimensional array,
 * replacing every duplicate occurrence with {@code 0}. No built-in lookup
 * helpers ({@code contains}, {@code containsKey}, {@code HashSet}, ...) are
 * used — membership is tracked with a hand-rolled open-addressing hash set
 * implemented in {@link IntHashSet} below.
 *
 * <p><b>Approach:</b> for each row, walk the elements left to right and offer
 * each value to a fresh hash set. If the value was already seen in that row,
 * write {@code 0}; otherwise keep the value. Hashing gives amortised O(1)
 * membership checks, so each element is processed in constant expected time.
 *
 * <p><b>Time complexity:</b> O(N) expected, where N is the total number of
 * elements across all rows (up to 40,000 rows x 100,000 elements). Each
 * element is hashed and probed a constant expected number of times because the
 * table is sized to keep the load factor at or below 0.5.
 *
 * <p><b>Space complexity:</b> O(m) per row for the hash set, where m is the
 * length of the longest row, plus O(N) for the result copy (the input is not
 * mutated).
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
        IntHashSet seen = new IntHashSet(row.length);
        for (int i = 0; i < row.length; i++) {
            deduplicated[i] = seen.add(row[i]) ? row[i] : 0;
        }
        return deduplicated;
    }

    /**
     * Minimal open-addressing (linear probing) hash set for primitive ints,
     * written from scratch so that no built-in membership function is used.
     *
     * <p>{@code 0} cannot be used as the internal "empty slot" marker because it
     * is a legal element value, so presence of 0 is tracked with a dedicated flag.
     */
    static final class IntHashSet {

        private final int[] keys;
        private final boolean[] occupied;
        private final int mask;
        private boolean containsZero;

        IntHashSet(int expectedSize) {
            // Capacity = smallest power of two >= 2 * expectedSize keeps the load
            // factor <= 0.5, which bounds the expected probe count to a constant.
            int capacity = 2;
            while (capacity < Math.max(2, expectedSize) * 2) {
                capacity <<= 1;
            }
            this.keys = new int[capacity];
            this.occupied = new boolean[capacity];
            this.mask = capacity - 1;
        }

        /**
         * Adds the value to the set.
         *
         * @return {@code true} if the value was not present before (first occurrence),
         *         {@code false} if it was already in the set (duplicate)
         */
        boolean add(int value) {
            if (value == 0) {
                if (containsZero) {
                    return false;
                }
                containsZero = true;
                return true;
            }
            int slot = mix(value) & mask;
            while (occupied[slot]) {
                if (keys[slot] == value) {
                    return false;
                }
                slot = (slot + 1) & mask;
            }
            occupied[slot] = true;
            keys[slot] = value;
            return true;
        }

        /**
         * Scrambles the bits of the value so that consecutive or patterned inputs
         * spread evenly across the table (variant of the murmur3 finaliser).
         */
        private static int mix(int value) {
            int h = value;
            h ^= h >>> 16;
            h *= 0x85ebca6b;
            h ^= h >>> 13;
            h *= 0xc2b2ae35;
            h ^= h >>> 16;
            return h;
        }
    }
}
