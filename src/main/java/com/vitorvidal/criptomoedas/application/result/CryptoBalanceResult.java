package com.vitorvidal.criptomoedas.application.result;

import java.math.BigDecimal;

public record CryptoBalanceResult(
        BigDecimal quantity,
        BigDecimal valueBRL
) {
}
