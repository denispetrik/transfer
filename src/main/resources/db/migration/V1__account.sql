CREATE TABLE account (
  id BIGINT PRIMARY KEY,
  number VARCHAR(16) NOT NULL,
  balance_value NUMERIC(16, 2) NOT NULL,
  balance_currency VARCHAR(3) NOT NULL
);

CREATE UNIQUE INDEX idx_account_number ON account (number);

--INSERT INTO account (id, number, balance_value, balance_currency) VALUES (1, '123', 1000.00, 'EUR');