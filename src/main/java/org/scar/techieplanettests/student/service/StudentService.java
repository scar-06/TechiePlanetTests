package org.scar.techieplanettests.student.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scar.techieplanettests.student.domain.Student;
import org.scar.techieplanettests.student.domain.Subject;
import org.scar.techieplanettests.student.dto.CreateStudentRequest;
import org.scar.techieplanettests.student.dto.PageResponse;
import org.scar.techieplanettests.student.dto.StudentReportResponse;
import org.scar.techieplanettests.student.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;

    @Transactional
    public StudentReportResponse createStudent(CreateStudentRequest request) {
        Student student = new Student(request.name().trim());
        for (Map.Entry<Subject, Integer> entry : request.scores().entrySet()) {
            student.addScore(entry.getKey(), entry.getValue());
        }
        Student saved = studentRepository.save(student);
        log.info("Recorded scores for student id={}", saved.getId());
        return StudentReportResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public StudentReportResponse getStudent(Long id) {
        return studentRepository.findWithScoresById(id)
                .map(StudentReportResponse::from)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    /**
     * Paginated report, optionally filtered by (partial, case-insensitive) name.
     *
     * <p>Two queries: page the matching students, then load the scores for just
     * that page in bulk — avoids both N+1 selects and in-memory paging.
     */
    @Transactional(readOnly = true)
    public PageResponse<StudentReportResponse> getReport(String nameFilter, Pageable pageable) {
        String filter = nameFilter == null ? "" : nameFilter.trim();
        Page<Student> page = studentRepository.findByNameContainingIgnoreCase(filter, pageable);

        Map<Long, Student> withScores = studentRepository
                .findWithScoresByIdIn(page.getContent().stream().map(Student::getId).toList())
                .stream()
                .collect(Collectors.toMap(Student::getId, Function.identity()));

        return PageResponse.from(page, student ->
                StudentReportResponse.from(withScores.get(student.getId())));
    }
}
