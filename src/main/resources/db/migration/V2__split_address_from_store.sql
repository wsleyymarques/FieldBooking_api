-- V2__split_address_from_store.sql

BEGIN;

CREATE TABLE addresses (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    street       VARCHAR(120),
    number       VARCHAR(20),
    neighborhood VARCHAR(120),
    city         VARCHAR(120),
    state        VARCHAR(2),
    zip_code     VARCHAR(12),
    complement   VARCHAR(120),
    latitude     DOUBLE PRECISION,
    longitude    DOUBLE PRECISION
);

ALTER TABLE stores
    ADD COLUMN address_id UUID;

ALTER TABLE stores
    ADD CONSTRAINT fk_stores_address
        FOREIGN KEY (address_id) REFERENCES addresses (id) ON DELETE SET NULL;


ALTER TABLE stores
    DROP COLUMN street,
    DROP COLUMN number,
    DROP COLUMN neighborhood,
    DROP COLUMN city,
    DROP COLUMN state,
    DROP COLUMN zip_code,
    DROP COLUMN complement,
    DROP COLUMN latitude,
    DROP COLUMN longitude;

COMMIT;
