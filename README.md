# Techie Planet — Software Engineer Assessment

Solution to the Techie Planet take-home technical assessment: programming &
algorithm questions, SQL questions, and a Spring Boot + PostgreSQL student
scores application, all in one repository.

## Repository layout

| Path | Contents |
|------|----------|
| `src/main/java/.../algorithms/` | Section A — algorithm solutions (Q1–Q3) |
| `sql/` | Section B — SQL answers ([`sql/ANSWERS.md`](sql/ANSWERS.md)) plus runnable scripts |
| `src/main/java/.../student/`, `config/` | Section C — student scores web application |
| `src/test/java/` | Unit tests (`*Test`) and integration tests (`*IT`) |
| `Dockerfile`, `docker-compose.yml` | Containerised deployment (app + PostgreSQL) |

## Tech stack

- Java 21, Spring Boot 4 (Web MVC, Data JPA, Validation), PostgreSQL 16
- springdoc-openapi (Swagger UI), Lombok
- JUnit 5, Mockito, AssertJ, Testcontainers
- Maven (wrapper included), Docker / Docker Compose

---

## Section A — Programming & Algorithm

| Question | Solution | Tests |
|----------|----------|-------|
| Q1 Time in words | [`TimeInWords`](src/main/java/org/scar/techieplanettests/algorithms/TimeInWords.java) (+ console runner [`TimeInWordsRunner`](src/main/java/org/scar/techieplanettests/algorithms/TimeInWordsRunner.java)) | 26 tests: all assessment examples, minute 0/59, halves, quarters, 12-o'clock wrap-around, invalid input |
| Q2 Remove duplicates per row | [`ArrayDeduplicator`](src/main/java/org/scar/techieplanettests/algorithms/ArrayDeduplicator.java) | 7 tests incl. a 100,000-element row |
| Q3 Digit sum & digital root | [`DigitMath`](src/main/java/org/scar/techieplanettests/algorithms/DigitMath.java) | 20 tests incl. the 49-digit sample (=161) and digital roots |

**Q2 approach & complexity.** A `HashSet` tracks the values already seen in
each row — `HashSet.add()` returns `false` on a repeat without calling
`contains` or `containsKey`, satisfying the assessment constraint. Duplicates
are overwritten with `0`. Expected time is **O(N)** over all N elements, space
is O(m) for the set (m = longest row) plus O(N) for the result copy. The input
array is not mutated — a new array is returned.

**Q3 notes.** The input is taken as a `String` because 100 digits exceed any
primitive numeric type. Part A is a pure recursive function; Part B re-applies
it until the value is a single digit (digital root).

Run the Q1 console program:

```bash
./mvnw -q compile exec:java -Dexec.mainClass=org.scar.techieplanettests.algorithms.TimeInWordsRunner
```

(or run the classes/tests directly from your IDE).

---

## Section B — Databases & SQL

All answers with reasoning are in **[`sql/ANSWERS.md`](sql/ANSWERS.md)**.
Each question also has a runnable PostgreSQL script (`sql/q1...q4*.sql`) that
creates the tables, loads the sample data and executes the queries.

Summary:
1. **Second-highest salary** — options **1, 2 and 3** are correct; options 4 and 5
   fail when the top salary is tied.
2. **Games/country** — `JOIN` from `games.city` to the `city` lookup table.
3. **LEFT vs RIGHT JOIN** — explanation plus both statements and their result sets.
4. **Sessions** — `GROUP BY userId HAVING COUNT(*) > 1` with `AVG(duration)`.

---

## Section C — Student Scores Application

A REST API that records students' scores in **5 subjects**
(`MATHEMATICS, ENGLISH, PHYSICS, CHEMISTRY, BIOLOGY`) in PostgreSQL and
produces a report with each student's individual scores and the **mean,
median and mode** of those scores.

### Run with Docker Compose (recommended)

Requires Docker. All services — the application **and** the database — are
defined in `docker-compose.yml`:

```bash
docker compose up --build -d
```

- API base URL: `http://localhost:8080`
- **Swagger UI: <http://localhost:8080/swagger-ui.html>**
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Configuration is environment-driven; defaults work out of the box. To
customise, copy `.env.example` to `.env`. Stop with `docker compose down`
(add `-v` to also drop the database volume).

### Run locally without Docker

Needs a local PostgreSQL with a `studentscores` database
(`postgres`/`postgres` by default — override via `SPRING_DATASOURCE_*`
environment variables):

```bash
./mvnw spring-boot:run
```

### API

| Method & path | Purpose |
|---------------|---------|
| `POST /api/v1/students` | Record a student with scores in all 5 subjects |
| `GET /api/v1/students/{id}` | One student's report |
| `GET /api/v1/students/report` | Paginated report; supports `page`, `size`, `name` filter, `sortBy` (`id`/`name`), `direction` (`asc`/`desc`) |

Example:

```bash
curl -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -d '{
        "name": "Ada Obi",
        "scores": { "MATHEMATICS": 85, "ENGLISH": 72, "PHYSICS": 90, "CHEMISTRY": 66, "BIOLOGY": 85 }
      }'
```

```json
{
  "id": 1,
  "name": "Ada Obi",
  "scores": { "MATHEMATICS": 85, "ENGLISH": 72, "PHYSICS": 90, "CHEMISTRY": 66, "BIOLOGY": 85 },
  "mean": 79.60,
  "median": 85.00,
  "mode": [85]
}
```

```bash
curl "http://localhost:8080/api/v1/students/report?page=0&size=20&name=ada&sortBy=name&direction=asc"
```

Validation errors return RFC 9457 problem details with a per-field `errors`
map; scores outside 0–100, missing subjects, unknown subjects and blank names
are all rejected with HTTP 400.

### Design decisions

- **Normalised schema.** Scores live in a `subject_scores` table (one row per
  subject) instead of five columns on the student, with a unique constraint on
  `(student_id, subject)`. Adding a subject later is a data change, not a
  schema change, and duplicate subject scores are impossible by construction.
- **Subjects as an enum.** The five subjects are an enum, so the API contract
  is explicit in Swagger and unknown subjects are rejected during JSON binding.
- **Statistics in a pure utility** ([`ScoreStatistics`](src/main/java/org/scar/techieplanettests/student/service/ScoreStatistics.java))
  with no framework dependencies — trivially unit-testable.
- **No N+1 queries.** The report pages students first, then loads that page's
  scores in one bulk query (`JOIN FETCH` and pagination don't mix — Hibernate
  would page in memory).
- **Layered architecture** (controller → service → repository) with DTO
  records at the boundary; entities never leave the service layer.
- **Error handling** via a `@RestControllerAdvice` producing RFC 9457
  `ProblemDetail` responses; internals are logged server-side, never leaked.

### Assumptions

- The five subjects were not specified, so a standard science-class set is
  assumed (see enum above).
- Scores are whole numbers from 0 to 100; a student must be submitted with all
  5 subject scores at once.
- **Mean** and **median** are rounded to 2 decimal places (HALF_UP).
  **Mode**: all most-frequent scores are returned (multimodal data gives
  several); if every score occurs exactly once there is no mode and an empty
  list is returned.
- Hibernate `ddl-auto=update` manages the schema, which keeps the assessment
  self-contained; a production service would use versioned migrations
  (Flyway/Liquibase).
- Database credentials default to `postgres`/`postgres` for local convenience
  and are overridable via environment variables (`.env` with docker-compose);
  real deployments would use a secret manager.

### Tests

```bash
./mvnw test     # unit tests only (no Docker needed)
./mvnw verify   # unit + integration tests (needs Docker for Testcontainers)
```

- **Unit tests (79):** algorithms, statistics, service (Mockito) and the web
  layer (`@WebMvcTest` + MockMvc, covering validation and error mapping).
- **Integration tests (3, bonus):** `StudentApiIT` boots the full application
  against a real PostgreSQL started by Testcontainers and exercises the
  create → read → report round trip, validation rollback and 404 handling.
