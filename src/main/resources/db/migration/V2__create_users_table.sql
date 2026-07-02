CREATE TABLE users (

                       id BIGSERIAL PRIMARY KEY,

                       full_name VARCHAR(255) NOT NULL,

                       wallet_balance NUMERIC(19,2) NOT NULL DEFAULT 0.00,

                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

);