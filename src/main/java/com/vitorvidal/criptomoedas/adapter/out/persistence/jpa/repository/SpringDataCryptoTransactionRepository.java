package com.vitorvidal.criptomoedas.adapter.out.persistence.jpa.repository;

import com.vitorvidal.criptomoedas.adapter.out.persistence.jpa.entity.CryptoTransactionJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCryptoTransactionRepository extends JpaRepository<CryptoTransactionJpaEntity, UUID> {
}
