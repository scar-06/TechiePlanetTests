package org.scar.techieplanettests.student.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pure statistics functions over a list of integer scores.
 *
 * <p>Kept free of any Spring/JPA dependency so the maths is trivially unit
 * testable in isolation.
 *
 * <p>Conventions (documented in the README):
 * <ul>
 *   <li><b>mean</b> — arithmetic average, rounded to 2 decimal places (HALF_UP)</li>
 *   <li><b>median</b> — middle value of the sorted scores; the average of the two
 *       middle values when the count is even; rounded to 2 decimal places</li>
 *   <li><b>mode</b> — the most frequently occurring score(s). Multimodal data
 *       returns every mode in ascending order; when all scores are distinct
 *       there is no mode and an empty list is returned.</li>
 * </ul>
 */
public final class ScoreStatistics {

    private static final int SCALE = 2;

    private ScoreStatistics() {
    }

    public static BigDecimal mean(List<Integer> scores) {
        requireScores(scores);
        long sum = 0;
        for (int score : scores) {
            sum += score;
        }
        return BigDecimal.valueOf(sum)
                .divide(BigDecimal.valueOf(scores.size()), SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal median(List<Integer> scores) {
        requireScores(scores);
        List<Integer> sorted = new ArrayList<>(scores);
        Collections.sort(sorted);
        int middle = sorted.size() / 2;
        if (sorted.size() % 2 == 1) {
            return BigDecimal.valueOf(sorted.get(middle)).setScale(SCALE, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf((long) sorted.get(middle - 1) + sorted.get(middle))
                .divide(BigDecimal.valueOf(2), SCALE, RoundingMode.HALF_UP);
    }

    public static List<Integer> modes(List<Integer> scores) {
        requireScores(scores);
        List<Integer> sorted = new ArrayList<>(scores);
        Collections.sort(sorted);

        // Sorted input lets us count runs of equal values in one pass.
        List<Integer> modes = new ArrayList<>();
        int bestFrequency = 0;
        int index = 0;
        while (index < sorted.size()) {
            int value = sorted.get(index);
            int frequency = 0;
            while (index < sorted.size() && sorted.get(index) == value) {
                frequency++;
                index++;
            }
            if (frequency > bestFrequency) {
                bestFrequency = frequency;
                modes.clear();
                modes.add(value);
            } else if (frequency == bestFrequency) {
                modes.add(value);
            }
        }

        // Every value occurring exactly once means the data has no mode.
        if (bestFrequency <= 1) {
            return List.of();
        }
        return List.copyOf(modes);
    }

    private static void requireScores(List<Integer> scores) {
        if (scores == null || scores.isEmpty()) {
            throw new IllegalArgumentException("At least one score is required");
        }
    }
}
