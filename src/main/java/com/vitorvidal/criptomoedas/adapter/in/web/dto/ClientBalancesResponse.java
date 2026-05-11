package com.vitorvidal.criptomoedas.adapter.in.web.dto;

import com.vitorvidal.criptomoedas.application.result.ClientBalancesResult;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public record ClientBalancesResponse(
        UUID clientId,
        Map<String, CryptoBalanceResponse> balances,
        BigDecimal totalValueBRL,
        Instant updatedAt
) {
    public static ClientBalancesResponse from(ClientBalancesResult result) {
        Map<String, CryptoBalanceResponse> balances = result.balances()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> CryptoBalanceResponse.from(entry.getValue()),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        return new ClientBalancesResponse(
                result.clientId(),
                balances,
                result.totalValueBRL(),
                result.updatedAt()
        );
    }
}
