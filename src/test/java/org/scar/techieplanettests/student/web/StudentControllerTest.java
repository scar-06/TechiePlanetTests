package org.scar.techieplanettests.student.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.scar.techieplanettests.student.domain.Subject;
import org.scar.techieplanettests.student.dto.CreateStudentRequest;
import org.scar.techieplanettests.student.dto.PageResponse;
import org.scar.techieplanettests.student.dto.StudentReportResponse;
import org.scar.techieplanettests.student.service.StudentNotFoundException;
import org.scar.techieplanettests.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    private static final StudentReportResponse SAMPLE_REPORT = new StudentReportResponse(
            1L,
            "Ada Obi",
            Map.of(Subject.MATHEMATICS, 85, Subject.ENGLISH, 72, Subject.PHYSICS, 90,
                    Subject.CHEMISTRY, 66, Subject.BIOLOGY, 85),
            new BigDecimal("79.60"),
            new BigDecimal("85.00"),
            List.of(85));

    private static final String VALID_BODY = """
            {
              "name": "Ada Obi",
              "scores": {
                "MATHEMATICS": 85, "ENGLISH": 72, "PHYSICS": 90, "CHEMISTRY": 66, "BIOLOGY": 85
              }
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Test
    @DisplayName("POST /api/v1/students returns 201 with the computed report")
    void createStudentReturns201() throws Exception {
        when(studentService.createStudent(any(CreateStudentRequest.class)))
                .thenReturn(SAMPLE_REPORT);

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ada Obi"))
                .andExpect(jsonPath("$.scores.MATHEMATICS").value(85))
                .andExpect(jsonPath("$.mean").value(79.60))
                .andExpect(jsonPath("$.median").value(85.00))
                .andExpect(jsonPath("$.mode[0]").value(85));
    }

    @Test
    @DisplayName("POST returns 400 when a score is above 100")
    void rejectsScoreAbove100() throws Exception {
        String body = VALID_BODY.replace("\"MATHEMATICS\": 85", "\"MATHEMATICS\": 101");

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"));
    }

    @Test
    @DisplayName("POST returns 400 when a score is negative")
    void rejectsNegativeScore() throws Exception {
        String body = VALID_BODY.replace("\"ENGLISH\": 72", "\"ENGLISH\": -1");

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST returns 400 when fewer than 5 subjects are supplied")
    void rejectsMissingSubjects() throws Exception {
        String body = """
                { "name": "Ada Obi", "scores": { "MATHEMATICS": 85 } }
                """;

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.scores").exists());
    }

    @Test
    @DisplayName("POST returns 400 for an unknown subject name")
    void rejectsUnknownSubject() throws Exception {
        String body = VALID_BODY.replace("MATHEMATICS", "BASKET_WEAVING");

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Malformed request body"));
    }

    @Test
    @DisplayName("POST returns 400 for a blank name")
    void rejectsBlankName() throws Exception {
        String body = VALID_BODY.replace("Ada Obi", "   ");

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} returns 404 for an unknown student")
    void getStudentReturns404() throws Exception {
        when(studentService.getStudent(99L)).thenThrow(new StudentNotFoundException(99L));

        mockMvc.perform(get("/api/v1/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Student not found"));
    }

    @Test
    @DisplayName("GET /api/v1/students/report returns a page of report rows")
    void getReportReturnsPage() throws Exception {
        when(studentService.getReport(eq("ada"), any()))
                .thenReturn(new PageResponse<>(List.of(SAMPLE_REPORT), 0, 20, 1, 1));

        mockMvc.perform(get("/api/v1/students/report").param("name", "ada"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Ada Obi"))
                .andExpect(jsonPath("$.content[0].mean").value(79.60))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET report returns 400 for an unsupported sort property")
    void rejectsInvalidSortProperty() throws Exception {
        mockMvc.perform(get("/api/v1/students/report").param("sortBy", "salary; DROP TABLE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET report returns 400 for a page size above 100")
    void rejectsOversizedPage() throws Exception {
        mockMvc.perform(get("/api/v1/students/report").param("size", "500"))
                .andExpect(status().isBadRequest());
    }
}
