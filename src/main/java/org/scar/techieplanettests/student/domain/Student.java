package org.scar.techieplanettests.student.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A student together with their subject scores.
 *
 * <p>Scores are modelled as a separate table (one row per subject) rather than
 * five columns, which keeps the schema normalised: adding a subject later is a
 * data change, statistics queries stay simple, and a unique constraint on
 * {@code (student_id, subject)} prevents duplicate subject scores.
 */
@Entity
@Table(name = "students")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // required by JPA only
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubjectScore> scores = new ArrayList<>();

    public Student(String name) {
        this.name = name;
        this.createdAt = Instant.now();
    }

    /** Adds a score while keeping both sides of the relationship in sync. */
    public void addScore(Subject subject, int score) {
        scores.add(new SubjectScore(this, subject, score));
    }

    /** @return an unmodifiable view, so callers cannot bypass {@link #addScore}. */
    public List<SubjectScore> getScores() {
        return List.copyOf(scores);
    }
}
