package org.scar.techieplanettests.student.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scar.techieplanettests.student.domain.Student;
import org.scar.techieplanettests.student.domain.Subject;
import org.scar.techieplanettests.student.dto.CreateStudentRequest;
import org.scar.techieplanettests.student.dto.PageResponse;
import org.scar.techieplanettests.student.dto.StudentReportResponse;
import org.scar.techieplanettests.student.repository.StudentRepository;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    private static final Map<Subject, Integer> FULL_SCORES = Map.of(
            Subject.MATHEMATICS, 85,
            Subject.ENGLISH, 72,
            Subject.PHYSICS, 90,
            Subject.CHEMISTRY, 66,
            Subject.BIOLOGY, 85);

    @Mock
    private StudentRepository studentRepository;

    @Test
    @DisplayName("createStudent persists the student with all 5 scores and returns the computed report")
    void createStudentReturnsReport() {
        StudentService service = new StudentService(studentRepository);
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> withId(invocation.getArgument(0), 1L));

        StudentReportResponse response =
                service.createStudent(new CreateStudentRequest("Ada Obi", FULL_SCORES));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Ada Obi");
        assertThat(response.scores()).containsAllEntriesOf(FULL_SCORES);
        assertThat(response.mean()).isEqualByComparingTo(new BigDecimal("79.60"));
        assertThat(response.median()).isEqualByComparingTo(new BigDecimal("85.00"));
        assertThat(response.mode()).containsExactly(85);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("createStudent trims surrounding whitespace from the name")
    void createStudentTrimsName() {
        StudentService service = new StudentService(studentRepository);
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> withId(invocation.getArgument(0), 2L));

        StudentReportResponse response =
                service.createStudent(new CreateStudentRequest("  Ada Obi  ", FULL_SCORES));

        assertThat(response.name()).isEqualTo("Ada Obi");
    }

    @Test
    @DisplayName("getStudent throws StudentNotFoundException for an unknown id")
    void getStudentThrowsWhenMissing() {
        StudentService service = new StudentService(studentRepository);
        when(studentRepository.findWithScoresById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getStudent(99L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("getReport pages students and loads their scores in bulk")
    void getReportAssemblesPage() {
        StudentService service = new StudentService(studentRepository);
        Student student = withId(studentWithScores("Ada Obi"), 1L);
        Pageable pageable = PageRequest.of(0, 20);
        when(studentRepository.findByNameContainingIgnoreCase(eq("ada"), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(student), pageable, 1));
        when(studentRepository.findWithScoresByIdIn(anyCollection()))
                .thenReturn(List.of(student));

        PageResponse<StudentReportResponse> response = service.getReport("ada", pageable);

        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.totalPages()).isEqualTo(1);
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().getFirst().name()).isEqualTo("Ada Obi");
        assertThat(response.content().getFirst().mean())
                .isEqualByComparingTo(new BigDecimal("79.60"));
    }

    @Test
    @DisplayName("getReport treats a null filter as 'match everything'")
    void getReportHandlesNullFilter() {
        StudentService service = new StudentService(studentRepository);
        Pageable pageable = PageRequest.of(0, 20);
        when(studentRepository.findByNameContainingIgnoreCase(eq(""), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));
        when(studentRepository.findWithScoresByIdIn(anyCollection())).thenReturn(List.of());

        PageResponse<StudentReportResponse> response = service.getReport(null, pageable);

        assertThat(response.content()).isEmpty();
        assertThat(response.totalElements()).isZero();
    }

    private static Student studentWithScores(String name) {
        Student student = new Student(name);
        FULL_SCORES.forEach(student::addScore);
        return student;
    }

    private static Student withId(Student student, Long id) {
        ReflectionTestUtils.setField(student, "id", id);
        return student;
    }
}
