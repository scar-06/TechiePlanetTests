package org.scar.techieplanettests.student.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.scar.techieplanettests.student.domain.Subject;

import java.util.Map;

/**
 * Request body for recording a student and their scores.
 *
 * <p>Scores are keyed by {@link Subject}, so the enum itself rejects unknown
 * subjects and duplicate keys are impossible. Exactly 5 entries are required
 * (one per subject) and each score must be between 0 and 100.
 */
@Schema(name = "CreateStudentRequest")
public record CreateStudentRequest(

        @Schema(description = "Student's full name", example = "Ada Obi")
        @NotBlank(message = "name must not be blank")
        @Size(max = 100, message = "name must be at most 100 characters")
        String name,

        @Schema(description = "Score per subject; all 5 subjects are required",
                example = "{\"MATHEMATICS\": 85, \"ENGLISH\": 72, \"PHYSICS\": 90, \"CHEMISTRY\": 66, \"BIOLOGY\": 85}")
        @NotNull(message = "scores are required")
        @Size(min = 5, max = 5, message = "scores for exactly 5 subjects are required: MATHEMATICS, ENGLISH, PHYSICS, CHEMISTRY, BIOLOGY")
        Map<Subject, @NotNull(message = "score must not be null")
                     @Min(value = 0, message = "score must be at least 0")
                     @Max(value = 100, message = "score must be at most 100") Integer> scores
) {
}
