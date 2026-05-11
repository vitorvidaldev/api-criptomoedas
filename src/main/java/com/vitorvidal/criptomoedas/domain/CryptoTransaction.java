package com.vitorvidal.criptomoedas.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class CryptoTransaction {
    private final UUID id;
    private final UUID clientId;
    private final TransactionType type;
    private final Cryptocurrency cryptocurrency;
    private final BigDecimal amountBRL;
    private final BigDecimal amountCrypto;
    private final BigDecimal exchangeRate;
    private final Instant createdAt;

    public CryptoTransaction(
            UUID id,
            UUID clientId,
            TransactionType type,
            Cryptocurrency cryptocurrency,
            BigDecimal amountBRL,
            BigDecimal amountCrypto,
            BigDecimal exchangeRate,
            Instant createdAt
    ) {
        this.id = id;
        this.clientId = clientId;
        this.type = type;
        this.cryptocurrency = cryptocurrency;
        this.amountBRL = DecimalSupport.toStorage(amountBRL);
        this.amountCrypto = DecimalSupport.toStorage(amountCrypto);
        this.exchangeRate = DecimalSupport.toStorage(exchangeRate);
        this.createdAt = createdAt;
    }

    public static CryptoTransaction create(
            UUID clientId,
            TransactionType type,
            Cryptocurrency cryptocurrency,
            BigDecimal amountBRL,
            BigDecimal amountCrypto,
            BigDecimal exchangeRate,
            Instant createdAt
    ) {
        return new CryptoTransaction(
                UUID.randomUUID(),
                clientId,
                type,
                cryptocurrency,
                amountBRL,
                amountCrypto,
                exchangeRate,
                createdAt
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public TransactionType getType() {
        return type;
    }

    public Cryptocurrency getCryptocurrency() {
        return cryptocurrency;
    }

    public BigDecimal getAmountBRL() {
        return amountBRL;
    }

    public BigDecimal getAmountCrypto() {
        return amountCrypto;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
