ALTER TABLE addresses
    ADD COLUMN IF NOT EXISTS profile_id uuid;

ALTER TABLE addresses
    ADD COLUMN IF NOT EXISTS store_id uuid;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'fk_addresses_profile'
  ) THEN
ALTER TABLE addresses
    ADD CONSTRAINT fk_addresses_profile
        FOREIGN KEY (profile_id) REFERENCES profiles(id);
END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint WHERE conname = 'fk_addresses_store'
  ) THEN
ALTER TABLE addresses
    ADD CONSTRAINT fk_addresses_store
        FOREIGN KEY (store_id) REFERENCES stores(id);
END IF;
END $$;
