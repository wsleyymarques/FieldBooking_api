-- Vincula Store ao Profile (dono)
ALTER TABLE stores
    ADD COLUMN profile_id UUID;

ALTER TABLE stores
    ADD CONSTRAINT fk_stores_profile
        FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_store_profile ON stores (profile_id);
