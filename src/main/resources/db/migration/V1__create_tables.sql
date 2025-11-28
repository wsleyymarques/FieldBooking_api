-- V1__create_schema.sql
-- Schema inicial do fieldbooking (versão aprimorada)
-- Requer: PostgreSQL >= 12 (preferível). Habilita algumas extensões úteis.

BEGIN;

-- Extensions
CREATE EXTENSION IF NOT EXISTS "pgcrypto";   -- gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS btree_gist;   -- necessário para EXCLUDE com = em UUID

-- =========================
-- TABELA: users (UserAccount)
-- =========================
CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(160) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    phone           VARCHAR(30),
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_users_email UNIQUE (email)
);

-- índice para busca por email em lowercase (caso queira comparações case-insensitive)
CREATE INDEX IF NOT EXISTS idx_users_email_lower ON users (LOWER(email));

-- =========================
-- TABELA: profiles (Profile) 1:1 com users
-- =========================
CREATE TABLE profiles (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name   VARCHAR(80),
    last_name    VARCHAR(120),
    birth_date   DATE,
    document_id  VARCHAR(32),
    avatar_url   VARCHAR(512),
    CONSTRAINT fk_profiles_user
        FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE
);

-- Se quiser garantir que profile.id = user.id (1:1), você pode popular com os mesmos ids no app.
-- Alternativa: usar user_id separado; aqui mantive o padrão que você já tinha (id = user_id).

-- =========================
-- TABELA: players (Player) 1:1 com users
-- =========================
CREATE TABLE players (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    skill_level        VARCHAR(20) NOT NULL,
    preferred_position VARCHAR(20),
    rating             NUMERIC(2,1) CHECK (rating >= 0 AND rating <= 5),
    bio                VARCHAR(500),
    CONSTRAINT fk_players_user
        FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE
);

-- =========================
-- TABELA: managers (Manager) 1:1 com users
-- =========================
CREATE TABLE managers (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    venue_name     VARCHAR(160) NOT NULL,
    tax_id         VARCHAR(32),
    contact_phone  VARCHAR(30),
    address_line   VARCHAR(200),
    verified       BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_managers_user
        FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE
);

-- =========================
-- TABELA: stores (Store)
-- =========================
CREATE TABLE stores (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(120) NOT NULL,
    cnpj            VARCHAR(18) UNIQUE,

    -- Address embutido (Address)
    street          VARCHAR(120),
    number          VARCHAR(20),
    neighborhood    VARCHAR(120),
    city            VARCHAR(120),
    state           VARCHAR(2),
    zip_code        VARCHAR(12),
    complement      VARCHAR(120),
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,

    contact_email   VARCHAR(120),
    contact_phone   VARCHAR(20),
    opening_hours   VARCHAR(120),

    active          BOOLEAN        DEFAULT TRUE,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_store_name ON stores (name);

-- =========================
-- TABELA: store_amenities (Set<StoreAmenity>)
-- =========================
CREATE TABLE store_amenities (
    store_id   UUID        NOT NULL,
    amenity    VARCHAR(40) NOT NULL,
    CONSTRAINT pk_store_amenities PRIMARY KEY (store_id, amenity),
    CONSTRAINT fk_store_amenities_store
        FOREIGN KEY (store_id) REFERENCES stores (id) ON DELETE CASCADE
);

-- =========================
-- TABELA: fields (Field)
-- =========================
CREATE TABLE fields (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    store_id        UUID         NOT NULL,
    name            VARCHAR(120) NOT NULL,
    type            VARCHAR(20)  NOT NULL,  -- FieldType
    surface         VARCHAR(20)  NOT NULL,  -- Surface
    price_per_hour  NUMERIC(10,2) CHECK (price_per_hour >= 0),
    size_label      VARCHAR(40),
    indoor          BOOLEAN      NOT NULL DEFAULT FALSE,
    lighting        BOOLEAN      NOT NULL DEFAULT FALSE,
    locker_room     BOOLEAN      NOT NULL DEFAULT FALSE,
    status          VARCHAR(20)  NOT NULL,  -- FieldStatus

    CONSTRAINT fk_fields_store
        FOREIGN KEY (store_id) REFERENCES stores (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_field_store ON fields (store_id);
CREATE INDEX IF NOT EXISTS idx_field_name  ON fields (name);

-- =========================
-- TABELA: bookings (Booking)
-- =========================
CREATE TABLE bookings (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    field_id        UUID        NOT NULL,
    user_id         UUID        NOT NULL,
    start_at        TIMESTAMPTZ NOT NULL,
    end_at          TIMESTAMPTZ NOT NULL,
    status          VARCHAR(20) NOT NULL,      -- BookingStatus
    price_total     NUMERIC(10,2) CHECK (price_total >= 0),
    command_id      UUID UNIQUE,
    hold_expires_at TIMESTAMPTZ,
    version         BIGINT DEFAULT 0,

    CONSTRAINT fk_booking_field
        FOREIGN KEY (field_id) REFERENCES fields (id) ON DELETE RESTRICT,
    CONSTRAINT fk_booking_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_booking_period CHECK (start_at < end_at)
);

CREATE INDEX IF NOT EXISTS idx_booking_field  ON bookings (field_id);
CREATE INDEX IF NOT EXISTS idx_booking_user   ON bookings (user_id);
CREATE INDEX IF NOT EXISTS idx_booking_period ON bookings (start_at, end_at);

-- Exclusion constraint para evitar reservas sobrepostas no mesmo campo.
-- Garante que, para um mesmo field_id, os intervalos [start_at, end_at) não se sobreponham.
ALTER TABLE bookings
  ADD CONSTRAINT no_overlapping_bookings
  EXCLUDE USING GIST (
    field_id WITH =,
    tstzrange(start_at, end_at, '[)') WITH &&
  );

-- Observação: btree_gist extension (criada no topo) é necessária para usar '=' em UUID dentro de EXCLUDE

COMMIT;
