package com.vitorvidal.criptomoedas.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ClientBalancesResult(
        UUID clientId,
        Map<String, CryptoBalanceResult> balances,
        BigDecimal totalValueBRL,
        Instant updatedAt
) {
}
