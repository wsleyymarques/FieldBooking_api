ALTER TABLE api_config
    ADD COLUMN IF NOT EXISTS allowed_origins TEXT NULL;
