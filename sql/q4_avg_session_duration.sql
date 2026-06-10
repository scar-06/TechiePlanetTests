-- Q4: Average session duration for users with more than one session (PostgreSQL)

DROP TABLE IF EXISTS sessions;

CREATE TABLE sessions (
    id       INTEGER NOT NULL PRIMARY KEY,
    userId   INTEGER NOT NULL,
    duration DECIMAL NOT NULL
);

INSERT INTO sessions (id, userId, duration) VALUES
    (1, 1, 10),
    (2, 1, 14),   -- user 1: two sessions, average 12
    (3, 2, 30);   -- user 2: a single session -> excluded

-- ANSWER: HAVING filters on the aggregate after grouping,
-- which a WHERE clause cannot do.
SELECT userId        AS "UserId",
       AVG(duration) AS "AverageDuration"
FROM sessions
GROUP BY userId
HAVING COUNT(*) > 1;

-- Expected output:
-- | UserId | AverageDuration |
-- |--------|-----------------|
-- | 1      | 12              |
