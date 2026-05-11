CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    client_id UUID NOT NULL,
    type VARCHAR(10) NOT NULL,
    cryptocurrency VARCHAR(3) NOT NULL,
    amount_brl NUMERIC(38, 18) NOT NULL,
    amount_crypto NUMERIC(38, 18) NOT NULL,
    exchange_rate NUMERIC(38, 18) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT ck_transactions_type CHECK (type IN ('BUY', 'SELL')),
    CONSTRAINT ck_transactions_cryptocurrency CHECK (cryptocurrency IN ('BTC', 'ETH', 'SOL')),
    CONSTRAINT ck_transactions_amount_brl_positive CHECK (amount_brl > 0),
    CONSTRAINT ck_transactions_amount_crypto_positive CHECK (amount_crypto > 0),
    CONSTRAINT ck_transactions_exchange_rate_positive CHECK (exchange_rate > 0)
);

CREATE INDEX idx_transactions_client_created_at ON transactions (client_id, created_at DESC);

CREATE TABLE client_balances (
    id UUID PRIMARY KEY,
    client_id UUID NOT NULL,
    cryptocurrency VARCHAR(3) NOT NULL,
    total_quantity NUMERIC(38, 18) NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_client_balances_client_crypto UNIQUE (client_id, cryptocurrency),
    CONSTRAINT ck_client_balances_cryptocurrency CHECK (cryptocurrency IN ('BTC', 'ETH', 'SOL')),
    CONSTRAINT ck_client_balances_total_quantity_non_negative CHECK (total_quantity >= 0)
);

CREATE INDEX idx_client_balances_client_id ON client_balances (client_id);
