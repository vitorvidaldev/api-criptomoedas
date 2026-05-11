package com.vitorvidal.criptomoedas.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ClientBalance {
    private final UUID id;
    private final UUID clientId;
    private final Cryptocurrency cryptocurrency;
    private BigDecimal totalQuantity;
    private Instant lastUpdated;

    public ClientBalance(UUID clientId, Cryptocurrency cryptocurrency, Instant now) {
        this.id = UUID.randomUUID();
        this.clientId = clientId;
        this.cryptocurrency = cryptocurrency;
        this.totalQuantity = DecimalSupport.toStorage(DecimalSupport.zeroCrypto());
        this.lastUpdated = now;
    }

    public ClientBalance(
            UUID id,
            UUID clientId,
            Cryptocurrency cryptocurrency,
            BigDecimal totalQuantity,
            Instant lastUpdated
    ) {
        this.id = id;
        this.clientId = clientId;
        this.cryptocurrency = cryptocurrency;
        this.totalQuantity = DecimalSupport.toStorage(totalQuantity);
        this.lastUpdated = lastUpdated;
    }

    public void increase(BigDecimal amountCrypto, Instant now) {
        this.totalQuantity = DecimalSupport.toStorage(this.totalQuantity.add(amountCrypto));
        this.lastUpdated = now;
    }

    public void decrease(BigDecimal amountCrypto, Instant now) {
        this.totalQuantity = DecimalSupport.toStorage(this.totalQuantity.subtract(amountCrypto));
        this.lastUpdated = now;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public Cryptocurrency getCryptocurrency() {
        return cryptocurrency;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }
}
