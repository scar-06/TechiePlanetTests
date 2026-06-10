package org.scar.techieplanettests.student.web;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.scar.techieplanettests.student.service.StudentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.TreeMap;

/**
 * Maps exceptions to RFC 9457 problem-detail responses. Detailed context is
 * logged server-side; clients only ever see safe, structured messages.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** Bean-validation failures on the request body (@Valid). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleBodyValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new TreeMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.merge(error.getField(), error.getDefaultMessage(),
                        (a, b) -> a + "; " + b));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Invalid request");
        problem.setProperty("errors", errors);
        return problem;
    }

    /** Bean-validation failures on query parameters (@Validated). */
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleParameterValidation(ConstraintViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid request parameter");
        return problem;
    }

    /** Malformed JSON, or an unknown subject name in the scores map. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadableBody(HttpMessageNotReadableException ex) {
        log.debug("Rejected unreadable request body", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Request body is invalid. Subjects must be one of: "
                        + "MATHEMATICS, ENGLISH, PHYSICS, CHEMISTRY, BIOLOGY "
                        + "and scores must be whole numbers.");
        problem.setTitle("Malformed request body");
        return problem;
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ProblemDetail handleNotFound(StudentNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Student not found");
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error handling request", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problem.setTitle("Internal server error");
        return problem;
    }
}
