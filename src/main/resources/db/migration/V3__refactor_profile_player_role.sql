-- V3__refactor_profile_player_role.sql

-- 1) Adiciona coluna de role em users
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- 2) Remove tabela managers (já que não será mais usada)
DROP TABLE IF EXISTS managers;

-- 3) Ajusta players para referenciar profiles (e não mais users)
--    Se for dev e não tiver dados importantes, é mais simples dropar e recriar

DROP TABLE IF EXISTS players;

CREATE TABLE players (
    id                  UUID PRIMARY KEY,
    skill_level         VARCHAR(20) NOT NULL,
    preferred_position  VARCHAR(20),
    rating              NUMERIC(2,1),
    bio                 VARCHAR(500),

    CONSTRAINT fk_players_profile
        FOREIGN KEY (id) REFERENCES profiles (id)
);

-- 4) Vincula address ao profile (1:1 opcional)
ALTER TABLE profiles
    ADD COLUMN IF NOT EXISTS address_id UUID;

ALTER TABLE profiles
    ADD CONSTRAINT fk_profiles_address
        FOREIGN KEY (address_id) REFERENCES addresses (id);
