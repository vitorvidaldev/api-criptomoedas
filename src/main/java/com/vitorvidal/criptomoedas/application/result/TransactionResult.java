package com.vitorvidal.criptomoedas.application.result;

import com.vitorvidal.criptomoedas.domain.CryptoTransaction;
import com.vitorvidal.criptomoedas.domain.Cryptocurrency;
import com.vitorvidal.criptomoedas.domain.DecimalSupport;
import com.vitorvidal.criptomoedas.domain.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResult(
        UUID id,
        UUID clientId,
        TransactionType type,
        Cryptocurrency cryptocurrency,
        BigDecimal amountBRL,
        BigDecimal amountCrypto,
        BigDecimal exchangeRate,
        Instant timestamp
) {
    public static TransactionResult from(CryptoTransaction transaction) {
        return new TransactionResult(
                transaction.getId(),
                transaction.getClientId(),
                transaction.getType(),
                transaction.getCryptocurrency(),
                DecimalSupport.toMoney(transaction.getAmountBRL()),
                DecimalSupport.toCrypto(transaction.getAmountCrypto()),
                DecimalSupport.toMoney(transaction.getExchangeRate()),
                transaction.getCreatedAt()
        );
    }
}
