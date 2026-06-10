package org.scar.techieplanettests.student.repository;

import org.scar.techieplanettests.student.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    /** Case-insensitive name filter used by the paginated report endpoint. */
    Page<Student> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Loads the given students together with their scores in a single query.
     *
     * <p>Used after the paged query above: paging and {@code JOIN FETCH} on a
     * collection do not mix well (Hibernate would page in memory), so we page
     * the students first and then fetch the scores for just that page —
     * two queries total instead of N+1.
     */
    @EntityGraph(attributePaths = "scores")
    List<Student> findWithScoresByIdIn(Collection<Long> ids);

    @EntityGraph(attributePaths = "scores")
    Optional<Student> findWithScoresById(Long id);
}
