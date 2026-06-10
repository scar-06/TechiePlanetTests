package org.scar.techieplanettests.student.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * A single subject score (0-100) belonging to a {@link Student}.
 */
@Entity
@Table(
        name = "subject_scores",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_subject_scores_student_subject",
                columnNames = {"student_id", "subject"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // required by JPA only
public class SubjectScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Subject subject;

    @Column(nullable = false)
    private int score;

    SubjectScore(Student student, Subject subject, int score) {
        this.student = student;
        this.subject = subject;
        this.score = score;
    }
}
