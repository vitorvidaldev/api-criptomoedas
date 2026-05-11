package com.vitorvidal.criptomoedas.adapter.out.persistence;

import com.vitorvidal.criptomoedas.adapter.out.persistence.jpa.entity.ClientBalanceJpaEntity;
import com.vitorvidal.criptomoedas.adapter.out.persistence.jpa.repository.SpringDataClientBalanceRepository;
import com.vitorvidal.criptomoedas.application.port.out.ClientBalancePort;
import com.vitorvidal.criptomoedas.domain.ClientBalance;
import com.vitorvidal.criptomoedas.domain.Cryptocurrency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PostgresClientBalanceAdapter implements ClientBalancePort {

    private final SpringDataClientBalanceRepository repository;

    public PostgresClientBalanceAdapter(SpringDataClientBalanceRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ClientBalance> findByClientId(UUID clientId) {
        return repository.findByClientId(clientId)
                .stream()
                .map(ClientBalanceJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<ClientBalance> findByClientIdAndCryptocurrencyForUpdate(UUID clientId, Cryptocurrency cryptocurrency) {
        return repository.findByClientIdAndCryptocurrencyForUpdate(clientId, cryptocurrency)
                .map(ClientBalanceJpaEntity::toDomain);
    }

    @Override
    public ClientBalance save(ClientBalance balance) {
        ClientBalanceJpaEntity entity = repository.findById(balance.getId())
                .orElseGet(() -> ClientBalanceJpaEntity.fromDomain(balance));

        entity.updateFromDomain(balance);
        return repository.save(entity).toDomain();
    }
}
