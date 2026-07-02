ALTER TABLE transactions
    ADD COLUMN virtual_account_number VARCHAR(30);

ALTER TABLE transactions
    ADD COLUMN virtual_account_name VARCHAR(255);

ALTER TABLE transactions
    ADD COLUMN bank_name VARCHAR(255);
