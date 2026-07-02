ALTER TABLE transactions
    DROP CONSTRAINT IF EXISTS transactions_transaction_status_check;

ALTER TABLE transactions
    ADD CONSTRAINT transactions_transaction_status_check
    CHECK (transaction_status IN ('PENDING', 'SUCCESS', 'FAILED', 'ABANDONED'));
