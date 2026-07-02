CREATE TABLE processed_webhooks (

                                    id BIGSERIAL PRIMARY KEY,

                                    reference VARCHAR(100) UNIQUE NOT NULL,

                                    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

);
