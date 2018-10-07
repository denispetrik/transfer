CREATE SEQUENCE seq_user_transaction_id AS BIGINT START WITH 1;

CREATE TABLE user_transaction (
  id BIGINT PRIMARY KEY,
  type INTEGER NOT NULL,
  external_id VARCHAR(36) NOT NULL,
  account_id BIGINT,
  amount_value NUMERIC(16, 2) NOT NULL,
  amount_currency VARCHAR(3) NOT NULL
);

CREATE UNIQUE INDEX idx_user_transaction_external_id ON user_transaction (external_id);
CREATE INDEX idx_user_transaction_account_id ON user_transaction (account_id);
