package com.vitorvidal.criptomoedas.adapter.out.persistence.jpa.entity;

import com.vitorvidal.criptomoedas.domain.CryptoTransaction;
import com.vitorvidal.criptomoedas.domain.Cryptocurrency;
import com.vitorvidal.criptomoedas.domain.DecimalSupport;
import com.vitorvidal.criptomoedas.domain.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class CryptoTransactionJpaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "client_id", nullable = false, updatable = false)
    private UUID clientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 10)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 3)
    private Cryptocurrency cryptocurrency;

    @Column(name = "amount_brl", nullable = false, updatable = false, precision = 38, scale = 18)
    private BigDecimal amountBRL;

    @Column(name = "amount_crypto", nullable = false, updatable = false, precision = 38, scale = 18)
    private BigDecimal amountCrypto;

    @Column(name = "exchange_rate", nullable = false, updatable = false, precision = 38, scale = 18)
    private BigDecimal exchangeRate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected CryptoTransactionJpaEntity() {
    }

    public CryptoTransactionJpaEntity(
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

    public static CryptoTransactionJpaEntity fromDomain(CryptoTransaction transaction) {
        return new CryptoTransactionJpaEntity(
                transaction.getId(),
                transaction.getClientId(),
                transaction.getType(),
                transaction.getCryptocurrency(),
                transaction.getAmountBRL(),
                transaction.getAmountCrypto(),
                transaction.getExchangeRate(),
                transaction.getCreatedAt()
        );
    }

    public CryptoTransaction toDomain() {
        return new CryptoTransaction(
                id,
                clientId,
                type,
                cryptocurrency,
                amountBRL,
                amountCrypto,
                exchangeRate,
                createdAt
        );
    }
}
