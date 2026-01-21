ALTER TABLE addresses
    ADD COLUMN country VARCHAR(2);

UPDATE addresses
SET country = 'BR'
WHERE country IS NULL;
