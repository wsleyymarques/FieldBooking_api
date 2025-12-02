ALTER TABLE users
    ADD COLUMN first_name VARCHAR(80),
    ADD COLUMN last_name VARCHAR(80);

ALTER TABLE profiles
    DROP COLUMN IF EXISTS first_name,
    DROP COLUMN IF EXISTS last_name;