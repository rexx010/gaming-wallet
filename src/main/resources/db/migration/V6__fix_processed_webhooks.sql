
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'processed_webhooks' AND column_name = 'event_id'
    ) THEN
        ALTER TABLE processed_webhooks ADD COLUMN event_id VARCHAR(128);
        ALTER TABLE processed_webhooks ADD CONSTRAINT uq_processed_webhooks_event_id UNIQUE (event_id);
    END IF;
END
$$;
