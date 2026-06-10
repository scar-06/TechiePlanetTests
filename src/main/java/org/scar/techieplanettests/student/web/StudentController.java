package org.scar.techieplanettests.student.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.scar.techieplanettests.student.dto.CreateStudentRequest;
import org.scar.techieplanettests.student.dto.PageResponse;
import org.scar.techieplanettests.student.dto.StudentReportResponse;
import org.scar.techieplanettests.student.service.StudentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Validated
@Tag(name = "Students", description = "Record student scores and generate score reports")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Record a student's scores in the 5 subjects")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Student created"),
            @ApiResponse(responseCode = "400", description = "Validation failure (missing subject, score out of 0-100, blank name, ...)")
    })
    public StudentReportResponse createStudent(@Valid @RequestBody CreateStudentRequest request) {
        return studentService.createStudent(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single student's report (scores, mean, median, mode)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Report found"),
            @ApiResponse(responseCode = "404", description = "No student with the given id")
    })
    public StudentReportResponse getStudent(@PathVariable Long id) {
        return studentService.getStudent(id);
    }

    @GetMapping("/report")
    @Operation(summary = "Paginated score report for all students",
            description = "Each row contains the student's individual subject scores plus the "
                    + "mean, median and mode of those scores. Supports filtering by (partial, "
                    + "case-insensitive) student name and sorting by id or name.")
    public PageResponse<StudentReportResponse> getReport(
            @Parameter(description = "Zero-based page index")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Page size (1-100)")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,

            @Parameter(description = "Filter: student name contains (case-insensitive)")
            @RequestParam(required = false) String name,

            @Parameter(description = "Sort property: id or name")
            @RequestParam(defaultValue = "id") @Pattern(regexp = "id|name",
                    message = "sortBy must be one of: id, name") String sortBy,

            @Parameter(description = "Sort direction: asc or desc")
            @RequestParam(defaultValue = "asc") @Pattern(regexp = "asc|desc",
                    message = "direction must be one of: asc, desc") String direction) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        return studentService.getReport(name, PageRequest.of(page, size, sort));
    }
}
