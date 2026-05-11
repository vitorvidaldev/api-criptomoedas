package com.vitorvidal.criptomoedas.application.port.out;

import com.vitorvidal.criptomoedas.domain.ClientBalance;
import com.vitorvidal.criptomoedas.domain.Cryptocurrency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientBalancePort {
    List<ClientBalance> findByClientId(UUID clientId);

    Optional<ClientBalance> findByClientIdAndCryptocurrencyForUpdate(UUID clientId, Cryptocurrency cryptocurrency);

    ClientBalance save(ClientBalance balance);
}
