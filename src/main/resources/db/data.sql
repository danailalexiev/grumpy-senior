INSERT INTO users (username, password)
SELECT 'admin', '$2a$10$S5JYMTMNTYY7p.v0Y91/D.Gv77WQI1PklJyVli3mKZ/KlAWZQ0pCu'
    WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);
