package com.vitorvidal.criptomoedas.adapter.in.web.dto;

import com.vitorvidal.criptomoedas.application.result.TransactionResult;
import com.vitorvidal.criptomoedas.domain.Cryptocurrency;
import com.vitorvidal.criptomoedas.domain.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID clientId,
        TransactionType type,
        Cryptocurrency cryptocurrency,
        BigDecimal amountBRL,
        BigDecimal amountCrypto,
        BigDecimal exchangeRate,
        Instant timestamp
) {
    public static TransactionResponse from(TransactionResult result) {
        return new TransactionResponse(
                result.id(),
                result.clientId(),
                result.type(),
                result.cryptocurrency(),
                result.amountBRL(),
                result.amountCrypto(),
                result.exchangeRate(),
                result.timestamp()
        );
    }
}
