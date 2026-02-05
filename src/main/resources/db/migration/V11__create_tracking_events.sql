CREATE TABLE tracking_event (
                                id UUID PRIMARY KEY,
                                source VARCHAR(20) NOT NULL,               -- "backend" | "frontend"
                                event_name VARCHAR(100) NOT NULL,          -- "http_request" | "click" | "heatmap"
                                user_id UUID NULL,
                                session_id VARCHAR(100) NULL,
                                request_id VARCHAR(100) NULL,
                                path VARCHAR(255) NULL,
                                method VARCHAR(10) NULL,
                                status INT NULL,
                                duration_ms BIGINT NULL,
                                ip VARCHAR(50) NULL,
                                user_agent TEXT NULL,
                                device VARCHAR(100) NULL,
                                payload JSONB NULL,
                                created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);