package com.vitorvidal.criptomoedas.adapter.out.persistence.jpa.entity;

import com.vitorvidal.criptomoedas.domain.ClientBalance;
import com.vitorvidal.criptomoedas.domain.Cryptocurrency;
import com.vitorvidal.criptomoedas.domain.DecimalSupport;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "client_balances",
        uniqueConstraints = @UniqueConstraint(name = "uk_client_balances_client_crypto", columnNames = {"client_id", "cryptocurrency"})
)
public class ClientBalanceJpaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "client_id", nullable = false, updatable = false)
    private UUID clientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false, length = 3)
    private Cryptocurrency cryptocurrency;

    @Column(name = "total_quantity", nullable = false, precision = 38, scale = 18)
    private BigDecimal totalQuantity;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @Version
    @Column(nullable = false)
    private Long version;

    protected ClientBalanceJpaEntity() {
    }

    public ClientBalanceJpaEntity(
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

    public static ClientBalanceJpaEntity fromDomain(ClientBalance balance) {
        return new ClientBalanceJpaEntity(
                balance.getId(),
                balance.getClientId(),
                balance.getCryptocurrency(),
                balance.getTotalQuantity(),
                balance.getLastUpdated()
        );
    }

    public ClientBalance toDomain() {
        return new ClientBalance(id, clientId, cryptocurrency, totalQuantity, lastUpdated);
    }

    public void updateFromDomain(ClientBalance balance) {
        this.totalQuantity = DecimalSupport.toStorage(balance.getTotalQuantity());
        this.lastUpdated = balance.getLastUpdated();
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
}
