package com.vitorvidal.criptomoedas.adapter.out.persistence.jpa.repository;

import com.vitorvidal.criptomoedas.adapter.out.persistence.jpa.entity.ClientBalanceJpaEntity;
import com.vitorvidal.criptomoedas.domain.Cryptocurrency;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataClientBalanceRepository extends JpaRepository<ClientBalanceJpaEntity, UUID> {

    List<ClientBalanceJpaEntity> findByClientId(UUID clientId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select balance
            from ClientBalanceJpaEntity balance
            where balance.clientId = :clientId
              and balance.cryptocurrency = :cryptocurrency
            """)
    Optional<ClientBalanceJpaEntity> findByClientIdAndCryptocurrencyForUpdate(
            @Param("clientId") UUID clientId,
            @Param("cryptocurrency") Cryptocurrency cryptocurrency
    );
}
