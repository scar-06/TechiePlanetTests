package org.scar.techieplanettests.student;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.scar.techieplanettests.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end integration test against a real PostgreSQL started by
 * Testcontainers (requires Docker; runs in the Maven {@code verify} phase).
 */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class StudentApiIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("full round trip: create a student, read them back, see them in the report")
    void createReadAndReport() throws Exception {
        String body = """
                {
                  "name": "Chinedu Okeke",
                  "scores": {
                    "MATHEMATICS": 85, "ENGLISH": 72, "PHYSICS": 90, "CHEMISTRY": 66, "BIOLOGY": 85
                  }
                }
                """;

        String location = mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.mean").value(79.60))
                .andExpect(jsonPath("$.median").value(85.00))
                .andExpect(jsonPath("$.mode[0]").value(85))
                .andReturn().getResponse().getContentAsString();

        long id = com.jayway.jsonpath.JsonPath.parse(location).read("$.id", Long.class);

        mockMvc.perform(get("/api/v1/students/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Chinedu Okeke"))
                .andExpect(jsonPath("$.scores.PHYSICS").value(90));

        mockMvc.perform(get("/api/v1/students/report").param("name", "chinedu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Chinedu Okeke"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("validation failures do not persist anything")
    void invalidRequestPersistsNothing() throws Exception {
        long before = studentRepository.count();

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "Bad Data", "scores": { "MATHEMATICS": 200 } }
                                """))
                .andExpect(status().isBadRequest());

        org.assertj.core.api.Assertions.assertThat(studentRepository.count()).isEqualTo(before);
    }

    @Test
    @DisplayName("unknown student id returns a 404 problem detail")
    void unknownStudentReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/students/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Student not found"));
    }
}
