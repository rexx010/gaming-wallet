CREATE TABLE transactions (

                              id BIGSERIAL PRIMARY KEY,

                              reference VARCHAR(100) NOT NULL UNIQUE,

                              user_id BIGINT NOT NULL,

                              amount NUMERIC(19,2) NOT NULL,

                              transaction_type VARCHAR(20) NOT NULL,

                              transaction_status VARCHAR(20) NOT NULL,

                              spotflow_reference VARCHAR(255),

                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT fk_transaction_user
                                  FOREIGN KEY(user_id)
                                      REFERENCES users(id)

);