-- Q3: LEFT JOIN vs RIGHT JOIN — runnable proof (PostgreSQL)
-- See sql/ANSWERS.md for the written explanation.

DROP TABLE IF EXISTS games;
DROP TABLE IF EXISTS city;

CREATE TABLE games (
    yr   INTEGER,
    city VARCHAR(30)
);

CREATE TABLE city (
    name    VARCHAR(30),
    country VARCHAR(30)
);

INSERT INTO games (yr, city) VALUES
    (2004, 'Athens'),
    (2008, 'Beijing'),
    (2012, 'London'),
    (2032, NULL);          -- future games, host city not decided yet

INSERT INTO city (name, country) VALUES
    ('Sydney',  'Australia'),
    ('Athens',  'Greece'),
    ('Beijing', 'China'),
    ('London',  'UK');

-- LEFT JOIN: keeps EVERY games row; city columns are NULL when unmatched.
-- The 2032 row survives with NULL country; Sydney does not appear.
SELECT g.yr, g.city, c.country
FROM games g
LEFT JOIN city c ON c.name = g.city;

-- RIGHT JOIN: keeps EVERY city row; games columns are NULL when unmatched.
-- Sydney/Australia survives with NULL yr; the unmatched 2032 row is dropped.
SELECT g.yr, g.city, c.country
FROM games g
RIGHT JOIN city c ON c.name = g.city;
