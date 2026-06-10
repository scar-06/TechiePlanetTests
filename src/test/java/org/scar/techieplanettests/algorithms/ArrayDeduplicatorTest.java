package org.scar.techieplanettests.algorithms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ArrayDeduplicatorTest {

    @Test
    @DisplayName("matches the sample input/output from the assessment")
    void matchesAssessmentSample() {
        int[][] input = {
                {1, 3, 1, 2, 3, 4, 4, 3, 5},
                {1, 1, 1, 1, 1, 1, 1}
        };

        int[][] result = ArrayDeduplicator.removeDuplicates(input);

        assertThat(result).isDeepEqualTo(new int[][]{
                {1, 3, 0, 2, 0, 4, 0, 0, 5},
                {1, 0, 0, 0, 0, 0, 0}
        });
    }

    @Test
    @DisplayName("does not mutate the input array")
    void doesNotMutateInput() {
        int[][] input = {{1, 1, 2}};

        ArrayDeduplicator.removeDuplicates(input);

        assertThat(input).isDeepEqualTo(new int[][]{{1, 1, 2}});
    }

    @Test
    @DisplayName("duplicates are tracked per row, not across rows")
    void duplicatesAreScopedToTheirRow() {
        int[][] input = {
                {7, 8},
                {7, 8, 7}
        };

        int[][] result = ArrayDeduplicator.removeDuplicates(input);

        assertThat(result).isDeepEqualTo(new int[][]{
                {7, 8},
                {7, 8, 0}
        });
    }

    @Test
    @DisplayName("a genuine 0 is kept and later 0s are treated as duplicates")
    void handlesZeroAsARealValue() {
        int[][] input = {{0, 5, 0, 0, 5}};

        int[][] result = ArrayDeduplicator.removeDuplicates(input);

        assertThat(result).isDeepEqualTo(new int[][]{{0, 5, 0, 0, 0}});
    }

    @Test
    @DisplayName("negative values are deduplicated correctly")
    void handlesNegativeValues() {
        int[][] input = {{-1, -1, -2, 3, -2}};

        int[][] result = ArrayDeduplicator.removeDuplicates(input);

        assertThat(result).isDeepEqualTo(new int[][]{{-1, 0, -2, 3, 0}});
    }

    @Test
    @DisplayName("handles empty arrays, empty rows and null rows")
    void handlesEmptyAndNullInput() {
        assertThat(ArrayDeduplicator.removeDuplicates(null)).isEmpty();
        assertThat(ArrayDeduplicator.removeDuplicates(new int[0][])).isEmpty();
        assertThat(ArrayDeduplicator.removeDuplicates(new int[][]{{}, null}))
                .isDeepEqualTo(new int[][]{{}, {}});
    }

    @Test
    @DisplayName("processes a large row (100,000 elements) well within time limits")
    void handlesLargeRowEfficiently() {
        int size = 100_000;
        int[] row = new int[size];
        Random random = new Random(42);
        for (int i = 0; i < size; i++) {
            row[i] = random.nextInt(1000); // guarantees plenty of duplicates
        }

        long start = System.nanoTime();
        int[][] result = ArrayDeduplicator.removeDuplicates(new int[][]{row});
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

        // 1,000 distinct values at most -> everything after the first occurrences is 0
        int nonZero = 0;
        for (int value : result[0]) {
            if (value != 0) {
                nonZero++;
            }
        }
        assertThat(nonZero).isLessThanOrEqualTo(1000);
        assertThat(elapsedMillis).isLessThan(2000);
    }
}
