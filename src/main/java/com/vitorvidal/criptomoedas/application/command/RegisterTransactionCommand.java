package com.vitorvidal.criptomoedas.application.command;

import com.vitorvidal.criptomoedas.domain.Cryptocurrency;
import com.vitorvidal.criptomoedas.domain.TransactionType;
import java.math.BigDecimal;
import java.util.UUID;

public record RegisterTransactionCommand(
        UUID clientId,
        TransactionType type,
        Cryptocurrency cryptocurrency,
        BigDecimal amountBRL,
        BigDecimal amountCrypto
) {
}
