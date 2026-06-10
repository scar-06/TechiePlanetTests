package org.scar.techieplanettests.student.domain;

/**
 * The five subjects every student is scored in.
 *
 * <p>The assessment asks for "scores of students in 5 subjects"; the concrete
 * subjects were not specified, so a standard set is assumed (documented in the
 * README). Using an enum guarantees a student can never have two scores for
 * the same subject and makes the API contract explicit in Swagger.
 */
public enum Subject {
    MATHEMATICS,
    ENGLISH,
    PHYSICS,
    CHEMISTRY,
    BIOLOGY
}
