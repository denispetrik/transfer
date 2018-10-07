CREATE SEQUENCE seq_transfer_operation_id AS BIGINT START WITH 1;

CREATE TABLE transfer_operation (
  id BIGINT PRIMARY KEY,
  state INTEGER NOT NULL,
  external_id VARCHAR(36) NOT NULL,
  sender_account_id BIGINT NOT NULL,
  receiver_account_id BIGINT NOT NULL,
  amount_value NUMERIC(16, 2) NOT NULL,
  amount_currency VARCHAR(3) NOT NULL,
  error_code INTEGER
);

CREATE UNIQUE INDEX idx_transfer_operation_external_id ON transfer_operation (external_id);
