package com.vitorvidal.criptomoedas.application.port.in;

import com.vitorvidal.criptomoedas.application.result.ClientBalancesResult;
import java.util.UUID;

public interface GetClientBalancesUseCase {
    ClientBalancesResult getBalances(UUID clientId);
}
