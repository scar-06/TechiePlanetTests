# Databases & SQL — Answers

## Question 1 — Second-largest salary

> Given `emp(id, name, salary)`, select all queries that return the second largest
> salary, where ties may exist. With salaries $100, $80, $100 the second highest
> salary is **$80**.

**Correct options: 1, 2 and 3.**

| # | Query | Verdict |
|---|-------|---------|
| 1 | `SELECT DISTINCT(salary) FROM emp ORDER BY salary DESC LIMIT 1 OFFSET 1;` | ✅ Correct |
| 2 | `SELECT MAX(salary) FROM emp WHERE salary < (SELECT MAX(salary) FROM emp);` | ✅ Correct |
| 3 | `SELECT salary FROM (SELECT DISTINCT salary FROM emp ORDER BY salary DESC LIMIT 2) AS emp ORDER BY salary LIMIT 1;` | ✅ Correct |
| 4 | `SELECT DISTINCT salary FROM (SELECT salary FROM emp ORDER BY salary DESC LIMIT 2) AS emp ORDER BY salary LIMIT 1;` | ❌ Incorrect |
| 5 | `SELECT salary FROM emp ORDER BY salary DESC OFFSET 1 LIMIT 1;` | ❌ Incorrect |

**Reasoning** (using salaries 100, 80, 100):

1. **Correct.** `DISTINCT` collapses the duplicate 100s to the set `{100, 80}`;
   ordering descending and skipping the first row yields **80**.
2. **Correct.** The subquery finds the maximum (100); the outer query finds the
   maximum among salaries strictly below it, which is **80**. (If every employee
   earned the same amount this returns `NULL`, which is reasonable since no
   second-highest salary exists.)
3. **Correct.** The inner query produces the two highest *distinct* salaries
   `(100, 80)`; the outer query orders them ascending and takes the first, **80**.
4. **Incorrect.** `DISTINCT` is applied *after* the limit. The inner query takes the
   top two *rows* — `(100, 100)` because of the tie — so the deduplicated result is
   just `{100}` and the query returns **100**.
5. **Incorrect.** Without `DISTINCT`, the ordered rows are `(100, 100, 80)`;
   skipping one row still lands on the duplicate **100**.

Runnable proof: [`q1_second_highest_salary.sql`](q1_second_highest_salary.sql)

---

## Question 2 — Country where the games took place each year

Tables (from the assessment):

```
games(yr, city)                 city(name, country)
1896  Athens                    Sydney   Australia
1948  London                    Athens   Greece
2004  Athens                    Beijing  China
2008  Beijing                   London   UK
2012  London
```

The `games` table stores the host *city*, so the country is obtained by joining
to the `city` lookup table on the city name:

```sql
SELECT g.yr, c.country
FROM games g
JOIN city c ON c.name = g.city
ORDER BY g.yr;
```

Result:

| yr   | country   |
|------|-----------|
| 1896 | Greece    |
| 1948 | UK        |
| 2004 | Greece    |
| 2008 | China     |
| 2012 | UK        |

Runnable proof: [`q2_games_country.sql`](q2_games_country.sql)

---

## Question 3 — LEFT JOIN vs RIGHT JOIN

Tables (from the assessment):

```
games(yr, city)                 city(name, country)
2004  Athens                    Sydney   Australia
2008  Beijing                   Athens   Greece
2012  London                    Beijing  China
2032  (no city yet)             London   UK
```

A plain `INNER JOIN` only returns rows that match on **both** sides. The outer
joins differ in which side's unmatched rows are preserved:

### LEFT JOIN

`LEFT JOIN` returns **every row of the left table** (`games`). When a games row
has no matching city, the columns coming from `city` are filled with `NULL`.

```sql
SELECT g.yr, g.city, c.country
FROM games g
LEFT JOIN city c ON c.name = g.city;
```

| yr   | city    | country |
|------|---------|---------|
| 2004 | Athens  | Greece  |
| 2008 | Beijing | China   |
| 2012 | London  | UK      |
| 2032 | NULL    | NULL    |

The 2032 games have no host city yet, but the row is still returned — that is
the defining behaviour of a LEFT JOIN. (Sydney does **not** appear: it is on the
right side and has no match.)

### RIGHT JOIN

`RIGHT JOIN` is the mirror image: it returns **every row of the right table**
(`city`). When a city has never hosted, the columns coming from `games` are
filled with `NULL`.

```sql
SELECT g.yr, g.city, c.country
FROM games g
RIGHT JOIN city c ON c.name = g.city;
```

| yr   | city    | country   |
|------|---------|-----------|
| 2004 | Athens  | Greece    |
| 2008 | Beijing | China     |
| 2012 | London  | UK        |
| NULL | NULL    | Australia |

Sydney/Australia is kept even though no games row references it, while the
unmatched 2032 games row is dropped (it is on the left side).

Runnable proof: [`q3_left_right_join.sql`](q3_left_right_join.sql)

---

## Question 4 — Average session duration for users with more than one session

```sql
CREATE TABLE sessions (
    id       INTEGER NOT NULL PRIMARY KEY,
    userId   INTEGER NOT NULL,
    duration DECIMAL NOT NULL
);
```

Group by user, keep only groups with more than one row (`HAVING` filters on the
aggregate, which a `WHERE` clause cannot do), and average the duration:

```sql
SELECT userId           AS "UserId",
       AVG(duration)    AS "AverageDuration"
FROM sessions
GROUP BY userId
HAVING COUNT(*) > 1;
```

Runnable proof: [`q4_avg_session_duration.sql`](q4_avg_session_duration.sql)
