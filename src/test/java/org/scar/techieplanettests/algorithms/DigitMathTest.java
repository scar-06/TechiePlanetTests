package org.scar.techieplanettests.algorithms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DigitMathTest {

    @Nested
    @DisplayName("Part A: recursive digit sum")
    class SumDigits {

        @Test
        @DisplayName("matches the assessment sample: 49-digit input sums to 161")
        void matchesAssessmentSample() {
            String input = "1234445123444512344451234445123444512344451234445";
            assertThat(DigitMath.sumDigits(input)).isEqualTo(161);
        }

        @ParameterizedTest(name = "sumDigits({0}) = {1}")
        @CsvSource({
                "0, 0",
                "7, 7",
                "10, 1",
                "1234445, 23",
                "99999, 45"
        })
        void sumsDigits(String input, long expected) {
            assertThat(DigitMath.sumDigits(input)).isEqualTo(expected);
        }

        @Test
        @DisplayName("handles the maximum input length of 100 digits")
        void handlesMaximumLength() {
            String input = "9".repeat(100);
            assertThat(DigitMath.sumDigits(input)).isEqualTo(900);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"12a4", "-5", "1.5", " 12"})
        @DisplayName("rejects null, empty and non-digit input")
        void rejectsInvalidInput(String input) {
            assertThatThrownBy(() -> DigitMath.sumDigits(input))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Part B: digital root")
    class DigitalRoot {

        @Test
        @DisplayName("matches the assessment example: 1234445 -> 23 -> 5")
        void matchesAssessmentExample() {
            assertThat(DigitMath.digitalRoot("1234445")).isEqualTo(5);
        }

        @ParameterizedTest(name = "digitalRoot({0}) = {1}")
        @CsvSource({
                "0, 0",
                "9, 9",
                "10, 1",
                "99999, 9",
                "1234445123444512344451234445123444512344451234445, 8" // sum 161 -> 8
        })
        void reducesToASingleDigit(String input, long expected) {
            assertThat(DigitMath.digitalRoot(input)).isEqualTo(expected);
        }

        @Test
        @DisplayName("result is always a single digit")
        void resultIsAlwaysSingleDigit() {
            String input = "9".repeat(100); // sum 900 -> 9
            assertThat(DigitMath.digitalRoot(input)).isBetween(0L, 9L);
        }
    }
}
