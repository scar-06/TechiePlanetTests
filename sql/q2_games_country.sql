-- Q2: Country where the games took place each year — runnable proof (PostgreSQL)

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
    (1896, 'Athens'),
    (1948, 'London'),
    (2004, 'Athens'),
    (2008, 'Beijing'),
    (2012, 'London');

INSERT INTO city (name, country) VALUES
    ('Sydney',  'Australia'),
    ('Athens',  'Greece'),
    ('Beijing', 'China'),
    ('London',  'UK');

-- ANSWER: join the host city to its country
SELECT g.yr, c.country
FROM games g
JOIN city c ON c.name = g.city
ORDER BY g.yr;
