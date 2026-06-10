package org.scar.techieplanettests.student.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScoreStatisticsTest {

    @Nested
    @DisplayName("mean")
    class Mean {

        @Test
        void averagesTheScores() {
            assertThat(ScoreStatistics.mean(List.of(85, 72, 90, 66, 85)))
                    .isEqualByComparingTo(new BigDecimal("79.60"));
        }

        @Test
        void roundsToTwoDecimalPlaces() {
            // (1 + 1 + 2) / 3 = 1.333... -> 1.33
            assertThat(ScoreStatistics.mean(List.of(1, 1, 2)))
                    .isEqualByComparingTo(new BigDecimal("1.33"));
        }

        @Test
        void handlesBoundaryScores() {
            assertThat(ScoreStatistics.mean(List.of(0, 100)))
                    .isEqualByComparingTo(new BigDecimal("50.00"));
        }
    }

    @Nested
    @DisplayName("median")
    class Median {

        @Test
        void returnsMiddleValueForOddCount() {
            assertThat(ScoreStatistics.median(List.of(85, 72, 90, 66, 85)))
                    .isEqualByComparingTo(new BigDecimal("85.00"));
        }

        @Test
        void averagesTwoMiddleValuesForEvenCount() {
            assertThat(ScoreStatistics.median(List.of(10, 20, 31, 41)))
                    .isEqualByComparingTo(new BigDecimal("25.50"));
        }

        @Test
        void isNotAffectedByInputOrder() {
            assertThat(ScoreStatistics.median(List.of(100, 0, 50)))
                    .isEqualByComparingTo(new BigDecimal("50.00"));
        }
    }

    @Nested
    @DisplayName("mode")
    class Mode {

        @Test
        void returnsTheMostFrequentScore() {
            assertThat(ScoreStatistics.modes(List.of(85, 72, 90, 66, 85)))
                    .containsExactly(85);
        }

        @Test
        void returnsAllModesAscendingWhenMultimodal() {
            assertThat(ScoreStatistics.modes(List.of(90, 85, 90, 85, 70)))
                    .containsExactly(85, 90);
        }

        @Test
        void returnsEmptyWhenAllScoresAreDistinct() {
            assertThat(ScoreStatistics.modes(List.of(10, 20, 30, 40, 50))).isEmpty();
        }

        @Test
        void singleValueRepeatedIsTheMode() {
            assertThat(ScoreStatistics.modes(List.of(70, 70, 70, 70, 70)))
                    .containsExactly(70);
        }
    }

    @Test
    @DisplayName("all functions reject null or empty input")
    void rejectsNullAndEmptyInput() {
        assertThatThrownBy(() -> ScoreStatistics.mean(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ScoreStatistics.median(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ScoreStatistics.modes(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
