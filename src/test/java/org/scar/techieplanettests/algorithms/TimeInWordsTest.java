package org.scar.techieplanettests.algorithms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeInWordsTest {

    @ParameterizedTest(name = "{0}:{1} -> {2}")
    @CsvSource({
            "5,  0, five o'clock",
            "5,  1, one minute past five",
            "5, 10, ten minutes past five",
            "5, 15, quarter past five",
            "5, 28, twenty-eight minutes past five",
            "5, 30, half past five",
            "5, 40, twenty minutes to six",
            "5, 45, quarter to six",
            "5, 47, thirteen minutes to six"
    })
    @DisplayName("matches every example given in the assessment")
    void matchesAssessmentExamples(int hour, int minute, String expected) {
        assertThat(TimeInWords.toWords(hour, minute)).isEqualTo(expected);
    }

    @Test
    @DisplayName("sample output is capitalised: 5,47 -> Thirteen minutes to six")
    void sampleOutputIsCapitalised() {
        assertThat(TimeInWords.toSentence(5, 47)).isEqualTo("Thirteen minutes to six");
    }

    @ParameterizedTest(name = "{0}:{1} -> {2}")
    @CsvSource({
            "12,  0, twelve o'clock",
            "1,   0, one o'clock",
            "5,  59, one minute to six",
            "12, 59, one minute to one",
            "12, 45, quarter to one",
            "12, 30, half past twelve",
            "12, 15, quarter past twelve",
            "1,  59, one minute to two",
            "6,  31, twenty-nine minutes to seven",
            "6,  29, twenty-nine minutes past six"
    })
    @DisplayName("handles edge cases: minute 0 and 59, halves, quarters, hour wrap-around")
    void handlesEdgeCases(int hour, int minute, String expected) {
        assertThat(TimeInWords.toWords(hour, minute)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "H={0}, M={1} is rejected")
    @CsvSource({
            "0,  10",
            "13, 10",
            "-1, 10",
            "5,  60",
            "5,  -1",
            "13, 60"
    })
    @DisplayName("rejects out-of-range hours and minutes with a clear message")
    void rejectsInvalidInput(int hour, int minute) {
        assertThatThrownBy(() -> TimeInWords.toWords(hour, minute))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be between");
    }
}
