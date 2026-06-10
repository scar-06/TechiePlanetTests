package org.scar.techieplanettests.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.scar.techieplanettests.student.domain.Student;
import org.scar.techieplanettests.student.domain.Subject;
import org.scar.techieplanettests.student.domain.SubjectScore;
import org.scar.techieplanettests.student.service.ScoreStatistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * One report row: a student's individual subject scores plus the
 * mean, median and mode of those scores.
 */
@Schema(name = "StudentReport")
public record StudentReportResponse(

        @Schema(example = "1")
        Long id,

        @Schema(example = "Ada Obi")
        String name,

        @Schema(description = "Individual score per subject")
        Map<Subject, Integer> scores,

        @Schema(description = "Arithmetic mean of the 5 scores", example = "79.60")
        BigDecimal mean,

        @Schema(description = "Median of the 5 scores", example = "85.00")
        BigDecimal median,

        @Schema(description = "Most frequent score(s); empty when all scores are distinct (no mode)",
                example = "[85]")
        List<Integer> mode
) {

    public static StudentReportResponse from(Student student) {
        Map<Subject, Integer> scoresBySubject = new EnumMap<>(Subject.class);
        List<Integer> values = new ArrayList<>();
        for (SubjectScore score : student.getScores()) {
            scoresBySubject.put(score.getSubject(), score.getScore());
            values.add(score.getScore());
        }
        return new StudentReportResponse(
                student.getId(),
                student.getName(),
                scoresBySubject,
                ScoreStatistics.mean(values),
                ScoreStatistics.median(values),
                ScoreStatistics.modes(values));
    }
}
