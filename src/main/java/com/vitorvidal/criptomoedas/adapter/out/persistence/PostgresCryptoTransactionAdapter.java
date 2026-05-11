package com.vitorvidal.criptomoedas.adapter.out.persistence;

import com.vitorvidal.criptomoedas.adapter.out.persistence.jpa.entity.CryptoTransactionJpaEntity;
import com.vitorvidal.criptomoedas.adapter.out.persistence.jpa.repository.SpringDataCryptoTransactionRepository;
import com.vitorvidal.criptomoedas.application.port.out.CryptoTransactionPort;
import com.vitorvidal.criptomoedas.domain.CryptoTransaction;
import org.springframework.stereotype.Component;

@Component
public class PostgresCryptoTransactionAdapter implements CryptoTransactionPort {

    private final SpringDataCryptoTransactionRepository repository;

    public PostgresCryptoTransactionAdapter(SpringDataCryptoTransactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public CryptoTransaction save(CryptoTransaction transaction) {
        return repository.save(CryptoTransactionJpaEntity.fromDomain(transaction)).toDomain();
    }
}
