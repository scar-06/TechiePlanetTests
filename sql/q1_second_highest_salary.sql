-- Q1: Second-highest salary — runnable proof (PostgreSQL)
-- Demonstrates why options 1, 2, 3 are correct and 4, 5 are not.

DROP TABLE IF EXISTS emp;
CREATE TABLE emp (
    id     INTEGER PRIMARY KEY,
    name   VARCHAR(30) NOT NULL,
    salary NUMERIC
);

-- A=$100, B=$80, C=$100 -> the second highest salary is $80
INSERT INTO emp (id, name, salary) VALUES
    (1, 'A', 100),
    (2, 'B', 80),
    (3, 'C', 100);

-- Option 1: CORRECT -> 80
SELECT DISTINCT(salary) FROM emp ORDER BY salary DESC LIMIT 1 OFFSET 1;

-- Option 2: CORRECT -> 80
SELECT MAX(salary) FROM emp WHERE salary < (SELECT MAX(salary) FROM emp);

-- Option 3: CORRECT -> 80
SELECT salary FROM (SELECT DISTINCT salary FROM emp ORDER BY salary DESC LIMIT 2) AS emp
ORDER BY salary LIMIT 1;

-- Option 4: INCORRECT -> 100 (DISTINCT applied after LIMIT keeps both 100s, drops the 80)
SELECT DISTINCT salary FROM (SELECT salary FROM emp ORDER BY salary DESC LIMIT 2) AS emp
ORDER BY salary LIMIT 1;

-- Option 5: INCORRECT -> 100 (no DISTINCT, the second row is still a 100)
SELECT salary FROM emp ORDER BY salary DESC OFFSET 1 LIMIT 1;
